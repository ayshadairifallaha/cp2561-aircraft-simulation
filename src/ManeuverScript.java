import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManeuverScript {
    public record Maneuver(double seconds, double roll, double pitch, double yaw) {}

    private final List<Maneuver> maneuvers = new ArrayList<>();

    public ManeuverScript(Path csvPath) {
        read(csvPath);
    }

    public ManeuverScript(String csvPath) {
        this(Path.of(csvPath));
    }

    private void read(Path csvPath) {
        try (BufferedReader r = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            boolean headerSeen = false;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                // remove BOM if present on first non-empty line
                if (!headerSeen) {
                    String hdr = trimmed;
                    if (hdr.startsWith("\uFEFF")) {
                        hdr = hdr.substring(1);
                    }
                    if (!hdr.equalsIgnoreCase("seconds,roll,pitch,yaw")) {
                        System.err.println("Error at line " + lineNo + ": expected header 'seconds,roll,pitch,yaw' but found: " + hdr);
                        throw new RuntimeException("Invalid CSV header at line " + lineNo);
                    }
                    headerSeen = true;
                    continue;
                }

                String[] parts = trimmed.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Error at line " + lineNo + ": expected 4 fields but found " + parts.length);
                    throw new RuntimeException("Invalid field count at line " + lineNo);
                }

                double seconds;
                double roll;
                double pitch;
                double yaw;
                try {
                    seconds = Double.parseDouble(parts[0].trim());
                    roll = Double.parseDouble(parts[1].trim());
                    pitch = Double.parseDouble(parts[2].trim());
                    yaw = Double.parseDouble(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error at line " + lineNo + ": non-numeric field - " + e.getMessage());
                    throw new RuntimeException("Non-numeric field at line " + lineNo, e);
                }

                if (roll < -180.0 || roll > 180.0) {
                    System.err.println("Error at line " + lineNo + ": roll out of range (-180..180): " + roll);
                    throw new RuntimeException("Roll out of range at line " + lineNo);
                }
                if (yaw < -180.0 || yaw > 180.0) {
                    System.err.println("Error at line " + lineNo + ": yaw out of range (-180..180): " + yaw);
                    throw new RuntimeException("Yaw out of range at line " + lineNo);
                }
                if (pitch < -90.0 || pitch > 90.0) {
                    System.err.println("Error at line " + lineNo + ": pitch out of range (-90..90): " + pitch);
                    throw new RuntimeException("Pitch out of range at line " + lineNo);
                }

                maneuvers.add(new Maneuver(seconds, roll, pitch, yaw));
            }

            if (!headerSeen) {
                System.err.println("Error: CSV header 'seconds,roll,pitch,yaw' not found in file: " + csvPath);
                throw new RuntimeException("Missing CSV header");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            throw new RuntimeException("Failed to read CSV file", e);
        }
    }

    public List<Maneuver> getManeuvers() {
        return Collections.unmodifiableList(maneuvers);
    }
    
    public boolean isLooping() {
        return true;
    }
}