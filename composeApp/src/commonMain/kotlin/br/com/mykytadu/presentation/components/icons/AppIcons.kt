package br.com.mykytadu.presentation.components.icons


import br.com.mykytadu.core.theme.AppColors
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Biblioteca de ícones oficial do MykytaDu.
 *
 * Os ícones são baseados em vetores SVG convertidos para ImageVector usando https://www.svgtocompose.com/.
 * A biblioteca contém apenas ícones efetivamente utilizados pelo aplicativo,
 * evitando dependências desnecessariamente grandes.
 */
object AppIcons {

    object Navigation {
        val Back: ImageVector
            get() {
                val current = _back
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.ArrowBack24dpE3E3E3FILL0Wght400GRAD0Opsz24",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // m313 -440 224 224 -57 56 -320 -320 320 -320 57 56 -224 224 h487 v80 H313Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 313 520
                        moveTo(x = 313.0f, y = 520.0f)
                        // l 224 224
                        lineToRelative(dx = 224.0f, dy = 224.0f)
                        // l -57 56
                        lineToRelative(dx = -57.0f, dy = 56.0f)
                        // l -320 -320
                        lineToRelative(dx = -320.0f, dy = -320.0f)
                        // l 320 -320
                        lineToRelative(dx = 320.0f, dy = -320.0f)
                        // l 57 56
                        lineToRelative(dx = 57.0f, dy = 56.0f)
                        // l -224 224
                        lineToRelative(dx = -224.0f, dy = 224.0f)
                        // l 487 0
                        lineToRelative(dx = 487.0f, dy = 0.0f)
                        // l 0 80
                        lineToRelative(dx = 0.0f, dy = 80.0f)
                        // L 313 520z
                        lineTo(x = 313.0f, y = 520.0f)
                        close()
                    }
                }.build().also { _back = it }
            }
        @Suppress("ObjectPropertyName")
        private var _back: ImageVector? = null

        val Home: ImageVector
            get() {
                val current = _home
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Home",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // M240 -200 h120 v-240 h240 v240 h120 v-360 L480 -740 240 -560 v360Z m-80 80 v-480 l320 -240 320 240 v480 H520 v-240 h-80 v240 H160Z m320 -350Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 240 760
                        moveTo(x = 240.0f, y = 760.0f)
                        // l 120 0
                        lineToRelative(dx = 120.0f, dy = 0.0f)
                        // l 0 -240
                        lineToRelative(dx = 0.0f, dy = -240.0f)
                        // l 240 0
                        lineToRelative(dx = 240.0f, dy = 0.0f)
                        // l 0 240
                        lineToRelative(dx = 0.0f, dy = 240.0f)
                        // l 120 0
                        lineToRelative(dx = 120.0f, dy = 0.0f)
                        // l 0 -360
                        lineToRelative(dx = 0.0f, dy = -360.0f)
                        // L 480 220
                        lineTo(x = 480.0f, y = 220.0f)
                        // L 240 400
                        lineTo(x = 240.0f, y = 400.0f)
                        // l 0 360z
                        lineToRelative(dx = 0.0f, dy = 360.0f)
                        close()
                        // m -80 80
                        moveToRelative(dx = -80.0f, dy = 80.0f)
                        // l 0 -480
                        lineToRelative(dx = 0.0f, dy = -480.0f)
                        // l 320 -240
                        lineToRelative(dx = 320.0f, dy = -240.0f)
                        // l 320 240
                        lineToRelative(dx = 320.0f, dy = 240.0f)
                        // l 0 480
                        lineToRelative(dx = 0.0f, dy = 480.0f)
                        // L 520 840
                        lineTo(x = 520.0f, y = 840.0f)
                        // l 0 -240
                        lineToRelative(dx = 0.0f, dy = -240.0f)
                        // l -80 0
                        lineToRelative(dx = -80.0f, dy = 0.0f)
                        // l 0 240
                        lineToRelative(dx = 0.0f, dy = 240.0f)
                        // L 160 840z
                        lineTo(x = 160.0f, y = 840.0f)
                        close()
                        // m 320 -350z
                        moveToRelative(dx = 320.0f, dy = -350.0f)
                        close()
                    }
                }.build().also { _home = it }
            }

        @Suppress("ObjectPropertyName")
        private var _home: ImageVector? = null

        val Profile: ImageVector
            get() {
                val current = _profile
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Profile",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // M367 -527 q-47 -47 -47 -113 t47 -113 q47 -47 113 -47 t113 47 q47 47 47 113 t-47 113 q-47 47 -113 47 t-113 -47Z M160 -160 v-112 q0 -34 17.5 -62.5 T224 -378 q62 -31 126 -46.5 T480 -440 q66 0 130 15.5 T736 -378 q29 15 46.5 43.5 T800 -272 v112 H160Z m80 -80 h480 v-32 q0 -11 -5.5 -20 T700 -306 q-54 -27 -109 -40.5 T480 -360 q-56 0 -111 13.5 T260 -306 q-9 5 -14.5 14 t-5.5 20 v32Z m296.5 -343.5 Q560 -607 560 -640 t-23.5 -56.5 Q513 -720 480 -720 t-56.5 23.5 Q400 -673 400 -640 t23.5 56.5 Q447 -560 480 -560 t56.5 -23.5Z M480 -640Z m0 400Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 367 433
                        moveTo(x = 367.0f, y = 433.0f)
                        // q -47 -47 -47 -113
                        quadToRelative(
                            dx1 = -47.0f,
                            dy1 = -47.0f,
                            dx2 = -47.0f,
                            dy2 = -113.0f,
                        )
                        // t 47 -113
                        reflectiveQuadToRelative(
                            dx1 = 47.0f,
                            dy1 = -113.0f,
                        )
                        // q 47 -47 113 -47
                        quadToRelative(
                            dx1 = 47.0f,
                            dy1 = -47.0f,
                            dx2 = 113.0f,
                            dy2 = -47.0f,
                        )
                        // t 113 47
                        reflectiveQuadToRelative(
                            dx1 = 113.0f,
                            dy1 = 47.0f,
                        )
                        // q 47 47 47 113
                        quadToRelative(
                            dx1 = 47.0f,
                            dy1 = 47.0f,
                            dx2 = 47.0f,
                            dy2 = 113.0f,
                        )
                        // t -47 113
                        reflectiveQuadToRelative(
                            dx1 = -47.0f,
                            dy1 = 113.0f,
                        )
                        // q -47 47 -113 47
                        quadToRelative(
                            dx1 = -47.0f,
                            dy1 = 47.0f,
                            dx2 = -113.0f,
                            dy2 = 47.0f,
                        )
                        // t -113 -47z
                        reflectiveQuadToRelative(
                            dx1 = -113.0f,
                            dy1 = -47.0f,
                        )
                        close()
                        // M 160 800
                        moveTo(x = 160.0f, y = 800.0f)
                        // l 0 -112
                        lineToRelative(dx = 0.0f, dy = -112.0f)
                        // q 0 -34 17.5 -62.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -34.0f,
                            dx2 = 17.5f,
                            dy2 = -62.5f,
                        )
                        // T 224 582
                        reflectiveQuadTo(
                            x1 = 224.0f,
                            y1 = 582.0f,
                        )
                        // q 62 -31 126 -46.5
                        quadToRelative(
                            dx1 = 62.0f,
                            dy1 = -31.0f,
                            dx2 = 126.0f,
                            dy2 = -46.5f,
                        )
                        // T 480 520
                        reflectiveQuadTo(
                            x1 = 480.0f,
                            y1 = 520.0f,
                        )
                        // q 66 0 130 15.5
                        quadToRelative(
                            dx1 = 66.0f,
                            dy1 = 0.0f,
                            dx2 = 130.0f,
                            dy2 = 15.5f,
                        )
                        // T 736 582
                        reflectiveQuadTo(
                            x1 = 736.0f,
                            y1 = 582.0f,
                        )
                        // q 29 15 46.5 43.5
                        quadToRelative(
                            dx1 = 29.0f,
                            dy1 = 15.0f,
                            dx2 = 46.5f,
                            dy2 = 43.5f,
                        )
                        // T 800 688
                        reflectiveQuadTo(
                            x1 = 800.0f,
                            y1 = 688.0f,
                        )
                        // l 0 112
                        lineToRelative(dx = 0.0f, dy = 112.0f)
                        // L 160 800z
                        lineTo(x = 160.0f, y = 800.0f)
                        close()
                        // m 80 -80
                        moveToRelative(dx = 80.0f, dy = -80.0f)
                        // l 480 0
                        lineToRelative(dx = 480.0f, dy = 0.0f)
                        // l 0 -32
                        lineToRelative(dx = 0.0f, dy = -32.0f)
                        // q 0 -11 -5.5 -20
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -11.0f,
                            dx2 = -5.5f,
                            dy2 = -20.0f,
                        )
                        // T 700 654
                        reflectiveQuadTo(
                            x1 = 700.0f,
                            y1 = 654.0f,
                        )
                        // q -54 -27 -109 -40.5
                        quadToRelative(
                            dx1 = -54.0f,
                            dy1 = -27.0f,
                            dx2 = -109.0f,
                            dy2 = -40.5f,
                        )
                        // T 480 600
                        reflectiveQuadTo(
                            x1 = 480.0f,
                            y1 = 600.0f,
                        )
                        // q -56 0 -111 13.5
                        quadToRelative(
                            dx1 = -56.0f,
                            dy1 = 0.0f,
                            dx2 = -111.0f,
                            dy2 = 13.5f,
                        )
                        // T 260 654
                        reflectiveQuadTo(
                            x1 = 260.0f,
                            y1 = 654.0f,
                        )
                        // q -9 5 -14.5 14
                        quadToRelative(
                            dx1 = -9.0f,
                            dy1 = 5.0f,
                            dx2 = -14.5f,
                            dy2 = 14.0f,
                        )
                        // t -5.5 20
                        reflectiveQuadToRelative(
                            dx1 = -5.5f,
                            dy1 = 20.0f,
                        )
                        // l 0 32z
                        lineToRelative(dx = 0.0f, dy = 32.0f)
                        close()
                        // m 296.5 -343.5
                        moveToRelative(dx = 296.5f, dy = -343.5f)
                        // Q 560 353 560 320
                        quadTo(
                            x1 = 560.0f,
                            y1 = 353.0f,
                            x2 = 560.0f,
                            y2 = 320.0f,
                        )
                        // t -23.5 -56.5
                        reflectiveQuadToRelative(
                            dx1 = -23.5f,
                            dy1 = -56.5f,
                        )
                        // Q 513 240 480 240
                        quadTo(
                            x1 = 513.0f,
                            y1 = 240.0f,
                            x2 = 480.0f,
                            y2 = 240.0f,
                        )
                        // t -56.5 23.5
                        reflectiveQuadToRelative(
                            dx1 = -56.5f,
                            dy1 = 23.5f,
                        )
                        // Q 400 287 400 320
                        quadTo(
                            x1 = 400.0f,
                            y1 = 287.0f,
                            x2 = 400.0f,
                            y2 = 320.0f,
                        )
                        // t 23.5 56.5
                        reflectiveQuadToRelative(
                            dx1 = 23.5f,
                            dy1 = 56.5f,
                        )
                        // Q 447 400 480 400
                        quadTo(
                            x1 = 447.0f,
                            y1 = 400.0f,
                            x2 = 480.0f,
                            y2 = 400.0f,
                        )
                        // t 56.5 -23.5z
                        reflectiveQuadToRelative(
                            dx1 = 56.5f,
                            dy1 = -23.5f,
                        )
                        close()
                        // M 480 320z
                        moveTo(x = 480.0f, y = 320.0f)
                        close()
                        // m 0 400z
                        moveToRelative(dx = 0.0f, dy = 400.0f)
                        close()
                    }
                }.build().also { _profile = it }
            }

        @Suppress("ObjectPropertyName")
        private var _profile: ImageVector? = null
    }

    object Actions {
        val Add: ImageVector
            get() {
                val current = _add
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Add",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // M440 -440 H200 v-80 h240 v-240 h80 v240 h240 v80 H520 v240 h-80 v-240Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 440 520
                        moveTo(x = 440.0f, y = 520.0f)
                        // L 200 520
                        lineTo(x = 200.0f, y = 520.0f)
                        // l 0 -80
                        lineToRelative(dx = 0.0f, dy = -80.0f)
                        // l 240 0
                        lineToRelative(dx = 240.0f, dy = 0.0f)
                        // l 0 -240
                        lineToRelative(dx = 0.0f, dy = -240.0f)
                        // l 80 0
                        lineToRelative(dx = 80.0f, dy = 0.0f)
                        // l 0 240
                        lineToRelative(dx = 0.0f, dy = 240.0f)
                        // l 240 0
                        lineToRelative(dx = 240.0f, dy = 0.0f)
                        // l 0 80
                        lineToRelative(dx = 0.0f, dy = 80.0f)
                        // L 520 520
                        lineTo(x = 520.0f, y = 520.0f)
                        // l 0 240
                        lineToRelative(dx = 0.0f, dy = 240.0f)
                        // l -80 0
                        lineToRelative(dx = -80.0f, dy = 0.0f)
                        // l 0 -240z
                        lineToRelative(dx = 0.0f, dy = -240.0f)
                        close()
                    }
                }.build().also { _add = it }
            }
        @Suppress("ObjectPropertyName")
        private var _add: ImageVector? = null

        val Close: ImageVector
            get() {
                val current = _close
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Close",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // m256 -200 -56 -56 224 -224 -224 -224 56 -56 224 224 224 -224 56 56 -224 224 224 224 -56 56 -224 -224 -224 224Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 256 760
                        moveTo(x = 256.0f, y = 760.0f)
                        // l -56 -56
                        lineToRelative(dx = -56.0f, dy = -56.0f)
                        // l 224 -224
                        lineToRelative(dx = 224.0f, dy = -224.0f)
                        // l -224 -224
                        lineToRelative(dx = -224.0f, dy = -224.0f)
                        // l 56 -56
                        lineToRelative(dx = 56.0f, dy = -56.0f)
                        // l 224 224
                        lineToRelative(dx = 224.0f, dy = 224.0f)
                        // l 224 -224
                        lineToRelative(dx = 224.0f, dy = -224.0f)
                        // l 56 56
                        lineToRelative(dx = 56.0f, dy = 56.0f)
                        // l -224 224
                        lineToRelative(dx = -224.0f, dy = 224.0f)
                        // l 224 224
                        lineToRelative(dx = 224.0f, dy = 224.0f)
                        // l -56 56
                        lineToRelative(dx = -56.0f, dy = 56.0f)
                        // l -224 -224
                        lineToRelative(dx = -224.0f, dy = -224.0f)
                        // l -224 224z
                        lineToRelative(dx = -224.0f, dy = 224.0f)
                        close()
                    }
                }.build().also { _close = it }
            }
        @Suppress("ObjectPropertyName")
        private var _close: ImageVector? = null

        val Settings: ImageVector
            get() {
                val current = _settings
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Settings",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // m370 -80 -16 -128 q-13 -5 -24.5 -12 T307 -235 l-119 50 L78 -375 l103 -78 q-1 -7 -1 -13.5 v-27 q0 -6.5 1 -13.5 L78 -585 l110 -190 119 50 q11 -8 23 -15 t24 -12 l16 -128 h220 l16 128 q13 5 24.5 12 t22.5 15 l119 -50 110 190 -103 78 q1 7 1 13.5 v27 q0 6.5 -2 13.5 l103 78 -110 190 -118 -50 q-11 8 -23 15 t-24 12 L590 -80 H370Z m70 -80 h79 l14 -106 q31 -8 57.5 -23.5 T639 -327 l99 41 39 -68 -86 -65 q5 -14 7 -29.5 t2 -31.5 q0 -16 -2 -31.5 t-7 -29.5 l86 -65 -39 -68 -99 42 q-22 -23 -48.5 -38.5 T533 -694 l-13 -106 h-79 l-14 106 q-31 8 -57.5 23.5 T321 -633 l-99 -41 -39 68 86 64 q-5 15 -7 30 t-2 32 q0 16 2 31 t7 30 l-86 65 39 68 99 -42 q22 23 48.5 38.5 T427 -266 l13 106Z m42 -180 q58 0 99 -41 t41 -99 q0 -58 -41 -99 t-99 -41 q-59 0 -99.5 41 T342 -480 q0 58 40.5 99 t99.5 41Z m-2 -140Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 370 880
                        moveTo(x = 370.0f, y = 880.0f)
                        // l -16 -128
                        lineToRelative(dx = -16.0f, dy = -128.0f)
                        // q -13 -5 -24.5 -12
                        quadToRelative(
                            dx1 = -13.0f,
                            dy1 = -5.0f,
                            dx2 = -24.5f,
                            dy2 = -12.0f,
                        )
                        // T 307 725
                        reflectiveQuadTo(
                            x1 = 307.0f,
                            y1 = 725.0f,
                        )
                        // l -119 50
                        lineToRelative(dx = -119.0f, dy = 50.0f)
                        // L 78 585
                        lineTo(x = 78.0f, y = 585.0f)
                        // l 103 -78
                        lineToRelative(dx = 103.0f, dy = -78.0f)
                        // q -1 -7 -1 -13.5
                        quadToRelative(
                            dx1 = -1.0f,
                            dy1 = -7.0f,
                            dx2 = -1.0f,
                            dy2 = -13.5f,
                        )
                        // l 0 -27
                        lineToRelative(dx = 0.0f, dy = -27.0f)
                        // q 0 -6.5 1 -13.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -6.5f,
                            dx2 = 1.0f,
                            dy2 = -13.5f,
                        )
                        // L 78 375
                        lineTo(x = 78.0f, y = 375.0f)
                        // l 110 -190
                        lineToRelative(dx = 110.0f, dy = -190.0f)
                        // l 119 50
                        lineToRelative(dx = 119.0f, dy = 50.0f)
                        // q 11 -8 23 -15
                        quadToRelative(
                            dx1 = 11.0f,
                            dy1 = -8.0f,
                            dx2 = 23.0f,
                            dy2 = -15.0f,
                        )
                        // t 24 -12
                        reflectiveQuadToRelative(
                            dx1 = 24.0f,
                            dy1 = -12.0f,
                        )
                        // l 16 -128
                        lineToRelative(dx = 16.0f, dy = -128.0f)
                        // l 220 0
                        lineToRelative(dx = 220.0f, dy = 0.0f)
                        // l 16 128
                        lineToRelative(dx = 16.0f, dy = 128.0f)
                        // q 13 5 24.5 12
                        quadToRelative(
                            dx1 = 13.0f,
                            dy1 = 5.0f,
                            dx2 = 24.5f,
                            dy2 = 12.0f,
                        )
                        // t 22.5 15
                        reflectiveQuadToRelative(
                            dx1 = 22.5f,
                            dy1 = 15.0f,
                        )
                        // l 119 -50
                        lineToRelative(dx = 119.0f, dy = -50.0f)
                        // l 110 190
                        lineToRelative(dx = 110.0f, dy = 190.0f)
                        // l -103 78
                        lineToRelative(dx = -103.0f, dy = 78.0f)
                        // q 1 7 1 13.5
                        quadToRelative(
                            dx1 = 1.0f,
                            dy1 = 7.0f,
                            dx2 = 1.0f,
                            dy2 = 13.5f,
                        )
                        // l 0 27
                        lineToRelative(dx = 0.0f, dy = 27.0f)
                        // q 0 6.5 -2 13.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 6.5f,
                            dx2 = -2.0f,
                            dy2 = 13.5f,
                        )
                        // l 103 78
                        lineToRelative(dx = 103.0f, dy = 78.0f)
                        // l -110 190
                        lineToRelative(dx = -110.0f, dy = 190.0f)
                        // l -118 -50
                        lineToRelative(dx = -118.0f, dy = -50.0f)
                        // q -11 8 -23 15
                        quadToRelative(
                            dx1 = -11.0f,
                            dy1 = 8.0f,
                            dx2 = -23.0f,
                            dy2 = 15.0f,
                        )
                        // t -24 12
                        reflectiveQuadToRelative(
                            dx1 = -24.0f,
                            dy1 = 12.0f,
                        )
                        // L 590 880
                        lineTo(x = 590.0f, y = 880.0f)
                        // L 370 880z
                        lineTo(x = 370.0f, y = 880.0f)
                        close()
                        // m 70 -80
                        moveToRelative(dx = 70.0f, dy = -80.0f)
                        // l 79 0
                        lineToRelative(dx = 79.0f, dy = 0.0f)
                        // l 14 -106
                        lineToRelative(dx = 14.0f, dy = -106.0f)
                        // q 31 -8 57.5 -23.5
                        quadToRelative(
                            dx1 = 31.0f,
                            dy1 = -8.0f,
                            dx2 = 57.5f,
                            dy2 = -23.5f,
                        )
                        // T 639 633
                        reflectiveQuadTo(
                            x1 = 639.0f,
                            y1 = 633.0f,
                        )
                        // l 99 41
                        lineToRelative(dx = 99.0f, dy = 41.0f)
                        // l 39 -68
                        lineToRelative(dx = 39.0f, dy = -68.0f)
                        // l -86 -65
                        lineToRelative(dx = -86.0f, dy = -65.0f)
                        // q 5 -14 7 -29.5
                        quadToRelative(
                            dx1 = 5.0f,
                            dy1 = -14.0f,
                            dx2 = 7.0f,
                            dy2 = -29.5f,
                        )
                        // t 2 -31.5
                        reflectiveQuadToRelative(
                            dx1 = 2.0f,
                            dy1 = -31.5f,
                        )
                        // q 0 -16 -2 -31.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -16.0f,
                            dx2 = -2.0f,
                            dy2 = -31.5f,
                        )
                        // t -7 -29.5
                        reflectiveQuadToRelative(
                            dx1 = -7.0f,
                            dy1 = -29.5f,
                        )
                        // l 86 -65
                        lineToRelative(dx = 86.0f, dy = -65.0f)
                        // l -39 -68
                        lineToRelative(dx = -39.0f, dy = -68.0f)
                        // l -99 42
                        lineToRelative(dx = -99.0f, dy = 42.0f)
                        // q -22 -23 -48.5 -38.5
                        quadToRelative(
                            dx1 = -22.0f,
                            dy1 = -23.0f,
                            dx2 = -48.5f,
                            dy2 = -38.5f,
                        )
                        // T 533 266
                        reflectiveQuadTo(
                            x1 = 533.0f,
                            y1 = 266.0f,
                        )
                        // l -13 -106
                        lineToRelative(dx = -13.0f, dy = -106.0f)
                        // l -79 0
                        lineToRelative(dx = -79.0f, dy = 0.0f)
                        // l -14 106
                        lineToRelative(dx = -14.0f, dy = 106.0f)
                        // q -31 8 -57.5 23.5
                        quadToRelative(
                            dx1 = -31.0f,
                            dy1 = 8.0f,
                            dx2 = -57.5f,
                            dy2 = 23.5f,
                        )
                        // T 321 327
                        reflectiveQuadTo(
                            x1 = 321.0f,
                            y1 = 327.0f,
                        )
                        // l -99 -41
                        lineToRelative(dx = -99.0f, dy = -41.0f)
                        // l -39 68
                        lineToRelative(dx = -39.0f, dy = 68.0f)
                        // l 86 64
                        lineToRelative(dx = 86.0f, dy = 64.0f)
                        // q -5 15 -7 30
                        quadToRelative(
                            dx1 = -5.0f,
                            dy1 = 15.0f,
                            dx2 = -7.0f,
                            dy2 = 30.0f,
                        )
                        // t -2 32
                        reflectiveQuadToRelative(
                            dx1 = -2.0f,
                            dy1 = 32.0f,
                        )
                        // q 0 16 2 31
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 16.0f,
                            dx2 = 2.0f,
                            dy2 = 31.0f,
                        )
                        // t 7 30
                        reflectiveQuadToRelative(
                            dx1 = 7.0f,
                            dy1 = 30.0f,
                        )
                        // l -86 65
                        lineToRelative(dx = -86.0f, dy = 65.0f)
                        // l 39 68
                        lineToRelative(dx = 39.0f, dy = 68.0f)
                        // l 99 -42
                        lineToRelative(dx = 99.0f, dy = -42.0f)
                        // q 22 23 48.5 38.5
                        quadToRelative(
                            dx1 = 22.0f,
                            dy1 = 23.0f,
                            dx2 = 48.5f,
                            dy2 = 38.5f,
                        )
                        // T 427 694
                        reflectiveQuadTo(
                            x1 = 427.0f,
                            y1 = 694.0f,
                        )
                        // l 13 106z
                        lineToRelative(dx = 13.0f, dy = 106.0f)
                        close()
                        // m 42 -180
                        moveToRelative(dx = 42.0f, dy = -180.0f)
                        // q 58 0 99 -41
                        quadToRelative(
                            dx1 = 58.0f,
                            dy1 = 0.0f,
                            dx2 = 99.0f,
                            dy2 = -41.0f,
                        )
                        // t 41 -99
                        reflectiveQuadToRelative(
                            dx1 = 41.0f,
                            dy1 = -99.0f,
                        )
                        // q 0 -58 -41 -99
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -58.0f,
                            dx2 = -41.0f,
                            dy2 = -99.0f,
                        )
                        // t -99 -41
                        reflectiveQuadToRelative(
                            dx1 = -99.0f,
                            dy1 = -41.0f,
                        )
                        // q -59 0 -99.5 41
                        quadToRelative(
                            dx1 = -59.0f,
                            dy1 = 0.0f,
                            dx2 = -99.5f,
                            dy2 = 41.0f,
                        )
                        // T 342 480
                        reflectiveQuadTo(
                            x1 = 342.0f,
                            y1 = 480.0f,
                        )
                        // q 0 58 40.5 99
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 58.0f,
                            dx2 = 40.5f,
                            dy2 = 99.0f,
                        )
                        // t 99.5 41z
                        reflectiveQuadToRelative(
                            dx1 = 99.5f,
                            dy1 = 41.0f,
                        )
                        close()
                        // m -2 -140z
                        moveToRelative(dx = -2.0f, dy = -140.0f)
                        close()
                    }
                }.build().also { _settings = it }
            }
        @Suppress("ObjectPropertyName")
        private var _settings: ImageVector? = null

        val Search: ImageVector
            get() {
                val current = _search
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Search",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // M784 -120 532 -372 q-30 24 -69 38 t-83 14 q-109 0 -184.5 -75.5 T120 -580 q0 -109 75.5 -184.5 T380 -840 q109 0 184.5 75.5 T640 -580 q0 44 -14 83 t-38 69 l252 252 -56 56Z M380 -400 q75 0 127.5 -52.5 T560 -580 q0 -75 -52.5 -127.5 T380 -760 q-75 0 -127.5 52.5 T200 -580 q0 75 52.5 127.5 T380 -400Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 784 840
                        moveTo(x = 784.0f, y = 840.0f)
                        // L 532 588
                        lineTo(x = 532.0f, y = 588.0f)
                        // q -30 24 -69 38
                        quadToRelative(
                            dx1 = -30.0f,
                            dy1 = 24.0f,
                            dx2 = -69.0f,
                            dy2 = 38.0f,
                        )
                        // t -83 14
                        reflectiveQuadToRelative(
                            dx1 = -83.0f,
                            dy1 = 14.0f,
                        )
                        // q -109 0 -184.5 -75.5
                        quadToRelative(
                            dx1 = -109.0f,
                            dy1 = 0.0f,
                            dx2 = -184.5f,
                            dy2 = -75.5f,
                        )
                        // T 120 380
                        reflectiveQuadTo(
                            x1 = 120.0f,
                            y1 = 380.0f,
                        )
                        // q 0 -109 75.5 -184.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -109.0f,
                            dx2 = 75.5f,
                            dy2 = -184.5f,
                        )
                        // T 380 120
                        reflectiveQuadTo(
                            x1 = 380.0f,
                            y1 = 120.0f,
                        )
                        // q 109 0 184.5 75.5
                        quadToRelative(
                            dx1 = 109.0f,
                            dy1 = 0.0f,
                            dx2 = 184.5f,
                            dy2 = 75.5f,
                        )
                        // T 640 380
                        reflectiveQuadTo(
                            x1 = 640.0f,
                            y1 = 380.0f,
                        )
                        // q 0 44 -14 83
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 44.0f,
                            dx2 = -14.0f,
                            dy2 = 83.0f,
                        )
                        // t -38 69
                        reflectiveQuadToRelative(
                            dx1 = -38.0f,
                            dy1 = 69.0f,
                        )
                        // l 252 252
                        lineToRelative(dx = 252.0f, dy = 252.0f)
                        // l -56 56z
                        lineToRelative(dx = -56.0f, dy = 56.0f)
                        close()
                        // M 380 560
                        moveTo(x = 380.0f, y = 560.0f)
                        // q 75 0 127.5 -52.5
                        quadToRelative(
                            dx1 = 75.0f,
                            dy1 = 0.0f,
                            dx2 = 127.5f,
                            dy2 = -52.5f,
                        )
                        // T 560 380
                        reflectiveQuadTo(
                            x1 = 560.0f,
                            y1 = 380.0f,
                        )
                        // q 0 -75 -52.5 -127.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -75.0f,
                            dx2 = -52.5f,
                            dy2 = -127.5f,
                        )
                        // T 380 200
                        reflectiveQuadTo(
                            x1 = 380.0f,
                            y1 = 200.0f,
                        )
                        // q -75 0 -127.5 52.5
                        quadToRelative(
                            dx1 = -75.0f,
                            dy1 = 0.0f,
                            dx2 = -127.5f,
                            dy2 = 52.5f,
                        )
                        // T 200 380
                        reflectiveQuadTo(
                            x1 = 200.0f,
                            y1 = 380.0f,
                        )
                        // q 0 75 52.5 127.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 75.0f,
                            dx2 = 52.5f,
                            dy2 = 127.5f,
                        )
                        // T 380 560z
                        reflectiveQuadTo(
                            x1 = 380.0f,
                            y1 = 560.0f,
                        )
                        close()
                    }
                }.build().also { _search = it }
            }
        @Suppress("ObjectPropertyName")
        private var _search: ImageVector? = null
    }

    object Anime{
        val Play: ImageVector
            get() {
                val current = _play
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Play",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 24.0f,
                    viewportHeight = 24.0f,
                ).apply {
                    // M0 0 h24 v24 H0z
                    path {
                        // M 0 0
                        moveTo(x = 0.0f, y = 0.0f)
                        // h 24
                        horizontalLineToRelative(dx = 24.0f)
                        // v 24
                        verticalLineToRelative(dy = 24.0f)
                        // H 0z
                        horizontalLineTo(x = 0.0f)
                        close()
                    }
                    // M8 5 v14 l11 -7z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 8 5
                        moveTo(x = 8.0f, y = 5.0f)
                        // v 14
                        verticalLineToRelative(dy = 14.0f)
                        // l 11 -7z
                        lineToRelative(dx = 11.0f, dy = -7.0f)
                        close()
                    }
                }.build().also { _play = it }
            }
        @Suppress("ObjectPropertyName")
        private var _play: ImageVector? = null

        val Favorite: ImageVector
            get() {
                val current = _favorite
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.Favorite",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 24.0f,
                    viewportHeight = 24.0f,
                ).apply {
                    // M0 0 h24 v24 H0z
                    path {
                        // M 0 0
                        moveTo(x = 0.0f, y = 0.0f)
                        // h 24
                        horizontalLineToRelative(dx = 24.0f)
                        // v 24
                        verticalLineToRelative(dy = 24.0f)
                        // H 0z
                        horizontalLineTo(x = 0.0f)
                        close()
                    }
                    // M12 21.35 l-1.45 -1.32 C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3 c1.74 0 3.41 .81 4.5 2.09 C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5 c0 3.78 -3.4 6.86 -8.55 11.54 L12 21.35z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 12 21.35
                        moveTo(x = 12.0f, y = 21.35f)
                        // l -1.45 -1.32
                        lineToRelative(dx = -1.45f, dy = -1.32f)
                        // C 5.4 15.36 2 12.28 2 8.5
                        curveTo(
                            x1 = 5.4f,
                            y1 = 15.36f,
                            x2 = 2.0f,
                            y2 = 12.28f,
                            x3 = 2.0f,
                            y3 = 8.5f,
                        )
                        // C 2 5.42 4.42 3 7.5 3
                        curveTo(
                            x1 = 2.0f,
                            y1 = 5.42f,
                            x2 = 4.42f,
                            y2 = 3.0f,
                            x3 = 7.5f,
                            y3 = 3.0f,
                        )
                        // c 1.74 0 3.41 0.81 4.5 2.09
                        curveToRelative(
                            dx1 = 1.74f,
                            dy1 = 0.0f,
                            dx2 = 3.41f,
                            dy2 = 0.81f,
                            dx3 = 4.5f,
                            dy3 = 2.09f,
                        )
                        // C 13.09 3.81 14.76 3 16.5 3
                        curveTo(
                            x1 = 13.09f,
                            y1 = 3.81f,
                            x2 = 14.76f,
                            y2 = 3.0f,
                            x3 = 16.5f,
                            y3 = 3.0f,
                        )
                        // C 19.58 3 22 5.42 22 8.5
                        curveTo(
                            x1 = 19.58f,
                            y1 = 3.0f,
                            x2 = 22.0f,
                            y2 = 5.42f,
                            x3 = 22.0f,
                            y3 = 8.5f,
                        )
                        // c 0 3.78 -3.4 6.86 -8.55 11.54
                        curveToRelative(
                            dx1 = 0.0f,
                            dy1 = 3.78f,
                            dx2 = -3.4f,
                            dy2 = 6.86f,
                            dx3 = -8.55f,
                            dy3 = 11.54f,
                        )
                        // L 12 21.35z
                        lineTo(x = 12.0f, y = 21.35f)
                        close()
                    }
                }.build().also { _favorite = it }
            }
        @Suppress("ObjectPropertyName")
        private var _favorite: ImageVector? = null

        val FavoriteBorder: ImageVector
            get() {
                val current = _favoriteBorder
                if (current != null) return current

                return ImageVector.Builder(
                    name = "com.example.theme.AppTheme.FavoriteBorder",
                    defaultWidth = 24.0.dp,
                    defaultHeight = 24.0.dp,
                    viewportWidth = 960.0f,
                    viewportHeight = 960.0f,
                ).apply {
                    // m480 -120 -58 -52 q-101 -91 -167 -157 T150 -447.5 Q111 -500 95.5 -544 T80 -634 q0 -94 63 -157 t157 -63 q52 0 99 22 t81 62 q34 -40 81 -62 t99 -22 q94 0 157 63 t63 157 q0 46 -15.5 90 T810 -447.5 Q771 -395 705 -329 T538 -172 l-58 52Z m0 -108 q96 -86 158 -147.5 t98 -107 q36 -45.5 50 -81 t14 -70.5 q0 -60 -40 -100 t-100 -40 q-47 0 -87 26.5 T518 -680 h-76 q-15 -41 -55 -67.5 T300 -774 q-60 0 -100 40 t-40 100 q0 35 14 70.5 t50 81 q36 45.5 98 107 T480 -228Z m0 -273Z
                    path(
                        fill = SolidColor(AppColors.light.divider),
                    ) {
                        // M 480 840
                        moveTo(x = 480.0f, y = 840.0f)
                        // l -58 -52
                        lineToRelative(dx = -58.0f, dy = -52.0f)
                        // q -101 -91 -167 -157
                        quadToRelative(
                            dx1 = -101.0f,
                            dy1 = -91.0f,
                            dx2 = -167.0f,
                            dy2 = -157.0f,
                        )
                        // T 150 512.5
                        reflectiveQuadTo(
                            x1 = 150.0f,
                            y1 = 512.5f,
                        )
                        // Q 111 460 95.5 416
                        quadTo(
                            x1 = 111.0f,
                            y1 = 460.0f,
                            x2 = 95.5f,
                            y2 = 416.0f,
                        )
                        // T 80 326
                        reflectiveQuadTo(
                            x1 = 80.0f,
                            y1 = 326.0f,
                        )
                        // q 0 -94 63 -157
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -94.0f,
                            dx2 = 63.0f,
                            dy2 = -157.0f,
                        )
                        // t 157 -63
                        reflectiveQuadToRelative(
                            dx1 = 157.0f,
                            dy1 = -63.0f,
                        )
                        // q 52 0 99 22
                        quadToRelative(
                            dx1 = 52.0f,
                            dy1 = 0.0f,
                            dx2 = 99.0f,
                            dy2 = 22.0f,
                        )
                        // t 81 62
                        reflectiveQuadToRelative(
                            dx1 = 81.0f,
                            dy1 = 62.0f,
                        )
                        // q 34 -40 81 -62
                        quadToRelative(
                            dx1 = 34.0f,
                            dy1 = -40.0f,
                            dx2 = 81.0f,
                            dy2 = -62.0f,
                        )
                        // t 99 -22
                        reflectiveQuadToRelative(
                            dx1 = 99.0f,
                            dy1 = -22.0f,
                        )
                        // q 94 0 157 63
                        quadToRelative(
                            dx1 = 94.0f,
                            dy1 = 0.0f,
                            dx2 = 157.0f,
                            dy2 = 63.0f,
                        )
                        // t 63 157
                        reflectiveQuadToRelative(
                            dx1 = 63.0f,
                            dy1 = 157.0f,
                        )
                        // q 0 46 -15.5 90
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 46.0f,
                            dx2 = -15.5f,
                            dy2 = 90.0f,
                        )
                        // T 810 512.5
                        reflectiveQuadTo(
                            x1 = 810.0f,
                            y1 = 512.5f,
                        )
                        // Q 771 565 705 631
                        quadTo(
                            x1 = 771.0f,
                            y1 = 565.0f,
                            x2 = 705.0f,
                            y2 = 631.0f,
                        )
                        // T 538 788
                        reflectiveQuadTo(
                            x1 = 538.0f,
                            y1 = 788.0f,
                        )
                        // l -58 52z
                        lineToRelative(dx = -58.0f, dy = 52.0f)
                        close()
                        // m 0 -108
                        moveToRelative(dx = 0.0f, dy = -108.0f)
                        // q 96 -86 158 -147.5
                        quadToRelative(
                            dx1 = 96.0f,
                            dy1 = -86.0f,
                            dx2 = 158.0f,
                            dy2 = -147.5f,
                        )
                        // t 98 -107
                        reflectiveQuadToRelative(
                            dx1 = 98.0f,
                            dy1 = -107.0f,
                        )
                        // q 36 -45.5 50 -81
                        quadToRelative(
                            dx1 = 36.0f,
                            dy1 = -45.5f,
                            dx2 = 50.0f,
                            dy2 = -81.0f,
                        )
                        // t 14 -70.5
                        reflectiveQuadToRelative(
                            dx1 = 14.0f,
                            dy1 = -70.5f,
                        )
                        // q 0 -60 -40 -100
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = -60.0f,
                            dx2 = -40.0f,
                            dy2 = -100.0f,
                        )
                        // t -100 -40
                        reflectiveQuadToRelative(
                            dx1 = -100.0f,
                            dy1 = -40.0f,
                        )
                        // q -47 0 -87 26.5
                        quadToRelative(
                            dx1 = -47.0f,
                            dy1 = 0.0f,
                            dx2 = -87.0f,
                            dy2 = 26.5f,
                        )
                        // T 518 280
                        reflectiveQuadTo(
                            x1 = 518.0f,
                            y1 = 280.0f,
                        )
                        // l -76 0
                        lineToRelative(dx = -76.0f, dy = 0.0f)
                        // q -15 -41 -55 -67.5
                        quadToRelative(
                            dx1 = -15.0f,
                            dy1 = -41.0f,
                            dx2 = -55.0f,
                            dy2 = -67.5f,
                        )
                        // T 300 186
                        reflectiveQuadTo(
                            x1 = 300.0f,
                            y1 = 186.0f,
                        )
                        // q -60 0 -100 40
                        quadToRelative(
                            dx1 = -60.0f,
                            dy1 = 0.0f,
                            dx2 = -100.0f,
                            dy2 = 40.0f,
                        )
                        // t -40 100
                        reflectiveQuadToRelative(
                            dx1 = -40.0f,
                            dy1 = 100.0f,
                        )
                        // q 0 35 14 70.5
                        quadToRelative(
                            dx1 = 0.0f,
                            dy1 = 35.0f,
                            dx2 = 14.0f,
                            dy2 = 70.5f,
                        )
                        // t 50 81
                        reflectiveQuadToRelative(
                            dx1 = 50.0f,
                            dy1 = 81.0f,
                        )
                        // q 36 45.5 98 107
                        quadToRelative(
                            dx1 = 36.0f,
                            dy1 = 45.5f,
                            dx2 = 98.0f,
                            dy2 = 107.0f,
                        )
                        // T 480 732z
                        reflectiveQuadTo(
                            x1 = 480.0f,
                            y1 = 732.0f,
                        )
                        close()
                        // m 0 -273z
                        moveToRelative(dx = 0.0f, dy = -273.0f)
                        close()
                    }
                }.build().also { _favoriteBorder = it }
            }
        @Suppress("ObjectPropertyName")
        private var _favoriteBorder: ImageVector? = null
    }
}
