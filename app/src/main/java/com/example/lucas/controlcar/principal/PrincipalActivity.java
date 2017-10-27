package com.example.lucas.controlcar.principal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.lucas.controlcar.aux.Alertas;
import com.example.lucas.controlcar.bluetooth.BluetoothAux;
import com.example.lucas.controlcar.carro.CarroCadActivity;
import com.example.lucas.controlcar.carro.CarroDAO;
import com.example.lucas.controlcar.carro.CarrosListFragment;
import com.example.lucas.controlcar.config.ConfigActivity;
import com.example.lucas.controlcar.relatorio.RelatorioActivity;
import com.example.lucas.controlcar.relatorio.RelatorioListFragment;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioListFragment;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SOLICITA_ATIVACAO = 1;
    private static final int MESSAGE_READ = 3;

    BluetoothAdapter btfAdapter = null;

    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;

    private static String MAC = "00:00:00:00:00:01";
    private static final UUID bt_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean conexao = false;

    PrincipalActivity.ConnectedThread connectedThread;

    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    private CarroDAO carroDAO;
    private TextView tvObdStatus, tvGpsStatus, tvBtfStatus;
    private boolean isFABOpen;
    FloatingActionButton fab;

    private static String TAG = PrincipalActivity.class.getName();

    private NotificationCompat.Builder mBuilder;
    private Notification notification;

    Alertas alerta = new Alertas();
    BluetoothAux bluetooth = new BluetoothAux();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvObdStatus = (TextView) findViewById(R.id.tvOBD);
        tvGpsStatus = (TextView) findViewById(R.id.tvGPS);
        tvBtfStatus = (TextView) findViewById(R.id.tvBTF);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conectar();
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

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
        String descs = new String("Control Car ainda está em execução");
        style.addLine(descs);
        mBuilder.setStyle(style);

        notification = mBuilder.build();
        notification.flags = Notification.FLAG_LOCAL_ONLY;
        mNotifyManager.notify(R.drawable.ic_menu_manage, notification);

        /*Conecao com OBD*/
        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btfAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btfAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }

//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//                if (msg.what == MESSAGE_READ) {
//                    //recebe dados
//                    String recebidos = (String) msg.obj;
//                    //reune os dados
//                    dadosBluetooth.append(recebidos);
//
//                    int fimInfo = dadosBluetooth.indexOf(".");
//
//                    if (fimInfo > 0) {
//                        String dadosCompletos = dadosBluetooth.substring(0, fimInfo);
//
//                        int tamInfo = dadosCompletos.length();
//                        //Chegou aqui e porque a informação veio correta
//                        if (dadosBluetooth.charAt(0) == '0') {
//                            String dadosFinal = dadosBluetooth.substring(1, tamInfo);
//                            Log.d("Recebidos", dadosFinal);
//                            if (dadosFinal.contains("0C")) {
////                                tvTal.setText("INformação");
//                                tvGpsStatus.setText(recebidos);
//                            }
//                        }
//                        dadosBluetooth.delete(0, dadosBluetooth.length());
//                    }
//                }
//            }
//        };

    }

    @Override
    public void onBackPressed() {
        //Alerta para sair do aplicativo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fechar");
        builder.setMessage("Deseja realmente parar o aplicativo?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Fecha a aplicação
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
                Intent it = new Intent(PrincipalActivity.this, RelatorioActivity.class);
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

    public void conectar() {
        if (conexao) {
            //desconectar
            try {
                btSocket.close();
                conexao = false;
                Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else {
            //conectar
            tvBtfStatus.setText(R.string.btf_ativado);

            btDevice = btfAdapter.getRemoteDevice(MAC);
            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(bt_UUID);
                btSocket.connect();

                conexao = true;

                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();

                try {
                    //new SelectProtocolCommand(ObdProtocols.AUTO).run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                    new RPMCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    int TESTE = new RPMCommand().getRPM();
//                    new AirIntakeTemperatureCommand().getResult();
//                    new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    new TimeoutCommand(125).run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    new AmbientAirTemperatureCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    Log.d("Passou 5", String.valueOf(TESTE));
//                    String teste = connectedThread.dadosBtf;
//                    Log.d("TESTE", teste);
//                    if (TESTE > 0) {
//                    tvGpsStatus.setText(teste);
//                    } else {
//                    }
                } catch (Exception e) {
                    // handle errors
                }
                //btnConectar.setText("Desconectar");
                Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                tvObdStatus.setText(R.string.obd_conectado);
            } catch (IOException e) {
                conexao = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
                builder.setTitle("OBD-II");
                builder.setMessage("O scanner não está disponível verifique as configurações");
                builder.setNegativeButton("Configurações", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent it = new Intent(getApplicationContext(), ConfigActivity.class);
                        startActivityForResult(it, 1);

                    }
                });
                builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Fecha a aplicação
                        finishAffinity();
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
                tvObdStatus.setText(R.string.obd_desconectado);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String dadosBtf = new String(buffer, 0, bytes);
                    Log.d("InStream", dadosBtf);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBtf).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


}
