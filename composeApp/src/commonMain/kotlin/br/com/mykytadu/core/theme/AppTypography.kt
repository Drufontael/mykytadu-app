package br.com.mykytadu.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.mykytadu.composeapp.generated.resources.PoppinsLatin_Bold
import br.com.mykytadu.composeapp.generated.resources.PoppinsLatin_SemiBold
import br.com.mykytadu.composeapp.generated.resources.PoppinsLatin_Regular
import br.com.mykytadu.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun appTypography(): AppTypographyTokens {

    val poppins = FontFamily(
        Font(
            Res.font.PoppinsLatin_Regular,
            weight = FontWeight.W400
        ),
        Font(
            Res.font.PoppinsLatin_SemiBold,
            weight = FontWeight.W600
        ),
        Font(
            Res.font.PoppinsLatin_Bold,
            weight = FontWeight.W700
        )
    )

    return AppTypographyTokens(
        display = TextStyle(
            fontFamily = poppins,
            fontSize = 32.sp,
            fontWeight = FontWeight.W700
        ),
        headline = TextStyle(
            fontFamily = poppins,
            fontSize = 24.sp,
            fontWeight = FontWeight.W700
        ),
        title = TextStyle(
            fontFamily = poppins,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        ),
        body = TextStyle(
            fontFamily = poppins,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        ),
        bodySmall = TextStyle(
            fontFamily = poppins,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400
        ),
        caption = TextStyle(
            fontFamily = poppins,
            fontSize = 12.sp,
            fontWeight = FontWeight.W400
        )
    )
}

data class AppTypographyTokens(
    val display: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val caption: TextStyle
)
