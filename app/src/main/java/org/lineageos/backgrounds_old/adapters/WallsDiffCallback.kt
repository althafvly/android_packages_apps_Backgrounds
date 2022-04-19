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
package org.lineageos.backgrounds_old.adapters

import androidx.recyclerview.widget.DiffUtil
import org.lineageos.backgrounds_old.bundle.WallpaperBundle

internal class WallsDiffCallback(
    private val mOld: List<WallpaperBundle>,
    private val mNew: List<WallpaperBundle>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOld.size
    }

    override fun getNewListSize(): Int {
        return mNew.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return mOld[oldItemPosition].name == mNew[newItemPosition].name
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return mOld[oldItemPosition] == mNew[newItemPosition]
    }
}