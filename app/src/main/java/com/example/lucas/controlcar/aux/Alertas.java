package com.example.lucas.controlcar.aux;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.principal.PrincipalActivity;

/**
 * Created by lucas on 24/10/17.
 */

public class Alertas extends Activity {

    public void exibeToast(Context context, String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }
}
