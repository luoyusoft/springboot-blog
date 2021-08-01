package com.jinhx.blog.controller.other;

import com.jinhx.blog.common.util.QRCodeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * QRCodeController
 *
 * @author jinhx
 * @since 2018-11-21
 */
@RestController
public class QRCodeController {

    /**
     * 生成普通二维码
     *
     * @param response response
     * @param url url
     */
    @GetMapping(value = "/manage/other/qrcode/common")
    public void getCommonQRCode(HttpServletResponse response, String url) throws Exception {
        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            //使用工具类生成二维码
            QRCodeUtils.encode(url, stream);
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }

    /**
     * 生成带有logo二维码
     *
     * @param response response
     * @param url url
     */
    @GetMapping(value = "/manage/other/qrcode/logo")
    public void getLogoQRCode(HttpServletResponse response, String url) throws Exception {
        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            // logo 地址
            String logoPath = Thread.currentThread().getContextClassLoader().getResource("").getPath()
                    + "templates" + File.separator + "advator.jpg";
            // String logoPath = "springboot-demo-list/qr-code/src/main/resources/templates/advator.jpg";
            //使用工具类生成二维码
            QRCodeUtils.encode(url, logoPath, stream, true);
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }

}
