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

import android.app.WallpaperManager
import android.content.res.Resources
import android.os.AsyncTask
import org.lineageos.backgrounds_old.bundle.WallpaperBundle

class FetchDataTask(private val mCallbacks: Callback) :
    AsyncTask<Void?, Int?, List<WallpaperBundle>>(), FetchDataImpl.Callback {
    override fun doInBackground(params: Array<Void?>): List<WallpaperBundle> {
        return FetchDataImpl(this).fetchData()
    }

    override fun onPostExecute(result: List<WallpaperBundle>) {
        mCallbacks.onCompleted(result)
    }

    override val resources: Resources
        get() = mCallbacks.getResources()
    override val wallpaperManager: WallpaperManager
        get() = mCallbacks.getWallpaperManager()

    interface Callback {
        fun onCompleted(data: List<WallpaperBundle>)
        fun getResources(): Resources
        fun getWallpaperManager(): WallpaperManager
    }
}