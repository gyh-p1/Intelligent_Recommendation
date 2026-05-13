package com.travel.travelserver.controller;

import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.vo.AboutVO;
import com.travel.travelserver.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AboutController {
    private final AboutService aboutService;

    @GetMapping("/api/about")
    public Result<AboutVO> getAbout() {
        return Result.success(aboutService.getAbout());
    }
}
