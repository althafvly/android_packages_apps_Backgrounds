/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.backgrounds_old.task

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.IOException

internal class LoadDrawableFromUriImpl(private val mCallbacks: Callback) {
    fun fetchDrawableFromUri(uriString: String): Drawable? {
        val uri = Uri.parse(uriString)
        return try {
            val parcelDescriptor = mCallbacks.contentResolver
                .openFileDescriptor(uri, "r") ?: return null
            val fileDescriptor = parcelDescriptor.fileDescriptor
            val bm = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            bm.density = Bitmap.DENSITY_NONE
            BitmapDrawable(mCallbacks.resources, bm)
        } catch (e: IOException) {
            null
        }
    }

    interface Callback {
        val contentResolver: ContentResolver
        val resources: Resources
    }
}