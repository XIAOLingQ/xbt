package com.xbt.server.service;

import com.google.gson.Gson;
import com.xbt.server.pojo.dto.CozeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

@Service
public class CozeService {

    @Value("${coze.api.token}")
    private String cozeToken;

    @Value("${coze.api.base-url}")
    private String cozeBaseUrl;

    @Value("${coze.workflow-id}")
    private String cozeWorkflowId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public String runWorkflow(String videoUrl, String userInput) {
        String url = cozeBaseUrl + "/workflow/run";

        // 构建 HTTP 头部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cozeToken);

        Map<String, Object> parameters;
        if (videoUrl != null && !videoUrl.isEmpty()) {
            parameters = Map.of(
                    "input", userInput,
                    "url", videoUrl);
        } else {
            parameters = Map.of(
                    "input", userInput);
        }

        Map<String, Object> body = Map.of(
                "workflow_id", cozeWorkflowId,
                "parameters", parameters);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // 发送 POST 请求并获取响应
            String jsonResponse = restTemplate.postForObject(url, requestEntity, String.class);

            System.out.println(jsonResponse);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return "请求失败，未收到响应。";
            }

            // 第一步：解析最外层
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // 第二步：拿出 data 字符串（里面还是个 JSON）
            String dataStr = jsonObject.getString("data");

            // 第三步：再解析 data 字符串
            JSONObject dataObject = new JSONObject(dataStr);

            // 最后：获取 output
            String response = dataObject.getString("output");

            if (response != null) {
                return response;
            } else {
                return "请求成功，但未获取到有效输出。";
            }

        } catch (Exception e) {
            // 更友好地返回异常信息
            e.printStackTrace();
            return "请求过程中发生异常：" + e.getMessage();
        }
    }

}