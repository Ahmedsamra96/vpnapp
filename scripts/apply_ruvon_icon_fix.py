from pathlib import Path
from PIL import Image, ImageDraw
import re
import shutil
import sys

root = Path(__file__).resolve().parents[1]
source_path = Path(sys.argv[1])
source = Image.open(source_path).convert("RGB")
if source.size != (512, 512):
    raise RuntimeError(f"Unexpected icon source size: {source.size}")

# Keep the supplied artwork itself for every in-app use. No crop, mask, overlay or recoloring.
in_app = root / "app/src/main/res/drawable-nodpi/ic_ruvon_app.webp"
shutil.copyfile(source_path, in_app)

# The normal launcher icon stays square. Only Android's roundIcon resource is circular.
sizes = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
for density, size in sizes.items():
    folder = root / f"app/src/main/res/mipmap-{density}"
    hi_size = size * 4
    image = source.resize((hi_size, hi_size), Image.Resampling.LANCZOS).convert("RGBA")
    mask = Image.new("L", (hi_size, hi_size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, hi_size - 1, hi_size - 1), fill=255)
    rounded = Image.new("RGBA", (hi_size, hi_size), (0, 0, 0, 0))
    rounded.paste(image, (0, 0), mask)
    rounded = rounded.resize((size, size), Image.Resampling.LANCZOS)
    if density in {"mdpi", "hdpi", "xhdpi"}:
        rounded.save(folder / "ic_launcher_round.png", "PNG", optimize=True)
    else:
        rounded.save(folder / "ic_launcher_round.webp", "WEBP", lossless=True, method=6)

# AppBrandLogo is now only the source artwork itself; existing call signature is preserved.
brand = root / "app/src/main/java/com/example/ui/components/AppBrandLogo.kt"
brand.write_text('''package com.example.ui.components

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
''', encoding="utf-8")

# Splash screen: display the original artwork directly.
splash = root / "app/src/main/java/com/example/ui/components/AppSplashScreen.kt"
text = splash.read_text(encoding="utf-8")
if "import androidx.compose.ui.layout.ContentScale" not in text:
    text = text.replace(
        "import androidx.compose.ui.graphics.Color\n",
        "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.layout.ContentScale\n",
    )
pattern = re.compile(
    r"\n\s*// Main Animated Shield Icon container.*?\n\s*Spacer\(modifier = Modifier\.height\(28\.dp\)\)",
    re.S,
)
replacement = '''
            // Original supplied icon: no crop, mask, border, overlay, glow or container.
            Image(
                painter = painterResource(id = R.drawable.ic_ruvon_app),
                contentDescription = "Ruvon VPN Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))'''
text, count = pattern.subn(replacement, text, count=1)
if count != 1:
    raise RuntimeError("Splash icon block not found")
splash.write_text(text, encoding="utf-8")

# Onboarding: same direct, unaltered artwork.
onboarding = root / "app/src/main/java/com/example/ui/components/OnboardingTourScreen.kt"
text = onboarding.read_text(encoding="utf-8")
if "import androidx.compose.ui.layout.ContentScale" not in text:
    text = text.replace(
        "import androidx.compose.ui.graphics.Color\n",
        "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.layout.ContentScale\n",
    )
pattern = re.compile(
    r"\n\s*// Shield Emblem with Dark Container & Cyan/Blue Glow.*?\n\s*Spacer\(modifier = Modifier\.height\(24\.dp\)\)",
    re.S,
)
replacement = '''
                // Original supplied icon: displayed directly without visual alteration.
                Image(
                    painter = painterResource(id = R.drawable.ic_ruvon_app),
                    contentDescription = "Ruvon VPN Icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(116.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))'''
text, count = pattern.subn(replacement, text, count=1)
if count != 1:
    raise RuntimeError("Onboarding icon block not found")
onboarding.write_text(text, encoding="utf-8")

print("Ruvon icon assets and in-app rendering updated successfully.")
