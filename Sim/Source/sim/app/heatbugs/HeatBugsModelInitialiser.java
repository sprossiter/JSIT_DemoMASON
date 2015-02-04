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
package sim.app.heatbugs;

import java.io.BufferedWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import ec.util.MersenneTwisterFast;
import uk.ac.soton.simulation.jsit.core.MainModel;
import uk.ac.soton.simulation.jsit.core.ModelInitialiser;
import uk.ac.soton.simulation.jsit.core.RunEnvironmentSettings;
import uk.ac.soton.simulation.jsit.core.Sampler;

public class HeatBugsModelInitialiser extends ModelInitialiser {

    // ************************* Instance Fields ***************************************
    
    private final MersenneTwisterFast random;
    private final RunEnvironmentSettings envSettings;

    
    // ************************** Constructors *****************************************
    
    public HeatBugsModelInitialiser(String experimentName,
                                    MainModel modelMain,
                                    MersenneTwisterFast random) {
        
        super(experimentName, modelMain);
        assert modelMain instanceof HeatBugs;           // HeatBugs specific initialiser!
        this.random = random;
        this.envSettings = new RunEnvironmentSettings(modelMain);

    }

    
    // **************** Protected/Package-Access Instance Methods **********************
    
    @Override
    protected Sampler createFrameworkSpecificSampler() {
        
        return new HeatBugsSampler(random);
        
    }

    @Override
    protected void setupForInfoSerialisation(XStream xstream) {
        
        // Nothing to do

    }

    @Override
    protected void writeModelSettings(XStream xstream, BufferedWriter writer)
            throws IOException {

        writer.write(xstream.toXML(envSettings));
        writer.newLine();      
        // Use the model-parms-only 'struct' instance for the settings
        xstream.alias("modelParms", HeatBugs.ModelParms.class);
        writer.write(xstream.toXML(((HeatBugs) modelMain).modelParms));        

    }
      
}
