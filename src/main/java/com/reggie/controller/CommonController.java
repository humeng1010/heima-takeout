package com.reggie.controller;

import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载 通用控制器
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    //通过spring配置文件的方式 配置文件存储路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file 必须和前端form上传表单中的name保持一致！！！
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后这个临时文件就会被删除
        log.info("上传的文件:{}",file.toString());
        //获取文件的原始文件名，但是我们不建议这样做，因为文件有可能出现重名的情况
        String originalFilename = file.getOriginalFilename();//xxx.jpg
        //从最后一个"."截取到最后（注意下面这样截取是包含"."的，包前到最后）
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID() +suffix;//21da3k41b4.jpg 我们要获取原始文件的后缀，进行组合

        //创建目录
        File dir = new File(basePath);
        //判断目录是否存在
        if (!dir.exists()){
            //如果目录不存在需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            //自定义文件名
//            file.transferTo(new File(basePath+"hello.jpg"));
            //使用原始文件名
//            file.transferTo(new File(basePath+originalFilename));
            //使用UUID
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //最后要返回文件名称，因为新增菜品中点击保存后需要文件的名称
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应类型
            response.setContentType("img/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
