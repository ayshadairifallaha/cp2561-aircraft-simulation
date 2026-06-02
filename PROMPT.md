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
# PERSON 2 - TASK 2 (Observer Pattern)
# ============================================

## Session 1 - 2026-06-02 14:30
**Task:** Task 2 (Observer pattern)
**Tool:** GitHub Copilot Chat
**Prompt:**
> Implement Observer pattern in DirectionControl. Add CopyOnWriteArrayList for listeners, addListener/removeListener methods, and notify listeners in update() after value changes. Also create DirectionControlListener interface.

**Suggestion summary:**
Copilot generated the interface and all required code for DirectionControl with proper thread-safe notification.

**Decision:** Accepted with modifications
**Why:** Added println statements for debugging output.

---

## Session 2 - 2026-06-02 15:00
**Task:** Task 2 (Observer pattern - GUI)
**Tool:** GitHub Copilot Chat
**Prompt:**
> Replace polling in AircraftGUI with observer pattern. Add volatile fields, register listeners in constructor, use volatile fields in Swing timer. Include thread-safety comment.

**Suggestion summary:**
Copilot provided complete code changes for AircraftGUI including volatile fields, listener registration, and timer modification.

**Decision:** Accepted as written
**Why:** Correct implementation with required thread-safety comment.