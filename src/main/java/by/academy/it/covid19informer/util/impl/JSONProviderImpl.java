package by.academy.it.covid19informer.util.impl;

import by.academy.it.covid19informer.util.JSONProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class JSONProviderImpl implements JSONProvider {

    @Override
    public JSONObject getJSON(String url, Map<String, String> headers) {
        log.debug("getting json from "+ url);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            url = url.replaceAll(" ", "+");

            HttpGet httpGet = new HttpGet(url);

            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpGet.setHeader(entry.getKey(),entry.getValue());
            }

            CloseableHttpResponse response;

            synchronized (JSONProviderImpl.class) {
                Thread.sleep(400);
                response = httpClient.execute(httpGet);
            }

            HttpEntity responseEntity = response.getEntity();
            String responseBodyString = convertInputStreamToString(responseEntity.getContent());
            JSONObject jsonObject = new JSONObject(responseBodyString);
            return jsonObject;
        } catch (Exception e) {
            log.error("Error getting json", e);
            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }



}
