package com.example.lucas.controlcar.relatorio;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.config.ConexaoObd;
import com.example.lucas.controlcar.config.DadosVeiculo;
import com.example.lucas.controlcar.config.IObdConexao;

public class RelatorioActivity extends AppCompatActivity {
    private static final String TAG = "Relatorio Activity";

    private Handler m_Handler;
    private WakeLock m_WakeLock;

    private IObdConexao m_Obd;
    public DadosVeiculo m_ObdData;

    private TextView tvFlow, tvRpm, tvSpeed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        m_WakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "My Tag");

        setContentView(R.layout.activity_relatorio);

        tvFlow = (TextView) findViewById(R.id.tvFlow);
        tvRpm = (TextView) findViewById(R.id.tvRpm);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);

        m_Obd = new ConexaoObd();
        m_ObdData = new DadosVeiculo();

        m_Handler = new Handler();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        try {
            m_Obd.iniciaConexaoObd();
            Toast.makeText(getApplicationContext(), "Conectado",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Sem conexao: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
            m_Obd.pararConexaoObd();

            // fall back to simulation
//            m_Obd = new ObdConnectionSim();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        m_WakeLock.acquire();

        m_Updater.run();
    }

    Runnable m_Updater = new Runnable() {
        @Override
        public void run() {
            m_Obd.atualizaDados(m_ObdData);
            m_ObdData.calculo();

            tvRpm.setText(String.valueOf(m_ObdData.m_RpmMotor));
            tvSpeed.setText(String.valueOf(m_ObdData.m_VelocidadeVeiculo));

            // Do it all again in 100 ms
            m_Handler.postDelayed(m_Updater, 100);
        }
    };

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        m_Handler.removeCallbacks(m_Updater);
        m_WakeLock.release();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        m_Obd.pararConexaoObd();
    }

}