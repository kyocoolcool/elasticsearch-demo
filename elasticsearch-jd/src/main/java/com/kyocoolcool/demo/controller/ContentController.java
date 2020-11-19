package com.kyocoolcool.demo.controller;

import com.kyocoolcool.demo.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Chris Chen https://blog.kyocoolcool.com
 * @version 1.0
 * @since 2020/11/17 7:39 PM
 **/
@RestController
public class ContentController {

    private ContentService service;

    @Autowired
    public ContentController(ContentService service) {
        this.service = service;
    }

    @GetMapping("/parse/{keyWord}")
    public Boolean parse(@PathVariable("keyWord") String keyWord) throws IOException {
        return service.parseContent(keyWord);
    }

    @GetMapping("/search/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyWord") String keyWord,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize) throws IOException {
        return service.searchPage(keyWord, pageNo, pageSize);
    }
}
