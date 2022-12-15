package com.libsgh.mybatis.gen.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.libsgh.mybatis.gen.model.GenConfig;
import com.libsgh.mybatis.gen.service.GenService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ApiController
 * @Description TODO
 * @Author Libs
 * @Date 2022/12/12 17:40
 * @Version 1.0
 */
@Controller
@Slf4j
public class ApiController {

    @Value("${gen_path}")
    private String genPath;

    @Resource
    private GenService genService;

    @PostMapping("/api/prefix")
    @ResponseBody
    public Map<String, Object> getPrefix(String sql){
        return genService.getPrefix(sql);
    }

    /**
     * @Author Libs
     * @Description 生成接口
     * @Date 2022/12/9 17:10
     **/
    @PostMapping("/api/gen")
    @ResponseBody
    public Map<String, Object> gen(@Valid @RequestBody GenConfig config){
        return genService.execGen(config);
    }

    @GetMapping("/api/download/{id}/{filename}")
    public String down(HttpServletResponse res, @PathVariable String id, @PathVariable String filename, Model model) throws UnsupportedEncodingException {
        File file = new File(genPath + File.separator + id + File.separator + filename);
        if(!FileUtil.exist(file)){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
            model.addAttribute("reasonPhrase", "File Not Found");
            return "error";
        }
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        res.setContentLengthLong(file.length());
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @GetMapping(value = "/api/preview/{id}/{filename}")
    public String preview(HttpServletResponse res, @PathVariable String id, @PathVariable String filename, Model model) throws Exception {
        File file = new File(genPath + File.separator + id + File.separator + filename);
        if(!FileUtil.exist(file)){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
            model.addAttribute("reasonPhrase", "File Not Found");
            return "error";
        }
        try {
            Path path = Paths.get("." + FileUtil.getSuffix(file));
            res.setContentType(Files.probeContentType(path));
        } catch (IOException e) {
            res.setContentType("application/octet-stream");
        }
        res.setCharacterEncoding("UTF-8");

        FileInputStream io = new FileInputStream(file);
        IoUtil.copy(io, res.getOutputStream());
        return null;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.add(error.getDefaultMessage());
        });
        result.put("code", -1);
        result.put("msg", errors.stream().collect(Collectors.joining("，")));
        return result;
    }

}
