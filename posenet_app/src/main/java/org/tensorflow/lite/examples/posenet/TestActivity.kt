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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import org.tensorflow.lite.examples.posenet.lib.Posenet as Posenet

class TestActivity : AppCompatActivity()
{
    /** Returns a resized bitmap of the drawable image.
     * 드로어 블 이미지의 크기가 조정 된 비트 맵을 반환합니다.
     *  */

    val TAG = "TestActivity"

    private fun drawableToBitmap(drawable: Drawable): Bitmap
    {
        Log.e(TAG, "drawableToBitmap() 실행됨")
        val bitmap = Bitmap.createBitmap(257, 353, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)

        drawable.draw(canvas)
        return bitmap
    }

    /** Calls the Posenet library functions.
     * Posenet 라이브러리 함수를 호출합니다. */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.e(TAG, "onCreate() 실행됨")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val sampleImageView = findViewById<ImageView>(R.id.image)
        val drawedImage = ResourcesCompat.getDrawable(resources, R.drawable.image, null)
        val imageBitmap = drawableToBitmap(drawedImage!!)
        sampleImageView.setImageBitmap(imageBitmap)
        val posenet = Posenet(this.applicationContext)
        val person = posenet.estimateSinglePose(imageBitmap)

        // Draw the keypoints over the image.
        // 이미지 위에 키포인트를 그립니다.
        val paint = Paint()
        paint.color = Color.RED
        val size = 2.0f

        val mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        for (keypoint in person.keyPoints)
        {
            canvas.drawCircle(
                    keypoint.position.x.toFloat(),
                    keypoint.position.y.toFloat(), size, paint
                             )
        }
        sampleImageView.adjustViewBounds = true
        sampleImageView.setImageBitmap(mutableBitmap)
    }
}
