package com.nuke.pesumenu.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val settings: ImageVector
  get() {
    if (_settings != null) {
      return _settings!!
    }
    _settings =
      ImageVector.Builder(
          name = "settings",
          defaultWidth = 20.dp,
          defaultHeight = 20.dp,
          viewportWidth = 20f,
          viewportHeight = 20f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(9.17f, 18f)
            quadTo(8.81f, 18f, 8.56f, 17.79f)
            reflectiveQuadTo(8.25f, 17.25f)
            lineTo(7.94f, 15.63f)
            quadTo(7.46f, 15.44f, 7.01f, 15.19f)
            reflectiveQuadTo(6.17f, 14.6f)
            lineTo(4.6f, 15.15f)
            quadTo(4.29f, 15.25f, 3.97f, 15.14f)
            reflectiveQuadTo(3.48f, 14.73f)
            lineTo(2.65f, 13.27f)
            quadTo(2.48f, 12.98f, 2.54f, 12.66f)
            reflectiveQuadTo(2.85f, 12.1f)
            lineTo(4.08f, 11.02f)
            quadTo(4.04f, 10.77f, 4.02f, 10.52f)
            reflectiveQuadTo(4f, 10f)
            reflectiveQuadTo(4.02f, 9.48f)
            reflectiveQuadTo(4.08f, 8.98f)
            lineTo(2.85f, 7.9f)
            quadTo(2.6f, 7.67f, 2.54f, 7.34f)
            reflectiveQuadTo(2.65f, 6.73f)
            lineTo(3.48f, 5.27f)
            quadTo(3.65f, 4.98f, 3.97f, 4.86f)
            reflectiveQuadTo(4.6f, 4.85f)
            lineTo(6.17f, 5.4f)
            quadTo(6.56f, 5.06f, 7.01f, 4.81f)
            reflectiveQuadTo(7.94f, 4.38f)
            lineTo(8.25f, 2.75f)
            quadTo(8.31f, 2.42f, 8.56f, 2.21f)
            reflectiveQuadTo(9.17f, 2f)
            horizontalLineToRelative(1.67f)
            quadToRelative(0.35f, 0f, 0.6f, 0.21f)
            reflectiveQuadToRelative(0.31f, 0.54f)
            lineToRelative(0.31f, 1.63f)
            quadToRelative(0.48f, 0.19f, 0.93f, 0.44f)
            reflectiveQuadTo(13.83f, 5.4f)
            lineTo(15.4f, 4.85f)
            quadToRelative(0.31f, -0.1f, 0.64f, 0.01f)
            reflectiveQuadToRelative(0.49f, 0.41f)
            lineToRelative(0.83f, 1.46f)
            quadToRelative(0.17f, 0.29f, 0.1f, 0.61f)
            reflectiveQuadTo(17.15f, 7.9f)
            lineTo(15.92f, 8.98f)
            quadToRelative(0.04f, 0.25f, 0.06f, 0.5f)
            reflectiveQuadTo(16f, 10f)
            reflectiveQuadToRelative(-0.02f, 0.52f)
            reflectiveQuadToRelative(-0.06f, 0.5f)
            lineToRelative(1.23f, 1.08f)
            quadToRelative(0.25f, 0.23f, 0.31f, 0.55f)
            reflectiveQuadToRelative(-0.1f, 0.61f)
            lineToRelative(-0.83f, 1.46f)
            quadToRelative(-0.17f, 0.29f, -0.49f, 0.41f)
            reflectiveQuadTo(15.4f, 15.15f)
            lineTo(13.83f, 14.6f)
            quadToRelative(-0.4f, 0.33f, -0.84f, 0.58f)
            reflectiveQuadToRelative(-0.93f, 0.44f)
            lineToRelative(-0.31f, 1.63f)
            quadToRelative(-0.06f, 0.33f, -0.31f, 0.54f)
            reflectiveQuadTo(10.83f, 18f)
            horizontalLineTo(9.17f)
            close()
            moveTo(9.63f, 16.5f)
            horizontalLineToRelative(0.75f)
            lineToRelative(0.4f, -2.06f)
            quadToRelative(0.79f, -0.15f, 1.48f, -0.54f)
            reflectiveQuadToRelative(1.19f, -1f)
            lineToRelative(2f, 0.67f)
            lineToRelative(0.38f, -0.63f)
            lineToRelative(-1.58f, -1.4f)
            quadToRelative(0.13f, -0.35f, 0.2f, -0.74f)
            reflectiveQuadTo(14.5f, 10f)
            reflectiveQuadTo(14.43f, 9.2f)
            reflectiveQuadTo(14.23f, 8.46f)
            lineToRelative(1.58f, -1.4f)
            lineTo(15.44f, 6.44f)
            lineToRelative(-2f, 0.67f)
            quadToRelative(-0.5f, -0.6f, -1.19f, -1f)
            reflectiveQuadTo(10.77f, 5.56f)
            lineTo(10.38f, 3.5f)
            horizontalLineTo(9.63f)
            lineTo(9.23f, 5.56f)
            quadTo(8.44f, 5.71f, 7.75f, 6.1f)
            reflectiveQuadToRelative(-1.19f, 1f)
            lineToRelative(-2f, -0.67f)
            lineTo(4.19f, 7.06f)
            lineToRelative(1.58f, 1.4f)
            quadTo(5.65f, 8.81f, 5.57f, 9.2f)
            reflectiveQuadTo(5.5f, 10f)
            reflectiveQuadToRelative(0.07f, 0.8f)
            reflectiveQuadToRelative(0.2f, 0.74f)
            lineToRelative(-1.58f, 1.4f)
            lineToRelative(0.38f, 0.63f)
            lineToRelative(2f, -0.67f)
            quadToRelative(0.5f, 0.6f, 1.19f, 1f)
            reflectiveQuadToRelative(1.48f, 0.54f)
            lineToRelative(0.4f, 2.06f)
            close()
            moveTo(10f, 13f)
            quadToRelative(1.25f, 0f, 2.13f, -0.88f)
            reflectiveQuadTo(13f, 10f)
            reflectiveQuadTo(12.13f, 7.88f)
            reflectiveQuadTo(10f, 7f)
            reflectiveQuadTo(7.88f, 7.88f)
            reflectiveQuadTo(7f, 10f)
            reflectiveQuadToRelative(0.88f, 2.13f)
            reflectiveQuadTo(10f, 13f)
            close()
            moveToRelative(0f, -3f)
            close()
          }
        }
        .build()
    return _settings!!
  }

private var _settings: ImageVector? = null
