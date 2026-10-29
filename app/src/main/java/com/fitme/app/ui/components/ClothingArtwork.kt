package com.fitme.app.ui.components

import androidx.annotation.DrawableRes
import com.fitme.app.R

/** Compile-time resource mapping: no reflection or network calls for clothing images. */
object ClothingArtwork {
    @DrawableRes
    fun forReference(reference: String): Int = when (reference) {
        "clothing_001" -> R.drawable.clothing_001
        "clothing_002" -> R.drawable.clothing_002
        "clothing_003" -> R.drawable.clothing_003
        "clothing_004" -> R.drawable.clothing_004
        "clothing_005" -> R.drawable.clothing_005
        "clothing_006" -> R.drawable.clothing_006
        "clothing_007" -> R.drawable.clothing_007
        "clothing_008" -> R.drawable.clothing_008
        "clothing_009" -> R.drawable.clothing_009
        "clothing_010" -> R.drawable.clothing_010
        "clothing_011" -> R.drawable.clothing_011
        "clothing_012" -> R.drawable.clothing_012
        "clothing_013" -> R.drawable.clothing_013
        "clothing_014" -> R.drawable.clothing_014
        "clothing_015" -> R.drawable.clothing_015
        "clothing_016" -> R.drawable.clothing_016
        "clothing_017" -> R.drawable.clothing_017
        "clothing_018" -> R.drawable.clothing_018
        "clothing_019" -> R.drawable.clothing_019
        "clothing_020" -> R.drawable.clothing_020
        "clothing_021" -> R.drawable.clothing_021
        "clothing_022" -> R.drawable.clothing_022
        "clothing_023" -> R.drawable.clothing_023
        "clothing_024" -> R.drawable.clothing_024
        "clothing_025" -> R.drawable.clothing_025
        "clothing_026" -> R.drawable.clothing_026
        "clothing_027" -> R.drawable.clothing_027
        "clothing_028" -> R.drawable.clothing_028
        "clothing_029" -> R.drawable.clothing_029
        "clothing_030" -> R.drawable.clothing_030
        "clothing_031" -> R.drawable.clothing_031
        "clothing_032" -> R.drawable.clothing_032
        "clothing_033" -> R.drawable.clothing_033
        "clothing_034" -> R.drawable.clothing_034
        "clothing_035" -> R.drawable.clothing_035
        "clothing_036" -> R.drawable.clothing_036
        "clothing_037" -> R.drawable.clothing_037
        "clothing_038" -> R.drawable.clothing_038
        "clothing_039" -> R.drawable.clothing_039
        "clothing_040" -> R.drawable.clothing_040
        "clothing_041" -> R.drawable.clothing_041
        "clothing_042" -> R.drawable.clothing_042
        "clothing_043" -> R.drawable.clothing_043
        "clothing_044" -> R.drawable.clothing_044
        "clothing_045" -> R.drawable.clothing_045
        "clothing_046" -> R.drawable.clothing_046
        "clothing_047" -> R.drawable.clothing_047
        "clothing_048" -> R.drawable.clothing_048
        else -> R.drawable.clothing_001
    }
}
