package com.example.getdeviceid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.getdeviceid.ui.theme.GetdeviceidTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GetdeviceidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private val GSF_URI: Uri = Uri.parse("content://com.google.android.gsf.gservices")
private const val ANDROID_ID_KEY = "android_id"

private fun getGsfAndroidId(context: Context): String? {
    val params = arrayOf(ANDROID_ID_KEY)
    val cursor = context.contentResolver.query(GSF_URI, null, null, params, null)
    cursor?.use { c ->
        if (c.moveToFirst() && c.columnCount >= 2) {
            val idString = c.getString(1)
            return try {
                java.lang.Long.toHexString(idString.toLong())
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
    return null
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val deviceId = getGsfAndroidId(context) ?: "Unknown"
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Get GSF ID",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        SelectionContainer {
            Text(
                text = "GSF ID: $deviceId",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            clipboardManager.setText(AnnotatedString(deviceId))
            copied = true
        }) {
            Text("Copy GSF ID")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "View source code!",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rifting/getgsfid"))
                context.startActivity(intent)
            }
        )

        if (copied) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Copied to clipboard!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GetdeviceidTheme {
        Greeting()
    }
}
