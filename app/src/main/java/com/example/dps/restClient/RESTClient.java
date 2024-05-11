package com.example.dps.restClient;

import com.example.dps.restClient.models.ActivityDataVO;
import com.example.dps.restClient.models.AvroRESTVO;
import com.example.dps.restClient.models.SleepDataVO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

public class RESTClient {
    public static void main(String[] args) throws Exception {
        RESTClient client = new RESTClient();
//        // 수면 데이터 인스턴스 생성
//        AvroRESTVO data = new SleepDataVO("moodeath", "김용기123", Instant.now(), 0,
//                Instant.now(), Instant.now(), 0.0, 0, 1000, 60, 40, 10000,
//                1000, 1000, true);
//        // 수면 데이터 전송
//        client.post(data, "http://3.34.218.215:8082/topics/sleep_data/");
//
//        Thread.sleep(1000);
//
//        // 활동 데이터 인스턴스 생성
//        data = new ActivityDataVO("moodeath", "김용기", Instant.now(), 100,
//                100 ,100, Instant.now(), Instant.now(), 10, 0, 0, 10,
//                0, 0, 100, 100, 100, 100, false);
//        // 수면 데이터 전송
//        client.post(data, "http://3.34.218.215:8082/topics/activity_data/");
    }

    public void post(AvroRESTVO data, String apiURL) {
        OutputStream output = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        HttpURLConnection conn = null;

        int connTimeout = 5000;
        int readTimeout = 3000;
        try {
            URL url = new URL(apiURL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestProperty("Content-Type", "application/vnd.kafka.avro.v2+json");
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);

            output = conn.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(output));
            writer.write(data.toRESTMessage());
            writer.flush();

            StringBuilder buffer = new StringBuilder();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String message = null;
                while((message = reader.readLine()) != null) {
                    buffer.append(message).append("\n");
                }
            } else {
                buffer.append("code : ");
                buffer.append(conn.getResponseCode()).append("\n");
                buffer.append("message : ");
                buffer.append(conn.getResponseMessage()).append("\n");
            }
            System.out.println(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (output != null) output.close();
                if (reader != null) reader.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
