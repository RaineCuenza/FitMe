package com.fitme.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fitme.app.domain.recommendation.OutfitRecommender
import com.fitme.app.ui.viewmodel.*
import com.fitme.app.ui.wireframe.*

private data class FitTab(val route: String, val name: String, val icon: ImageVector)
private val bottomTabs = listOf(
    FitTab("home", "Home", Icons.Outlined.Home),
    FitTab("explore", "Explore", Icons.Outlined.Search),
    FitTab("builder", "Builder", Icons.Outlined.AutoAwesome),
    FitTab("favorites", "Favorites", Icons.Outlined.FavoriteBorder),
    FitTab("profile", "Profile", Icons.Outlined.PersonOutline)
)

@Composable
fun FitMeApp(settingsViewModel: SettingsViewModel) {
    val initialized by settingsViewModel.loaded.collectAsStateWithLifecycle()
    val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()
    if (!initialized) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    FitMeNavigation(settingsViewModel, prefs.onboardingCompleted, prefs.loggedIn)
}

@Composable
private fun FitMeNavigation(settingsViewModel: SettingsViewModel, onboardingCompleted: Boolean, loggedIn: Boolean) {
    val nav = rememberNavController()
    val catalog: CatalogViewModel = viewModel()
    val weather: WeatherViewModel = viewModel()
    val outfits: OutfitViewModel = viewModel()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val message by outfits.message.collectAsStateWithLifecycle()
    val weatherState by weather.weather.collectAsStateWithLifecycle()
    val wardrobe by outfits.catalog.collectAsStateWithLifecycle()
    val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()
    val look = remember(weatherState, wardrobe, prefs.style, prefs.occasion) {
        val ready = weatherState as? WeatherUiState.Ready
        val situation = ready?.let { OutfitRecommender.describe(it.weather) }
        if (situation != null) OutfitRecommender.suggest(wardrobe, situation, prefs.occasion, prefs.style).firstOrNull() else null
    }

    fun back() { nav.popBackStack() }
    fun openTab(target: String) {
        nav.navigate(target) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun showHome() {
        nav.navigate("home") { popUpTo(nav.graph.findStartDestination().id) { inclusive = true }; launchSingleTop = true }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (bottomTabs.any { it.route == route }) {
                NavigationBar(containerColor=MaterialTheme.colorScheme.surface, tonalElevation=0.dp) {
                    bottomTabs.forEach {tab ->
                        NavigationBarItem(selected=route==tab.route,onClick={openTab(tab.route)},
                            icon={Icon(tab.icon,contentDescription=null)},
                            label={Text(tab.name)}, alwaysShowLabel=true,
                            colors=NavigationBarItemDefaults.colors(
                                selectedIconColor=MaterialTheme.colorScheme.primary,
                                indicatorColor=MaterialTheme.colorScheme.primaryContainer
                            ))
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController=nav,startDestination=when {
            !loggedIn -> "login"
            onboardingCompleted -> "home"
            else -> "splash"
        },modifier=Modifier.padding(padding)) {
            composable("login") {
                FitLogin(onLogin = { _, _ ->
                    settingsViewModel.setLoggedIn(true)
                    if (onboardingCompleted) {
                        showHome()
                    } else {
                        nav.navigate("splash") { popUpTo("login") { inclusive = true } }
                    }
                })
            }
            composable("splash") { FitSplash { nav.navigate("onboarding") {popUpTo("splash"){inclusive=true}} } }
            composable("onboarding") {
                FitOnboarding(onNext={nav.navigate("setup_styles")},onSkip={settingsViewModel.completeOnboarding();showHome()})
            }
            composable("setup_styles") { FitStyleSetup(settingsViewModel){nav.navigate("setup_location")} }
            composable("setup_location") { FitLocationSetup(settingsViewModel,true,onConfirm={settingsViewModel.completeOnboarding();showHome()}) }
            composable("home") {
                FitHome(settingsViewModel,weather,outfits,
                    onLocation={nav.navigate("location")},onForecast={nav.navigate("forecast")},
                    onExplore={openTab("explore")},onBuilder={openTab("builder")},
                    onRecommendation={nav.navigate("recommendation")},onSaved={openTab("favorites")})
            }
            composable("forecast") { FitForecast(weather,settingsViewModel,onBack=::back) }
            composable("recommendation") {
                FitSuggestionDetails(look,onBack=::back,onSave={look?.let {
                    outfits.saveSuggestion(it) { openTab("favorites") }
                }},onBuilder={
                    outfits.clearBuilder(prefs.occasion)
                    look?.items?.forEach { outfits.select(it) }
                    openTab("builder")
                })
            }
            composable("explore") { FitExplore(catalog,onClothing={id->nav.navigate("clothing/$id")}) }
            composable("clothing/{id}",arguments=listOf(navArgument("id"){type=NavType.LongType})) {entry->
                FitClothingDetails(itemId=entry.arguments?.getLong("id")?:-1L,vm=catalog,onBack=::back,onAdd={item->
                    outfits.select(item);openTab("builder")
                })
            }
            composable("builder") {FitBuilder(outfits,onSelect={category->nav.navigate("select/$category")},onSaved={nav.navigate("outfit/$it")})}
            composable("builder/edit/{id}",arguments=listOf(navArgument("id"){type=NavType.LongType})){entry->
                val id=entry.arguments?.getLong("id")?:-1L
                LaunchedEffect(id){outfits.startEdit(id)}
                FitBuilder(outfits,onSelect={category->nav.navigate("select/$category")},onSaved={nav.navigate("outfit/$it")})
            }
            composable("select/{category}",arguments=listOf(navArgument("category"){type=NavType.StringType})){entry->
                FitSelectClothing(outfits,entry.arguments?.getString("category")?:"Tops",onBack=::back)
            }
            composable("favorites") {FitFavorites(outfits,onOpen={nav.navigate("outfit/$it")},onEdit={nav.navigate("builder/edit/$it")},
                onBuild={outfits.clearBuilder(prefs.occasion);openTab("builder")})}
            composable("outfit/{id}",arguments=listOf(navArgument("id"){type=NavType.LongType})){entry->
                val id=entry.arguments?.getLong("id")?:-1L
                FitOutfitDetails(outfits,id,onBack=::back,onEdit={nav.navigate("builder/edit/$it")},onPlan={nav.navigate("planner/$it")})
            }
            composable("planner/{id}",arguments=listOf(navArgument("id"){type=NavType.LongType})){entry->
                FitPlanner(outfits,entry.arguments?.getLong("id")?:-1L,onBack=::back,onChooseOutfit={nav.navigate("choose_plan")})
            }
            composable("choose_plan") {FitPlanChooser(outfits,onBack=::back,onSelect={nav.navigate("planner/$it")})}
            composable("planned") {FitPlannedOutfits(outfits,onBack=::back,onOpen={nav.navigate("outfit/$it")},onBuild={nav.navigate("choose_plan")})}
            composable("profile") {FitProfile(settingsViewModel,onLocation={nav.navigate("location")},onStyles={nav.navigate("styles")},
                onOccasions={nav.navigate("occasions")},onPlanned={nav.navigate("planned")},onReplay={nav.navigate("onboarding")},
                onLogout={
                    settingsViewModel.setLoggedIn(false)
                    bottomTabs.forEach { nav.clearBackStack(it.route) }
                    nav.navigate("login") {
                        popUpTo(nav.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                })}
            composable("location") {FitLocationSetup(settingsViewModel,false,onConfirm=::back,onBack=::back)}
            composable("styles") {FitEditStyles(settingsViewModel,onBack=::back)}
            composable("occasions") {FitEditOccasions(settingsViewModel,onBack=::back)}
        }
    }
    if (message != null) {
        AlertDialog(onDismissRequest=outfits::dismissMessage,title={Text("FitMe")},
            text={Text(message.orEmpty())},confirmButton={TextButton(onClick=outfits::dismissMessage){Text("OK")}})
    }
}
