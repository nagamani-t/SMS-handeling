package com.example.myapplication

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
@Composable
fun PermissionDailog(
    permissionTextProvider: permissionTextProvider,
    isPermanentlyDeclained: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingClick: () -> Unit,
    modifier:Modifier

) {
    AlertDialog(onDismissRequest = { onDismiss } ,confirmButton = {
        Column(modifier = Modifier.size(width = 400.dp, height = 38.dp)) {
            Divider()
            Text(
                text = if (isPermanentlyDeclained) {
                    "Grant Permission"
                } else {
                    "Ok"
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxSize().clickable {
                    if (isPermanentlyDeclained) {
                        onGoToAppSettingClick()
                        onDismiss()

                    } else {
                        onOkClick()
                    }

                }.padding(8.dp)
            )
        }
    }, title = {
        Text(text = "Permission required")
    }, text = {
        Text(
            text = permissionTextProvider.getDescription(
                isPermanentlyDeclained=isPermanentlyDeclained
            )
        )
    }, modifier = modifier)
}

interface permissionTextProvider {
    fun getDescription(isPermanentlyDeclained: Boolean):String
}
class SmsPermissionTextProvider:permissionTextProvider{
    override  fun getDescription(isPermanentlyDeclained: Boolean):String{
        return if(isPermanentlyDeclained){
            "It seems you permanently declined the sms access permission."+"You can go to the app settings to grant it"
        }else{
            "This app needs access to your SMS messages so that  "+"you can see the anaytics of your expenses and investments "
        }
    }
}

class LocationPermissionTextProvider:permissionTextProvider{
    override  fun getDescription(isPermanentlyDeclained: Boolean):String{
        return if(isPermanentlyDeclained){
            "It seems you permanently declined the location access permission."+"You can go to the app settings to grant it"
        }else{
            "This app needs access to your location so that  "+"you can see the anaytics of your expenses and investments "
        }
    }
}