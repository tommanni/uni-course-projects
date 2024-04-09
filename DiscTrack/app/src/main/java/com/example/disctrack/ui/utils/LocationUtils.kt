package com.example.disctrack.ui.utils

import android.location.Location
import androidx.compose.ui.geometry.Size
import com.google.android.gms.maps.model.LatLng
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Returns the distance between two points in meters as a double
 */
fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val location1 = Location("")
    location1.latitude = lat1
    location1.longitude = lon1

    val location2 = Location("")
    location2.latitude = lat2
    location2.longitude = lon2

    return location1.distanceTo(location2).toDouble()
}