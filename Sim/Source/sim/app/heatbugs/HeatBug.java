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

import sim.field.grid.*;
import sim.util.*;
import sim.engine.*;

import uk.ac.soton.simulation.jsit.core.EventSource;
import uk.ac.soton.simulation.jsit.core.EventReceiver;

import org.slf4j.*;

// JSIT++: This is now a source and a receiver for JSIT events
//public /*strictfp*/ class HeatBug implements Steppable
public /*strictfp*/ class HeatBug implements Steppable, EventSource<HeatBug.EventType>, EventReceiver
// JSIT--
    {
    private static final Logger logger = LoggerFactory.getLogger(HeatBug.class);
    
    private static final long serialVersionUID = 1;

    public double idealTemp;
    public double getIdealTemperature() { return idealTemp; }
    public void setIdealTemperature( double t ) { idealTemp = t; }

    public double heatOutput;
    public double getHeatOutput() { return heatOutput; }
    public void setHeatOutput( double t ) { heatOutput = t; }

    public double randomMovementProbability;
    public double getRandomMovementProbability() { return randomMovementProbability; }
    public void setRandomMovementProbability( double t ) { if (t >= 0 && t <= 1) randomMovementProbability = t; }
    public Object domRandomMovementProbability() { return new Interval(0.0,1.0); }
    
    // JSIT++: Add reference to the main model HeatBugs instance
    //public HeatBug( double idealTemp, double heatOutput, double randomMovementProbability) 
    public HeatBug( double idealTemp, double heatOutput, double randomMovementProbability, HeatBugs mainModel)
    // JSIT--
        {
        this.heatOutput = heatOutput;
        this.idealTemp = idealTemp;
        this.randomMovementProbability = randomMovementProbability;
        // JSIT++: Store main model ref and register for receipt of HeatBug events
        this.mainModel = mainModel;
        mainModel.eventMgr.register(HeatBug.class, this);
        // JSIT--
        }
        
    public void addHeat(final DoubleGrid2D grid, final int x, final int y, final double heat)
        {
        grid.field[x][y] += heat;
        // JSIT++: If causes spot to reach max heat, can explode (if model parameter set)!
        //if (grid.field[x][y] > HeatBugs.MAX_HEAT) grid.field[x][y] = HeatBugs.MAX_HEAT;
        if (grid.field[x][y] >= HeatBugs.MAX_HEAT)
            {
            if (mainModel.heatWaveAtMaxHeat)
                {
                grid.field[x][y] = HeatBugs.MAX_HEAT / 2.0d;        // Expends half the heat in the wave!
                mainModel.eventMgr.publish(this, "Bug at (" + x + "," + y + ") launched a heat wave!");
                }
            else
                {
                grid.field[x][y] = HeatBugs.MAX_HEAT;
                }
            }
        // JSIT--
        }
        
    public void step( final SimState state )
        {
        HeatBugs hb = (HeatBugs)state;
        
        Int2D location = hb.buggrid.getObjectLocation(this);
        int myx = location.x;
        int myy = location.y;
        logger.trace("Stepping heat bug at (" + myx + "," + myy + ")");
        
        final int START=-1;
        int bestx = START;
        int besty = 0;
        
        if (state.random.nextBoolean(randomMovementProbability))  // go to random place
            {
            bestx = hb.buggrid.stx(state.random.nextInt(3) - 1 + myx);  // toroidal
            besty = hb.buggrid.sty(state.random.nextInt(3) - 1 + myy);  // toroidal
            }
        else if( hb.valgrid.field[myx][myy] > idealTemp )  // go to coldest place
            {
            for(int x=-1;x<2;x++)
                for (int y=-1;y<2;y++)
                    if (!(x==0 && y==0))
                        {
                        int xx = hb.buggrid.stx(x + myx);    // toroidal
                        int yy = hb.buggrid.sty(y + myy);       // toroidal
                        if (bestx==START ||
                            (hb.valgrid.field[xx][yy] < hb.valgrid.field[bestx][besty]) ||
                            (hb.valgrid.field[xx][yy] == hb.valgrid.field[bestx][besty] && state.random.nextBoolean()))  // not uniform, but enough to break up the go-up-and-to-the-left syndrome
                            { bestx = xx; besty = yy; }
                        }
            }
        else if ( hb.valgrid.field[myx][myy] < idealTemp )  // go to warmest place
            {
            for(int x=-1;x<2;x++)
                for (int y=-1;y<2;y++)
                    if (!(x==0 && y==0))
                        {
                        int xx = hb.buggrid.stx(x + myx);    // toroidal
                        int yy = hb.buggrid.sty(y + myy);       // toroidal
                        if (bestx==START || 
                            (hb.valgrid.field[xx][yy] > hb.valgrid.field[bestx][besty]) ||
                            (hb.valgrid.field[xx][yy] == hb.valgrid.field[bestx][besty] && state.random.nextBoolean()))  // not uniform, but enough to break up the go-up-and-to-the-left syndrome
                            { bestx = xx; besty = yy; }
                        }
            }
        else            // stay put
            {
            bestx = myx;
            besty = myy;
            }

        hb.buggrid.setObjectLocation(this,bestx,besty);
        addHeat(hb.valgrid,bestx,besty,heatOutput);
        }
    
    // JSIT++: Add code for bugs to create JSIT domain events when they cause an area to reach max heat, and
    // this to cause other bugs to 'jump back' from the super-mega heat wave
    
    public static enum EventType { BUG_LAUNCHED_HEAT_WAVE };
    private HeatBugs mainModel;             // Keep reference to the main model
    
    // EventReceiver interface method
    
    @Override
    public void notifyOfEvent(EventSource<?> source) {
        assert source.getEventSourceClass() == HeatBug.class;
        assert source.getLastEventType() == EventType.BUG_LAUNCHED_HEAT_WAVE;
        // 'Jump back' a parametrised distance away from the wave-generating bug!
        Int2D waveCentre = mainModel.buggrid.getObjectLocation((HeatBug) source);
        assert waveCentre != null;
        Int2D myLocation = mainModel.buggrid.getObjectLocation(this);
        assert myLocation != null;
        int afterJumpX = waveCentre.x > myLocation.x
                         ? myLocation.x - mainModel.getWaveJumpBackDistance()
                         : myLocation.x + mainModel.getWaveJumpBackDistance();
        if (afterJumpX >= mainModel.gridWidth) { afterJumpX = mainModel.gridWidth - 1; }
        if (afterJumpX < 0) { afterJumpX = 0; }
        int afterJumpY = waveCentre.y > myLocation.y
                ? myLocation.y - mainModel.getWaveJumpBackDistance()
                : myLocation.y + mainModel.getWaveJumpBackDistance();
        if (afterJumpY >= mainModel.gridHeight) { afterJumpY = mainModel.gridHeight - 1; }
        if (afterJumpY < 0) { afterJumpY = 0; }
        logger.debug("Heat-wave jump from ({},{}) to ({},{})",
                     myLocation.x, myLocation.y, afterJumpX, afterJumpY);
        mainModel.buggrid.setObjectLocation(this, afterJumpX, afterJumpY);      
    }
    
    // EventSource interface methods
    
    @Override
    public Class<?> getEventSourceClass() {
        return HeatBug.class;
    }
    @Override
    public EventType getLastEventType() {
        return EventType.BUG_LAUNCHED_HEAT_WAVE;   // No-one should be calling this before they are notified of an event
    }
    
    // JSIT--

    }
