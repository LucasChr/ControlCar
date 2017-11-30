package com.example.lucas.controlcar.principal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.carro.CarroCadActivity;
import com.example.lucas.controlcar.carro.CarroDAO;
import com.example.lucas.controlcar.carro.CarrosListFragment;
import com.example.lucas.controlcar.config.ConfigActivity;
import com.example.lucas.controlcar.relatorio.RelatorioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioListFragment;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SOLICITA_ATIVACAO = 1;
    private CarroDAO carroDAO;
    FloatingActionButton fab;
    private TextView tvBt;
    private BluetoothAdapter btAdapter = null;
    private NotificationCompat.Builder mBuilder;
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvBt = (TextView) findViewById(R.id.tvStatusBt);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PrincipalActivity.this, RelatorioCadActivity.class);
                startActivity(it);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        carroDAO = new CarroDAO(this);
        carroDAO.listar();

        setDisplay(R.id.nav_carros);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this, PrincipalActivity.class), 0);

        /*Notificacao ao abrir o app*/
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker("Ticker Texto");
        mBuilder.setContentTitle("Executando");
        //mBuilder.setContentText("Descrição");
        mBuilder.setSmallIcon(R.drawable.ic_menu_manage);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_camera));
        mBuilder.setContentIntent(p);

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        String descs = new String("Recebendo dados...");
        style.addLine(descs);
        mBuilder.setStyle(style);

        notification = mBuilder.build();
        notification.flags = Notification.FLAG_LOCAL_ONLY;
        notificationManager.notify(R.drawable.ic_menu_manage, notification);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth, a aplicação será encerrada", Toast.LENGTH_LONG).show();
            notificationManager.cancelAll();
            finishAffinity();
        } else if (!btAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        } else {
//            tvBt.setText(R.string.status_bt_conectado);
        }

    }

    @Override
    public void onBackPressed() {
        //Alerta para sair do aplicativo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.fechar);
        builder.setMessage(R.string.perguntaFechar);
        builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Fecha a aplicação
                notificationManager.cancelAll();
                finishAffinity();
            }
        });
        builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_carro) {
            Intent it = new Intent(PrincipalActivity.this, CarroCadActivity.class);
            startActivityForResult(it, 1);
        } else if (id == R.id.action_usuario) {
            Intent it = new Intent(PrincipalActivity.this, UsuarioCadActivity.class);
            startActivityForResult(it, 3);
        } else if (id == R.id.action_config) {
            Intent it = new Intent(PrincipalActivity.this, ConfigActivity.class);
            startActivityForResult(it, 3);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SOLICITA_ATIVACAO:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não ativado neste aparelho", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(it, SOLICITA_ATIVACAO);
                }
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setDisplay(item.getItemId());
        return true;
    }

    public void setDisplay(int pos) {
        Fragment fragment;
        Activity activity;

        switch (pos) {
            case R.id.nav_carros: {
                fragment = new CarrosListFragment();
                abrirFragment(fragment, "Carros");
                break;
            }
            case R.id.nav_usuarios: {
                fragment = new UsuarioListFragment();
                abrirFragment(fragment, "Usuários");
                break;
            }
            case R.id.nav_relatorio: {
                Intent it = new Intent(PrincipalActivity.this, RelatorioCadActivity.class);
                startActivityForResult(it, 3);
                break;
            }
            case R.id.nav_config: {
                Intent it = new Intent(PrincipalActivity.this, ConfigActivity.class);
                startActivityForResult(it, 2);
                break;
            }
        }
    }

    public void abrirFragment(Fragment fragment, String title) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }

}
