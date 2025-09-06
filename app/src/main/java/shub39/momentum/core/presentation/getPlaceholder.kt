package shub39.momentum.core.presentation

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

fun getPlaceholder(): Painter {
    return BitmapPainter(
        createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ).apply { eraseColor(android.graphics.Color.BLACK) }.asImageBitmap()
    )
}