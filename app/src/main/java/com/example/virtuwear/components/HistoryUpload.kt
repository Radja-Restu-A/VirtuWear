package com.example.virtuwear.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.virtuwear.data.model.GarmentDto
import com.example.virtuwear.data.model.ModelDto

@Composable
fun HistoryDialog(
    items: List<Any>,
    itemType: String,
    onItemSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$itemType History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (items.isEmpty()) {
                    Text(
                        text = "No $itemType history available",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 600.dp)
                            .fillMaxWidth()
                    ) {
                        items(items.size) { index ->
                            val item = items[index]
                            val imageUrl = when (item) {
                                is ModelDto -> item.modelImage
                                is GarmentDto -> item.garmentImage
                                else -> ""
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(Uri.parse(imageUrl))
                                        onDismiss()
                                    }
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "$itemType Image",
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        }
    }
}