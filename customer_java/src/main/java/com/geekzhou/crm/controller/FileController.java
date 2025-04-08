package com.geekzhou.crm.controller;

import cn.hutool.core.io.FileUtil;
import com.geekzhou.crm.common.Result;
import com.geekzhou.crm.exception.CustomException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 找到文件位置
        String filePath = System.getProperty("user.dir") + "/files/"; // 获取当前项目的根路径
        if (!FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        byte[] bytes = file.getBytes();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 写入文件
        FileUtil.writeBytes(bytes, filePath + fileName);
        // TODO:到时还需要动态调整
        String host = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String fileUrl = host + "/files/download/" + fileName;
        return Result.success(fileUrl);
    }


    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        // 找到文件位置
        String filePath = System.getProperty("user.dir") + "/files/"; // 获取当前项目的根路径
        String realPath = filePath + fileName;
        boolean exist = FileUtil.exist(realPath);
        if (!exist) {
            throw new CustomException("3002", "文件不存在");
        }
        // 读取文件字节流
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        // 输出流对象把文件写出到客户端(浏览器)
        os.write(bytes);
        os.flush();
        os.close();
    }
}
