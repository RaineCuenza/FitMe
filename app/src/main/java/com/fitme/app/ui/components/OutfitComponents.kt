package com.fitme.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitme.app.data.local.ClothingItemEntity

@Composable
fun ClothingThumbnail(item: ClothingItemEntity, modifier: Modifier = Modifier, height: Int = 105) {
    Image(
        painter = painterResource(ClothingArtwork.forReference(item.imageReference)),
        contentDescription = item.name,
        modifier = modifier.height(height.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun OutfitPreview(clothes: List<ClothingItemEntity>, modifier: Modifier = Modifier) {
    if (clothes.isEmpty()) {
        Box(modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
            Text("Choose clothing to preview your outfit", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(clothes.sortedBy { listOf("Tops", "Dresses", "Bottoms", "Outerwear", "Footwear", "Accessories").indexOf(it.category) }, key = { it.id }) { item ->
            Column(modifier = Modifier.width(102.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ClothingThumbnail(item, Modifier.fillMaxWidth(), 110)
                Text(item.name, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun TitleSection(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
