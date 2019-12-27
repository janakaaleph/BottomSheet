package com.aleph_labs.botttomsheet

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_sheet.*


class MainActivity : AppCompatActivity() {

    private var contentHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                contentHeight = contentView.height
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        val handler = Handler()

        // Toggle this field to observe bottom sheet peak height
        // toastTextView.visibility = View.GONE

        // Simulate something happens in the back end, then triggers the show.
        handler.postDelayed(1000) {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val windowHeight = displayMetrics.heightPixels

            val toastLocation = IntArray(2)
            toastTextView.getLocationInWindow(toastLocation)
            val toastBottom =
                windowHeight - (toastLocation[1] + toastTextView.height + 16.dpToPixel(this))

            val logoLocation = IntArray(2)
            logoTextView.getLocationInWindow(logoLocation)
            val logoBottom =
                windowHeight - (logoLocation[1] + logoTextView.height + 16.dpToPixel(this))

            BottomSheetBehavior.from(rootLayout).peekHeight =
                listOf(contentHeight, toastBottom, logoBottom).min() ?: 0
            BottomSheetBehavior.from(rootLayout).state = BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.from(rootLayout).isHideable = false
        }

        Handler().postDelayed(2000) {
            val peakHeightAnimator =
                ValueAnimator.ofInt(BottomSheetBehavior.from(rootLayout).peekHeight, 500)
            peakHeightAnimator.addUpdateListener {
                (it.animatedValue as? Int)?.let { height ->
                    BottomSheetBehavior.from(rootLayout).peekHeight = height
                }
            }
            peakHeightAnimator.start()
        }

        BottomSheetBehavior.from(rootLayout).state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

fun Int.dpToPixel(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}
