package com.example.virtuwear.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.virtuwear.data.model.SingleGarmentModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.viewmodel.DownloadViewModel
import com.example.virtuwear.R
import com.example.virtuwear.components.Alert
import com.example.virtuwear.components.AlertType
import com.example.virtuwear.components.BookmarkButton
import com.example.virtuwear.components.HistoryItem
import com.example.virtuwear.components.Notes
import com.example.virtuwear.data.model.SingleGarmentResponse
import com.example.virtuwear.viewmodel.GarmentDetailViewModel
import com.example.virtuwear.viewmodel.GarmentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarmentDetailScreen(
    idSingle: Long,
    onDismiss: () -> Unit,
    downloadViewModel: DownloadViewModel = hiltViewModel(),
    garmentDetailViewModel: GarmentDetailViewModel = hiltViewModel(),
    garmentViewModel : GarmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var garment by remember { mutableStateOf<SingleGarmentResponse?>(null) }
    val notesInput = remember { mutableStateOf("") }
    var showModals by remember { mutableStateOf(false) }

    LaunchedEffect(idSingle) {
        garment = garmentDetailViewModel.getById(idSingle).body()
        notesInput.value = garment?.notes ?: ""
    }

    LaunchedEffect(notesInput.value) {
        garment?.id?.let {
            garmentDetailViewModel.updateNotes(it, notesInput.value)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = garment?.outfitName ?: "myOutfit",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                garment?.resultImage?.let {
                    HistoryItem(
                        it,
                        null,
                        bookmarkButton = {
                            garment?.id?.let {
                                BookmarkButton(
                                    id = it,
                                    isSingle = true,
                                    viewModel = garmentViewModel
                                )
                            }

                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    garment?.modelImage?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Model Image",
                            modifier = Modifier
                                .size(150.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )

                    garment?.garmentImage?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Garment Image",
                            modifier = Modifier
                                .size(150.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Notes(
                    value = notesInput.value,
                    onValueChange = { notesInput.value = it },
                    modifier = Modifier
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = {
                            garment?.resultImage?.let {
                                downloadViewModel.downloadPhoto(
                                    context,
                                    it
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Download Photo",
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.download_svg),
                                contentDescription = "Download Icon",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (garment != null) {
                                showModals = true
                            } else {
                                Toast.makeText(context, "Cant delete image", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.White
                        )
                    }

                    if (showModals) {
                        Alert(
                            showDialog = showModals,
                            onDismiss = { showModals = false },
                            title = "Delete Image?",
                            message = "Once deleted, the image cannot be recovered",
                            confirmButtonText = "Delete",
                            cancelButtonText = "Cancel",
                            onConfirmClick = {
                                coroutineScope.launch {
                                    garment?.let { garmentDetailViewModel.deleteGarment(it.id) }
                                    onDismiss()
                                    showModals = false
                                }
                            },
                            onCancelClick = { showModals = false },
                            type = AlertType.CONFIRMATION
                        )
                    }
                }
            }
        }
    )
}