package com.example.lucas.controlcar.config;

import android.util.Log;

import java.util.EnumMap;

/**
 * Created by Mesa on 15/11/2017.
 */

public class DadosVeiculo {

    private static final String TAG = "Dados Veiculo";
    private static final double MIN_RPM = 1450;

    // Raw readings
    public double m_VelocidadeVeiculo;
    public double m_RpmMotor;
    public double m_CargaMotor;
    public double m_RateRpmMotor;
    public double m_LastSampleTime;

    // Limits
    public double m_VelociadadeMax;

    // Derived readings
    public double m_CurrentRatio;
    public Gear m_CurrentGear;
    public Gear m_OptimumGear;
    public double m_SpeedRate;

    EnumMap<Gear, Double> m_GearRatios = new EnumMap<Gear, Double>(Gear.class);

    public EnumMap<Gear, String> m_GearString = new EnumMap<Gear, String>(
            Gear.class);
    public EnumMap<Gear, Double> m_GearMaxRpm = new EnumMap<Gear, Double>(
            Gear.class);

    public enum Gear {
        GEAR0, GEAR1, GEAR2, GEAR3, GEAR4, GEAR5
    }

    public DadosVeiculo() {
        Log.d(TAG, "Vehicle data init");
        m_GearRatios.put(Gear.GEAR0, 0.0); // rpm per kph
        m_GearRatios.put(Gear.GEAR1, 130.0); // rpm per kph
        m_GearRatios.put(Gear.GEAR2, 65.0);
        m_GearRatios.put(Gear.GEAR3, 43.0);
        m_GearRatios.put(Gear.GEAR4, 31.0);
        m_GearRatios.put(Gear.GEAR5, 24.0);

        m_GearString.put(Gear.GEAR0, "---");
        m_GearString.put(Gear.GEAR1, "1st");
        m_GearString.put(Gear.GEAR2, "2nd");
        m_GearString.put(Gear.GEAR3, "3rd");
        m_GearString.put(Gear.GEAR4, "4th");
        m_GearString.put(Gear.GEAR5, "5th");

        m_GearMaxRpm.put(Gear.GEAR0, 2000.0);
        m_GearMaxRpm.put(Gear.GEAR1, m_GearRatios.get(Gear.GEAR1)
                / m_GearRatios.get(Gear.GEAR2) * MIN_RPM);
        m_GearMaxRpm.put(Gear.GEAR2, m_GearRatios.get(Gear.GEAR2)
                / m_GearRatios.get(Gear.GEAR3) * MIN_RPM);
        m_GearMaxRpm.put(Gear.GEAR3, m_GearRatios.get(Gear.GEAR3)
                / m_GearRatios.get(Gear.GEAR4) * MIN_RPM);
        m_GearMaxRpm.put(Gear.GEAR4, m_GearRatios.get(Gear.GEAR4)
                / m_GearRatios.get(Gear.GEAR5) * MIN_RPM);
        m_GearMaxRpm.put(Gear.GEAR5, 3000.0);

        m_VelociadadeMax = 80.0;
        m_CurrentGear = Gear.GEAR0;
        m_OptimumGear = Gear.GEAR0;
    }

    private void calculaValoresDerivados() {
        // Determine rpms for each gear
        if (m_VelocidadeVeiculo != 0)
            m_CurrentRatio = m_RpmMotor / m_VelocidadeVeiculo;
        else
            m_CurrentRatio = 0;

        // See what gear we are in
        m_CurrentGear = Gear.GEAR0;
        for (Gear gear : Gear.values()) {
            double rpm = m_VelocidadeVeiculo * m_GearRatios.get(gear);

            // allow 10% uncertainty
            if (m_RpmMotor > rpm * 0.9 && m_RpmMotor < rpm * 1.1) {
                m_CurrentGear = gear;
                break;
            }
        }

        // Compute optimum gear
        if (m_VelocidadeVeiculo == 0) {
            m_OptimumGear = Gear.GEAR1;
        } else {
            // See which gear has revs closest to MIN_RPM
            for (Gear gear : Gear.values()) {
                double rpm = m_VelocidadeVeiculo * m_GearRatios.get(gear);

                if (rpm >= MIN_RPM) {
                    m_OptimumGear = gear;
                }
            }
        }

        if (m_CurrentGear != Gear.GEAR0)
            m_SpeedRate = m_RateRpmMotor / m_GearRatios.get(m_CurrentGear);
        else
            m_SpeedRate = 0;
    }

    public void calculo() {
        calculaValoresDerivados();
    }
}

