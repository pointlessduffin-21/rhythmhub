package edu.uc.intprog32.escarro.myapplication.presentation.arcades

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import edu.uc.intprog32.escarro.myapplication.data.model.Arcade
import edu.uc.intprog32.escarro.myapplication.data.repository.UserRepository
import edu.uc.intprog32.escarro.myapplication.presentation.components.GradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun ArcadesScreen(
        userRepository: UserRepository,
        viewModel: ArcadesViewModel =
                viewModel<ArcadesViewModel>(factory = ArcadesViewModel.Factory(userRepository)),
        onViewQueue: (String) -> Unit
) {
    val arcades by viewModel.arcades.collectAsState()
    val checkInResult by viewModel.checkInResult.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permission Launcher
    val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                    isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted, user can retry check-in
                }
            }

    // Show Toast on Check-In Result
    LaunchedEffect(checkInResult) {
        checkInResult?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearCheckInResult()
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Arcade Locator") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
    ) { paddingValues ->
        GradientBackground {
            // Group arcades by City
            val groupedArcades = arcades.groupBy { it.city }

            LazyColumn(
                    modifier =
                            Modifier.fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedArcades.forEach { (city, cityArcades) ->
                    item {
                        Text(
                                text = city,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(cityArcades) { arcade ->
                        ArcadeItem(
                                arcade = arcade,
                                onViewQueue = onViewQueue,
                                onCheckIn = {
                                    if (ActivityCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                            ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        fusedLocationClient.lastLocation.addOnSuccessListener {
                                                location ->
                                            if (location != null) {
                                                viewModel.performCheckIn(arcade, location)
                                            } else {
                                                android.widget.Toast.makeText(
                                                                context,
                                                                "Location not available",
                                                                android.widget.Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                            }
                                        }
                                    } else {
                                        requestPermissionLauncher.launch(
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ArcadeItem(arcade: Arcade, onViewQueue: (String) -> Unit, onCheckIn: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                        text = arcade.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = arcade.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoTag(
                        icon = Icons.Default.VideogameAsset,
                        text = "${arcade.machineCount} Machines"
                )
                InfoTag(icon = Icons.Default.People, text = "${arcade.currentQueueSize} in Queue")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                        onClick = { onViewQueue(arcade.id) },
                        modifier = Modifier.weight(1f),
                        enabled = arcade.isOpen
                ) { Text(if (arcade.isOpen) "View Queue" else "Closed") }
                OutlinedButton(onClick = onCheckIn, modifier = Modifier.weight(1f)) {
                    Text("Check In")
                }
            }
        }
    }
}

@Composable
fun InfoTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
        )
    }
}
