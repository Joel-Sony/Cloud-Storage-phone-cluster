package com.phonecluster.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonecluster.app.storage.PreferencesManager
import com.phonecluster.app.utils.DeviceInfoProvider
import androidx.compose.foundation.BorderStroke

private val BgDeep        = Color(0xFF020617)
private val BgCard        = Color(0xFF0D1424)
private val BgCardAlt     = Color(0xFF0A1120)
private val BorderSubtle  = Color(0xFF1E293B)
private val AccentCyan    = Color(0xFF22D3EE)
private val AccentPurple  = Color(0xFFA78BFA)
private val AccentGreen   = Color(0xFF34D399)
private val AccentAmber   = Color(0xFFFBBF24)
private val TextPrimary   = Color(0xFFF1F5F9)
private val TextSecondary = Color(0xFF94A3B8)
private val TextMuted     = Color(0xFF475569)
private val ErrorRed      = Color(0xFFEF4444)
@Composable
fun StorageModeScreen(
    onBackClick: (() -> Unit)? = null
) {

    val context = LocalContext.current
    val deviceId = PreferencesManager.getDeviceId(context) ?: -1

    android.util.Log.d(
        "FINGERPRINT",
        DeviceInfoProvider.getDeviceFingerprint(context)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {

        Column {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Spacer(modifier = Modifier.height(40.dp))
                DeviceIdCard(deviceId)

                StorageNodeCard()

                Spacer(modifier = Modifier.height(8.dp))

                NodeStatusCard()
            }
        }
    }
}

@Composable
private fun DeviceIdCard(deviceId: Int) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentCyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Storage,
                    contentDescription = null,
                    tint = AccentCyan
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Device ID",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Text(
                    "$deviceId",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = AccentCyan
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentGreen.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        AccentGreen.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    "Active",
                    fontSize = 11.sp,
                    color = AccentGreen
                )
            }
        }
    }
}

@Composable
private fun StorageNodeCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            AccentPurple.copy(alpha = 0.05f),
                            BgCard
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                "Storage Mode Active",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "This device is acting as a storage node in the PocketCluster network.",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun NodeStatusCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCardAlt),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Status",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentAmber)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    "Waiting for chunks...",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}