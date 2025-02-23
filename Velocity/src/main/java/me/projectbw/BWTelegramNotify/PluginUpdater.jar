package me.projectbw.BWTelegramNotify;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class PluginUpdater {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/bwproject/BWTelegramNotify/releases/latest";
    private static final String DOWNLOAD_URL_TEMPLATE = "https://github.com/bwproject/BWTelegramNotify/releases/download/%s/BWTelegramNotify-Velocity-%s.jar";

    public void checkForUpdates() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String latestVersion = jsonResponse.getString("tag_name");

            if (!latestVersion.equals("1.0-SNAPSHOT")) { // Тут можно динамически брать текущую версию
                System.out.println("Новая версия доступна: " + latestVersion);
                downloadNewVersion(latestVersion);
            } else {
                System.out.println("У вас последняя версия.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadNewVersion(String latestVersion) {
        try {
            String downloadUrl = String.format(DOWNLOAD_URL_TEMPLATE, latestVersion, latestVersion);
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream("plugin.jar");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("Плагин обновлен до версии " + latestVersion + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
