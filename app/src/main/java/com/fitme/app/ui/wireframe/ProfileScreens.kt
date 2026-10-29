package com.fitme.app.ui.wireframe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.ui.viewmodel.SettingsViewModel
import com.fitme.app.ui.viewmodel.OutfitViewModel

@Composable private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector,title:String,value:String,onClick:()->Unit) {
    Surface(onClick=onClick,color=MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(43.dp).background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center) {
                Icon(icon,null,tint=MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f)) {
                Text(title,fontSize=13.sp,fontWeight=FontWeight.SemiBold)
                Text(value,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=2)
            }
            Icon(Icons.Outlined.ChevronRight,null,Modifier.size(19.dp))
        }
    }
}

@Composable fun FitProfile(settingsVM:SettingsViewModel,onLocation:()->Unit,onStyles:()->Unit,onOccasions:()->Unit,onPlanned:()->Unit,onReplay:()->Unit,onLogout:()->Unit) {
    val prefs by settingsVM.preferences.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        FitPageHeader("Profile")
        Surface(shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.secondaryContainer) {
            Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
                Box(Modifier.size(66.dp).background(MaterialTheme.colorScheme.primary,CircleShape),contentAlignment=Alignment.Center) {
                    Icon(Icons.Outlined.PersonOutline,null,Modifier.size(33.dp),tint=MaterialTheme.colorScheme.onPrimary)
                }
                Column {Text("YOUR FITME",fontSize=10.sp,color=MaterialTheme.colorScheme.primary,letterSpacing=1.sp)
                    Text("Made for you",fontFamily=FontFamily.Serif,fontSize=25.sp)
                    Text(prefs.styles.replace('|', '·'),fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
            }
        }
        Text("PREFERENCES",fontSize=10.sp,letterSpacing=1.5.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)
        Column(verticalArrangement=Arrangement.spacedBy(5.dp)) {
            SettingRow(Icons.Outlined.Place,"Preferred location",prefs.city,onLocation)
            SettingRow(Icons.Outlined.AutoAwesome,"Personal style",prefs.styles.replace('|', ','),onStyles)
            SettingRow(Icons.Outlined.CalendarMonth,"Preferred occasions",prefs.occasion,onOccasions)
            SettingRow(Icons.Outlined.EventAvailable,"Outfit planner","Your scheduled looks",onPlanned)
        }
        Text("APPEARANCE & APP",fontSize=10.sp,letterSpacing=1.5.sp,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)
        Surface(color=MaterialTheme.colorScheme.surface,shape=RoundedCornerShape(14.dp)) {
            Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.DarkMode,null,tint=MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f)) {Text("Dark mode",fontWeight=FontWeight.SemiBold,fontSize=13.sp)
                    Text("Switch your appearance",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                Switch(checked=prefs.darkMode,onCheckedChange=settingsVM::setDarkMode)
            }
        }
        SettingRow(Icons.Outlined.Replay,"Replay introduction","See FitMe's welcome screens",onReplay)
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Log out", fontWeight = FontWeight.SemiBold)
        }
        Surface(shape=RoundedCornerShape(17.dp),color=MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.fillMaxWidth().padding(18.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                FitLogo(compact=true)
                Text("Thoughtful outfits for every forecast.",fontSize=12.sp)
                Text("Version 1.0 · Weather by Open-Meteo · Photos: Unsplash",fontSize=10.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun FitEditStyles(vm:SettingsViewModel,onBack:()->Unit) {
    val prefs by vm.preferences.collectAsStateWithLifecycle()
    var selected by remember(prefs.styles){mutableStateOf(prefs.styles.split('|').filter{it.isNotBlank()}.toSet())}
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        FitPageHeader("Your personal style",onBack=onBack)
        Text("What feels most like you?",fontFamily=FontFamily.Serif,fontSize=27.sp)
        Text("Select the styles that suit you best.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            FitStyles.forEach{style->FitChip(style,style in selected){selected=if(style in selected)selected-style else selected+style}}
        }
        Spacer(Modifier.weight(1f))
        FitPrimaryButton("Save preferences",Modifier.fillMaxWidth(),enabled=selected.isNotEmpty()){
            vm.setStyles(selected);onBack()
        }
    }
}

@Composable fun FitEditOccasions(vm:SettingsViewModel,onBack:()->Unit) {
    val prefs by vm.preferences.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        FitPageHeader("Preferred occasion",onBack=onBack)
        Text("Where are you going?",fontFamily=FontFamily.Serif,fontSize=28.sp)
        Text("FitMe uses this occasion when suggesting your daily look.",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)
        FitOccasions.forEach {occasion ->
            Surface(onClick={vm.setOccasion(occasion);onBack()},shape=RoundedCornerShape(15.dp),color=if(prefs.occasion==occasion)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) {
                Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                    Text(occasion,fontSize=14.sp,fontWeight=FontWeight.Medium)
                    if(prefs.occasion==occasion) Icon(Icons.Outlined.CheckCircle,null,tint=MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable fun FitPlannedOutfits(vm:OutfitViewModel,onBack:()->Unit,onOpen:(Long)->Unit,onBuild:()->Unit) {
    val plans by vm.plannedOutfits.collectAsStateWithLifecycle()
    val all by vm.allOutfits.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        FitPageHeader("Your outfit calendar",onBack=onBack)
        if(plans.isEmpty()) FitEmpty("Nothing planned yet","Plan one of your saved looks for an upcoming day.",onBuild,"Choose an outfit")
        else LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
            items(plans,key={it.id}) { plan ->
                val outfit=all.firstOrNull {it.outfit.id==plan.outfitId}
                Surface(shape=RoundedCornerShape(15.dp),color=MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(plan.plannedDate,fontSize=11.sp,color=MaterialTheme.colorScheme.primary)
                            Text(outfit?.outfit?.name ?: "Saved outfit",fontFamily=FontFamily.Serif,fontSize=19.sp)
                            outfit?.let { Text(it.outfit.occasion,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                        IconButton(onClick={if(outfit!=null)onOpen(outfit.outfit.id)}){Icon(Icons.Outlined.ChevronRight,"Open outfit")}
                        IconButton(onClick={vm.deletePlan(plan.id)}){Icon(Icons.Outlined.DeleteOutline,"Delete plan")}
                    }
                }
            }
        }
    }
}

@Composable fun FitPlanChooser(vm:OutfitViewModel,onBack:()->Unit,onSelect:(Long)->Unit) {
    val all by vm.allOutfits.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        FitPageHeader("Select an outfit",onBack=onBack)
        if(all.isEmpty()) FitEmpty("No saved outfits","Create an outfit before planning a date.",onBack,"Go back")
        else LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            items(all,key={it.outfit.id}){o ->
                Surface(onClick={onSelect(o.outfit.id)},shape=RoundedCornerShape(15.dp),color=MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(9.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)) {
                        FitLookComposition(o.items,Modifier.size(90.dp))
                        Column(Modifier.weight(1f)) {Text(o.outfit.name,fontFamily=FontFamily.Serif,fontSize=19.sp)
                            Text(o.outfit.occasion,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                        Icon(Icons.Outlined.ArrowForward,null)
                    }
                }
            }
        }
    }
}
