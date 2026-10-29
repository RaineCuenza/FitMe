package com.fitme.app.ui.wireframe

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.local.OutfitWithItems
import com.fitme.app.ui.viewmodel.OutfitViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val slots = listOf("Tops", "Bottoms", "Dresses", "Outerwear", "Footwear", "Accessories")

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun FitBuilder(vm: OutfitViewModel,onSelect:(String)->Unit,onSaved:(Long)->Unit) {
    val state by vm.builder.collectAsStateWithLifecycle()
    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val chosen=state.selections.values.mapNotNull { id->catalog.firstOrNull {it.id==id} }
    var saveSheet by remember {mutableStateOf(false)}
    var favorite by remember {mutableStateOf(true)}
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
            FitPageHeader(if(state.editingId==null)"Outfit builder" else "Edit outfit",trailing={TextButton(onClick={vm.clearBuilder()}){Text("Clear")}})
            Surface(color=MaterialTheme.colorScheme.secondaryContainer,shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(13.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                        Text("LIVE PREVIEW",fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,letterSpacing=1.2.sp)
                        Text("${chosen.size} pieces",fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    FitLookComposition(chosen,Modifier.fillMaxWidth().height(245.dp))
                }
            }
            FitSection("Your pieces")
            if(chosen.isEmpty()) Text("Build a look one piece at a time.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            slots.forEach {category->
                // A dress is a full-body piece. Don't require separate tops and bottoms.
                if((category=="Tops" || category=="Bottoms") && state.selections.containsKey("Dresses")) return@forEach
                if(category=="Dresses" && (state.selections.containsKey("Tops") || state.selections.containsKey("Bottoms"))) return@forEach
                val selected=catalog.firstOrNull { it.id==state.selections[category] }
                Surface(onClick={onSelect(category)},shape=RoundedCornerShape(16.dp),color=MaterialTheme.colorScheme.surface,
                    border=BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant)) {
                    Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                        if(selected!=null) FitItemArt(selected,Modifier.size(54.dp))
                        else Box(Modifier.size(54.dp).background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){Icon(Icons.Outlined.Add,null,tint=MaterialTheme.colorScheme.primary)}
                        Column(Modifier.weight(1f)) {
                            Text(category.uppercase(),fontSize=10.sp,color=MaterialTheme.colorScheme.primary,letterSpacing=1.sp)
                            Text(selected?.name ?: "Choose ${category.lowercase()}",fontSize=12.sp,fontWeight=FontWeight.SemiBold)
                        }
                        Text(if(selected==null)"Add" else "Change",fontSize=11.sp,color=MaterialTheme.colorScheme.primary)
                    }
                }
                if(selected!=null) TextButton(onClick={vm.removeCategory(category)},modifier=Modifier.align(Alignment.End)){Text("Remove ${category.lowercase()}",fontSize=11.sp)}
            }
            state.error?.let{Text(it,color=MaterialTheme.colorScheme.error,fontSize=12.sp)}
            Spacer(Modifier.height(12.dp))
        }
        Surface(shadowElevation=3.dp) {FitPrimaryButton("Save outfit",Modifier.fillMaxWidth().padding(horizontal=18.dp, vertical=13.dp)) {saveSheet=true} }
    }
    if(saveSheet) ModalBottomSheet(onDismissRequest={saveSheet=false},containerColor=MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text("Save your look",fontFamily=FontFamily.Serif,fontSize=27.sp)
            OutlinedTextField(value=state.name,onValueChange=vm::setName,modifier=Modifier.fillMaxWidth(),label={Text("Outfit name")},singleLine=true)
            Text("Occasion",fontSize=12.sp,fontWeight=FontWeight.SemiBold)
            LazyRow(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                items(FitOccasions){occasion->FitChip(occasion,state.occasion==occasion){vm.setOccasion(occasion)}}
            }
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
                Column { Text("Save as favorite",fontSize=13.sp,fontWeight=FontWeight.SemiBold)
                    Text("Keep this look close for another day",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                Switch(favorite,onCheckedChange={favorite=it})
            }
            state.error?.let{Text(it,color=MaterialTheme.colorScheme.error,fontSize=12.sp)}
            FitPrimaryButton(if(state.busy)"Saving…" else "Save outfit",Modifier.fillMaxWidth(),enabled=!state.busy){
                vm.saveFromSheet(favorite){ id->saveSheet=false;onSaved(id) }
            }
            Spacer(Modifier.height(15.dp))
        }
    }
}

@Composable fun FitSelectClothing(vm: OutfitViewModel, category:String, onBack:()->Unit) {
    val catalog by vm.catalog.collectAsStateWithLifecycle()
    val builder by vm.builder.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val selectedId=builder.selections[category]
    val matching=catalog.filter{it.category==category && it.name.contains(query,ignoreCase=true)}
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            FitPageHeader("Choose ${category.lowercase().removeSuffix("s")}","Outfit builder",onBack)
            OutlinedTextField(value=query,onValueChange={query=it},modifier=Modifier.fillMaxWidth(),singleLine=true,
                placeholder={Text("Search ${category.lowercase()}")},leadingIcon={Icon(Icons.Outlined.Search,null)},shape=RoundedCornerShape(16.dp))
        }
        if(matching.isEmpty())FitEmpty("No pieces found","Try another search term.")
        else LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f),
            contentPadding=PaddingValues(18.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
            items(matching,key={it.id}) { item ->
                Box(Modifier.clickable { vm.select(item);onBack() }) {
                    FitClothingCard(item,onClick={if(selectedId!=item.id) vm.select(item);onBack()})
                    if(selectedId==item.id) Icon(Icons.Outlined.CheckCircle,"Selected",Modifier.align(Alignment.TopEnd).padding(8.dp),tint=MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun FitFavorites(vm: OutfitViewModel,onOpen:(Long)->Unit,onEdit:(Long)->Unit,onBuild:()->Unit) {
    val outfits by vm.allOutfits.collectAsStateWithLifecycle()
    var showAll by remember { mutableStateOf(false) }
    val displayed = if(showAll) outfits else outfits.filter{it.outfit.isFavorite}
    Column(Modifier.fillMaxSize()) {
        FitPageHeader("Favorites",if(showAll)"${displayed.size} saved looks" else "${displayed.size} favorite looks")
        Row(Modifier.fillMaxWidth().padding(horizontal=18.dp),horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            FitChip("Favorites",!showAll){showAll=false}
            FitChip("All saved looks",showAll){showAll=true}
        }
        if(displayed.isEmpty()) {
            FitEmpty(if(showAll)"Nothing saved yet" else "No favorites yet",
                "Save looks you love to find them here quickly.",onBuild,"Create an outfit")
        } else {
            LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f),contentPadding=PaddingValues(18.dp),
                horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
                items(displayed,key={it.outfit.id}) { saved ->
                    Column(Modifier.clickable{onOpen(saved.outfit.id)},verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.fillMaxWidth().aspectRatio(.86f)) {
                            FitLookComposition(saved.items,Modifier.fillMaxSize())
                            IconButton(onClick={vm.toggleFavorite(saved)},modifier=Modifier.align(Alignment.TopEnd)) {
                                Icon(if(saved.outfit.isFavorite)Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    "Toggle favorite",tint=MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(saved.outfit.name,maxLines=1,overflow=TextOverflow.Ellipsis,fontSize=13.sp,fontWeight=FontWeight.SemiBold)
                        Text(saved.outfit.occasion,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable fun FitOutfitDetails(vm: OutfitViewModel,id:Long,onBack:()->Unit,onEdit:(Long)->Unit,onPlan:(Long)->Unit) {
    val all by vm.allOutfits.collectAsStateWithLifecycle()
    val saved=all.firstOrNull {it.outfit.id==id}
    var confirmDelete by remember { mutableStateOf(false) }
    if(saved==null){FitEmpty("Outfit unavailable","This look may have been deleted.",onBack,"Go back");return}
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            FitPageHeader("Outfit details",onBack=onBack,trailing={IconButton(onClick={vm.toggleFavorite(saved)}){
                Icon(if(saved.outfit.isFavorite)Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,"Favorite",tint=MaterialTheme.colorScheme.primary)
            }})
            FitLookComposition(saved.items,Modifier.fillMaxWidth().height(320.dp))
            Text(saved.outfit.occasion.uppercase(),fontSize=11.sp,color=MaterialTheme.colorScheme.primary,letterSpacing=1.1.sp)
            Text(saved.outfit.name,fontFamily=FontFamily.Serif,fontSize=32.sp)
            Text("A curated combination using your favorite FitMe pieces.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            FitSection("${saved.items.size} pieces")
            saved.items.forEach {item->FitPieceRow(item,onClick={})}
            TextButton(onClick={confirmDelete=true}) {
                Icon(Icons.Outlined.DeleteOutline,null);Spacer(Modifier.width(6.dp));Text("Delete outfit")
            }
        }
        Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={onEdit(id)},modifier=Modifier.weight(1f)){Text("Edit outfit")}
            FitPrimaryButton("Plan outfit",Modifier.weight(1f),onClick={onPlan(id)})
        }
    }
    if(confirmDelete) AlertDialog(onDismissRequest={confirmDelete=false},title={Text("Delete outfit?")},
        text={Text("This will permanently delete this look and any associated plans.")},
        confirmButton={TextButton(onClick={vm.delete(id);confirmDelete=false;onBack()}){Text("Delete")}},
        dismissButton={TextButton(onClick={confirmDelete=false}){Text("Cancel")}})
}

@Composable fun FitPlanner(vm: OutfitViewModel,outfitId:Long,onBack:()->Unit,onChooseOutfit:()->Unit) {
    val context=LocalContext.current
    val outfits by vm.allOutfits.collectAsStateWithLifecycle()
    val plans by vm.plannedOutfits.collectAsStateWithLifecycle()
    val chosen=outfits.firstOrNull{it.outfit.id==outfitId} ?: outfits.firstOrNull()
    var day by remember { mutableStateOf(LocalDate.now()) }
    var selectedOutfit by remember(outfitId){mutableLongStateOf(outfitId)}
    val displayed=outfits.firstOrNull {it.outfit.id==selectedOutfit} ?: chosen
    val storedPlan=plans.firstOrNull { it.plannedDate==day.toString() }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            FitPageHeader("Plan an outfit",onBack=onBack)
            Surface(shape=RoundedCornerShape(22.dp),color=MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        IconButton(onClick={day=day.minusMonths(1)}){Icon(Icons.Outlined.ChevronLeft,"Previous month")}
                        Text(day.format(DateTimeFormatter.ofPattern("MMMM yyyy")),fontFamily=FontFamily.Serif,fontSize=23.sp)
                        IconButton(onClick={day=day.plusMonths(1)}){Icon(Icons.Outlined.ChevronRight,"Next month")}
                    }
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                        (0..6).forEach {offset->
                            val candidate=day.minusDays(day.dayOfWeek.value.toLong()-1).plusDays(offset.toLong())
                            Column(Modifier.weight(1f).clickable{day=candidate}.background(
                                if(day==candidate)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)).padding(vertical=11.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                                Text(candidate.dayOfWeek.name.take(1),fontSize=11.sp,
                                    color=if(day==candidate)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Text("${candidate.dayOfMonth}",fontWeight=FontWeight.SemiBold,
                                    color=if(day==candidate)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    TextButton(onClick={
                        DatePickerDialog(context,{_,y,m,d->day=LocalDate.of(y,m+1,d)},day.year,day.monthValue-1,day.dayOfMonth).show()
                    },modifier=Modifier.align(Alignment.CenterHorizontally)){Text("Choose another date")}
                }
            }
            Column {
                Text("SELECTED DATE",fontSize=10.sp,color=MaterialTheme.colorScheme.primary,letterSpacing=1.sp)
                Text(day.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),fontWeight=FontWeight.SemiBold)
            }
            FitSection("Planned look",if(outfits.size>1)"Change" else null,if(outfits.size>1) { { onChooseOutfit() } } else null)
            if(displayed!=null) {
                Surface(shape=RoundedCornerShape(17.dp),color=MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(9.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)) {
                        FitLookComposition(displayed.items,Modifier.size(110.dp,115.dp))
                        Column(Modifier.weight(1f)) {
                            Text(displayed.outfit.occasion.uppercase(),fontSize=10.sp,color=MaterialTheme.colorScheme.primary)
                            Text(displayed.outfit.name,fontFamily=FontFamily.Serif,fontSize=21.sp)
                            Text("${displayed.items.size} pieces",fontSize=11.sp)
                        }
                    }
                }
            } else FitEmpty("No saved outfits","Create a look before planning a date.")
            if(storedPlan!=null) {
                Text("You already have an outfit planned for this date.",color=MaterialTheme.colorScheme.primary,fontSize=12.sp)
                TextButton(onClick={vm.deletePlan(storedPlan.id)}){Text("Remove existing plan")}
            }
            Spacer(Modifier.height(12.dp))
        }
        FitPrimaryButton(if(storedPlan==null) "Save plan" else "Update plan",Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=12.dp),enabled=displayed!=null) {
            displayed?.let {vm.plan(it.outfit.id,day.toString())}
        }
    }
}
