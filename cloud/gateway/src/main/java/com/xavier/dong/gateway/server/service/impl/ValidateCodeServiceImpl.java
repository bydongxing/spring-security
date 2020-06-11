package com.xavier.dong.gateway.server.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.code.kaptcha.Producer;
import com.google.common.collect.Maps;
import com.xavier.dong.gateway.server.common.Constants;
import com.xavier.dong.gateway.server.common.Result;
import com.xavier.dong.gateway.server.exception.CaptchaException;
import com.xavier.dong.gateway.server.redis.RedisService;
import com.xavier.dong.gateway.server.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 验证码实现处理
 *
 * @author ruoyi
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Autowired
    private Producer producer;

    @Autowired
    private RedisService redisService;

    /**
     * 生成验证码
     */
    @Override
    public Result createCapcha() {
        // 生成验证码
        String capText = producer.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String verifyCode = capText.substring(capText.lastIndexOf("@") + 1);
        BufferedImage image = producer.createImage(capStr);
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        this.redisService.set(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION);

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return Result.createByErrorMessage(e.getMessage());
        }
        Map<String, String> map = Maps.newHashMap();
        map.put("uuid", uuid);
        map.put("img", Base64.encode(os.toByteArray()));
        return Result.createBySuccess(map);
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCapcha(String code, String uuid) throws CaptchaException {
        if (StrUtil.isBlank(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        if (StrUtil.isEmpty(uuid)) {
            throw new CaptchaException("验证码已失效");
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = ((String) this.redisService.get(verifyKey));
        this.redisService.remove(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("验证码错误");
        }
    }
}
