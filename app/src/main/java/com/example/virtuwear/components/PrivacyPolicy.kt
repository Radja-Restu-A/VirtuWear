package com.example.virtuwear.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PrivacyPolicy(
    showPrivacy: Boolean,
    showCheckbox: Boolean,
    onDismiss: () -> Unit,
    onAccept: (Boolean) -> Unit
) {
    if (showPrivacy) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            var isChecked by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(520.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = """
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at lacus ac elit hendrerit convallis. Curabitur nec semper elit, sit amet ultrices metus. Suspendisse et vehicula nibh. Nam commodo imperdiet ligula, nec dignissim arcu pretium sed.

                                Nulla facilisi. Proin sed nulla sit amet sapien tempor rutrum. Aenean at lorem ipsum. Quisque quis feugiat libero. Duis ut lacus at turpis fringilla suscipit at nec nisi.

                                Donec sed efficitur lorem, non posuere arcu. Curabitur congue ligula sed orci ultricies tincidunt. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.

                                In hac habitasse platea dictumst. Aenean at sodales sem, sed sagittis erat. Cras ut lacus augue. Mauris fermentum fermentum dolor, non bibendum sapien ultricies a.
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (showCheckbox) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it }
                                )
                                Text(text = "Saya setuju dengan kebijakan privasi.")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    onAccept(isChecked)
                                    onDismiss()
                                },
                                enabled = isChecked,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Setuju")
                            }
                        }
                    }
                }
            }
        }
    }
}