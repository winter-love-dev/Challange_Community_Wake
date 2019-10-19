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

@file:JvmName("Constants")

package org.tensorflow.lite.examples.posenet

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


/** Request camera and external storage permission.
 * 카메라 및 외부 저장 권한을 요청하십시오.*/
const val REQUEST_CAMERA_PERMISSION = 1

val date = Date()
val dateFormat = java.text.SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")

@JvmField val PIC_FILE_NAME = "pic_${dateFormat.format(date)}.jpg"

/** Model input shape for images.
 * 이미지의 모델 입력 모양. */
//const val MODEL_WIDTH = 128
//const val MODEL_HEIGHT = 128

const val MODEL_WIDTH = 257
const val MODEL_HEIGHT = 257

//const val MODEL_WIDTH = 514
//const val MODEL_HEIGHT = 514


