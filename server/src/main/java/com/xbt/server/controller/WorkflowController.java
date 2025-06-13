package com.xbt.server.controller;

import com.xbt.server.service.CozeService;
import com.xbt.server.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkflowController {

    @Autowired
    private CozeService cozeService;

    @PostMapping("/workflow/run")
    public Result<String> runWorkflow(@RequestBody Map<String, String> payload) {
        String videoUrl = payload.get("videoUrl");
        String userInput = payload.get("userInput");

        if (userInput == null || userInput.isEmpty()) {
            return Result.error(405, "错误");
        }

        try {
            String result = cozeService.runWorkflow(videoUrl, userInput);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(405, "错误");
        }
    }
}