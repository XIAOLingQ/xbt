package com.xbt.server.pojo.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CozeResponse {
    private int code;
    private String msg;
    private CozeData data;

    @Data
    public static class CozeData {
        private String run_id;
        private String workflow_id;
        private String status;
        private Map<String, Object> outputs;
    }
}