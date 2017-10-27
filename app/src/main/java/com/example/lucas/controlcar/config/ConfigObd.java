package com.example.lucas.controlcar.config;

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

import java.util.ArrayList;

/**
 * Created by lucas on 23/10/17.
 */

public class ConfigObd {

    public static ArrayList<ObdCommand> getCommands(){
        ArrayList<ObdCommand> comandos = new ArrayList<>();

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

        // Misc
        comandos.add(new SpeedCommand());

        return comandos;
    }
}
