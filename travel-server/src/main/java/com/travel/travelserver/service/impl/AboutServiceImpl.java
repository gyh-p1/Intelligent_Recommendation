package com.travel.travelserver.service.impl;

import com.travel.travelserver.model.vo.AboutVO;
import com.travel.travelserver.service.AboutService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AboutServiceImpl implements AboutService {
    @Override
    public AboutVO getAbout() {
        return new AboutVO(
                "旅游景点推荐智能助手 Agent 全栈项目",
                "1.0.0",
                "基于 Vue 3、Vite、Vant、Spring Boot、Redis 和 AI 能力构建的移动端旅游推荐应用。",
                List.of(
                        "AI Agent 智能旅游推荐",
                        "结构化景点推荐与预算建议",
                        "SSE 流式对话响应",
                        "移动端 H5 前后端联调",
                        "Redis 缓存与限流预留"
                )
        );
    }
}
