package org.qiu.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Description: 验证码配置类
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: flashSale
 * @Date: 2024/5/29 16:11
 * @Version 1.0
 * @Since 1.0
 **/
@Configuration
public class VertifyConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha dk = new DefaultKaptcha();
        Properties properties = new Properties();
        // 边框
        properties.put("kaptcha.border", "no");

        // 字体颜色
        properties.put("kaptcha.textproducer.font.color","250,250,250");
        // 字体大小
        properties.put("kaptcha.textproducer.font.size","30");
        //文字间隔
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        //干扰实现类
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        //干扰颜色
        properties.setProperty("kaptcha.noise.color", "232,241,245");
        // 字符个数
        properties.put("kaptcha.textproducer.char.length","4");
        // 字体
        properties.put("kaptcha.textproducer.font.names","宋体");
        //文字渲染器
        properties.setProperty("kaptcha.word.impl", "com.google.code.kaptcha.text.impl.DefaultWordRenderer");

        // 图片宽度
        properties.put("kaptcha.image.width","190");
        // 图片高度
        properties.put("kaptcha.image.height","30");
        //干扰图片样式（使用阴影）
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        // 背景渐变
        properties.setProperty("kaptcha.background.clear.from", "69,93,122");
        properties.setProperty("kaptcha.background.clear.to", "227,227,227");
        //背景实现类
        properties.setProperty("kaptcha.background.impl", "com.google.code.kaptcha.impl.DefaultBackground");

        Config config = new Config(properties );
        dk.setConfig(config);
        return dk;
    }
}
