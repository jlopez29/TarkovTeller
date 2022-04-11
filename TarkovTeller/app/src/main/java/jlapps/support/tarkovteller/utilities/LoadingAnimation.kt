package jlapps.support.tarkovteller.utilities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import jlapps.support.tarkovteller.R

class LoadingAnimation(val context:Context,private val layout: RelativeLayout) {
    var loadContext = this
    var LOG_TAG = "Load anim"
    var margin = 100
    var endCount = 0
    var totalCount = 0
    var delayTime = 250
    var delayIncrement = 2
    var endAnimDistance = 1500
    var animationIndex = 0
    var loopDirections = arrayListOf<String>("right","left")
    var logoImages = arrayListOf<Int>(R.drawable.eft_logo,R.drawable.eft_logo_1,R.drawable.eft_logo_2,R.drawable.eft_logo_3)
    var imageIndex = 0
    lateinit var anim: Animator
    var animSetXY = AnimatorSet()

    fun stop(){
        layout.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 1f

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(500.toLong())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        layout.visibility = GONE
                        layout.alpha = 1f
                        if(loadContext::anim.isInitialized){
                            anim.removeAllListeners()
                            anim.end()
                            anim.cancel()
                        }
                        animSetXY.removeAllListeners()
                        animSetXY.end()
                        animSetXY.cancel()
                    }
                })
        }

    }

    fun setupImage(
        context: Context,
        startX: Float,
        startY: Float,
        drawable: Int,
        offset: Int,
        rotate: Float,
        right: Boolean,
        direction: String
    ): ImageView {

        var image = ImageView(context)
        layout.addView(image)
        image.setImageResource(drawable)
        image.rotation = rotate
        image.x = startX
        image.y = startY

        image.layoutParams.height = 200
        image.layoutParams.width = 200

        when (direction) {
            "diagonal_right" -> {
                if (right) {
                    image.translationX += offset * (margin)
                    image.translationY -= offset * margin
                } else {
                    image.translationX -= offset * (margin)
                    image.translationY += offset * margin
                }
            }
            "diagonal_left" -> {
                if (right) {
                    image.translationX += offset * (margin)
                    image.translationY += offset * margin
                } else {
                    image.translationX -= offset * (margin)
                    image.translationY -= offset * margin
                }
            }
            "down" -> {
                if (right) {
                    image.translationX += offset * (margin)
                    image.translationY -= offset * margin
                } else {
                    image.translationX -= offset * (margin)
                    image.translationY -= offset * margin
                }
            }
        }



        image.requestLayout()
        return image
    }

    private fun animatePan(v: View, targetX: Float, targetY: Float, delayTime: Long) {
        animSetXY = AnimatorSet()
        val y = ObjectAnimator.ofFloat(
            v,
            "translationY", v.y, targetY
        )
        val x = ObjectAnimator.ofFloat(
            v,
            "translationX", v.x, targetX
        )
        animSetXY.playTogether(x, y)
        animSetXY.interpolator = LinearInterpolator()
        animSetXY.duration = 3000
        animSetXY.startDelay = delayTime
        animSetXY.start()
        animSetXY.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                endCount++

                if(endCount == totalCount){
                    delayIncrement = 2
                    endCount = 0
                    if(animationIndex == loopDirections.size-1)
                        animationIndex = 0
                    else animationIndex++

                    animateDiagonal(loopDirections[animationIndex],9)
                }
            }
        })

    }

    fun animateDiagonal(direction:String,count:Int){
        layout.visibility = VISIBLE
        totalCount = count
        when(direction){
            "left"->{

                for(i in 0 until count){
                    var right = (i % 2) != 0
                    var delay = 0L

                    if(i == 0) {
                        delay = 0L
                        right = true
                    }else{
                        delay = (delayTime * delayIncrement).toLong()
                        if(i % 2 == 0)
                            delayIncrement++
                    }

                    var image = setupImage(context,layout.width.toFloat(),0f,
                        R.drawable.ammo_762_bp,i,135f,right,"diagonal_left")
                    animatePan(image,image.x-endAnimDistance,image.y+endAnimDistance,delay)
                }

            }
            "right"->{

                for(i in 0 until count) {
                    var right = (i % 2) != 0
                    var delay = 0L

                    if(i == 0) {
                        delay = 0L
                        right = true
                    }else{
                        delay = (delayTime * delayIncrement).toLong()
                        if(i % 2 == 0)
                            delayIncrement++
                    }

                    var image = setupImage(
                        context, 0f, 0f,
                        R.drawable.ammo_762_bp, i, 45f, right, "diagonal_right"
                    )

                    animatePan(
                        image, image.x + endAnimDistance, image.y + endAnimDistance, delay
                    )
                }

            }
        }
    }

    fun animateLinearReveal(myView:ImageView,direction:String){
        myView.setImageResource(logoImages[imageIndex])
        imageIndex++
        if(imageIndex == logoImages.size)
            imageIndex = 0
        layout.visibility = VISIBLE
        val cx = myView.measuredWidth * 2
        val cy = myView.measuredHeight / 2
        val finalRadius =
            Math.hypot((myView.width * 2).toDouble(), myView.height.toDouble()).toInt()

        when(direction){
            "right"->{
                anim = ViewAnimationUtils.createCircularReveal(myView, -1000, cy, 0f, finalRadius.toFloat())
            }
            "left"->{
                anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius.toFloat())
            }
        }
        anim.duration = 3000

        myView.visibility = View.VISIBLE
        anim.start()
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {

                myView.apply {
                    // Set the content view to 0% opacity but visible, so that it is visible
                    // (but fully transparent) during the animation.
                    alpha = 1f

                    // Animate the content view to 100% opacity, and clear any animation
                    // listener set on the view.
                    animate()
                        .alpha(0f)
                        .setDuration(500.toLong())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationCancel(animation: Animator?) {}
                            override fun onAnimationStart(animation: Animator?) {}

                            override fun onAnimationEnd(animation: Animator?) {
                                myView.visibility = INVISIBLE
                                myView.alpha = 1f
                                animationIndex++
                                if(animationIndex == loopDirections.size)
                                    animationIndex = 0
                                animateLinearReveal(myView, loopDirections[animationIndex])
                            }
                        })
                }
            }
        })


    }

}