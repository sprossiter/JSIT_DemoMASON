======================
JSIT MASON Demo README
======================

This is a demonstration simulation model for the Java Simulation Infrastructure
Toolkit (JSIT_). It adapts the Heat Bugs demo provided by MASON_ so that it uses
the JSIT library, with examples of using the various JSIT features. It also
helps demonstrate how to integrate with JSIT without using a
simulation-framework-specific helper library (which only currently exists for
AnyLogic_).

JSIT_DemoMASON is developed by Stuart Rossiter (as part of research at the
University of Southampton, UK). Contact him at stuart.p.rossiter@gmail.com.

It is open source software released under the LGPL license (for further details,
see the Licensing_ section). The source code is stored on GitHub at
`https://github.com/sprossiter/JSIT_DemoMASON
<https://github.com/sprossiter/JSIT_DemoMASON>`_.

Installation
============

Perform the following steps.

1. Either:

    i) Download the latest JSIT_DemoMASON distribution. This includes the source
       code and the MASON v18 JAR library. (Model source code is included, rather
       than a JAR file, because this software is only intended to highlight how to
       code a simulation model using JSIT and MASON, not as a useful model in
       itself.)

    ii) Clone the JSIT_DemoMASON Git repository, download the latest MASON_ library
        (from its site) and add that to the ``Sim\Libs`` folder.

2. Download the latest JSIT_ distribution and copy the contents of its ``lib``
   folder into the ``lib`` folder of the JSIT_DemoMASON code.

3. Compile the model code, referencing all the ``lib`` folder libraries as
   dependencies.

4. Run either of the experiments (``DefaultDemo`` or ``HeatWaveDemo`` in package
   ``sim.app.heatbugs.experiments``) with a command-line parameter ``GUI`` or
   ``NOGUI`` to run in GUI or batch mode. The ``HeatWaveDemo`` is the same as the
   default one, but sets up added model parameters so that bugs produce 'heat
   waves' when a location reaches the max. temperature (which was written just to
   show a way to use JSIT events and event-handling; it makes no sense for the
   'science of heat bugs', if such a thing exists!).

.. _Licensing:

Licensing
=========

.. image:: lgplv3-88x31.png

JSIT_DemoMASON is distributed under the GNU LGPL V3 license, which has a copying
permission statement as below. (See the full `LGPL license`_ and `GPL license`_
for more details.)

::

        Copyright University of Southampton 2015
        
        JSIT_DemoMASON is free software: you can redistribute it and/or modify
        it under the terms of the GNU Lesser General Public License as published
        by the Free Software Foundation, either version 3 of the License, or (at
        your option) any later version.
        
        JSIT_DemoMASON is distributed in the hope that it will be useful, but
        WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
        General Public License for more details.
        
        You should have received a copy of the GNU Lesser General Public License along
        with JSIT_DemoMASON. If not, see <http://www.gnu.org/licenses/>.

The original MASON demo code it adapts was released under the Academic Free
Licence v3 (AFL_), which permits for relicensing as done here. The attribution
text for this is as below:

::

        Copyright 2006 by Sean Luke and George Mason University
        Licensed under the Academic Free License version 3.0
        See the file "LICENSE" for more information

The original LICENSE file is preserved here as `masonLicense.txt
<masonLicense.txt>`_.

Dependencies
------------

JSIT_DemoMASON requires Java 1.6+ and the main MASON_ library (included in the
distribution). It also requires all the JSIT_ libraries. All these third-party
libraries with LGPL-compatible licenses are shown below (nested items show
sub-dependencies).

* MASON_

* JSIT_

  * Logback_
  
  * SLF4J_
  
  * XStream_
  
    - XMLPull_
  
    - Xpp3_
  
  * `Apache Commons Codec`_
  
  * `Apache Commons IO`_
  
  * `Apache Commons Configuration`_
  
    - `Apache Commons Lang`_ 2.x
  
    - `Apache Commons Logging`_
  
  * `Apache Subversion`_ JavaHL (Java binding for SVN)

The links above can be used to get source code for these dependencies.

Copies of all their licences and any required attribution (notice) files are
included in the JSIT_DemoMASON and JSIT distributions (in the ``lib`` folder
together with the dependencies themselves).

.. _JSIT: https://github.com/sprossiter/JSIT
.. _MASON: http://cs.gmu.edu/~eclab/projects/mason
.. _AnyLogic: http://www.anylogic.com
.. _AFL: http://opensource.org/licenses/afl-3.0
.. _Logback: http://logback.qos.ch
.. _SLF4J: http://www.slf4j.org
.. _XStream: http://xstream.codehaus.org
.. _XMLPull: http://www.xmlpull.org
.. _Xpp3: http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1
.. _Apache Commons Codec: http://commons.apache.org/proper/commons-codec
.. _Apache Commons IO: http://commons.apache.org/proper/commons-io
.. _Apache Commons Configuration: http://commons.apache.org/proper/commons-configuration
.. _Apache Commons Lang: http://commons.apache.org/proper/commons-lang
.. _Apache Commons Logging: http://commons.apache.org/proper/commons-logging
.. _Apache Subversion: https://subversion.apache.org/
.. _LGPL license: lgpl.txt
.. _GPL license: gpl.txt
