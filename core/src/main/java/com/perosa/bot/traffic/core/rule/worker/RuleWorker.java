package com.perosa.bot.traffic.core.rule.worker;

import com.perosa.bot.traffic.core.BotProxyRequest;
import com.perosa.bot.traffic.core.service.Consumable;

public interface RuleWorker {

    Consumable process(BotProxyRequest request);
}
