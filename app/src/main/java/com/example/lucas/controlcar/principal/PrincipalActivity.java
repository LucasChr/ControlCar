package com.example.lucas.controlcar.principal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.carro.CarroCadActivity;
import com.example.lucas.controlcar.carro.CarroDAO;
import com.example.lucas.controlcar.carro.CarrosListFragment;
import com.example.lucas.controlcar.configuracoes.ConfiguracoesActivity;
import com.example.lucas.controlcar.service.NotificacaoActivity;
import com.example.lucas.controlcar.service.TestService;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioListFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CarroDAO carroDAO;
    private boolean isFABOpen;
    FloatingActionButton fab;
    private Notification notification;
    private NotificationCompat.Builder mBuilder;
    private static final int SOLICITA_ATIVACAO = 1;
    BluetoothAdapter btfAdapter = null;
    BluetoothDevice btfDevice = null;
    BluetoothSocket btfSocket = null;
    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String MAC = "00:00:00:00:00:01";
    private boolean running;
    private boolean conexao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        startService(new Intent(getBaseContext(), TestService.class));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Float Action Button*/
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PrincipalActivity.this, CarroCadActivity.class);
                startActivityForResult(it, 1);
            }
        });
        /*Final Float Action Button*/

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

        /*Notificacao*/
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this, PrincipalActivity.class), 0);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker("Ticker Texto");
        mBuilder.setContentTitle("Executando");
        //mBuilder.setContentText("Descrição");
        mBuilder.setSmallIcon(R.drawable.ic_menu_manage);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_camera));
        mBuilder.setContentIntent(p);
//        mBuilder.setProgress(0, 0, true);
//        mNotifyManager.notify(0, mBuilder.build());

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        String descs = new String("Control Car ainda está em execução");
        style.addLine(descs);
        mBuilder.setStyle(style);

        notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        mNotifyManager.notify(R.drawable.ic_menu_manage, notification);
        /*Final Notificacao*/

        /*Conecao com OBD*/
        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btfAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btfAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }

        if (conexao) {
            //desconectar
            try {
                btfSocket.close();
                conexao = false;
                exibeToast("Bluetooth desconectado");
            } catch (IOException e) {
                exibeToast("Ocorreu um erro: " + e);
            }
        } else {
            //Conectar
            btfDevice = btfAdapter.getRemoteDevice(MAC);
            try {
                btfSocket = btfDevice.createRfcommSocketToServiceRecord(btf_UUID);
                btfSocket.connect();
                conexao = true;
                if (btfAdapter != null && btfAdapter.isEnabled()) {
                    new ThreadServidor().start();
                    running = true;
                }
                exibeToast("Conecado com: " + MAC);
            } catch (IOException e) {
                conexao = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("OBD-II");
                builder.setMessage("O scanner não está disponível verifique as configurações");
                builder.setNegativeButton("Configurações", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Abre a activity de configurações
                        Intent it = new Intent(getApplicationContext(), ConfiguracoesActivity.class);
                        startActivityForResult(it, 1);
                    }
                });
                builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Fecha a aplicação
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        finishAffinity();
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
            }
        }
    }

    //Método para controlar o botão voltar do android
    @Override
    public void onBackPressed() {
        //Alerta para sair do aplicativo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fechar");
        builder.setMessage("Deseja realmente parar o aplicativo?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Fecha a aplicação
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                finishAffinity();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
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
            Intent it = new Intent(PrincipalActivity.this, ConfiguracoesActivity.class);
            startActivityForResult(it, 3);
        }

        return super.onOptionsItemSelected(item);
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
            case R.id.nav_config: {
                Intent it = new Intent(PrincipalActivity.this, ConfiguracoesActivity.class);
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

    public void exibeToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    //Thread para controlar a conexao e nao travar a tela
    public class ThreadServidor extends Thread {
        private String TAG = "Control car";

        @Override
        public void run() {
            super.run();
            try {
                //Abre o socket servidor (quem for conectar precisa utilizar o mesmo UUID)
                BluetoothServerSocket serverSocket = btfAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", btf_UUID);
                //Fica aguardando alguem conectar (Chamada bloqueante)
                try {
                    //Aguarda conexoes
                    btfSocket = serverSocket.accept();
                } catch (Exception e) {
                    //Em caso de erro encerrar aqui
                    return;
                }

                if (btfSocket != null) {
                    //Alguem conectou enstao encerra-se o socket server
                    serverSocket.close();
                    //O socket possui a InputStream e OutputStram para ler e escrever
                    InputStream in = btfSocket.getInputStream();
                    //Recupera o dispositivo cliente que fez a conexao
                    btfDevice = btfSocket.getRemoteDevice();
                    updateViewConectou(btfDevice);
                    byte[] bytes = new byte[1024];
                    int length;
                    //Fica em loop para receber as mensagens
                    while (running) {
                        //Le a mensagem (chamada bloqueada até alguem escrever)
                        length = in.read(bytes);
                        String mensagemRecebida = new String(bytes, 0, length);
                        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
                        final String s = tvMsg.getText().toString() + mensagemRecebida + "\n";
                        updateViewMensagem(s);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro no servidor: " + e.getMessage(), e);
                running = false;
            }
        }
    }

    //Exibe a mensagem na tela, informando o nome do dispositivo que fez a conexao
    private void updateViewConectou(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvNome = (TextView) findViewById(R.id.tvNomeDispositivo);
                tvNome.setText(device.getName() + " - " + device.getAddress());
            }
        });
    }

    //Exibe a mensagem recebida na tela (Enviada pelo outro dispositivo)
    private void updateViewMensagem(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
                tvMsg.setText(s);
            }
        });
    }
}
