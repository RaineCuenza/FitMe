package com.fitme.app.ui.wireframe

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.data.remote.CityLocation
import com.fitme.app.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

val FitStyles = listOf("Casual", "Minimalist", "Streetwear", "Formal", "Sporty", "Smart Casual")
val FitOccasions = listOf("School", "Casual Outing", "Work", "Formal Event", "Party", "Sports", "Special Events")

@Composable fun FitSplash(onFinished: () -> Unit) {
    LaunchedEffect(Unit) { delay(1100); onFinished() }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(112.dp).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .6f), RoundedCornerShape(45)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
            }
            FitLogo()
            Text("Your style, your weather.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LinearProgressIndicator(Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp).width(100.dp), color = MaterialTheme.colorScheme.primary)
    }
}

@Composable fun FitLogin(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(bottomStart = 92.dp, bottomEnd = 92.dp)
                )
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(86.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 5.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "FitMe",
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Welcome back",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Sign in to continue styling your day.",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            androidx.compose.ui.text.input.VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    Text(
                        "Forgot password?",
                        modifier = Modifier.align(Alignment.End),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (error != null) {
                        Text(
                            error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = {
                            val emailText = email.trim()
                            when {
                                emailText.isBlank() || password.isBlank() ->
                                    error = "Email and password are required."
                                !emailText.contains("@") ->
                                    error = "Enter a valid email address."
                                else -> onLogin(emailText, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Log in", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New to FitMe? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(
                    onClick = {
                        email = "demo@fitme.app"
                        password = "fitme123"
                        onLogin("demo@fitme.app", "fitme123")
                    }
                ) {
                    Text("Try demo login")
                }
            }
        }
    }
}

private data class OnboardingPage(val title: String, val subtitle: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val pages = listOf(
    OnboardingPage("Style your everyday.", "Discover thoughtful outfit ideas made for your life and personal style.", Icons.Outlined.Checkroom),
    OnboardingPage("Dress for the weather.", "Get practical outfit recommendations based on today's local forecast.", Icons.Outlined.WbSunny),
    OnboardingPage("Make every outfit yours.", "Mix, match, save favorites, and plan looks for the days ahead.", Icons.Outlined.AutoAwesome)
)

@Composable fun FitOnboarding(onNext: () -> Unit, onSkip: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    val item = pages[page]
    Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            FitLogo(compact = true)
            TextButton(onClick = onSkip) { Text("Skip") }
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(top = 12.dp).background(if(page==1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            RoundedCornerShape(topStart = 175.dp, topEnd = 175.dp, bottomStart = 18.dp, bottomEnd = 18.dp)), contentAlignment = Alignment.Center) {
            Box(Modifier.size(245.dp, 285.dp).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=.18f), RoundedCornerShape(topStart = 130.dp, topEnd = 130.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(53.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    Box(Modifier.size(125.dp, 145.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=.85f), RoundedCornerShape(topStart = 55.dp, topEnd = 55.dp, bottomStart = 18.dp, bottomEnd = 18.dp)), contentAlignment = Alignment.Center) {
                        Icon(item.icon, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(54.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                        Box(Modifier.size(34.dp, 73.dp).background(MaterialTheme.colorScheme.primary))
                        Box(Modifier.size(34.dp, 73.dp).background(MaterialTheme.colorScheme.primary))
                    }
                }
            }
            Surface(Modifier.align(Alignment.TopEnd).padding(28.dp).size(53.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary) }
            }
        }
        Text("0${page+1} / 03", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
        Text(item.title, fontFamily = FontFamily.Serif, fontSize = 35.sp, lineHeight = 38.sp)
        Text(item.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, lineHeight = 22.sp)
        Row(Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                pages.indices.forEach { index -> Box(Modifier.width(if(index==page) 24.dp else 8.dp).height(8.dp).background(
                    if(index==page) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape)) }
            }
            FitPrimaryButton(if(page==2) "Get started" else "Next") { if (page == 2) onNext() else page++ }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun FitStyleSetup(settings: SettingsViewModel, onContinue: () -> Unit) {
    val preferences by settings.preferences.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf(preferences.styles.split('|').filter { it.isNotBlank() }.toSet()) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        FitPageHeader("Define your style", "Step 1 of 2")
        Spacer(Modifier.height(20.dp))
        Text("What feels most like you?", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, fontSize = 30.sp)
        Spacer(Modifier.height(9.dp))
        Text("Choose as many as you like. You can update these anytime.", modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, fontSize = 13.sp)
        Spacer(Modifier.height(23.dp))
        FitStyles.chunked(2).forEachIndexed { rowIndex, row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEachIndexed { columnIndex, style ->
                    val index = rowIndex * 2 + columnIndex
                    val checked = style in selected
                    Surface(
                        modifier = Modifier.weight(1f).height(126.dp).clickable {
                            selected = if(checked) selected-style else selected+style
                        },
                        color = if(checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if(checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0${index+1}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if(checked) Icon(Icons.Outlined.CheckCircle, null, tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(19.dp))
                            }
                            Icon(if(index%2==0) Icons.Outlined.Checkroom else Icons.Outlined.AutoAwesome, null, tint=MaterialTheme.colorScheme.primary)
                            Text(style, fontSize=14.sp, fontWeight=FontWeight.SemiBold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.weight(1f))
        FitPrimaryButton("Continue", Modifier.fillMaxWidth(), enabled = selected.isNotEmpty()) {
            settings.setStyles(selected); onContinue()
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable fun FitLocationSetup(settings: SettingsViewModel, onboarding: Boolean, onConfirm: () -> Unit, onBack: (() -> Unit)? = null) {
    val prefs by settings.preferences.collectAsStateWithLifecycle()
    val matches by settings.cities.collectAsStateWithLifecycle()
    val error by settings.locationError.collectAsStateWithLifecycle()
    val searching by settings.searching.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<CityLocation?>(null) }
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        FitPageHeader("Set your location", if (onboarding) "Step 2 of 2" else "Weather location", onBack)
        Spacer(Modifier.height(19.dp))
        Box(Modifier.align(Alignment.CenterHorizontally).size(68.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Place, null, tint=MaterialTheme.colorScheme.primary, modifier=Modifier.size(32.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("Weather, where you are", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontFamily=FontFamily.Serif,fontSize=30.sp)
        Text("Search for a city to receive accurate outfit recommendations. GPS is optional.",
            Modifier.fillMaxWidth().padding(vertical=14.dp), color=MaterialTheme.colorScheme.onSurfaceVariant, textAlign=TextAlign.Center, fontSize=13.sp)
        OutlinedTextField(query, onValueChange = { query=it }, modifier=Modifier.fillMaxWidth(), singleLine=true,
            placeholder={ Text("Search city") }, leadingIcon={ Icon(Icons.Outlined.Search,null) }, shape=RoundedCornerShape(15.dp),
            trailingIcon={ if(query.isNotBlank()) TextButton(onClick={settings.searchCities(query)}) { Text("Find") } })
        Spacer(Modifier.height(18.dp))
        Text(if(query.isBlank()) "CURRENT LOCATION" else "SEARCH RESULTS", fontSize=10.sp, letterSpacing=1.2.sp, color=MaterialTheme.colorScheme.primary)
        if (query.isBlank()) {
            ListItem(headlineContent={Text(prefs.city)}, leadingContent={Icon(Icons.Outlined.Place,null)}, trailingContent={Icon(Icons.Outlined.Check,null,tint=MaterialTheme.colorScheme.primary)},
                modifier=Modifier.clickable { selected=null })
        }
        if (searching) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (error!=null) Text(error.orEmpty(), modifier=Modifier.padding(vertical=10.dp),color=MaterialTheme.colorScheme.error,fontSize=12.sp)
        LazyColumn(Modifier.weight(1f)) {
            items(matches, key={it.displayName+it.latitude}) { city ->
                ListItem(headlineContent={Text(city.displayName)}, leadingContent={Icon(Icons.Outlined.Place,null)},
                    trailingContent={if(selected?.displayName==city.displayName) Icon(Icons.Outlined.Check,null) },
                    modifier=Modifier.clickable { selected=city })
                HorizontalDivider()
            }
        }
        if(query.isBlank()) Spacer(Modifier.weight(1f))
        Text("Selected city", modifier=Modifier.fillMaxWidth(), fontSize=11.sp,color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=TextAlign.Center)
        Text(selected?.displayName ?: prefs.city, Modifier.fillMaxWidth(), textAlign=TextAlign.Center,fontWeight=FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        FitPrimaryButton("Confirm location", Modifier.fillMaxWidth()) {
            selected?.let(settings::chooseCity); onConfirm()
        }
        Spacer(Modifier.height(20.dp))
    }
}
