package com.example.lucas.controlcar.obd;

import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.EquivalentRatioCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.fuel.WidebandAirFuelRatioCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.FuelTrim;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lucas on 23/10/17.
 */

public class ComandosObd {

    public static ArrayList<ObdCommand> getComandos() {
        ArrayList<ObdCommand> comandos = new ArrayList<ObdCommand>();

        //Controles
        comandos.add(new ModuleVoltageCommand());
        comandos.add(new EquivalentRatioCommand());
        comandos.add(new DistanceMILOnCommand());
        comandos.add(new DtcNumberCommand());
        comandos.add(new TimingAdvanceCommand());
        comandos.add(new TroubleCodesCommand());
        comandos.add(new VinCommand());
        // Motor
        comandos.add(new LoadCommand());
        comandos.add(new RPMCommand());
        comandos.add(new RuntimeCommand());
        comandos.add(new MassAirFlowCommand());
        comandos.add(new ThrottlePositionCommand());
        // Combustivel
        comandos.add(new FindFuelTypeCommand());
        comandos.add(new ConsumptionRateCommand());
        comandos.add(new FuelLevelCommand());
        comandos.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
        comandos.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
        comandos.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
        comandos.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        comandos.add(new AirFuelRatioCommand());
        comandos.add(new WidebandAirFuelRatioCommand());
        comandos.add(new OilTempCommand());
        // Pressão
        comandos.add(new BarometricPressureCommand());
        comandos.add(new FuelPressureCommand());
        comandos.add(new FuelRailPressureCommand());
        comandos.add(new IntakeManifoldPressureCommand());
        // Temperatura
        comandos.add(new AirIntakeTemperatureCommand());
        comandos.add(new AmbientAirTemperatureCommand());
        comandos.add(new EngineCoolantTemperatureCommand());
        // Velocidade
        comandos.add(new SpeedCommand());
        return comandos;
    }


    public ModuleVoltageCommand getModuleVoltage(BluetoothSocket btSocket) throws IOException {
        ModuleVoltageCommand moduleVoltageCommand = new ModuleVoltageCommand();
        try {
            moduleVoltageCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
        return moduleVoltageCommand;
    }

    public void getEquivalentRatio(BluetoothSocket btSocket) throws IOException {
        EquivalentRatioCommand equivalentRatioCommand = new EquivalentRatioCommand();
        try {
            equivalentRatioCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getDistanceMIL(BluetoothSocket btSocket) throws IOException {
        DistanceMILOnCommand distanceMILOnCommand = new DistanceMILOnCommand();
        try {
            distanceMILOnCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getDtvNumber(BluetoothSocket btSocket) throws IOException {
        DtcNumberCommand dtcNumberCommand = new DtcNumberCommand();
        try {
            dtcNumberCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getTimingAdvance(BluetoothSocket btSocket) throws IOException {
        TimingAdvanceCommand timingAdvanceCommand = new TimingAdvanceCommand();
        try {
            timingAdvanceCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getTroubleCodes(BluetoothSocket btSocket) throws IOException {
        TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();
        try {
            troubleCodesCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getVin(BluetoothSocket btSocket) throws IOException {
        VinCommand vinCommand = new VinCommand();
        try {
            vinCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getLoad(BluetoothSocket btSocket) throws IOException {
        LoadCommand loadCommand = new LoadCommand();
        try {
            loadCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getRPM(BluetoothSocket btSocket) throws IOException {
        RPMCommand rpmCommand = new RPMCommand();
        try {
            rpmCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getRuntime(BluetoothSocket btSocket) throws IOException {
        RuntimeCommand runtimeCommand = new RuntimeCommand();
        try {
            runtimeCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getMassAirFlow(BluetoothSocket btSocket) throws IOException {
        MassAirFlowCommand massAirFlowCommand = new MassAirFlowCommand();
        try {
            massAirFlowCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getThrottlePosition(BluetoothSocket btSocket) throws IOException {
        ThrottlePositionCommand throttlePositionCommand = new ThrottlePositionCommand();
        try {
            throttlePositionCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getFindFuel(BluetoothSocket btSocket) throws IOException {
        FindFuelTypeCommand findFuelTypeCommand = new FindFuelTypeCommand();
        try {
            findFuelTypeCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getConsumptionRate(BluetoothSocket btSocket) throws IOException {
        ConsumptionRateCommand consumptionRateCommand = new ConsumptionRateCommand();
        try {
            consumptionRateCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getFuelLevel(BluetoothSocket btSocket) throws IOException {
        FuelLevelCommand fuelLevelCommand = new FuelLevelCommand();
        try {
            fuelLevelCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getAirFuelRatio(BluetoothSocket btSocket) throws IOException {
        AirFuelRatioCommand airFuelRatioCommand = new AirFuelRatioCommand();
        try {
            airFuelRatioCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getWideBand(BluetoothSocket btSocket) throws IOException {
        WidebandAirFuelRatioCommand widebandAirFuelRatioCommand = new WidebandAirFuelRatioCommand();
        try {
            widebandAirFuelRatioCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getOilTemp(BluetoothSocket btSocket) throws IOException {
        OilTempCommand oilTempCommand = new OilTempCommand();
        try {
            oilTempCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getBarometricPressure(BluetoothSocket btSocket) throws IOException {
        BarometricPressureCommand barometricPressureCommand = new BarometricPressureCommand();
        try {
            barometricPressureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getFuelPressure(BluetoothSocket btSocket) throws IOException {
        FuelPressureCommand fuelPressureCommand = new FuelPressureCommand();
        try {
            fuelPressureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getFuelRail(BluetoothSocket btSocket) throws IOException {
        FuelRailPressureCommand fuelRailPressureCommand = new FuelRailPressureCommand();
        try {
            fuelRailPressureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getIntakeManifold(BluetoothSocket btSocket) throws IOException {
        IntakeManifoldPressureCommand intakeManifoldPressureCommand = new IntakeManifoldPressureCommand();
        try {
            intakeManifoldPressureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getAirIntake(BluetoothSocket btSocket) throws IOException {
        AirIntakeTemperatureCommand airIntakeTemperatureCommand = new AirIntakeTemperatureCommand();
        try {
            airIntakeTemperatureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getAmbientAir(BluetoothSocket btSocket) throws IOException {
        AmbientAirTemperatureCommand ambientAirTemperatureCommand = new AmbientAirTemperatureCommand();
        try {
            ambientAirTemperatureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getEngineCoolant(BluetoothSocket btSocket) throws IOException {
        EngineCoolantTemperatureCommand engineCoolantTemperatureCommand = new EngineCoolantTemperatureCommand();
        try {
            engineCoolantTemperatureCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }

    public void getSpeed(BluetoothSocket btSocket) throws IOException {
        SpeedCommand speedCommand = new SpeedCommand();
        try {
            speedCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnableToConnectException u) {
            u.printStackTrace();
        }
    }
}
