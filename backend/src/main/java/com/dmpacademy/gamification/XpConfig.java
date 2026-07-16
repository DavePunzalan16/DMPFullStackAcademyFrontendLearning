package com.dmpacademy.gamification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.xp")
@Getter
@Setter
public class XpConfig {

    private int lessonCompletion = 10;
    private int quizPass = 25;
    private int challengeCompletion = 50;
}
