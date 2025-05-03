package com.example.virtuwear.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.painterResource
import com.example.virtuwear.R

enum class AlertType {
    SUCCESS,
    ERROR,
    CONFIRMATION
}

@Composable
fun Alert(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String? = null,
    confirmButtonText: String = "Confirm",
    cancelButtonText: String? = null,
    onConfirmClick: () -> Unit,
    onCancelClick: (() -> Unit)? = null,
    type: AlertType = AlertType.CONFIRMATION
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (type != AlertType.CONFIRMATION) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (type) {
                                AlertType.SUCCESS -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = "Success",
                                        tint = Color(0xFF00AA00),
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                AlertType.ERROR -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_cross),
                                        contentDescription = "Error",
                                        tint = Color.Black,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                else -> {}
                            }
                        }
                    }

                    // Title
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    // Message
                    if (message != null) {
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }

                    // Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onConfirmClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = confirmButtonText,
                                color = Color.White
                            )
                        }

                        if (cancelButtonText != null && onCancelClick != null) {
                            Button(
                                onClick = onCancelClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = cancelButtonText,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Contoh Pemanggilan
//Alert(
//    showDialog = showDeleteAccountAlert,
//    onDismiss = { showDeleteAccountAlert = false },
//    title = "Delete Account?",
//    message = "Bakal kahapus akun maneh teh ieu",
//    confirmButtonText = "Delete",
//    cancelButtonText = "Cancel",
//    onConfirmClick = {
//        showDeleteAccountAlert = false
//    },
//    onCancelClick = { showDeleteAccountAlert = false },
//    type = AlertType.CONFIRMATION
//)