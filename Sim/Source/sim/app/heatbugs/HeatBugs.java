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
    
    ************************************************************************
    This file is an altered and relicensed version of the original released
    by MASON under the AFL v3 license, as below.
    ************************************************************************

    Copyright 2006 by Sean Luke and George Mason University
    Licensed under the Academic Free License version 3.0
    See the file "LICENSE" for more information
*/

package sim.app.heatbugs;

import java.io.File;
import java.io.IOException;

import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;

// JSIT++
import org.slf4j.*;

import uk.ac.soton.simulation.jsit.core.DistUniformDiscrete;
import uk.ac.soton.simulation.jsit.core.EventManager;
import uk.ac.soton.simulation.jsit.core.EventSource;
import uk.ac.soton.simulation.jsit.core.MainModel;
import uk.ac.soton.simulation.jsit.core.Sampler.NumericCategory;
import uk.ac.soton.simulation.jsit.core.StochasticAccessorMDC;
// JSIT--

public /*strictfp*/ class HeatBugs extends SimState implements MainModel, EventSource<HeatBugs.EventType>
    {
    private static final long serialVersionUID = 1;

    public double minIdealTemp = 17000;
    public double maxIdealTemp = 31000;
    public double minOutputHeat = 6000;
    public double maxOutputHeat = 10000;

    public double evaporationRate = 0.993;
    public double diffusionRate = 1.0;
    public static final double MAX_HEAT = 32000;
    public double randomMovementProbability = 0.1;

    public int gridHeight;
    public int gridWidth;
    public int bugCount;
    HeatBug[] bugs;
    
    public double getMinimumIdealTemperature() { return minIdealTemp; }
    public void setMinimumIdealTemperature( double temp ) { if( temp <= maxIdealTemp ) minIdealTemp = temp; }
    public double getMaximumIdealTemperature() { return maxIdealTemp; }
    public void setMaximumIdealTemperature( double temp ) { if( temp >= minIdealTemp ) maxIdealTemp = temp; }
    public double getMinimumOutputHeat() { return minOutputHeat; }
    public void setMinimumOutputHeat( double temp ) { if( temp <= maxOutputHeat ) minOutputHeat = temp; }
    public double getMaximumOutputHeat() { return maxOutputHeat; }
    public void setMaximumOutputHeat( double temp ) { if( temp >= minOutputHeat ) maxOutputHeat = temp; }
    public double getEvaporationConstant() { return evaporationRate; }
    public void setEvaporationConstant( double temp ) { if( temp >= 0 && temp <= 1 ) evaporationRate = temp; }
    public Object domEvaporationConstant() { return new Interval(0.0,1.0); }
    public double getDiffusionConstant() { return diffusionRate; }
    public void setDiffusionConstant( double temp ) { if( temp >= 0 && temp <= 1 ) diffusionRate = temp; }
    public Object domDiffusionConstant() { return new Interval(0.0, 1.0); }
    public double getRandomMovementProbability() { return randomMovementProbability; }
        
    public double[] getBugXPos() {
        try
            {
            double[] d = new double[bugs.length];
            for(int x=0;x<bugs.length;x++)
                {
                d[x] = ((Int2D)(buggrid.getObjectLocation(bugs[x]))).x;
                }
            return d;
            }
        catch (Exception e) { return new double[0]; }
        }
    
    public double[] getBugYPos() {
        try
            {
            double[] d = new double[bugs.length];
            for(int x=0;x<bugs.length;x++)
                {
                d[x] = ((Int2D)(buggrid.getObjectLocation(bugs[x]))).y;
                }
            return d;
            }
        catch (Exception e) { return new double[0]; }
        }


    public void setRandomMovementProbability( double t )
        {
        if (t >= 0 && t <= 1)
            {
            randomMovementProbability = t;
            for( int i = 0 ; i < bugCount ; i++ )
                if (bugs[i]!=null)
                    bugs[i].setRandomMovementProbability( randomMovementProbability );
            }
        }
    public Object domRandomMovementProbability() { return new Interval(0.0, 1.0); }
        
    public double getMaximumHeat() { return MAX_HEAT; }

    // we presume that no one relies on these DURING a simulation
    public int getGridHeight() { return gridHeight; }
    public void setGridHeight(int val) { if (val > 0) gridHeight = val; }
    public int getGridWidth() { return gridWidth; }
    public void setGridWidth(int val) { if (val > 0) gridWidth = val; }
    public int getBugCount() { return bugCount; }
    public void setBugCount(int val) { if (val >= 0) bugCount = val; }
    
    public DoubleGrid2D valgrid;
    public DoubleGrid2D valgrid2;
    public SparseGrid2D buggrid;
    

    /** Creates a HeatBugs simulation with the given random number seed. */
    public HeatBugs(long seed)
        {
        this(seed, 100, 100, 100);
        }
        
    public HeatBugs(long seed, int width, int height, int count)
        {
        super(seed);      
        gridWidth = width; gridHeight = height; bugCount = count;        
        createGrids();
        }

    protected void createGrids()
        {
        bugs = new HeatBug[bugCount];
        valgrid = new DoubleGrid2D(gridWidth, gridHeight,0);
        valgrid2 = new DoubleGrid2D(gridWidth, gridHeight, 0);
        buggrid = new SparseGrid2D(gridWidth, gridHeight);      
        }
    
    ThreadedDiffuser diffuser = null;
        
    /** Resets and starts a simulation */
    public void start()
        {
        super.start();  // clear out the schedule
        
        // JSIT++: Create needed objects and create/register JSIT distributions
        modelInit = new HeatBugsModelInitialiser("TestRun", this, random);
        // Capture parms as they are now at model start time (which may be DIFFERENT from model
        // instantiation time for a GUI-based run of the model)
        modelParms = ModelParms.createParmsFromModel(this);
        eventMgr = new EventManager();
        HeatBugs.bugInitWidth.addForRun(new DistUniformDiscrete<NumericCategory>(0, gridWidth - 1));
        HeatBugs.bugInitHeight.addForRun(new DistUniformDiscrete<NumericCategory>(0, gridHeight - 1));
        try {
            modelInit.saveModelSettings();
            modelInit.finaliseStochRegistrations();			// Also completes writing of settings file
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing model settings", e);
        }
        // Add sample logging statement
        logger.info("Creating HeatBugs model with " + gridWidth + "x" + gridHeight + " grid...");
        // JSIT--
        
        // make new grids
        createGrids();
    
        // Schedule the heat bugs -- we could instead use a RandomSequence, which would be faster
        // But we spend no more than 3% of our total runtime in the scheduler max, so it's not worthwhile
        for(int x=0;x<bugCount;x++)
            {
            // JSIT++: Bugs now have a ref back to this object, and replace the random draws for bug x,y
        	// coords with draws from JSIT distributions
        	
        	//bugs[x] = new HeatBug(random.nextDouble() * (maxIdealTemp - minIdealTemp) + minIdealTemp,
            //        random.nextDouble() * (maxOutputHeat - minOutputHeat) + minOutputHeat,
            //       randomMovementProbability);
        	//buggrid.setObjectLocation(bugs[x],random.nextInt(gridWidth),random.nextInt(gridHeight));
        	
            bugs[x] = new HeatBug(random.nextDouble() * (maxIdealTemp - minIdealTemp) + minIdealTemp,
                random.nextDouble() * (maxOutputHeat - minOutputHeat) + minOutputHeat,
                randomMovementProbability, this);
            int bugX = bugInitWidth.getForRun().sampleInt();
            int bugY = bugInitHeight.getForRun().sampleInt();
            buggrid.setObjectLocation(bugs[x], bugX, bugY);
            // JSIT--
            
            schedule.scheduleRepeating(bugs[x]);
            
            // JSIT++: Create an event on bug creation
            lastEventType = EventType.CREATED_BUG;
            eventMgr.publish(this, "Created heat bug with ideal temp " + bugs[x].idealTemp
                                   + ", heat output " + bugs[x].heatOutput + " at (" + bugX + "," + bugY + ")");
            // JSIT--
            }
                        
        // Here we're going to pick whether or not to use Diffuser (the default) or if
        // we're really going for the gusto and have multiple processors on our computer, we
        // can use our multithreaded super-neato ThreadedDiffuser!  On a Power Mac G5 with
        // two processors, we get almost a 90% speedup in the underlying model because *so*
        // much time is spent in the Diffuser.
                            
        // Schedule the diffuser to happen after the heatbugs
        if (HeatBugs.availableProcessors() >  1)  // yay, multi-processor!
            {
            // store away the ThreadedHexaDiffuser so we can call cleanup() on it later in our stop() method.
            diffuser = new ThreadedDiffuser(2);
            schedule.scheduleRepeating(Schedule.EPOCH,1,diffuser,1);
            }
        else
            schedule.scheduleRepeating(Schedule.EPOCH,1,new Diffuser(),1);
        }
    
    // JSIT++: Bug in original HeatBugs code; should have been finish() not stop()!
    //public void stop()
    @Override
    // JSIT--
    public void finish()
        {
        if (diffuser != null) diffuser.cleanup();
        diffuser = null;
        // JSIT++: Add JSIT model-end logic
        logger.info("Model finish() called; finalising JSIT...");
        modelInit.onMainModelDestroy();
        // JSIT--
        }
    
    /** This little function calls Runtime.getRuntime().availableProcessors() if it's available,
        else returns 1.  That function is nonexistent in Java 1.3.1, but it exists in 1.4.x.
        So we're doing a little dance through the Reflection library to call the method tentatively!
        The value returned by Runtime is the number of available processors on the computer.  
        If you're only using 1.4.x, then all this is unnecessary -- you can just call
        Runtime.getRuntime().availableProcessors() instead. */
    public static int availableProcessors()
        {
        Runtime runtime = Runtime.getRuntime();
        try { return ((Integer)runtime.getClass().getMethod("availableProcessors", (Class[])null).
                invoke(runtime,(Object[])null)).intValue(); }
        catch (Exception e) { return 1; }  // a safe but sometimes wrong assumption!
        }
        
    
    // JSIT++: Separate out experiments
    //public static void main(String[] args)
    //    {
    //    doLoop(HeatBugs.class, args);
    //    System.exit(0);
    //    }
    // JSIT--
    
    // JSIT++: ADDITIONAL CODE FOR JSIT 'CONVERSION'
       
    private static final Logger logger = LoggerFactory.getLogger(HeatBugs.class);
    
    // There will not be parallel runs in the same JVM so we don't need to use StochasticAccessorMDC
    // 'wrappers'
    private static StochasticAccessorMDC<DistUniformDiscrete<NumericCategory>> bugInitWidth
             = new StochasticAccessorMDC<DistUniformDiscrete<NumericCategory>>(HeatBugs.class, "bugInitWidth");
    private static StochasticAccessorMDC<DistUniformDiscrete<NumericCategory>> bugInitHeight
             = new StochasticAccessorMDC<DistUniformDiscrete<NumericCategory>>(HeatBugs.class, "bugInitHeight");
    
    /*
     * Static member class to represent the model parameters, using object types so that setting them
     * to null implies using the default for this parameter. This is used as part of a crude scheme to
     * separate model from experiment without distorting the existing demo code too much. Instances of
     * this can be used to set the parms of the model, or can be created with the current model
     * parameters for serialisation as the model settings.
     * 
     * If done 'properly', you can, for example, use null values to represent parameters for optional model 
     * components which aren't needed.
     */
    public static class ModelParms {
        public Double minIdealTemp;      
        public Double maxIdealTemp;
        public Double minOutputHeat;
        public Double maxOutputHeat;

        public Double evaporationRate;
        public Double diffusionRate;
        public Double randomMovementProbability;

        public Integer gridHeight;
        public Integer gridWidth;
        public Integer bugCount;
        
        public Boolean heatWaveAtMaxHeat;
        public Integer waveJumpBackDistance;
               
        public static ModelParms createParmsFromModel(HeatBugs model) {
            return new ModelParms(model.minIdealTemp,
            					  model.maxIdealTemp,
            					  model.minOutputHeat,
            					  model.maxOutputHeat,
            					  model.evaporationRate,
            					  model.diffusionRate,
            					  model.randomMovementProbability,
            					  model.gridHeight,
            					  model.gridWidth,
            					  model.bugCount,
            					  model.heatWaveAtMaxHeat,
            					  model.waveJumpBackDistance);
        }
        
        public ModelParms(Double minIdealTemp,
        		 		  Double maxIdealTemp,
        		 		  Double minOutputHeat,
        		 		  Double maxOutputHeat,
        		 		  Double evaporationRate,
        		 		  Double diffusionRate,
        		 		  Double randomMovementProbability,
        		 		  Integer gridHeight,
        		 		  Integer gridWidth,
        		 		  Integer bugCount,  
        		 		  Boolean heatWaveAtMaxHeat,
        		 		  Integer waveJumpBackDistance) {
        	this.minIdealTemp = minIdealTemp;
        	this.maxIdealTemp = maxIdealTemp;
        	this.minOutputHeat = minOutputHeat;
        	this.maxOutputHeat = maxOutputHeat;
        	this.evaporationRate = evaporationRate;
        	this.diffusionRate = diffusionRate;
        	this.randomMovementProbability = randomMovementProbability;
        	this.gridHeight = gridHeight;
        	this.gridWidth = gridWidth;
        	this.bugCount = bugCount;
        	this.heatWaveAtMaxHeat = heatWaveAtMaxHeat;
        	this.waveJumpBackDistance = waveJumpBackDistance;
        }
        
        public void applyToModel(HeatBugs model) {
        	if (minIdealTemp != null) {
        		model.setMinimumIdealTemperature(minIdealTemp);
        	}
        	if (maxIdealTemp != null) {
        		model.setMaximumIdealTemperature(maxIdealTemp);
        	}
        	if (minOutputHeat != null) {
        		model.setMinimumOutputHeat(minOutputHeat);
        	}
        	if (maxOutputHeat != null) {
        		model.setMaximumOutputHeat(maxOutputHeat);
        	}
        	if (evaporationRate != null) {
        		model.setEvaporationConstant(evaporationRate);
        	}
        	if (diffusionRate != null) {
        		model.setDiffusionConstant(diffusionRate);
        	}
        	if (randomMovementProbability != null) {
        		model.setRandomMovementProbability(randomMovementProbability);
        	}
        	if (gridHeight != null) {
        		model.setGridHeight(gridHeight);
        	}
        	if (gridWidth != null) {
        		model.setGridWidth(gridWidth);
        	}
        	if (bugCount != null) {
        		model.setBugCount(bugCount);
        	}
        	if (heatWaveAtMaxHeat != null) {
        		model.setHeatWaveAtMaxHeat(heatWaveAtMaxHeat);
        	}
        	if (waveJumpBackDistance != null) {
        		model.setWaveJumpBackDistance(waveJumpBackDistance);
        	}
        }
    };
    
    public static enum EventType { CREATED_BUG };
    
    // Extra model parameters to turn max-heat-explosion on or off, and the distance others jump back!
    public boolean heatWaveAtMaxHeat = false;
    public boolean getHeatWaveAtMaxHeat() { return heatWaveAtMaxHeat; }
    public void setHeatWaveAtMaxHeat(boolean heatWaveAtMaxHeat) { this.heatWaveAtMaxHeat = heatWaveAtMaxHeat; }
    public int waveJumpBackDistance = 2;
    public int getWaveJumpBackDistance() { return waveJumpBackDistance; }
    public void setWaveJumpBackDistance(int distance) {
        if (distance > 0 && distance < gridWidth && distance < gridHeight) {
            waveJumpBackDistance = distance;
        }
    }
    
    private HeatBugsModelInitialiser modelInit  = null;     // Created in start()
    ModelParms modelParms;
    EventManager eventMgr = null;
    private EventType lastEventType = null;                 // Start as null
    
    // MainModel interface methods
    // Because some of these are get methods, MASON interprets them as model parameters and thus shows
    // them in the GUI console Model tab. This is something to sort out when writing the MASON helper
    // library
    
    @Override
    public void doAllStaticStochRegistration() {
        
        // Nothing to do
        
    }
    
    @Override
    public String getDiagnosticLogFormattedSimTime() {
        
        return getEventsLogFormattedSimTime();
        
    }
    
    @Override
    public String getEventsLogFormattedSimTime() {
        
        return Double.toString(schedule.getTime());
        
    }
    
    @Override
    public String getInputsBasePath() {
        
        return "Experiments" + File.separator + "Inputs";
        
    }
    
    @Override
    public String getOutputsBasePath() {
        
        return "Experiments" + File.separator + "Outputs";
        
    }
    
    @Override
    public void runSpecificEnvironmentSetup() {
        
        // Nothing to do
        
    }
    
    // EventSource interface methods
    
    @Override
    public Class<?> getEventSourceClass() {
       
        return HeatBugs.class;
        
    }
    @Override
    public EventType getLastEventType() {
        
        // Will be null before any events generated
        return lastEventType;
        
    }
    // JSIT--
    
}
