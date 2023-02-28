package com.experimental.gestures.github.demo_plane

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.experimental.gestures.R
import com.experimental.gestures.github.library.SwipeLayout

class MyActivity : AppCompatActivity() {
    //private var sample1: SwipeLayout? = null
    private lateinit var sample1: SwipeLayout
    private lateinit var sample2: SwipeLayout
    private lateinit var sample3: SwipeLayout

    //null, private  var sample2:SwipeLayout? = null, private  var sample3:SwipeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

//        SwipeLayout swipeLayout = (SwipeLayout)findViewById(R.id.godfather);
//        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Bottom); // Set in XML

        //sample1
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
            Toast.makeText(this@MyActivity, "Click on surface", Toast.LENGTH_SHORT).show()
            Log.d(MyActivity::class.java.name, "click on surface")
        })
        sample1.getSurfaceView().setOnLongClickListener(OnLongClickListener {
            Toast.makeText(this@MyActivity, "longClick on surface", Toast.LENGTH_SHORT).show()
            Log.d(MyActivity::class.java.name, "longClick on surface")
            true
        })
        sample1.findViewById<View>(R.id.star2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Star",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample1.findViewById<View>(R.id.trash2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Trash Bin",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample1.findViewById<View>(R.id.magnifier2).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
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

        //sample2


        //sample2
        sample2 = findViewById<View>(R.id.sample2) as SwipeLayout
        sample2.setShowMode(SwipeLayout.ShowMode.LayDown)
        sample2.addDrag(SwipeLayout.DragEdge.Right, sample2.findViewWithTag("Bottom2"))
//        sample2.setShowMode(SwipeLayout.ShowMode.PullOut);
        //        sample2.setShowMode(SwipeLayout.ShowMode.PullOut);
        sample2.findViewById<View>(R.id.star).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Star",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample2.findViewById<View>(R.id.trash).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Trash Bin",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample2.findViewById<View>(R.id.magnifier).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Magnifier",
                Toast.LENGTH_SHORT
            ).show()
        })

        sample2.findViewById<View>(R.id.click).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Yo",
                Toast.LENGTH_SHORT
            ).show()
        })
        sample2.surfaceView.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Click on surface",
                Toast.LENGTH_SHORT
            ).show()
        })

        //sample3


        //sample3
        sample3 = findViewById<View>(R.id.sample3) as SwipeLayout
        sample3.addDrag(SwipeLayout.DragEdge.Top, sample3.findViewWithTag("Bottom3"))
        sample3.addRevealListener(R.id.bottom_wrapper_child1
        ) { child, edge, fraction, distance ->
            val star = child.findViewById<View>(R.id.star)
            val d = (child.height / 2 - star.height / 2).toFloat()
            /*ViewHelper.setTranslationY(star, d * fraction)
            ViewHelper.setScaleX(star, fraction + 0.6f)
            ViewHelper.setScaleY(star, fraction + 0.6f)
            val c = evaluate(
                fraction,
                Color.parseColor("#dddddd"),
                Color.parseColor("#4C535B")
            ) as Int
            child.setBackgroundColor(c)*/
        }
        sample3!!.findViewById<View>(R.id.bottom_wrapper_child1).setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Yo!",
                Toast.LENGTH_SHORT
            ).show()
        })
        sample3!!.surfaceView.setOnClickListener {
            Toast.makeText(
                this@MyActivity,
                "Click on surface",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun evaluate(fraction: Float, startValue: Any, endValue: Any): Any? {
        val startInt = startValue as Int
        val startA = startInt shr 24 and 0xff
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff
        val endInt = endValue as Int
        val endA = endInt shr 24 and 0xff
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff
        return (startA + (fraction * (endA - startA)).toInt() shl 24) or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                (startB + (fraction * (endB - startB)).toInt())
    }
}