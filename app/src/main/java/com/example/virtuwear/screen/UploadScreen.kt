package com.example.virtuwear.screen

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.viewmodel.UploadViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.example.virtuwear.viewmodel.LoginViewModel

@Composable
fun UploadPhotoScreen(
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedGarmentType by uploadViewModel.selectedGarmentType
    val imageUris by uploadViewModel.imageUris

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Upload Photo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    loginViewModel.getGoogleSignInClient().signOut().addOnCompleteListener {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Logout", color = Color.Red)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFEAECF0), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                UploadBox(imageUri = imageUris.getOrNull(0)) { uri -> uploadViewModel.addImageUris(uri, 0) }
                Text(text = "PNG    JPG    JPEG    <10MB", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Garment Type", fontWeight = FontWeight.Bold, modifier = Modifier)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ToggleButton("Single Garment", selectedGarmentType) { uploadViewModel.setGarmentType(it) }
                    Spacer(modifier = Modifier.width(8.dp))
                    ToggleButton("Multiple Garments", selectedGarmentType) { uploadViewModel.setGarmentType(it) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedGarmentType == "Multiple Garments") {
                    Row {
                        UploadBox(imageUri = imageUris.getOrNull(1)) { uri -> uploadViewModel.addImageUris(uri, 1) }
                        Spacer(modifier = Modifier.width(8.dp))
                        UploadBox(imageUri = imageUris.getOrNull(2)) { uri -> uploadViewModel.addImageUris(uri, 2) }
                    }
                } else {
                    UploadBox(imageUri = imageUris.getOrNull(1)) { uri -> uploadViewModel.addImageUris(uri, 1) }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    uploadViewModel.uploadImage()
                    navController.navigate("download?garmentType=$selectedGarmentType")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Generate Try On", color = Color.White)
            }
        }
    }
}

// UploadBox and ToggleButton remain unchanged
@Composable
fun UploadBox(imageUri: Uri?, onImageSelected: (Uri) -> Unit) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { imagePickerLauncher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "Upload",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun ToggleButton(label: String, selected: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(label) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == label) Color(0xFFCDCDD0) else Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(text = label)
    }
}