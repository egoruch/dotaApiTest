package com.eb.pulse.telegrambot.util;

import com.eb.schedule.shared.bean.GameBean;

/**
 * Created by Egor on 24.09.2017.
 */
public class TransformerUtil {

    public static String transform(GameBean gameBean){
        StringBuilder sb = new StringBuilder();
                sb.append(gameBean.radiant.getName()).append("   ")
                        .append("_").append(gameBean.radiantWin).append(" : _").append(gameBean.direWin).append("_   ")
                        .append(gameBean.dire.getName());
        return sb.toString();
    }
}
