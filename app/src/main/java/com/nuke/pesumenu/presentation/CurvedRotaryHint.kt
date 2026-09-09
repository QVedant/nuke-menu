package com.nuke.pesumenu.presentation

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat

import com.nuke.pesumenu.R

@Composable
fun CurvedRotaryHint(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val fontTypeface =
        ResourcesCompat.getFont(
            context,
            R.font.roboto_flex_variable
        ) ?: Typeface.DEFAULT

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f)
    ) {

        val width = size.width
        val height = size.height

        /*
         * Radius based on the actual watch width.
         * This keeps the geometry responsive across
         * different round Wear OS displays.
         */
        val radius = width * 0.43f

        val centerX = width / 2f

        /*
         * Position the circle so that its bottom arc
         * sits just inside the bottom of the Canvas.
         */
        val centerY =
            height - radius - 8f

        val path = Path().apply {

            /*
             * Lower section of the circle.
             *
             * 140° → 40° passes through the bottom.
             */
            addArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                140f,
                -100f
            )
        }

        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {

                color =
                    Color(0xFF8E89A8).toArgb()

                textSize =
                    width * 0.06f

                this.typeface =
                    fontTypeface

                textAlign =
                    Paint.Align.LEFT

                isAntiAlias = true
            }

        val text =
            "Rotate bezel"

        val textWidth =
            paint.measureText(text)

        val arcLength =
            Math.toRadians(100.0).toFloat() *
                    radius

        val horizontalOffset =
            (arcLength - textWidth) / 2f

        drawContext.canvas.nativeCanvas
            .drawTextOnPath(
                text,
                path,
                horizontalOffset,
                0f,
                paint
            )
    }
}