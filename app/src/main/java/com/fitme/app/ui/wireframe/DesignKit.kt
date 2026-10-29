package com.fitme.app.ui.wireframe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.ui.components.ClothingArtwork

object FitColors {
    val aqua = Color(0xFF1FA7A0)
    val aquaDeep = Color(0xFF0B564F)
    val aquaSoft = Color(0xFF7CCFC8)
    val mist = Color(0xFFF4FBFA)
    val foam = Color(0xFFE3F8F5)
    val muted = Color(0xFF5F7F7B)
    val border = Color(0xFFD9F3F0)
}

@Composable fun FitLogo(compact: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier.size(if (compact) 34.dp else 44.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .55f), RoundedCornerShape(50))
                .clip(CircleShape), contentAlignment = Alignment.Center
        ) { Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(if (compact) 19.dp else 26.dp)) }
        Text("FitMe", fontFamily = FontFamily.Serif, fontSize = if (compact) 25.sp else 37.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable fun FitPageHeader(title: String, eyebrow: String? = null, onBack: (() -> Unit)? = null, trailing: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBackIosNew, "Back", modifier = Modifier.size(19.dp)) }
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            if (eyebrow != null) Text(eyebrow.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.primary)
            Text(title, fontSize = 23.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Box(Modifier.width(48.dp), contentAlignment = Alignment.CenterEnd) { trailing?.invoke() }
    }
}

@Composable fun FitPrimaryButton(label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.heightIn(min = 50.dp), enabled = enabled, shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable fun FitSection(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, fontFamily = FontFamily.Serif, fontSize = 23.sp, lineHeight = 27.sp)
        if (action != null && onAction != null) TextButton(onClick = onAction) {
            Text(action, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            Icon(Icons.Outlined.ArrowForward, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable fun FitChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(text, fontSize = 12.sp) },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ), border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selected))
}

@Composable fun FitItemArt(item: ClothingItemEntity, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        Image(painterResource(ClothingArtwork.forReference(item.imageReference)), contentDescription = item.name,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}

@Composable fun FitClothingCard(item: ClothingItemEntity, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.clickable(onClick = onClick), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.fillMaxWidth().aspectRatio(.85f)) {
            FitItemArt(item, Modifier.fillMaxSize())
            Surface(Modifier.align(Alignment.BottomEnd).padding(9.dp).size(32.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = .96f)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Add, null, modifier = Modifier.size(19.dp)) }
            }
        }
        Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("${item.color} · ${item.category}", maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable fun FitLookComposition(items: List<ClothingItemEntity>, modifier: Modifier = Modifier, emptyText: String = "Start with a top or dress") {
    Box(modifier.clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
        if (items.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Checkroom, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp))
                Text(emptyText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val shown = items.sortedBy { listOf("Tops", "Dresses", "Bottoms", "Outerwear", "Footwear", "Accessories").indexOf(it.category) }.take(4)
            Row(Modifier.fillMaxSize().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                val first = shown.first()
                FitItemArt(first, Modifier.weight(1f).fillMaxHeight(.86f))
                if (shown.size > 1) {
                    Column(Modifier.weight(.85f).fillMaxHeight(.86f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        shown.drop(1).take(2).forEach { clothing ->
                            FitItemArt(clothing, Modifier.weight(1f).fillMaxWidth())
                        }
                    }
                }
            }
            Surface(Modifier.align(Alignment.TopStart).padding(14.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = .91f)) {
                Text("${items.size} pieces", Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 11.sp)
            }
        }
    }
}

@Composable fun FitEmpty(title: String, description: String, onAction: (() -> Unit)? = null, action: String = "Explore clothing") {
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(76.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.FavoriteBorder, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        }
        Text(title, fontFamily = FontFamily.Serif, fontSize = 26.sp, textAlign = TextAlign.Center)
        Text(description, fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (onAction != null) FitPrimaryButton(action, onClick = onAction)
    }
}
