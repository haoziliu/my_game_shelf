import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygameshelf.presentation.theme.PixelFont
import kotlin.random.Random

@Composable
fun Modifier.noiseBackground(
    baseColor: Color,
    noiseColor: Color = Color.Black.copy(alpha = 0.1f),
    noiseDensity: Float = 0.08f,
    noiseBitmapSize: Int = 128
): Modifier = this.composed {

    val noiseBitmap = remember(noiseBitmapSize, noiseColor, noiseDensity) {
        createNoiseBitmap(
            size = noiseBitmapSize,
            color = noiseColor,
            density = noiseDensity
        )
    }

    val noiseShader = remember(noiseBitmap) {
        ImageShader(noiseBitmap, TileMode.Repeated, TileMode.Repeated)
    }

    val noisePaint = remember(noiseShader) {
        Paint().apply {
            shader = noiseShader
        }
    }

    drawBehind {
        drawRect(color = baseColor)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawPaint(noisePaint.asFrameworkPaint())
        }
    }
}

private fun createNoiseBitmap(size: Int, color: Color, density: Float): ImageBitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(size * size)
    val random = Random.Default
    val noiseColorInt = android.graphics.Color.argb(
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )

    for (i in pixels.indices) {
        if (random.nextFloat() < density) {
            pixels[i] = noiseColorInt
        }
    }

    bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
    return bitmap.asImageBitmap()
}

@Composable
fun SkeuomorphicImagePlate(
    modifier: Modifier = Modifier,
    painter: Painter,
    text: String,
    elevation: Dp = 6.dp
) {
    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        val shadowOffsetX = (elevation * 0.5f)
        val shadowOffsetY = (elevation * 0.7f)
        val shadowBlurRadius = (elevation * 1.5f)
        val shadowAlpha = 0.4f

        Image(
            painter = painter,
            contentDescription = "Plate Shadow",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffsetX, y = shadowOffsetY)
                .blur(radius = shadowBlurRadius),
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = shadowAlpha))
        )

        Image(
            painter = painter,
            contentDescription = text,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Text(
            text = text,
            fontFamily = PixelFont,
            color = Color(0xFF4C3D21).copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.3f),
                    offset = Offset(1f, 1f),
                    blurRadius = 1f
                )
            ),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        )
    }
}