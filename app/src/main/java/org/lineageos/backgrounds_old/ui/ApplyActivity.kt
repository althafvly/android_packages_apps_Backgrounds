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
package org.lineageos.backgrounds_old.ui

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.lineageos.backgrounds_old.R
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.bundle.WallpaperType
import org.lineageos.backgrounds_old.task.ApplyWallpaperTask
import org.lineageos.backgrounds_old.task.LoadDrawableFromUriTask
import org.lineageos.backgrounds_old.util.ColorUtils.extractColor
import org.lineageos.backgrounds_old.util.ColorUtils.extractPalette
import org.lineageos.backgrounds_old.util.TypeConverter.drawableToBitmap
import org.lineageos.backgrounds_old.util.UiUtils.setStatusBarColor

class ApplyActivity : AppCompatActivity() {
    private var mPreviewView: ImageView? = null
    private var mApplySheetBehavior: BottomSheetBehavior<*>? = null
    private var mIsApplyingWallpaper = false
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_apply)
        mPreviewView = findViewById(R.id.apply_preview)
        val closeView = findViewById<ImageView>(R.id.apply_close)
        val applySheetView = findViewById<LinearLayout>(R.id.apply_button)
        val bothView = findViewById<TextView>(R.id.apply_both)
        val homeView = findViewById<TextView>(R.id.apply_home)
        val lockView = findViewById<TextView>(R.id.apply_lock)
        mApplySheetBehavior = BottomSheetBehavior.from(applySheetView)
        closeView.setOnClickListener { v: View? -> quitIfDoingNothing() }
        bothView.setOnClickListener { v: View? -> applyWallpaper(BOTH_FLAG) }
        homeView.setOnClickListener { v: View? -> applyWallpaper(HOME_FLAG) }
        lockView.setOnClickListener { v: View? -> applyWallpaper(LOCK_FLAG) }
        setup()
    }

    private fun setup() {
        setupBottomSheet()
        val wallpaperBundle: WallpaperBundle = intent.getParcelableExtra(EXTRA_WALLPAPER)
            ?: return
        when (wallpaperBundle.type) {
            WallpaperType.BUILT_IN -> setupBuiltIn(wallpaperBundle)
            WallpaperType.DEFAULT -> setupDefault()
            WallpaperType.GRADIENT -> setupGradient(wallpaperBundle)
            WallpaperType.MONO -> setupMono(wallpaperBundle)
            WallpaperType.USER -> setupUser(wallpaperBundle.name)
        }
    }

    private fun setupBottomSheet() {
        mApplySheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        mApplySheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(view: View, i: Int) {
            }

            override fun onSlide(view: View, v: Float) {
                /*  1 - Expanded
                 *  :
                 *  0 - Peek
                 *  :
                 * -1 - Hidden
                 */
                if (v == 1f || v == 0f || v == -1f) {
                    // Let the sliding sheet "lock in" the new position
                    view.performHapticFeedback(
                        HapticFeedbackConstants.CLOCK_TICK,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                    // Quit if the user slides the sheet out
                    if (v == -1f) {
                        quitIfDoingNothing()
                    }
                }
            }
        })
    }

    private fun setupBuiltIn(bundle: WallpaperBundle) {
        val drawable = ContextCompat.getDrawable(this, bundle.descriptor)
        displayPreview(drawable)
    }

    private fun setupDefault() {
        val manager = getSystemService(WallpaperManager::class.java)
        val drawable = manager.builtInDrawable
        displayPreview(drawable)
    }

    private fun setupMono(bundle: WallpaperBundle) {
        /*
         * Welcome to HackLand: ColorDrawable doesn't play nicely with
         * shared element transitions, so we have to draw a Bitmap and convert
         * it to a Drawable through BitmapDrawable
         */
        val bm = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        bm.eraseColor(bundle.descriptor)
        val drawable: Drawable = BitmapDrawable(resources, bm)
        displayPreview(drawable)
    }

    private fun setupGradient(bundle: WallpaperBundle) {
        /*
         * Welcome to HackLand pt2: GradientDrawable doesn't play nicely with
         * shared element transitions, so we have to make some magic:
         * 1. Get the OG drawable
         * 2. Convert to a Bitmap
         * 3. Convert the Bitmap to a BitmapDrawable
         * 4. Apply the BitmapDrawable
         */
        val origDrawable = ContextCompat.getDrawable(this, bundle.descriptor) ?: return
        val bm = drawableToBitmap(origDrawable)
        val bmd = BitmapDrawable(resources, bm)
        displayPreview(bmd)
    }

    private fun setupUser(wallpaperUri: String?) {
        if (wallpaperUri == null) {
            finish()
            return
        }
        LoadDrawableFromUriTask(object : LoadDrawableFromUriTask.Callback {
            override fun onCompleted(drawable: Drawable?) {
                displayPreview(drawable)
            }

            override fun getContentResolver(): ContentResolver {
                return this@ApplyActivity.contentResolver
            }

            override fun getResources(): Resources {
                return this@ApplyActivity.resources
            }
        }).execute(wallpaperUri)
    }

    private fun applyWallpaper(flags: Int) {
        mIsApplyingWallpaper = true
        hideApplyLayout()
        val drawable = mPreviewView!!.drawable
        ApplyWallpaperTask(object : ApplyWallpaperTask.Callback {
            override fun onCompleted(result: Boolean) {
                onWallpaperApplied(result)
            }

            override fun getWallpaperManager(): WallpaperManager {
                return getSystemService(WallpaperManager::class.java)
            }

            override fun getFlags(): Int {
                return flags
            }
        }).execute(drawable)
    }

    private fun displayPreview(drawable: Drawable?) {
        if (drawable == null) {
            return
        }
        mPreviewView!!.setImageDrawable(drawable)
        colorUi()
        showApplyLayout()
    }

    private fun showApplyLayout() {
        Handler().postDelayed(
            { mApplySheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED) },
            350
        )
    }

    private fun hideApplyLayout() {
        mApplySheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun onWallpaperApplied(success: Boolean) {
        if (success) {
            setResult(MainActivity.Companion.RESULT_APPLIED)
        }
        Toast.makeText(
            this, if (success) R.string.apply_success else R.string.apply_failure,
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun colorUi() {
        val previewDrawable = mPreviewView!!.drawable
        val color = extractColor(extractPalette(previewDrawable))

        // SystemUI
        setStatusBarColor(window, color)
    }

    private fun quitIfDoingNothing() {
        if (mIsApplyingWallpaper) {
            return
        }
        finish()
    }

    companion object {
        const val EXTRA_TRANSITION_NAME = "transition_shared_preview"
        const val EXTRA_WALLPAPER = "apply_extra_wallpaper_parcel"
        private const val BOTH_FLAG = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        private const val HOME_FLAG = WallpaperManager.FLAG_SYSTEM
        private const val LOCK_FLAG = WallpaperManager.FLAG_LOCK
    }
}