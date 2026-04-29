import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// AIService handles all communication with OpenAI API
// Sends task details and gets back priority suggestion
public class AIService {

private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    // Private - URL is internal implementation detail
    private static final String API_URL =
        "https://api.openai.com/v1/chat/completions";

    // Public - TaskHandler calls this to get priority
    // Returns String array: [0] = priority, [1] = reason
    public static String[] getPriority(String title, String description) {
        try {
            // Build prompt - force structured format so we can parse easily
            // String prompt = "Given this task suggest a priority. Title: "

            //     + title
            //     + " Description: "
            //     + description
            //     + " Reply ONLY in this format:"
            //     + " PRIORITY: high|medium|low"
            //     + " REASON: one short sentence";

            String prompt =
"Classify task priority as HIGH, MEDIUM, or LOW.\n" +
"Return ONLY this format:\n" +
"{\"priority\":\"HIGH|MEDIUM|LOW\",\"reason\":\"short reason\"}\n\n" +
"Task: " + title + "\nDescription: " + description;

            // Build JSON request body for OpenAI
            String body = "{\"model\":\"gpt-3.5-turbo\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\""
                + prompt
                + "\"}],\"max_tokens\":60}";

            // Open HTTP connection to OpenAI
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send request body to OpenAI
            conn.getOutputStream().write(
                body.getBytes(StandardCharsets.UTF_8));

            // Read OpenAI response
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String raw = response.toString();

            // BAD AI OUTPUT FIX #4
            // AI assumed content field always exists
            // But when API key is wrong OpenAI returns error JSON
            // Added defensive check before parsing
            if (!raw.contains("\"content\":\"")) {
                System.out.println("AI response missing content: " + raw);
                return new String[]{"medium", "AI service unavailable"};
            }

            // Extract AI reply text from OpenAI JSON response
            int start = raw.indexOf("\"content\":\"") + 11;
            int end = raw.indexOf("\"", start);
            String content = raw.substring(start, end)
                .replace("\\n", "\n");

            // Parse priority and reason from AI reply
            String priority = "medium";
            String reason = "Default priority";

            for (String l : content.split("\n")) {
                if (l.startsWith("PRIORITY:")) {
                    priority = l.replace("PRIORITY:", "")
                        .trim().toLowerCase();
                }
                if (l.startsWith("REASON:")) {
                    reason = l.replace("REASON:", "").trim();
                }
            }

            return new String[]{priority, reason};

        } catch (Exception e) {
            // If AI fails - return safe default
            // App should never crash because AI is unavailable
            System.out.println("AI Error: " + e.getMessage());
            return new String[]{"medium", "AI service unavailable"};
        }
    }
}