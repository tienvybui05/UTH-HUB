package com.example.uth_hub.feature.admin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSkeleton(
    itemCount: Int = 3
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(itemCount) {
            SkeletonPostItem()
        }
    }
}

@Composable
fun SkeletonPostItem() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Skeleton cho header (avatar + info)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar skeleton
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color.LightGray.copy(alpha = 0.3f)
                ) {}

                // Info skeleton
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    ) {}

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    ) {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content skeleton
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.LightGray.copy(alpha = 0.3f)
                ) {}

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.LightGray.copy(alpha = 0.3f)
                ) {}

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.LightGray.copy(alpha = 0.3f)
                ) {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image skeleton (nếu có)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color.LightGray.copy(alpha = 0.3f)
            ) {}

            Spacer(modifier = Modifier.height(16.dp))

            // Actions skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(3) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    ) {}
                    if (it < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}