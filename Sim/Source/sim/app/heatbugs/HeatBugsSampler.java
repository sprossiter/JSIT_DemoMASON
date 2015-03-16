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

import ec.util.MersenneTwisterFast;
import uk.ac.soton.simulation.jsit.core.Distribution;
import uk.ac.soton.simulation.jsit.core.DummySampler;

public class HeatBugsSampler extends DummySampler {
    
    // ************************** Class Fields *****************************************

    private static final long serialVersionUID = 1L;
    
    
    // ************************* Instance Fields ***************************************
    
    private final MersenneTwisterFast random;

    
    // ************************** Constructors *****************************************
    
    public HeatBugsSampler(MersenneTwisterFast random) {
        
        super();
        this.random = random;
        
    }

    
    // **************** Protected/Package-Access Instance Methods **********************
    
    @Override
    protected boolean distIsSupported(Distribution dist) {
        
        // Just say we support all here for convenience in this example code!
        return true;
        
    }

    /*
     * Bernoulli (probabilistic trial). To fit 1-K raw
     * scheme, this should return 1 (failure) or 2 (success)
     */
    @Override
    protected int sampleBernoulli(double p) {
        
        return random.nextBoolean(p) == true ? 2 : 1;
        
    }

    /*
     * Continuous uniform dist. Only [1, max] version since use ranges to
     * transform to others
     */
    @Override
    protected int sampleUniformDiscrete(int max) {
        
        return random.nextInt(max) + 1;         // MersenneTwister samples [0, n-1]
        
    }

}
