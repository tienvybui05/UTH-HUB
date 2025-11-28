package com.example.uth_hub.feature.post.ui.component

import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

// HÀM TIME AGO DÙNG CHUNG CHO COMMENT + POST
fun formatTimeAgo(timestamp: Timestamp?, nowMillis: Long): String {
    if (timestamp == null) return ""

    val time = timestamp.toDate().time
    val diffRaw = nowMillis - time

    // nếu giờ máy bị chậm hơn server → diffRaw âm
    val diff = if (diffRaw < 0) 0L else diffRaw

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút"
        minutes < 60 * 24 -> "${minutes / 60} giờ"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)} ngày"
        minutes < 60 * 24 * 30 -> "${minutes / (60 * 24 * 7)} tuần"
        minutes < 60 * 24 * 365 -> "${minutes / (60 * 24 * 30)} tháng"
        else -> "${minutes / (60 * 24 * 365)} năm"
    }
}
