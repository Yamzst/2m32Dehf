/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */
package com.testlabx.mashle.getColor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * A class the processes media notifications and extracts the right text and background colors.
 */
class MediaNotificationProcessor {
    private var mFilteredBackgroundHsl: FloatArray? = null
    private val mBlackWhiteFilter =
        Palette.Filter { rgb, hsl -> !isWhiteOrBlack(hsl) }
    private var mIsLowPriority = false
    var backgroundColor = 0
        private set
    var secondaryTextColor = 0
        private set
    var primaryTextColor = 0
        private set
    private var actionBarColor = 0
    private var drawable: Drawable? = null
    private var context: Context

    constructor(context: Context, drawable: Drawable?) {
        this.context = context
        this.drawable = drawable
        mediaPalette
    }

    constructor(context: Context, bitmap: Bitmap?) {
        this.context = context
        drawable = BitmapDrawable(context.resources, bitmap)
        mediaPalette
    }

    constructor(context: Context) {
        this.context = context
    }

    fun getPaletteAsync(onPaletteLoadedListener: OnPaletteLoadedListener, drawable: Drawable?) {
        this.drawable = drawable
        val handler = Handler(Looper.getMainLooper())
        Thread {
            mediaPalette
            handler.post { onPaletteLoadedListener.onPaletteLoaded(this) }
        }.start()
    }

    fun getPaletteAsync(onPaletteLoadedListener: OnPaletteLoadedListener, bitmap: Bitmap?) {
        drawable = BitmapDrawable(context.resources, bitmap)
        getPaletteAsync(onPaletteLoadedListener, drawable)
    }// at least 10 degrees hue difference// we want all colors, red / white / black ones too!
    // we want most of the full region again, slightly shifted to the right
// for the background we only take the left side of the image to ensure
    // a smooth transition
// We're transforming the builder, let's make sure all baked in RemoteViews are
    // rebuilt!
    /**
     * Processes a drawable and calculates the appropriate colors that should
     * be used.
     */
    private val mediaPalette: Unit
        get() {
            val bitmap: Bitmap
            if (drawable != null) {
                // We're transforming the builder, let's make sure all baked in RemoteViews are
                // rebuilt!
                var width = drawable!!.intrinsicWidth
                var height = drawable!!.intrinsicHeight
                val area = width * height
                if (area > RESIZE_BITMAP_AREA) {
                    val factor = Math.sqrt((RESIZE_BITMAP_AREA.toFloat() / area).toDouble())
                    width = (factor * width).toInt()
                    height = (factor * height).toInt()
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable!!.setBounds(0, 0, width, height)
                    drawable!!.draw(canvas)

                    // for the background we only take the left side of the image to ensure
                    // a smooth transition
                    val paletteBuilder = Palette.from(bitmap)
                        .setRegion(0, 0, bitmap.width / 2, bitmap.height)
                        .clearFilters() // we want all colors, red / white / black ones too!
                        .resizeBitmapArea(RESIZE_BITMAP_AREA)
                    var palette = paletteBuilder.generate()
                    backgroundColor = findBackgroundColorAndFilter(drawable!!)
                    // we want most of the full region again, slightly shifted to the right
                    val textColorStartWidthFraction = 0.4f
                    paletteBuilder.setRegion(
                        (bitmap.width * textColorStartWidthFraction).toInt(), 0,
                        bitmap.width,
                        bitmap.height
                    )
                    if (mFilteredBackgroundHsl != null) {
                        paletteBuilder.addFilter { rgb, hsl -> // at least 10 degrees hue difference
                            val diff = abs(hsl[0] - mFilteredBackgroundHsl!![0])
                            diff > 10 && diff < 350
                        }
                    }
                    paletteBuilder.addFilter(mBlackWhiteFilter)
                    palette = paletteBuilder.generate()
                    val foregroundColor = selectForegroundColor(backgroundColor, palette)
                    ensureColors(backgroundColor, foregroundColor)
                }
            }
        }

    private fun selectForegroundColor(backgroundColor: Int, palette: Palette): Int {
        return if (isColorLight(backgroundColor)) {
            selectForegroundColorForSwatches(
                palette.darkVibrantSwatch,
                palette.vibrantSwatch,
                palette.darkMutedSwatch,
                palette.mutedSwatch,
                palette.dominantSwatch,
                Color.BLACK
            )
        } else {
            selectForegroundColorForSwatches(
                palette.lightVibrantSwatch,
                palette.vibrantSwatch,
                palette.lightMutedSwatch,
                palette.mutedSwatch,
                palette.dominantSwatch,
                Color.WHITE
            )
        }
    }

    val isLight: Boolean
        get() = isColorLight(backgroundColor)

    private fun selectForegroundColorForSwatches(
        moreVibrant: Swatch?,
        vibrant: Swatch?, moreMutedSwatch: Swatch?, mutedSwatch: Swatch?,
        dominantSwatch: Swatch?, fallbackColor: Int
    ): Int {
        var coloredCandidate = selectVibrantCandidate(moreVibrant, vibrant)
        if (coloredCandidate == null) {
            coloredCandidate = selectMutedCandidate(mutedSwatch, moreMutedSwatch)
        }
        return if (coloredCandidate != null) {
            if (dominantSwatch === coloredCandidate) {
                coloredCandidate.rgb
            } else if (coloredCandidate.population.toFloat() / dominantSwatch!!.population
                < POPULATION_FRACTION_FOR_DOMINANT
                && dominantSwatch.hsl[1] > MIN_SATURATION_WHEN_DECIDING
            ) {
                dominantSwatch.rgb
            } else {
                coloredCandidate.rgb
            }
        } else if (hasEnoughPopulation(dominantSwatch)) {
            dominantSwatch!!.rgb
        } else {
            fallbackColor
        }
    }

    private fun selectMutedCandidate(
        first: Swatch?,
        second: Swatch?
    ): Swatch? {
        val firstValid = hasEnoughPopulation(first)
        val secondValid = hasEnoughPopulation(second)
        if (firstValid && secondValid) {
            val firstSaturation = first!!.hsl[1]
            val secondSaturation = second!!.hsl[1]
            val populationFraction = first.population / second.population
                .toFloat()
            return if (firstSaturation * populationFraction > secondSaturation) {
                first
            } else {
                second
            }
        } else if (firstValid) {
            return first
        } else if (secondValid) {
            return second
        }
        return null
    }

    private fun selectVibrantCandidate(first: Swatch?, second: Swatch?): Swatch? {
        val firstValid = hasEnoughPopulation(first)
        val secondValid = hasEnoughPopulation(second)
        if (firstValid && secondValid) {
            val firstPopulation = first!!.population
            val secondPopulation = second!!.population
            return if ((firstPopulation / secondPopulation.toFloat()) < POPULATION_FRACTION_FOR_MORE_VIBRANT) {
                second
            } else {
                first
            }
        } else if (firstValid) {
            return first
        } else if (secondValid) {
            return second
        }
        return null
    }

    private fun hasEnoughPopulation(swatch: Swatch?): Boolean {
        // We want a fraction that is at least 1% of the image
        return (swatch != null
                && swatch.population / RESIZE_BITMAP_AREA.toFloat() > MINIMUM_IMAGE_FRACTION)
    }

    private fun findBackgroundColorAndFilter(drawable: Drawable): Int {
        var width = drawable.intrinsicWidth
        var height = drawable.intrinsicHeight
        val area = width * height
        val factor = sqrt((RESIZE_BITMAP_AREA.toFloat() / area).toDouble())
        width = (factor * width).toInt()
        height = (factor * height).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        // for the background we only take the left side of the image to ensure
        // a smooth transition
        val paletteBuilder = Palette.from(bitmap)
            .setRegion(0, 0, bitmap.width / 2, bitmap.height)
            .clearFilters() // we want all colors, red / white / black ones too!
            .resizeBitmapArea(RESIZE_BITMAP_AREA)
        val palette = paletteBuilder.generate()
        // by default we use the dominant palette
        val dominantSwatch = palette.dominantSwatch
        if (dominantSwatch == null) {
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null
            return Color.WHITE
        }
        if (!isWhiteOrBlack(dominantSwatch.hsl)) {
            mFilteredBackgroundHsl = dominantSwatch.hsl
            return dominantSwatch.rgb
        }
        // Oh well, we selected black or white. Lets look at the second color!
        val swatches = palette.swatches
        var highestNonWhitePopulation = -1f
        var second: Swatch? = null
        for (swatch in swatches) {
            if (swatch !== dominantSwatch && swatch.population > highestNonWhitePopulation && !isWhiteOrBlack(
                    swatch.hsl
                )
            ) {
                second = swatch
                highestNonWhitePopulation = swatch.population.toFloat()
            }
        }
        if (second == null) {
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null
            return dominantSwatch.rgb
        }
        return if (dominantSwatch.population / highestNonWhitePopulation
            > POPULATION_FRACTION_FOR_WHITE_OR_BLACK
        ) {
            // The dominant swatch is very dominant, lets take it!
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null
            dominantSwatch.rgb
        } else {
            mFilteredBackgroundHsl = second.hsl
            second.rgb
        }
    }

    private fun isWhiteOrBlack(hsl: FloatArray): Boolean {
        return isBlack(hsl) || isWhite(hsl)
    }

    /**
     * @return true if the color represents a color which is close to black.
     */
    private fun isBlack(hslColor: FloatArray): Boolean {
        return hslColor[2] <= BLACK_MAX_LIGHTNESS
    }

    /**
     * @return true if the color represents a color which is close to white.
     */
    private fun isWhite(hslColor: FloatArray): Boolean {
        return hslColor[2] >= WHITE_MIN_LIGHTNESS
    }

    fun setIsLowPriority(isLowPriority: Boolean) {
        mIsLowPriority = isLowPriority
    }

    private fun ensureColors(backgroundColor: Int, mForegroundColor: Int) {
        run {
            val backLum = NotificationColorUtil.calculateLuminance(backgroundColor)
            val textLum = NotificationColorUtil.calculateLuminance(mForegroundColor)
            val contrast = NotificationColorUtil.calculateContrast(
                mForegroundColor,
                backgroundColor
            )
            // We only respect the given colors if worst case Black or White still has
            // contrast
            val backgroundLight = (backLum > textLum
                    && NotificationColorUtil.satisfiesTextContrast(
                backgroundColor,
                Color.BLACK
            )
                    || backLum <= textLum
                    && !NotificationColorUtil.satisfiesTextContrast(
                backgroundColor,
                Color.WHITE
            ))
            if (contrast < 4.5f) {
                if (backgroundLight) {
                    secondaryTextColor = NotificationColorUtil.findContrastColor(
                        mForegroundColor,
                        backgroundColor,
                        true /* findFG */, 4.5
                    )
                    primaryTextColor = NotificationColorUtil.changeColorLightness(
                        secondaryTextColor,
                        -LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                    )
                } else {
                    secondaryTextColor = NotificationColorUtil.findContrastColorAgainstDark(
                        mForegroundColor,
                        backgroundColor,
                        true /* findFG */, 4.5
                    )
                    primaryTextColor = NotificationColorUtil.changeColorLightness(
                        secondaryTextColor,
                        -LIGHTNESS_TEXT_DIFFERENCE_DARK
                    )
                }
            } else {
                primaryTextColor = mForegroundColor
                secondaryTextColor = NotificationColorUtil.changeColorLightness(
                    primaryTextColor,
                    if (backgroundLight) LIGHTNESS_TEXT_DIFFERENCE_LIGHT else LIGHTNESS_TEXT_DIFFERENCE_DARK
                )
                if (NotificationColorUtil.calculateContrast(
                        secondaryTextColor,
                        backgroundColor
                    ) < 4.5f
                ) {
                    // oh well the secondary is not good enough
                    secondaryTextColor = if (backgroundLight) {
                        NotificationColorUtil.findContrastColor(
                            secondaryTextColor,
                            backgroundColor,
                            true /* findFG */, 4.5
                        )
                    } else {
                        NotificationColorUtil.findContrastColorAgainstDark(
                            secondaryTextColor,
                            backgroundColor,
                            true /* findFG */, 4.5
                        )
                    }
                    primaryTextColor = NotificationColorUtil.changeColorLightness(
                        secondaryTextColor,
                        if (backgroundLight) -LIGHTNESS_TEXT_DIFFERENCE_LIGHT else -LIGHTNESS_TEXT_DIFFERENCE_DARK
                    )
                }
            }
        }
        actionBarColor = NotificationColorUtil.resolveActionBarColor(
            context,
            backgroundColor
        )
    }

    interface OnPaletteLoadedListener {
        fun onPaletteLoaded(mediaNotificationProcessor: MediaNotificationProcessor)
    }

    companion object {
        /**
         * The fraction below which we select the vibrant instead of the light/dark vibrant color
         */
        private const val POPULATION_FRACTION_FOR_MORE_VIBRANT = 1.0f

        /**
         * Minimum saturation that a muted color must have if there exists if deciding between two
         * colors
         */
        private const val MIN_SATURATION_WHEN_DECIDING = 0.19f

        /**
         * Minimum fraction that any color must have to be picked up as a text color
         */
        private const val MINIMUM_IMAGE_FRACTION = 0.002

        /**
         * The population fraction to select the dominant color as the text color over a the colored
         * ones.
         */
        private const val POPULATION_FRACTION_FOR_DOMINANT = 0.01f

        /**
         * The population fraction to select a white or black color as the background over a color.
         */
        private const val POPULATION_FRACTION_FOR_WHITE_OR_BLACK = 2.5f
        private const val BLACK_MAX_LIGHTNESS = 0.08f
        private const val WHITE_MIN_LIGHTNESS = 0.90f
        private const val RESIZE_BITMAP_AREA = 150 * 150
        private fun isColorLight(backgroundColor: Int): Boolean {
            return calculateLuminance(backgroundColor) > 0.5f
        }

        /**
         * Returns the luminance of a color as a float between `0.0` and `1.0`.
         *
         * Defined as the Y component in the XYZ representation of `color`.
         */
        @FloatRange(from = 0.0, to = 1.0)
        private fun calculateLuminance(@ColorInt color: Int): Double {
            val result = tempDouble3Array
            colorToXYZ(color, result)
            // Luminance is the Y component
            return result[1] / 100
        }

        private val TEMP_ARRAY = ThreadLocal<DoubleArray>()
        private val tempDouble3Array: DoubleArray
            get() {
                var result = TEMP_ARRAY.get()
                if (result == null) {
                    result = DoubleArray(3)
                    TEMP_ARRAY.set(result)
                }
                return result
            }

        private fun colorToXYZ(@ColorInt color: Int, outXyz: DoubleArray) {
            ColorUtils.RGBToXYZ(Color.red(color), Color.green(color), Color.blue(color), outXyz)
        }

        /**
         * The lightness difference that has to be added to the primary text color to obtain the
         * secondary text color when the background is light.
         */
        private const val LIGHTNESS_TEXT_DIFFERENCE_LIGHT = 20

        /**
         * The lightness difference that has to be added to the primary text color to obtain the
         * secondary text color when the background is dark.
         * A bit less then the above value, since it looks better on dark backgrounds.
         */
        private const val LIGHTNESS_TEXT_DIFFERENCE_DARK = -10
    }
}