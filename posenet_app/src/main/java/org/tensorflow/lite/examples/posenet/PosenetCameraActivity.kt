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

package org.tensorflow.lite.examples.posenet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_posenet_camera.*
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.mScore
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.needScore
import android.os.Handler
import android.os.Message
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.LeftAnkleY
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.LeftAnkleX
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.LeftHipX
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.LeftHipY
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.RightAnkleX
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.RightAnkleY
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.RightHipX
import org.tensorflow.lite.examples.posenet.PosenetActivity.Companion.RightHipY

class PosenetCameraActivity : AppCompatActivity()
{
    val TAG = "PosenetCameraActivity"

    var getVariables: String = "String"

    //         일정 카운트에 들면 촬영하기
//    var count: Int = 0
//    var isRunning = true
//    var isCount = false
//    var isSend = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posenet_camera)

        Log.e(TAG, "코틀린 posenet 실행됨")

        // 이전 자바 액티비티에서 넘어온 값 받기
        if (intent.hasExtra("getId"))
        {
            getID = intent.getStringExtra("getId")
            GET_JOIN_INDEX = intent.getStringExtra("GET_JOIN_INDEX")
            GET_JOIN_MEMBER_INDEX = intent.getStringExtra("GET_JOIN_MEMBER_INDEX")
            GET_PROOF_CATEGORY = intent.getStringExtra("GET_PROOF_CATEGORY")
            GET_PROOF_CERTI_COUNT = intent.getStringExtra("GET_PROOF_CERTI_COUNT")

            GET_PROOF_INDEX = intent.getStringExtra("GET_PROOF_INDEX")
            GET_PROOF_JOIN_PRICE = intent.getStringExtra("GET_PROOF_JOIN_PRICE")
            GET_PROOF_MY_CERTI_COUNT = intent.getStringExtra("GET_PROOF_MY_CERTI_COUNT")
            GET_PROOF_TITLE = intent.getStringExtra("GET_PROOF_TITLE")
            GET_PROOF_TYPE = intent.getStringExtra("GET_PROOF_TYPE")

            GET_REWARD = intent.getStringExtra("GET_REWARD")
        }
        else
        {
            Toast.makeText(this, "전달된 값이 없습니다", Toast.LENGTH_SHORT).show()
        }

        // 카메라 서피스뷰 연결하기
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, PosenetActivity())
                .commit()

        isRunning = true
        handler = Handler()
        var thread = ThreadClass()
        handler?.postDelayed(thread, 1000)

        // 촬영 신호를 보낼 핸들러
        receiveHandler = Handler()

        handler = object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                if (msg.what === START_CODE)
                {
                    Log.e(TAG, "메시지 받음 START_CODE")
                }
                else if (msg.what === PROGRESS_CODE)
                {
                    // ?? 여기엔 뭘 하지?
                    Log.e(TAG, "메시지 받음 PROGRESS_CODE")

                    isCount = true
                    countHandler = Handler()
                    var countThread = CountThread()
                    countHandler?.postDelayed(countThread, 1000)
                }
            }
        }
    }

    inner class ThreadClass : Thread()
    {
        override fun run()
        {
            // isRunning으로 액티비티 실행여부 판단
            if (isRunning)
            {
                handler?.postDelayed(this, 1000)

                Log.e(TAG, "mScore: " + mScore)
                Log.e(TAG, "needScore: " + needScore)

                if (mScore.equals("0.00"))
                {
                    // Log.e(TAG, "mScore: 가 널임")

                    Log.e(TAG, "자세 탐색중")

                    CancelDetect()
                }
                else if (mScore == null)
                {
                    Log.e(TAG, "자세 탐색중")

                    CancelDetect()
                }

                // todo: 탐지 시작
                else if (needScore < mScore!!.toFloat())
                {
                    // todo: 촬영 중 위치 벗어나면 촬영 취소
                    if (isSend == 1)
                    {
                        if (
                            127 >= LeftHipX && 147 <= LeftHipX &&       // 엉덩이 좌 x 좌표 범위 설정
                            144 >= LeftHipY && 163 <= LeftHipY &&       // 엉덩이 우 y 좌표 범위 설정

                            144  >= RightHipX && 162 <= RightHipX &&     // 엉덩이 좌 x 좌표 범위 설정
                            96 >=  RightHipY && 120 <= RightHipY         // 엉덩이 우 y 좌표 범위 설정
                        )
                        {
                            Log.e(TAG, "엉덩이 위치 벗어남. 촬영 취소")
                            CancelDetect()
                        }
                    }

                    Log.e(TAG, "80점 이상. 자세 조건 충족함")
                    Log.e(TAG, "하체 부위를 탐지합니다")

                    Log.e(TAG, "발목 좌표")
                    Log.e(TAG, "LeftAnkle: " + LeftAnkleX + ", " + LeftAnkleY)
                    Log.e(TAG, "RightAnkle: " + RightAnkleX + ", " + RightAnkleY)

                    Log.e(TAG, "엉덩이 좌표")
                    Log.e(TAG, "LeftHip: " + LeftHipX + ", " + LeftHipY)
                    Log.e(TAG, "RightHip: " + RightHipX + ", " + RightHipY)

//                    Log.e(TAG, "LeftKnee: " + LeftKneeX + ", " + LeftKneeY)
//                    Log.e(TAG, "RightKnee: " + RightKneeX + ", " + RightKneeY)

//                    Log.e(TAG, "LeftShoulder: " + LeftShoulderX + ", " + LeftShoulderY)
//                    Log.e(TAG, "RightShoulderX: " + RightShoulderX  + ", " + RightShoulderY)

//                    Log.e(TAG, "LeftElbowX: " + LeftElbowX  + ", " + LeftElbowY)
//                    Log.e(TAG, "RightElbowX: " + RightElbowX  + ", "+ RightElbowY)
//
//                    Log.e(TAG, "LeftWristX: " + LeftWristX  + ", " + LeftWristY)
//                    Log.e(TAG, "RightWristX: " + RightWristX +  ", " + RightWristY)

                    // todo: 하체 부위 탐지하기

                    /** 발목 좌 평균 좌표 (row, col): (144, 205) . (143, 197) . (145, 196) . (151, 202) */
                    /** 발목 우 평균 좌표 (row, col): (202, 89) . (206, 85) . (207, 97) . (208, 88) */

                    // && = 모두 참이어야 한다
                    // || = 둘 중 적어도 하나가 참이면 참이다

                    if (
                        117 <= LeftAnkleX && 156 >= LeftAnkleX &&       // 발목 좌 x 좌표 범위 설정
                        196 <= LeftAnkleY && 237 >= LeftAnkleY &&       // 발목 우 y 좌표 범위 설정

                        138 <= RightAnkleX && 212 >= RightAnkleX &&     // 발목 좌 x 좌표 범위 설정
                        85 <= RightAnkleY && 105 >= RightAnkleY           // 발목 우 y 좌표 범위 설정
                    )
                    {
                        Log.e(TAG, "발목 위치 일치함")

                        if(isCount == false)
                        {
                            od_pose_shot_notice.setText("발목 위치 일치")
                            od_pose_shot_icon.setImageDrawable(getDrawable(R.drawable.eye_white_2))
                        }
                        else
                        {

                        }

                        /** 엉덩이 좌 평균 좌표 (row, col): (131, 157) . (132, 157) . (133, 157) . (132, 157) */
                        /** 엉덩이 우 평균 좌표 (row, col): (155, 103) . (156, 103) . (155, 103) . (156, 103) */

                        if (
                            127 <= LeftHipX && 155 >= LeftHipX &&       // 엉덩이 좌 x 좌표 범위 설정
                            134 <= LeftHipY && 163 >= LeftHipY &&       // 엉덩이 우 y 좌표 범위 설정

                            144 <= RightHipX && 162 >= RightHipX &&     // 엉덩이 좌 x 좌표 범위 설정
                            96 <= RightHipY && 120 >= RightHipY         // 엉덩이 우 y 좌표 범위 설정
                        )
                        {
//                            od_pose_shot_notice.setText("엉덩이 위치 일치")

                            /** 어깨 좌 평균 좌표 (row, col): () . () . () . () */
                            /** 어깨 우 평균 좌표 (row, col): () . () . () . () */

                            // todo: 촬영 카운터 시작하기
                            if (isSend == 0)
                            {
                                // 카운트 시작
                                val message = Message()
                                message.what = PROGRESS_CODE
                                message.arg1 = 1
                                handler?.sendMessage(message)

                                isSend = 1
                            }
                        }



                        // 탐지 취소
//                        else
//                        {
//                            CancelDetect()
//                        }
                    }

                    // 탐지 취소
                    else
                    {
                        CancelDetect()
                    }

                }
                else
                {
                    CancelDetect()
//                    isSend = 0
                }
            }
            else
            {
                Log.e(TAG, "핸들러 중지됨")
//                count = 0
            }
        }
    }

    fun CancelDetect()
    {
        Log.e(TAG, "자세 탐색중")

        od_pose_shot_notice.setText("(탐색중)\n선을 발 아래 두십시오")
        od_pose_shot_icon.setImageDrawable(getDrawable(R.drawable.eye_white_1))
        od_pose_shot_icon.borderWidth = 0
        isCount = false
    }

    // 카운트 1씩 감소
    // var tvCount = 10
    // var tvBorder = 12
    inner class CountThread : Thread()
    {
        override fun run()
        {
            // isCount로 카운트 실행여부 판단
            if (isCount == true)
            {
                countHandler?.postDelayed(this, 1000)

//                count = count + 1
                count++ // 5초까지 세기
                tvCount--

                Log.e(TAG, "count: " + count)

                od_pose_shot_notice.setText("자세를 유지하십시오 (${tvCount}초)")
                od_pose_shot_icon.setImageDrawable(getDrawable(R.drawable.eye_white_2))
                od_pose_shot_icon.borderWidth = tvBorder

                Log.e(TAG, "od_pose_shot_icon.borderWidth: " + od_pose_shot_icon.borderWidth)

                if (2 <= count)
                {
                    tvBorder = 8
//                    Log.e(TAG, "도달함 3")
                }

                if (3 <= count)
                {
                    tvBorder = 4

//                    Log.e(TAG, "도달함 6")
                }

                if (4 <= count)
                {
                    tvBorder = 1

//                    Log.e(TAG, "도달함 9")
                }

                if (5 <= count)
                {
                    tvBorder = 0

//                    Log.e(TAG, "도달함 9")
                }

                // 8초가 되면 카운트 중지
                if (8 <= count)
                {
                    // Pose Net 액티비티로 촬영 신호 보내기
                    TAKE_PICTUR = true

                    // 카운트 핸들러 중지하기
                    isCount = false

                    // 탐지 핸들러도 중지하기
                    isRunning = false
                    od_pose_shot_icon.setImageDrawable(getDrawable(R.drawable.eye_white_1))
                    od_pose_shot_notice.setText("촬영 중")

                    // PosenetActivity의 receiveHandler 핸들러로 완료 메시지 보내기
                    val message = Message()
                    message.what = PROGRESS_CODE
                    message.arg1 = 1
                    receiveHandler?.sendMessage(message)
                }
            }
            else
            {
                Log.e(TAG, "카운트 핸들러 중지됨")

                // 초기화
                isSend = 0
                count = 0
                tvCount = 8
                tvBorder = 14
            }
        }
    }

    inner class DetectStartNotiThread : Thread()
    {
        override fun run()
        {
            // isCount로 카운트 실행여부 판단
            if (isDetectStartNoti == true)
            {

            }
            else
            {
                Log.e(TAG, "시작 알림 핸들러 중지됨")
            }
        }
    }

    override fun onPause()
    {
        // 핸들러 중지하기
        isRunning = false

        // 카운트 핸들러 중지하기
        isCount = false

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume()


        // 핸들러 재시작
        isRunning = true
    }

    companion object
    {
        var getID: String? = null
        var GET_JOIN_INDEX: String? = null
        var GET_JOIN_MEMBER_INDEX: String? = null
        var GET_PROOF_CATEGORY: String? = null
        var GET_PROOF_CERTI_COUNT: String? = null

        var GET_PROOF_INDEX: String? = null
        var GET_PROOF_JOIN_PRICE: String? = null
        var GET_PROOF_MY_CERTI_COUNT: String? = null
        var GET_PROOF_TITLE: String? = null
        var GET_PROOF_TYPE: String? = null

        var GET_REWARD: String? = null

        var TAKE_PICTUR = false

        var handler: android.os.Handler? = null         // 핸들러: 자세 탐지 1초마다 갱신
        var receiveHandler: android.os.Handler? = null         // 핸들러: 자세 탐지 1초마다 갱신
        var countHandler: android.os.Handler? = null    // 핸들러: 조건에 맞는 자세가 탐지되면 숫자 카운트
        var startNotiHandler: android.os.Handler? = null    // 핸들러: 조건에 맞는 자세가 탐지되면 숫자 카운트
        val START_CODE = 100
        val PROGRESS_CODE = 101

        var tvCount = 8
        var tvBorder = 12
        var count: Int = 0
        var isCount = false
        var isDetectStartNoti = false
        var isRunning = true
        var isSend = 0

        /*
            GET_JOIN_INDEX
            GET_JOIN_MEMBER_INDEX
            GET_PROOF_CATEGORY
            GET_PROOF_CERTI_COUNT

            GET_PROOF_INDEX
            GET_PROOF_JOIN_PRICE
            GET_PROOF_MY_CERTI_COUNT
            GET_PROOF_TITLE
            GET_PROOF_TYPE

            GET_REWARD
        * */
    }
}
