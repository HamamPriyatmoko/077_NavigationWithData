@file:OptIn(ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class)

package com.example.navdata

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navdata.data.SumberData.flavors
import com.example.navdata.ui.FormHalaman
import com.example.navdata.ui.HalamanDua
import com.example.navdata.ui.HalamanHome
import com.example.navdata.ui.HalamanSatu
import com.example.navdata.ui.OrderViewModel

enum class PengelolaHalaman {
    Home,
    Form,
    Rasa,
    Summary
}

@Composable
fun EsJumboAppBar(
    bisaNavigasiBack: Boolean,
    navigasiUp: () -> Unit,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (bisaNavigasiBack) {
                IconButton(onClick = navigasiUp) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(
                        R.string.back_button)
                    )
                }
            }
        }
    )
}


@Composable
fun EsJumboApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){
    Scaffold (
        topBar = {
            EsJumboAppBar(
                bisaNavigasiBack = false,
                navigasiUp = { /*TODO: implement back navigation*/ }
            )
        }
    ){ innerPadding ->
        val uiState by viewModel.stateUI.collectAsState()
        NavHost(
            navController = navController,
            startDestination = PengelolaHalaman.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = PengelolaHalaman.Home.name){
                HalamanHome (
                    onNextButtonClicked = {
                        navController.navigate(PengelolaHalaman.Form.name)
                    }
                )
            }
            composable(route = PengelolaHalaman.Form.name){
                FormHalaman(
                    onCancelButtonClicked = {navController.navigate(PengelolaHalaman.Home.name)},
                    onSubmitButtonClicked = {
                        viewModel.setContact(it)
                        navController.navigate(PengelolaHalaman.Rasa.name)
                    }
                )
            }
            composable(route = PengelolaHalaman.Rasa.name) {
                val context = LocalContext.current
                HalamanSatu(
                    pilihanRasa = flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setRasa(it)},
                    onConfirmButtonClicked = {viewModel.setJumlah(it)},
                    onNextButtonClicked = { navController.navigate(PengelolaHalaman.Form.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToHome(
                        viewModel,
                        navController
                    )},
                    modifier = Modifier
                )
            }
            composable(route = PengelolaHalaman.Summary.name) {
                HalamanDua(
                    orderUIState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToRasa(navController)},
                    )
            }
        }
    }
}
private fun cancelOrderAndNavigateToHome(
    viewModel: OrderViewModel,
    navController: NavHostController
){
    viewModel.resetOrder()
    navController.popBackStack(PengelolaHalaman.Home.name, inclusive
    = false)
}
private fun cancelOrderAndNavigateToRasa(
    navController: NavHostController
){
    navController.popBackStack(PengelolaHalaman.Rasa.name, inclusive
    = false)
}
