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
import java.io.File;

/**
 * JSIT demo class to represent the default experiment (running the model in GUI or non-GUI form with
 * the default parameter values).
 */
public class DefaultDemo {
	
	public static void main(String[] args) {
			
		// Useful to check working directory is where you think it is and that
		// correct set of libraries on the classpath
		System.out.println("Working dir: " + new File(".").getAbsolutePath());
		System.out.println("Classpath: " + System.getProperty("java.class.path"));
		
		if (args.length != 1 || !(args[0].equals("GUI") || args[0].equals("NOGUI"))) {
			throw new IllegalArgumentException("Requires one GUI or NOGUI argument");
		}	
		
		if (args[0].equals("GUI")) {
			new HeatBugsWithUI().createController();
		}
		else {
			SimState.doLoop(HeatBugs.class, args);
			System.exit(0);
		}

	}

}
