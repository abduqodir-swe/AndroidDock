package dev.androiddock.core.model

/** Physical pixels, bounded to two pixels in either direction. No continuous animation. */
fun pixelShiftOffset(epochMillis: Long): Pair<Int, Int> {
    val offsets = listOf(0 to 0, 2 to 0, 2 to 2, 0 to 2, -2 to 2, -2 to 0, -2 to -2, 0 to -2, 2 to -2)
    return offsets[Math.floorMod(epochMillis / 60_000, offsets.size.toLong()).toInt()]
}
