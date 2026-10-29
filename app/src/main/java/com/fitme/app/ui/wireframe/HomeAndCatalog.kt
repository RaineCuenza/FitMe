package com.fitme.app.ui.wireframe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.domain.recommendation.OutfitRecommender
import com.fitme.app.domain.recommendation.OutfitSuggestion
import com.fitme.app.ui.viewmodel.CatalogViewModel
import com.fitme.app.ui.viewmodel.OutfitViewModel
import com.fitme.app.ui.viewmodel.SettingsViewModel
import com.fitme.app.ui.viewmodel.WeatherUiState
import com.fitme.app.ui.viewmodel.WeatherViewModel
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable fun FitHome(
    settings: SettingsViewModel, weatherVM: WeatherViewModel, outfits: OutfitViewModel,
    onLocation: () -> Unit, onForecast: () -> Unit, onExplore: () -> Unit,
    onBuilder: () -> Unit, onRecommendation: () -> Unit, onSaved: () -> Unit
) {
    val prefs by settings.preferences.collectAsStateWithLifecycle()
    val weather by weatherVM.weather.collectAsStateWithLifecycle()
    val catalog by outfits.catalog.collectAsStateWithLifecycle()
    val situation = remember(weather) { (weather as? WeatherUiState.Ready)?.let { OutfitRecommender.describe(it.weather) } }
    val suggestions = remember(catalog, situation, prefs.occasion, prefs.style) {
        if(situation != null) OutfitRecommender.suggest(catalog, situation, prefs.occasion, prefs.style) else emptyList()
    }
    val suggestion = suggestions.firstOrNull()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=18.dp, vertical=19.dp),
        verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween) {
            Column { FitLogo(compact=true); Spacer(Modifier.height(6.dp)); Text("A little more you, every day.", fontSize=13.sp, fontWeight=FontWeight.Medium) }
            Surface(onClick=onLocation, shape=CircleShape, color=MaterialTheme.colorScheme.surface,
                border=BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                Row(Modifier.padding(horizontal=11.dp, vertical=9.dp), verticalAlignment=Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, null,modifier=Modifier.size(15.dp)); Spacer(Modifier.width(3.dp))
                    Text(prefs.city.substringBefore(",").take(17), fontSize=11.sp, fontWeight=FontWeight.SemiBold)
                    Icon(Icons.Outlined.KeyboardArrowDown,null,modifier=Modifier.size(17.dp))
                }
            }
        }
        Surface(onClick=onForecast, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(18.dp), color=MaterialTheme.colorScheme.primaryContainer) {
            Row(Modifier.padding(14.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.surface, CircleShape), contentAlignment=Alignment.Center) {
                    Icon(if(situation?.rainExpected==true) Icons.Outlined.Grain else Icons.Outlined.Cloud, null,
                        tint=MaterialTheme.colorScheme.primary, modifier=Modifier.size(27.dp))
                }
                Column(Modifier.weight(1f)) {
                    when(val w=weather) {
                        is WeatherUiState.Ready -> {
                            Text(String.format(Locale.US,"%.0f°",w.weather.current?.temperature ?:0.0),fontFamily=FontFamily.Serif,fontSize=29.sp)
                            Text(situation?.label ?: "Current conditions",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        WeatherUiState.Loading -> { Text("Checking weather…",fontSize=13.sp); LinearProgressIndicator(Modifier.width(92.dp)) }
                        is WeatherUiState.Error -> { Text("Weather unavailable",fontSize=13.sp); TextButton(onClick=weatherVM::refresh){Text("Retry")}}
                    }
                }
                Column(horizontalAlignment=Alignment.End) {
                    val daily=(weather as? WeatherUiState.Ready)?.weather?.daily
                    if(daily!=null) {
                        Text("H ${daily.maximum.firstOrNull()?.toInt() ?: "–"}° · L ${daily.minimum.firstOrNull()?.toInt() ?: "–"}°",fontSize=11.sp)
                        Text("${daily.rainProbability.firstOrNull() ?: 0}% rain",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Outlined.ArrowForward,null,modifier=Modifier.size(18.dp))
            }
        }
        FitSection("Today's edit", "Why this look?") { onRecommendation() }
        Box(Modifier.fillMaxWidth().height(358.dp).clip(RoundedCornerShape(23.dp)).background(MaterialTheme.colorScheme.secondaryContainer)) {
            if(suggestion!=null) {
                FitLookComposition(suggestion.items,Modifier.fillMaxSize())
                // Editorial photograph from the supplied Figma export. Offline artwork stays beneath it.
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1648066380643-675d30c3cd17?auto=format&fit=crop&w=900&q=85",
                    contentDescription = "Editorial outfit inspiration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Surface(Modifier.align(Alignment.TopStart).padding(14.dp),shape=CircleShape,color=MaterialTheme.colorScheme.surface) {
                    Text("Made for ${situation?.temperature?.toInt() ?: "–"}°C",Modifier.padding(horizontal=13.dp, vertical=8.dp),fontSize=11.sp)
                }
                Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(MaterialTheme.colorScheme.surface.copy(alpha=.96f),RoundedCornerShape(20.dp)).padding(16.dp),
                    verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Text(prefs.style.uppercase(), fontSize=10.sp,letterSpacing=1.sp,color=MaterialTheme.colorScheme.primary)
                    Text("Your weather-ready look",fontFamily=FontFamily.Serif,fontSize=25.sp)
                    Text(suggestion.items.joinToString(" · "){it.name},maxLines=1,overflow=TextOverflow.Ellipsis,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    FitPrimaryButton("View outfit",Modifier.fillMaxWidth()) { onRecommendation() }
                }
            } else {
                Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Checkroom,null,Modifier.size(46.dp),tint=MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(10.dp))
                    Text("Your look is on its way",fontFamily=FontFamily.Serif,fontSize=23.sp)
                    Text("Add outfits anytime, or browse the clothing collection while weather loads.",textAlign=TextAlign.Center,fontSize=12.sp)
                    Spacer(Modifier.height(12.dp))
                    FitPrimaryButton("Explore clothing",onClick=onExplore)
                }
            }
        }
        if(suggestion!=null) {
            Surface(shape=RoundedCornerShape(16.dp),color=MaterialTheme.colorScheme.primaryContainer) {
                Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    Icon(Icons.Outlined.AutoAwesome,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(20.dp))
                    Column { Text("Why it works",fontWeight=FontWeight.Bold,fontSize=12.sp); Text(suggestion.reason,fontSize=11.sp,lineHeight=17.sp) }
                }
            }
        }
        FitSection("Explore your way")
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            listOf("Tops","Layers","Shoes","More").forEachIndexed { index,name ->
                Column(Modifier.weight(1f).clickable(onClick=onExplore),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(55.dp).background(MaterialTheme.colorScheme.surface, CircleShape),contentAlignment=Alignment.Center) {
                        Icon(listOf(Icons.Outlined.Checkroom,Icons.Outlined.Layers,Icons.Outlined.Hiking,Icons.Outlined.Apps)[index],null,tint=MaterialTheme.colorScheme.primary)
                    }
                    Text(name,fontSize=11.sp)
                }
            }
        }
        Surface(onClick=onBuilder,shape=RoundedCornerShape(18.dp),color=MaterialTheme.colorScheme.secondaryContainer) {
            Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.AutoAwesome,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(30.dp))
                Column(Modifier.weight(1f)) { Text("Your wardrobe, your rules",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Create an outfit",fontWeight=FontWeight.Bold,fontSize=15.sp) }
                Icon(Icons.Outlined.ArrowForward,null)
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable fun FitForecast(weatherVM: WeatherViewModel, settingsVM: SettingsViewModel, onBack:()->Unit) {
    val state by weatherVM.weather.collectAsStateWithLifecycle()
    val prefs by settingsVM.preferences.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
        FitPageHeader("Weather",prefs.city,onBack)
        when(val current=state) {
            WeatherUiState.Loading -> Box(Modifier.fillMaxWidth().height(160.dp),contentAlignment=Alignment.Center){CircularProgressIndicator()}
            is WeatherUiState.Error -> FitEmpty("Weather unavailable",current.description,{weatherVM.refresh()},"Try again")
            is WeatherUiState.Ready -> {
                val c=current.weather.current
                val d=current.weather.daily
                val sit=OutfitRecommender.describe(current.weather)
                Surface(shape=RoundedCornerShape(24.dp),color=MaterialTheme.colorScheme.primaryContainer) {
                    Row(Modifier.fillMaxWidth().padding(25.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        Column {
                            Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),fontSize=12.sp)
                            Text("${c?.temperature?.toInt() ?: "–"}°",fontFamily=FontFamily.Serif,fontSize=66.sp)
                            Text(sit?.label ?: "Current forecast",fontSize=14.sp)
                        }
                        Icon(if(sit?.rainExpected==true) Icons.Outlined.Grain else Icons.Outlined.WbCloudy,null,
                            tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(72.dp))
                    }
                }
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    listOf("Humidity" to "${c?.humidity ?: 0}%","Rain" to "${d?.rainProbability?.firstOrNull() ?: 0}%","Precip." to "${c?.precipitation ?: 0.0} mm").forEach { (label,value) ->
                        Surface(Modifier.weight(1f),color=MaterialTheme.colorScheme.surface,shape=RoundedCornerShape(14.dp)) {
                            Column(Modifier.padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(label,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(5.dp));Text(value,fontWeight=FontWeight.Bold,fontSize=14.sp)}
                        }
                    }
                }
                FitSection("7-day forecast")
                d?.let { daily -> daily.time.take(7).forEachIndexed {index,date ->
                    Row(Modifier.fillMaxWidth().padding(vertical=11.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        Text(if(index==0) "Today" else date,fontSize=13.sp)
                        Icon(if((daily.rainProbability.getOrNull(index) ?:0)>50) Icons.Outlined.Grain else Icons.Outlined.WbSunny,null,tint=MaterialTheme.colorScheme.primary)
                        Text("${daily.maximum.getOrNull(index)?.toInt() ?: "–"}°",fontWeight=FontWeight.Bold)
                        Text("${daily.minimum.getOrNull(index)?.toInt() ?: "–"}°",color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
                } }
                Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=RoundedCornerShape(17.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.AutoAwesome,null,tint=MaterialTheme.colorScheme.primary)
                        Column {Text("Styling advice",fontSize=11.sp,color=MaterialTheme.colorScheme.primary)
                            Text(if(sit?.rainExpected==true) "Keep a rain-ready layer close" else "Keep it light and layered",fontFamily=FontFamily.Serif,fontSize=23.sp)
                            Text(if(sit?.rainExpected==true) "Choose comfortable closed shoes and a water-resistant layer." else "Choose breathable pieces that suit the forecast.",fontSize=12.sp) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun FitExplore(vm: CatalogViewModel, onClothing: (Long)->Unit) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    val categories = listOf("All","Tops","Bottoms","Dresses","Outerwear","Footwear","Accessories")
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
            FitPageHeader("Explore",trailing={ IconButton(onClick={showFilters=true}) {Icon(Icons.Outlined.Tune,"Filters")} })
            OutlinedTextField(state.filters.search,vm::setSearch,modifier=Modifier.fillMaxWidth(),singleLine=true,
                placeholder={Text("Search clothing")},leadingIcon={Icon(Icons.Outlined.Search,null)},
                shape=RoundedCornerShape(16.dp))
        }
        LazyRow(contentPadding=PaddingValues(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            items(categories){ c -> FitChip(c,state.filters.category==c){vm.setCategory(c)} }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=8.dp),horizontalArrangement=Arrangement.SpaceBetween) {
            Text("${state.items.size} pieces",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Curated for you  ⌄",fontSize=12.sp,color=MaterialTheme.colorScheme.primary)
        }
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){CircularProgressIndicator()}
            state.error!=null -> FitEmpty("Catalog unavailable",state.error.orEmpty(),onAction=vm::loadCatalog,action="Retry")
            state.items.isEmpty() -> FitEmpty("No pieces found","Try a different keyword or clear your filters.",onAction=vm::clearFilters,action="Clear filters")
            else -> LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.fillMaxSize(),
                contentPadding=PaddingValues(start=18.dp,end=18.dp,bottom=20.dp,top=6.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
                items(state.items,key={it.id}) { item -> FitClothingCard(item,onClick={onClothing(item.id)}) }
            }
        }
    }
    if(showFilters) ModalBottomSheet(onDismissRequest={showFilters=false},containerColor=MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().padding(horizontal=22.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                Text("Refine pieces",fontFamily=FontFamily.Serif,fontSize=27.sp)
                IconButton(onClick={showFilters=false}){Icon(Icons.Outlined.Close,"Close")}
            }
            @Composable fun filterRow(title:String,values:List<String>,selected:String?,choose:(String?)->Unit) {
                Text(title,fontSize=13.sp,fontWeight=FontWeight.SemiBold)
                LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    items(values){ option->FitChip(option,selected==option){choose(if(option==selected)null else option)} }
                }
            }
            filterRow("Category",categories.drop(1),state.filters.category.takeUnless{it=="All"}){vm.setCategory(it?:"All")}
            filterRow("Color",state.colors,state.filters.color,vm::setColor)
            filterRow("Style",state.styles,state.filters.style,vm::setStyle)
            filterRow("Occasion",state.occasions,state.filters.occasion,vm::setOccasion)
            Row(Modifier.fillMaxWidth().padding(vertical=12.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                TextButton(onClick=vm::clearFilters,modifier=Modifier.weight(.38f)){Text("Clear all")}
                FitPrimaryButton("Apply filters",Modifier.weight(.62f)){showFilters=false}
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable fun FitClothingDetails(itemId: Long, vm: CatalogViewModel, onBack:()->Unit, onAdd:(ClothingItemEntity)->Unit) {
    val itemFlow=remember(itemId){vm.observeItem(itemId)}
    val clothing by itemFlow.collectAsStateWithLifecycle(initialValue=null)
    val item=clothing
    if(item==null){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text("Loading clothing details…")};return}
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(0.dp)) {
            Box(Modifier.fillMaxWidth().height(350.dp).background(MaterialTheme.colorScheme.secondaryContainer)) {
                FitItemArt(item,Modifier.fillMaxSize())
                Surface(onClick=onBack,modifier=Modifier.align(Alignment.TopStart).padding(19.dp),shape=CircleShape,color=MaterialTheme.colorScheme.surface) {
                    Icon(Icons.Outlined.ArrowBackIosNew,"Back",Modifier.padding(15.dp))
                }
            }
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Text(item.category.uppercase(),fontSize=11.sp,letterSpacing=1.2.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)
                Text(item.name,fontFamily=FontFamily.Serif,fontSize=32.sp)
                Text("${item.color} · ${item.style}",fontSize=13.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    items(item.weatherConditions+item.occasions.take(2)) { tag -> SuggestionChip(tag) }
                }
                Text("An easy-to-style piece from FitMe's curated clothing collection. Pair it with other essentials to create a look.",fontSize=13.sp,lineHeight=20.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider()
                listOf("Color" to item.color,"Style" to item.style,"Best for" to item.weatherConditions.joinToString(", "),"Occasions" to item.occasions.joinToString(", ")).forEach { (name,value)->
                    Row(Modifier.fillMaxWidth().padding(vertical=6.dp),horizontalArrangement=Arrangement.SpaceBetween){
                        Text(name,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value,fontSize=12.sp,fontWeight=FontWeight.Medium,modifier=Modifier.widthIn(max=220.dp),textAlign=TextAlign.End)
                    }
                    HorizontalDivider()
                }
            }
        }
        Surface(tonalElevation=2.dp,shadowElevation=3.dp) {FitPrimaryButton("Add to outfit",Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=11.dp)){onAdd(item)} }
    }
}

@Composable private fun SuggestionChip(text:String) {
    Surface(shape=CircleShape,color=MaterialTheme.colorScheme.primaryContainer) {
        Text(text,Modifier.padding(horizontal=11.dp,vertical=7.dp),fontSize=11.sp,color=MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable fun FitSuggestionDetails(suggestion:OutfitSuggestion?, onBack:()->Unit,onSave:()->Unit,onBuilder:()->Unit) {
    if(suggestion==null){FitEmpty("Weather recommendation unavailable","Return to Home when the weather has loaded.");return}
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
            FitPageHeader("Recommended look","Weather-ready outfit",onBack)
            FitLookComposition(suggestion.items,Modifier.fillMaxWidth().height(320.dp))
            Text("YOUR DAILY EDIT",fontSize=11.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)
            Text("Ready for the day",fontFamily=FontFamily.Serif,fontSize=31.sp)
            Text(suggestion.reason,fontSize=13.sp,lineHeight=19.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(13.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.AutoAwesome,null, tint=MaterialTheme.colorScheme.primary)
                    Text("Recommended for ${suggestion.occasion}",fontSize=12.sp)
                }
            }
            FitSection("${suggestion.items.size} pieces")
            suggestion.items.forEach { item -> FitPieceRow(item,onClick=onBuilder) }
        }
        Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick=onBuilder,modifier=Modifier.weight(1f)){Text("Customize")}
            FitPrimaryButton("Save outfit",Modifier.weight(1f),onClick=onSave)
        }
    }
}

@Composable fun FitPieceRow(item: ClothingItemEntity,onClick:()->Unit, end:String="") {
    Surface(onClick=onClick,shape=RoundedCornerShape(14.dp),color=MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)) {
            FitItemArt(item,Modifier.size(56.dp))
            Column(Modifier.weight(1f)) {Text(item.category.uppercase(),fontSize=10.sp,color=MaterialTheme.colorScheme.primary,letterSpacing=1.sp)
                Text(item.name,fontSize=12.sp,fontWeight=FontWeight.SemiBold)}
            if(end.isNotBlank())Text(end,fontSize=12.sp,color=MaterialTheme.colorScheme.primary)
        }
    }
}
