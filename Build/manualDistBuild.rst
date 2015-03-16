Manual Distribution Build
=========================

Pending an automated Ant build for this, here are the manual steps:

1. Copy the code folder to one called JSIT_DemoMASON_<release>.

2. Remove bin folder, Build folder, .git folder, and any IDE-specific
files (e.g., .project and .classpath Eclipse files).
(Leave .gitignore files since they cause no issues really.)

3. Remove any JSIT commit files (workingChanges.txt, jsitCommitHistory.txt).

4. Make sure Sim/Libs contains only the MASON JAR.

5. Make sure Experiments/Outputs contains only the latest run.

6. ZIP the whole folder up and post as the distribution.
 