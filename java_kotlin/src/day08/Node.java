package day08;

public record Node(String name, String left, String right) {
    public static Node parseLine(String line) {
        // Format:
        // FSC = (QNS, TMF)
        String cleaned = line.replaceAll("[()]", "");
        cleaned = cleaned.replaceAll(" ", "");
        String[] parts = cleaned.split("=");
        String name = parts[0];
        String left = parts[1].split(",")[0];
        String right = parts[1].split(",")[1];
        return new Node(name, left, right);
    }
}
