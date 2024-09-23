package com.example.stylus.ui

import android.graphics.PointF
import com.example.stylus.data.DrawPointType

class DrawPoint(
    x: Float,
    y: Float,
    val type: DrawPointType
): PointF(x, y)
