# ============================================
# PERSON 1 (Aysha) - TASK 1 
# ============================================

## Session 1 - 2026-06-01, 2:15 PM
**Task:** Task 1 - Create ManeuverScript class
**Tool:** GitHub Copilot Chat
**Prompt:**
> Create a Java class ManeuverScript that reads CSV files with header "seconds,roll,pitch,yaw". Skip blank lines and comment lines starting with #. Validate each line has 4 numeric fields, with roll/yaw between -180/180 and pitch between -90/90. On error, print to System.err with line number and throw RuntimeException. Store maneuvers in a List of a record called Maneuver. Provide getManeuvers() method.

**Suggestion Summary:**
Copilot generated a complete ManeuverScript class that includes:
- Record for Maneuver
- Handles BOM characters in UTF-8 files
- Validates CSV header
- Validates field count (exactly 4)
- Validates numeric values with proper error messages
- Validates roll/yaw/pitch ranges
- Prints errors to System.err with line numbers
- Throws RuntimeException after each error
- Returns unmodifiable list from getManeuvers()

**Decision:** Accepted with one modification
**Why:** The code perfectly matches all spec requirements. I only added the required isLooping() method that returns true, which the spec explicitly requires for Task 1.

**File created:** src/ManeuverScript.java

## Session 2 - 2026-06-01, 2:50 PM
**Task:** Task 1 - Create CSV test files
**Tool:** GitHub Copilot Chat
**Prompt:**
> Create two CSV files for testing the maneuver script. default_maneuvers.csv should contain the original sequence from the starter code. error_maneuvers.csv should have at least 3 different types of errors including wrong field count, non-numeric value, and out-of-range values.

**Suggestion Summary:**
Copilot generated both CSV files with the correct format. default_maneuvers.csv has 7 maneuvers matching the original. error_maneuvers.csv has 4 different errors: missing field, non-numeric, roll out of range, pitch out of range.

**Decision:** Accepted as written
**Why:** The files meet all requirements and properly test the validation logic.

**Files created:** default_maneuvers.csv, error_maneuvers.csv

## Session 3 - 2026-06-02, 9:44 AM
**Task:** Task 1 - Commit 3: Modify Main.java to integrate ManeuverScript
**Tool:** GitHub Copilot Chat
**Prompt:**
> I need to complete Task 1 of my aircraft simulation project by modifying Main.java to integrate the ManeuverScript class. Please help me make ALL of these changes to Main.java: 1. Add imports for java.io.File and java.util.List, 2. Add code to parse --script command line argument, 3. If --script provided load that file, if not look for default_maneuvers.csv, 4. If file not found print error and exit, 5. Pass ManeuverScript to createAutomatedDemoThread, 6. Rewrite createAutomatedDemoThread to loop through maneuvers from the script.

**Suggestion Summary:**
Copilot provided a complete modified Main.java. The createAutomatedDemoThread method it generated calculated time deltas between maneuvers using cycleLength and nextSeconds, which is more complex than needed. The code worked but included logic for handling non-sequential timestamps.

**Decision:** Accepted with modifications
**Why:** I used Copilot's code for the script loading part (lines 160-190) but simplified the createAutomatedDemoThread method. The spec only requires sequential execution with looping, not precise delta timing. The simplified version is easier to understand and explain in the demo. I kept Copilot's error handling and file loading logic.

**Files modified:** src/Main.java

**Changes made:**
- Added imports for java.io.File and java.util.List
- Added script loading with --script flag and default_maneuvers.csv fallback
- Added error handling with System.err and System.exit(1)
- Replaced hardcoded demo thread with script-driven version
- Simplified the loop to use sequential index with reset to 0 at end

# ============================================
# PERSON 2 (Tim) - TASK 2 (Observer Pattern)
# ============================================

## Session 1 - 2026-06-02 10:30 AM 
**Task:** Task 2 (Observer pattern)
**Tool:** GitHub Copilot Chat
**Prompt:**
> Implement Observer pattern in DirectionControl. Add CopyOnWriteArrayList for listeners, addListener/removeListener methods, and notify listeners in update() after value changes. Also create DirectionControlListener interface.

**Suggestion summary:**
Copilot generated the interface and all required code for DirectionControl with proper thread-safe notification.

**Decision:** Accepted with modifications
**Why:** Added println statements for debugging output.

---

## Session 2 - 2026-06-02 11:00 AM
**Task:** Task 2 (Observer pattern - GUI)
**Tool:** GitHub Copilot Chat
**Prompt:**
> Replace polling in AircraftGUI with observer pattern. Add volatile fields, register listeners in constructor, use volatile fields in Swing timer. Include thread-safety comment.

**Suggestion summary:**
Copilot provided complete code changes for AircraftGUI including volatile fields, listener registration, and timer modification.

**Decision:** Accepted as written
**Why:** Correct implementation with required thread-safety comment.

# ============================================
# PERSON 1 (Aysha) -  Task 3 - (Create SupervisedRunner class)
# ============================================

## Session 4 - 2026-06-09 10:46 AM
**Task:** Task 3 - Create SupervisedRunner class
**Tool:** GitHub Copilot Chat
**Prompt:**
> I need to create a SupervisedRunner class in Java for an aircraft simulation that makes worker threads self-healing. Requirements: 1. Implements Runnable, 2. Constructor takes workerName, workerTask, and AtomicBoolean runningFlag, 3. Exponential backoff from 100ms to 5 seconds (doubling), 4. Restart budget: 5 failures in 30 seconds = give up, 5. Reset backoff after 10 seconds of success, 6. Log failures with worker name and stack trace. The workerTask is an infinite loop, so track time since last failure for success detection.

**Suggestion Summary:**
Copilot generated a complete SupervisedRunner class that uses a Deque to track failure timestamps within the 30-second budget window, exponential backoff with Math.min(backoff * 2, maxBackoffMs), an AtomicReference to capture exceptions from the worker thread, and a monitor loop that checks for failures and tracks success window.

**Decision:** Accepted with modifications
**Why:** Added console println statements alongside the logger to ensure output is visible in the terminal. The core logic is correct and handles infinite worker loops properly.

**File created:** src/SupervisedRunner.java

# ============================================
# PERSON 2 (Tim) - TASK 3 (Editing Main.java)
# ============================================

## Session 3 - 2026-06-14 5:00PM
**Task:** Task 3 - Editing Main.java
**Tool:** GitHub Copilot Chat
**Prompt:**
> check if the map contains the key "inject-failures". Set the injectFailures variable to true if present. Print a warning message

**Decision:** Accepted 

**File edited:** src/Main.java

## Session 3 - 2026-06-14 5:45PM
**Task:** Task 3 - Editing Main.java
**Tool:** GitHub Copilot Chat
**Prompt:**
> replace the createTurbulenceThread method in Main.java with a new method called createTurbulenceTask that returns runnable instead of thread. add failure injection logic. throw a RuntimeException at 3 6 and 9 seconds.

**Decision:** Accepted 

**File edited:** src/Main.java

## Session 3 - 2026-06-14 5:45PM
**Task:** Task 3 - Editing Main.java
**Tool:** GitHub Copilot Chat
**Prompt:**
> replace the createAutomatedDemoThread method in Main.java with a new method called createAutomatedDemoTask that returns runnable instead of thread. keep all the existing maneuver execution logic the same.

**Decision:** Accepted 

**File edited:** src/Main.java