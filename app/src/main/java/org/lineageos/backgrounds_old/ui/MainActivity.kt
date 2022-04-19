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

import android.app.WallpaperManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import org.lineageos.backgrounds_old.R
import org.lineageos.backgrounds_old.adapters.WallsAdapter
import org.lineageos.backgrounds_old.bundle.WallpaperBundle
import org.lineageos.backgrounds_old.bundle.WallpaperType
import org.lineageos.backgrounds_old.factory.UserWallpaperFactory
import org.lineageos.backgrounds_old.task.FetchDataTask
import org.lineageos.backgrounds_old.ui.ApplyActivity

class MainActivity : AppCompatActivity(), SelectionInterface {
    private var mLoadingProgressBar: ProgressBar? = null
    private var mLoadingTextView: TextView? = null
    private var mContentLayout: NestedScrollView? = null
    private var mTitleView: TextView? = null
    private var mContentRecyclerView: RecyclerView? = null
    private var mAdapter: WallsAdapter? = null
    private var mHolder: View? = null
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_main)
        mLoadingProgressBar = findViewById(R.id.main_loading_bar)
        mLoadingTextView = findViewById(R.id.main_loading_text)
        mContentLayout = findViewById(R.id.main_contents)
        mTitleView = findViewById(R.id.main_title)
        mContentRecyclerView = findViewById(R.id.main_recyclerview)
        setupRecyclerView()
        loadContent()
        setupTitle()
    }

    public override fun onResume() {
        super.onResume()

        // Cleanup
        if (mHolder != null) {
            mHolder!!.transitionName = ""
            mHolder = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_EXT && data != null) {
            onPickedFromExt(data.dataString)
            return
        }
        if (requestCode == APPLY_WALLPAPER && resultCode == RESULT_APPLIED) {
            // We're done
            finish()
        }
    }

    override fun onWallpaperSelected(view: View, bundle: WallpaperBundle?) {
        mHolder = view
        if (bundle == null) {
            pickWallpaperFromExternalStorage()
        } else {
            openPreview(bundle)
        }
    }

    private fun setupRecyclerView() {
        mAdapter = WallsAdapter(this)
        val numOfColumns = resources.getInteger(R.integer.main_list_columns)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(
            numOfColumns, LinearLayout.VERTICAL
        )
        mContentRecyclerView!!.layoutManager = staggeredGridLayoutManager
        mContentRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mContentRecyclerView!!.adapter = mAdapter
    }

    private fun setupTitle() {
        mContentLayout!!.setOnScrollChangeListener(View.OnScrollChangeListener { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val base = v.height / 8
            mTitleView!!.alpha = (base - scrollY).toFloat() / base
        })
    }

    private fun loadContent() {
        FetchDataTask(object : FetchDataTask.Callback {
            override fun onCompleted(data: List<WallpaperBundle>) {
                onContentLoaded(data)
            }

            override fun getResources(): Resources {
                return this@MainActivity.resources
            }

            override fun getWallpaperManager(): WallpaperManager {
                return getSystemService(WallpaperManager::class.java)
            }
        }).execute()
    }

    private fun onContentLoaded(data: List<WallpaperBundle>) {
        mAdapter!!.setData(data)
        postContentLoaded()
    }

    private fun postContentLoaded() {
        mLoadingTextView!!.visibility = View.GONE
        mLoadingProgressBar!!.visibility = View.GONE
        mContentLayout!!.visibility = View.VISIBLE
    }

    private fun pickWallpaperFromExternalStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
        startActivityForResult(intent, PICK_IMAGE_FROM_EXT)
    }

    private fun openPreview(bundle: WallpaperBundle) {
        val intent = Intent(this, ApplyActivity::class.java)
            .putExtra(ApplyActivity.Companion.EXTRA_WALLPAPER, bundle)
        if (mHolder == null) {
            return
        }
        if (bundle.type == WallpaperType.USER) {
            // No animations for you
            startActivity(intent)
            return
        }

        // Shared element transition
        mHolder!!.transitionName = ApplyActivity.Companion.EXTRA_TRANSITION_NAME
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, mHolder!!, ApplyActivity.Companion.EXTRA_TRANSITION_NAME
        )
        startActivityForResult(intent, APPLY_WALLPAPER, options.toBundle())
    }

    private fun onPickedFromExt(uriString: String?) {
        if (uriString == null) {
            return
        }
        // Pass a fake bundle with name as URI path
        val fakeBundle = UserWallpaperFactory.build(uriString)
        onWallpaperSelected(mHolder!!, fakeBundle)
    }

    companion object {
        const val RESULT_APPLIED = 917
        private const val PICK_IMAGE_FROM_EXT = 618
        private const val APPLY_WALLPAPER = 619
    }
}