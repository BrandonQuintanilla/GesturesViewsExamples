package com.experimental.gestures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.experimental.gestures.github.demo_plane.MyActivity
import com.experimental.gestures.github.library.SwipeLayout

class GithubSwipeLayoutDemo : AppCompatActivity() {

    private lateinit var sample1: SwipeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_swipe_layout_demo)

        sample1 = findViewById<View>(R.id.sample1) as SwipeLayout
        sample1.showMode = SwipeLayout.ShowMode.PullOut
        val starBottView: View = sample1.findViewById(R.id.starbott)
        sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.bottom_wrapper))
        sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.bottom_wrapper_2))
        sample1.addDrag(SwipeLayout.DragEdge.Top, starBottView)
        sample1.addDrag(SwipeLayout.DragEdge.Bottom, starBottView)
        sample1.addRevealListener(R.id.delete
        ) { child, edge, fraction, distance -> }

        sample1.getSurfaceView().setOnClickListener(View.OnClickListener {
            Toast.makeText(this@GithubSwipeLayoutDemo, "Click on surface", Toast.LENGTH_SHORT).show()
            Log.d(MyActivity::class.java.name, "click on surface")
        })
        sample1.getSurfaceView().setOnLongClickListener(View.OnLongClickListener {
            Toast.makeText(this@GithubSwipeLayoutDemo, "longClick on surface", Toast.LENGTH_SHORT)
                .show()
            Log.d(MyActivity::class.java.name, "longClick on surface")
            true
        })
        sample1.findViewById<View>(R.id.star2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@GithubSwipeLayoutDemo,
                "Star",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample1.findViewById<View>(R.id.trash2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@GithubSwipeLayoutDemo,
                "Trash Bin",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample1.findViewById<View>(R.id.magnifier2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@GithubSwipeLayoutDemo,
                "Magnifier",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample1.addRevealListener(R.id.starbott
        ) { child, edge, fraction, distance ->
            val star = child.findViewById<View>(R.id.star)
            val d = (child.height / 2 - star.height / 2).toFloat()
            /*ViewHelper.setTranslationY(star, d * fraction)
                    ViewHelper.setScaleX(star, fraction + 0.6f)
                    ViewHelper.setScaleY(star, fraction + 0.6f)*/
        }
    }
}