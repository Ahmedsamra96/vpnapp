package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/** Displays the supplied Ruvon artwork directly, without masks, crops or visual effects. */
@Suppress("UNUSED_PARAMETER")
@Composable
fun AppBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showContainer: Boolean = false,
    showGlow: Boolean = false,
    contentDescription: String? = "Ruvon VPN Logo"
) {
    Image(
        painter = painterResource(id = R.drawable.ic_ruvon_app),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}
