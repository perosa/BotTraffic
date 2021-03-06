package com.perosa.bot.traffic.core.service;

public interface Consumable {

    String getHost();

    int getPort();

    String getId();

    String getUrl();

    int getWeight();

    boolean isRouting();

    boolean isFiltering();

    boolean isShadowing();
}
