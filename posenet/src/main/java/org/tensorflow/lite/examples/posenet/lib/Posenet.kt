/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensorflow.lite.examples.posenet.lib

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp
import org.tensorflow.lite.Interpreter

// 인식할 부위
// BodyPart 클래스에는 17 개의 본문 부분의 이름이 있습니다.
enum class BodyPart
{
    NOSE,           // 코
    LEFT_EYE,
    RIGHT_EYE,      // 눈 좌, 우
    LEFT_EAR,
    RIGHT_EAR,      // 귀 좌, 우
    LEFT_SHOULDER,
    RIGHT_SHOULDER, // 어깨 좌, 우
    LEFT_ELBOW,
    RIGHT_ELBOW,    // 팔꿈치 좌, 우
    LEFT_WRIST,
    RIGHT_WRIST,    // 손목 좌, 우
    LEFT_HIP,
    RIGHT_HIP,      // 엉덩이 좌, 우
    LEFT_KNEE,
    RIGHT_KNEE,     // 무릎 좌, 우
    LEFT_ANKLE,
    RIGHT_ANKLE     // 발목 좌, 우
}

// 위치 클래스는 비트 맵에서 키 포인트의 x 및 y 좌표를 포함합니다
class Position
{
    var x: Int = 0
    var y: Int = 0
}

// KeyPoint 클래스에는 각 bodyPart, 위치 및 점수에 대한 정보가 있습니다.
class KeyPoint
{
    var bodyPart: BodyPart = BodyPart.NOSE
    var position: Position = Position()
    var score: Float = 0.0f
//    var keypointposition: KeyPointPositionRowCol = KeyPointPositionRowCol()
}

// Person 클래스에는 주요 포인트 목록과 관련 신뢰 점수가 있습니다.
class Person
{
    var keyPoints = listOf<KeyPoint>()
    var keyPointPosition = listOf<KeyPointPositionRowCol>()
    var score: Float = 0.0f
}

// 위치 클래스는 비트 맵에서 키 포인트의 x 및 y 좌표를 포함합니다
class KeyPointPositionRowCol
{
    var row: Int = 0
    var col: Int = 0
}

class Posenet(context: Context)
{
    val TAG = "Posenet"

    /** An Interpreter for the TFLite model.
     * TFLite 모델을위한 통역사 */
    private var interpreter: Interpreter? = null

    init
    {
        interpreter = Interpreter(loadModelFile("posenet_model.tflite", context))
    }

    /** Returns value within [0,1].
     * [0,1] 내의 값을 반환합니다. */
    private fun sigmoid(x: Float): Float
    {
        return (1.0f / (1.0f + exp(-x)))
    }

    /**
     * Scale the image to a byteBuffer of [-1,1] values.
     * 이미지를 [-1,1] 값의 byteBuffer로 스케일하십시오.
     */
    private fun initInputArray(bitmap: Bitmap): ByteBuffer
    {
//        Log.e(TAG, "initInputArray() 초기화 배열 입력")

        val bytesPerChannel = 4

        val inputChannels = 3

        val batchSize = 1

        val inputBuffer = ByteBuffer.allocateDirect(
                batchSize * bytesPerChannel * bitmap.height * bitmap.width * inputChannels
                                                   )

//        Log.e(TAG, "initInputArray(): inputBuffer: " + inputBuffer)

        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        // mean = 평균
        val mean = 128.0f
        val std = 128.0f
        for (row in 0 until bitmap.height)
        {
            for (col in 0 until bitmap.width)
            {
                val pixelValue = bitmap.getPixel(col, row)
                inputBuffer.putFloat(((pixelValue shr 16 and 0xFF) - mean) / std)
                inputBuffer.putFloat(((pixelValue shr 8 and 0xFF) - mean) / std)
                inputBuffer.putFloat(((pixelValue and 0xFF) - mean) / std)
            }
        }
//        Log.e(TAG, "initInputArray(): inputBuffer: " + inputBuffer.get(0))

        return inputBuffer
    }

    /** Preload and memory map the model file, returning a MappedByteBuffer containing the model.
     * 사전로드 및 메모리는 모델 파일을 맵핑하여 모델이 포함 된 MappedByteBuffer를 리턴합니다. */
    private fun loadModelFile(path: String, context: Context): MappedByteBuffer
    {
        Log.e(TAG, "loadModelFile()")
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)

        return inputStream.channel.map(
                FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength
                                      )
    }

    /**
     * Initializes an outputMap of 1 * x * y * z FloatArrays for the model processing to populate.
     * 모델 처리를 위해 1 * x * y * z FloatArrays의 outputMap을 초기화합니다.
     */
    private fun initOutputMap(interpreter: Interpreter): HashMap<Int, Any>
    {
//        Log.e(TAG, "initOutputMap()")
        val outputMap = HashMap<Int, Any>()

        // 1 * 17 * 17 * 17 contains heatmaps
        // 1 * 17 * 17 * 17에는 히트 맵이 포함됩니다
        val heatmapsShape = interpreter.getOutputTensor(0).shape()
        outputMap[0] = Array(heatmapsShape[0])
        {
            Array(heatmapsShape[1])
            {
                Array(heatmapsShape[2])
                {
                    FloatArray(heatmapsShape[3])
                }
            }
        }

//        Log.e(TAG, "heatmapsShape[0]: " + heatmapsShape[0])
//        Log.e(TAG, "heatmapsShape[1]: " + heatmapsShape[1])
//        Log.e(TAG, "heatmapsShape[2]: " + heatmapsShape[2])
//        Log.e(TAG, "heatmapsShape[3]: " + heatmapsShape[3])

        // 1 * 17 * 17 * 34 contains offsets
        // 1 * 17 * 17 * 34 오프셋 포함
        val offsetsShape = interpreter.getOutputTensor(1).shape()
        outputMap[1] = Array(offsetsShape[0])
        {
            Array(offsetsShape[1])
            { Array(offsetsShape[2])
                {
                    FloatArray(offsetsShape[3])
                }
            }
        }

//        Log.e(TAG, "offsetsShape[0]: " + offsetsShape[0])
//        Log.e(TAG, "offsetsShape[1]: " + offsetsShape[1])
//        Log.e(TAG, "offsetsShape[2]: " + offsetsShape[2])
//        Log.e(TAG, "offsetsShape[3]: " + offsetsShape[3])

        // 1 * 17 * 17 * 32 contains forward displacements
        // 1 * 17 * 17 * 32는 전방 변위를 포함합니다
        val displacementsFwdShape = interpreter.getOutputTensor(2).shape()
        outputMap[2] = Array(offsetsShape[0])
        {
            Array(displacementsFwdShape[1])
            {
                Array(displacementsFwdShape[2])
                {
                    FloatArray(displacementsFwdShape[3])
                }
            }
        }
//        Log.e(TAG, "displacementsFwdShape[0]: " + displacementsFwdShape[0])
//        Log.e(TAG, "displacementsFwdShape[1]: " + displacementsFwdShape[1])
//        Log.e(TAG, "displacementsFwdShape[2]: " + displacementsFwdShape[2])
//        Log.e(TAG, "displacementsFwdShape[3]: " + displacementsFwdShape[3])

        // 1 * 17 * 17 * 32 contains backward displacements
        // 1 * 17 * 17 * 32는 후방 변위를 포함합니다
        val displacementsBwdShape = interpreter.getOutputTensor(3).shape()
        outputMap[3] = Array(displacementsBwdShape[0])
        {
            Array(displacementsBwdShape[1])
            {
                Array(displacementsBwdShape[2])
                {
                    FloatArray(displacementsBwdShape[3])
                }
            }
        }

//        Log.e(TAG, "displacementsBwdShape[0]: " + displacementsBwdShape[0])
//        Log.e(TAG, "displacementsBwdShape[1]: " + displacementsBwdShape[1])
//        Log.e(TAG, "displacementsBwdShape[2]: " + displacementsBwdShape[2])
//        Log.e(TAG, "displacementsBwdShape[3]: " + displacementsBwdShape[3])

        return outputMap
    }

    /**
    Estimates the pose for a single person.
    args:
    bitmap: image bitmap of frame that should be processed
    returns:
    person: a Person object containing data about keypoint locations and confidence scores

    한 사람의 자세를 추정합니다.
         인수 :
              비트 맵 : 처리해야하는 프레임의 이미지 비트 맵
         보고:
              person : 키포인트 위치 및 신뢰도 점수에 대한 데이터가 포함 된 Person 객체
     */

    // 한 사람의 신체 부위 위치를 추정하십시오.
    // 비트 맵을 전달하고 Person 객체를 얻습니다.
    fun estimateSinglePose(bitmap: Bitmap): Person
    {
//        Log.e(TAG, "estimateSinglePose() 단일 포즈 추정 메소드 실행")
        var t1: Long = SystemClock.elapsedRealtimeNanos()
        var t2: Long = SystemClock.elapsedRealtimeNanos()
        val inputArray = arrayOf(initInputArray(bitmap))

        val person = Person()

        var result = String.format("Scaling to [-1,1] took %.2f ms", 1.0f * (t2 - t1) / 1_000_000)

        Log.i("posenet", result)
//        Log.e("posenet", result)

        val outputMap = initOutputMap(interpreter!!)

        t1 = SystemClock.elapsedRealtimeNanos()
        // runForMultipleInputsOutputs = 다중 입력 출력을 위해 실행
        interpreter!!.runForMultipleInputsOutputs(inputArray, outputMap)
        t2 = SystemClock.elapsedRealtimeNanos()

        var result2 = String.format("Interpreter took %.2f ms", 1.0f * (t2 - t1) / 1_000_000)

        Log.i("posenet", result2)
//        Log.e("posenet", result2)

        val heatmaps = outputMap[0] as Array<Array<Array<FloatArray>>>
        val offsets = outputMap[1] as Array<Array<Array<FloatArray>>>

//        Log.e("posenet", "heatmaps[0]: " + heatmaps)
//        Log.e("posenet", "offsets[1]: " + offsets)

        val height = heatmaps[0].size
        val width = heatmaps[0][0].size
        val numKeypoints = heatmaps[0][0][0].size

        // Finds the (row, col) locations of where the keypoints are most likely to be.
        // 키포인트가 가장있을 위치 (행, 열) 위치를 찾습니다.
        val keypointPositions = Array(numKeypoints) { Pair(0, 0) }



//        Log.e("posenet", "keypointPositions: " + keypointPositions[numKeypoints])

        var maxRow = 0
        var maxCol = 0

        val keypointpositionList = Array(numKeypoints) { KeyPointPositionRowCol() }

        for (keypoint in 0 until numKeypoints)
        {
            var maxVal = heatmaps[0][0][0][keypoint]

//            Log.e(TAG, "heatmaps[0][0][0][" + keypoint + "]")

            for (row in 0 until height)
            {
                for (col in 0 until width)
                {
                    heatmaps[0][row][col][keypoint] = sigmoid(heatmaps[0][row][col][keypoint])
                    // Log.e(TAG, "heatmaps[0][" + row + "][" + col+ "][" + keypoint + "]: " + heatmaps[0][row][col][keypoint] )

                    if (heatmaps[0][row][col][keypoint] > maxVal)
                    {
                        maxVal = heatmaps[0][row][col][keypoint]
                        maxRow = row
                        maxCol = col
                    }
                }
            }

            // row 열, 가로
            // column 행, 세로

            keypointPositions[keypoint] = Pair(maxRow, maxCol)
//            Log.e(TAG, "keypointPositions[" + keypoint+ "]: " + keypointPositions[keypoint])

            // 인체 부위별 히트맵 위치를 PosenetActivity로 보내기
//            keypointpositionList[keypoint].row = maxRow
//            keypointpositionList[keypoint].col = maxCol
        }

        // 상체 좌표 확인

        /** 어깨 좌 평균 좌표
         *  서 있을 때 (row, col): (3, 4)
         *  스쿼트 할 때 (row, col): (3, 4)
         */

        /** 어깨 우 평균 좌표
         *  서 있을 때 (row, col): (3, 3)
         *  스쿼트 할 때 (row, col): (3, 3)
         */

        /** 팔꿈치 좌 평균 좌표
         *  서 있을 때 (row, col): (4, 4) . (4, 5)
         *  스쿼트 할 때 (row, col): (3, 5) . (3, 6)
         */

        /** 팔꿈치 우 평균 좌표
         *  서 있을 때 (row, col): (4, 3)
         *  스쿼트 할 때 (row, col): (3, 3) .(3, 4)
         */

        /** 손목 좌 평균 좌표
         *  서 있을 때 (row, col): 사진_(4, 5) . 실제_(4, 5)
         *  스쿼트 할 때 (row, col): 사진_(3, 6), 실제_(3, 6)
         */

        /** 손목 좌 평균 좌표
         *  서 있을 때 (row, col): 사진_(4, 3) . 실제_(4, 3)
         *  스쿼트 할 때 (row, col): 사진_(3, 6), 실제_(3, 6)
         */

//        Log.e(TAG, "keypointPositions[" + 5 + "]: LeftShoulder: " + keypointPositions[5])
//        Log.e(TAG, "keypointPositions[" + 6 + "]: RightShoulder: " + keypointPositions[6])
//        Log.e(TAG, "keypointPositions[" + 7 + "]: LeftElbow: " + keypointPositions[7])
//        Log.e(TAG, "keypointPositions[" + 8 + "]: RightElbow: " + keypointPositions[8])
//        Log.e(TAG, "keypointPositions[" + 9 + "]: LeftWrist: " + keypointPositions[9])
//        Log.e(TAG, "keypointPositions[" + 10 + "]: RightWrist: " + keypointPositions[10])

//        person.keyPointPosition = keypointpositionList.toList()

        // Calculating the x and y coordinates of the keypoints with offset adjustment.
        // 오프셋 조정으로 키포인트의 x 및 y 좌표를 계산합니다.
        val xCoords = IntArray(numKeypoints)
        val yCoords = IntArray(numKeypoints)

//        Log.e("posenet", "xCoords: " + xCoords)
//        Log.e("posenet", "yCoords: " + yCoords)

        val confidenceScores = FloatArray(numKeypoints)
        keypointPositions.forEachIndexed { idx, position ->
            val positionY = keypointPositions[idx].first
            val positionX = keypointPositions[idx].second

//            Log.e("posenet", "positionY: " + positionY)
//            Log.e("posenet", "positionX: " + positionX)

            yCoords[idx] = (
                    position.first / (height - 1).toFloat() * bitmap.height +
                            offsets[0][positionY][positionX][idx]
                    ).toInt()
            xCoords[idx] = (
                    position.second / (width - 1).toFloat() * bitmap.width +
                            offsets[0][positionY]
                                    [positionX][idx + numKeypoints]
                    ).toInt()
            confidenceScores[idx] = heatmaps[0][positionY][positionX][idx]

//            Log.e("posenet", "yCoords[idx]: " + yCoords[idx])
//            Log.e("posenet", "xCoords[idx]: " + xCoords[idx])
//            Log.e("posenet", "confidenceScores[idx]: " + confidenceScores[idx])
        }

//        val person = Person()
        val keypointList = Array(numKeypoints) { KeyPoint() }
        var totalScore = 0.0f

//        Log.e("posenet", "keypointList: " + keypointList)

        enumValues<BodyPart>().forEachIndexed { idx, it ->
            keypointList[idx].bodyPart = it
            keypointList[idx].position.x = xCoords[idx]
            keypointList[idx].position.y = yCoords[idx]
            keypointList[idx].score = confidenceScores[idx]

            totalScore += confidenceScores[idx]

//            Log.e("posenet", "totalScore: " + totalScore)
        }

        person.keyPoints = keypointList.toList()
        person.score = totalScore / numKeypoints


//        person.score =

//        Log.e("posenet", "person: " + person)
//        Log.e("posenet", "person.keyPoints: " + person.keyPoints)
//        Log.e("posenet", "person.score: " + person.score)

        return person
    }


}
