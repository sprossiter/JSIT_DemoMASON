/*  
    Copyright 2015 University of Southampton
    
    This file is part of JSIT_DemoMASON.

    JSIT_DemoMASON is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSIT_DemoMASON is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with JSIT_DemoMASON.  If not, see <http://www.gnu.org/licenses/>.
*/
package sim.app.heatbugs.experiments;

import sim.app.heatbugs.HeatBugs;
import sim.app.heatbugs.HeatBugsWithUI;
import sim.engine.SimState;
import sim.engine.MakesSimState;

/**
 * JSIT demo class to represent an experiment (running the model in GUI or non-GUI form) with
 * the 'heat wave' event-oriented feature turned on (but other parameters left as the default).
 * We act as a MakesSimState instance so that we control the model instance-creation process
 * and can set up the required parameters before returning the 'new' model back.
 */
public class HeatWaveDemo implements MakesSimState {
	
	private HeatBugs.ModelParms modelParms;

	public static void main(String[] args) {
				
		if (args.length != 1 || !(args[0].equals("GUI") || args[0].equals("NOGUI"))) {
			throw new IllegalArgumentException("Requires one GUI or NOGUI argument");
		}
		
		// Specify model parameters to use using the ModelParms nested class
		HeatWaveDemo demo = new HeatWaveDemo(new HeatBugs.ModelParms(
							null,		// minIdealTemp
							null,		// maxIdealTemp
							null,		// minOutputHeat
							null,		// maxOutputHeat
							null,		// evaporationRate
							null,		// diffusionRate
							null,		// randomMovementProbability
							null,		// gridHeight
							null,		// gridWidth
							null,		// bugCount
							new Boolean(true),		// heatWaveAtMaxHeat
							new Integer(3)));		// waveJumpBackDistance	
		
		if (args[0].equals("GUI")) {
			new HeatBugsWithUI(demo.newInstance(System.currentTimeMillis(), args)).createController();
		}
		else {
			SimState.doLoop(demo, args);
			System.exit(0);
		}

	}
		
	public HeatWaveDemo(HeatBugs.ModelParms modelParms) {
		
		this.modelParms = modelParms;
		
	}

	@Override
	public SimState newInstance(long seed, String[] args) {
		
		HeatBugs model = new HeatBugs(seed);
		modelParms.applyToModel(model); 			// Set custom parms for the model
		return model;
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class simulationClass() {
				
		return HeatBugs.class;

	}

}
