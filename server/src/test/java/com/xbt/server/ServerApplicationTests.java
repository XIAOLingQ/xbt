package com.xbt.server;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpPostExample {
    public static void main(String[] args) {
        try {
            // 设置请求的 URL
            URL url = new URL("https://api.coze.cn/v1/workflow/run");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            connection.setRequestMethod("POST");

            // 设置请求头
            connection.setRequestProperty("Authorization", "Bearer pat_dCx16gPuOdmdRjzMfVrEcYjXAZiaDvMVLQ15HkFdSewaDnQqD5SHkkTp4XD285LS");
            connection.setRequestProperty("Content-Type", "application/json");

            // 设置允许输出
            connection.setDoOutput(true);

            // 准备请求体
            String jsonInputString = "{\n" +
                    "  \"parameters\": {\n" +
                    "    \"input\": \"你好\",\n" +
                    "    \"url\": \"http://sxh8oib6z.hb-bkt.clouddn.com/videos/1/2_1749372129693.mp4\"\n" +
                    "  },\n" +
                    "  \"workflow_id\": \"7513522089846423567\"\n" +
                    "}";

            // 写入请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 如果响应码为 200，处理响应内容
                // 这里可以根据需要读取响应内容
                System.out.println("Request succeeded");
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}