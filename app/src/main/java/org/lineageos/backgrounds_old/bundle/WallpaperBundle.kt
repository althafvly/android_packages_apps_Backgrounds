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
package org.lineageos.backgrounds_old.bundle

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import org.lineageos.backgrounds_old.util.TypeConverter.intToWallpaperType
import org.lineageos.backgrounds_old.util.TypeConverter.wallpaperTypeToInt

class WallpaperBundle : Parcelable {
    val name: String
    val contentDrawable: Drawable?

    /*
       * Can be both color or resource drawable
       */
    val descriptor: Int
    val type: WallpaperType

    constructor(
        name: String,
        contentDrawable: Drawable?,
        descriptor: Int,
        type: WallpaperType
    ) {
        this.name = name
        this.contentDrawable = contentDrawable
        this.descriptor = descriptor
        this.type = type
    }

    private constructor(parcel: Parcel) {
        val parcelName = parcel.readString()
        name = parcelName ?: ""
        contentDrawable = null
        descriptor = parcel.readInt()
        type = intToWallpaperType(parcel.readInt())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is WallpaperBundle) {
            return false
        }
        val otherBundle = other
        return otherBundle.type == type && otherBundle.contentDrawable === contentDrawable
    }

    override fun hashCode(): Int {
        return type.hashCode() + if (contentDrawable == null) 0 else contentDrawable.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(descriptor)
        dest.writeInt(wallpaperTypeToInt(type))
    }

    companion object {
        const val DESCRIPTOR_EMPTY = -1

        @JvmField
        val CREATOR: Creator<WallpaperBundle?> = object : Creator<WallpaperBundle?> {
            override fun createFromParcel(`in`: Parcel): WallpaperBundle? {
                return WallpaperBundle(`in`)
            }

            override fun newArray(size: Int): Array<WallpaperBundle?> {
                return arrayOfNulls(size)
            }
        }
    }
}