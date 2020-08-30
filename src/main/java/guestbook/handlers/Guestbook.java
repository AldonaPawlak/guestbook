package guestbook.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import guestbook.models.GuestbookEntry;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class Guestbook implements HttpHandler {
    private static List<GuestbookEntry> guestbookEntries = new ArrayList<>();
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("POST")) {
            String formData = transformBodyToString(exchange);
            Map<String, String> inputs = parseFormData(formData);
            addGuestbookEntry(inputs);
        }

        String response = createTemplate();

        sendResponse(exchange, response);
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String createTemplate() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/guestbook.twig");
        JtwigModel model = JtwigModel.newModel();

        model.with("entries", guestbookEntries);

        String response = template.render(model);
        return response;
    }

    private void addGuestbookEntry(Map<String, String> inputs) {
        String entryName = inputs.get("name");
        String entryMessage = inputs.get("message");
        GuestbookEntry guestbookEntry = new GuestbookEntry(entryName, entryMessage, new Date());

        guestbookEntries.add(guestbookEntry);
    }

    private String transformBodyToString(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        return formData;
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
