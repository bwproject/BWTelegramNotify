package me.projectbw.BWTelegramNotify;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class PluginUpdater {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/bwproject/BWTelegramNotify/releases/latest";
    private static final String DOWNLOAD_BASE_URL = "https://github.com/bwproject/BWTelegramNotify/releases/download/";

    public void checkForUpdates() throws IOException {  // Объявляем выбрасывание IOException
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

            JSONArray assets = jsonResponse.getJSONArray("assets");
            String downloadUrl = null;

            // Ищем файл с "BWTelegramNotify-Paper" в имени
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String assetName = asset.getString("name");
                if (assetName.startsWith("BWTelegramNotify-Paper")) {
                    downloadUrl = asset.getString("browser_download_url");
                    break;
                }
            }

            if (downloadUrl == null) {
                System.out.println("Не удалось найти нужный файл для загрузки.");
                return;
            }

            System.out.println("Новая версия доступна: " + latestVersion);
            downloadNewVersion(downloadUrl, latestVersion);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Ошибка при проверке обновлений плагина: " + e.getMessage(), e);
        }
    }

    public void downloadNewVersion(String downloadUrl, String latestVersion) throws IOException {
        try {
            System.out.println("Загрузка файла: " + downloadUrl);
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream("plugins/BWTelegramNotify-Paper.jar");

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
            throw new IOException("Ошибка при скачивании новой версии плагина: " + e.getMessage(), e);
        }
    }
}