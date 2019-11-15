package com.perosa.bot.traffic.core.rule.registry;

import com.perosa.bot.traffic.core.rule.Rule;
import com.perosa.bot.traffic.core.rule.registry.storage.RuleRegistryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class RuleRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleRegistry.class);

    private static List<Rule> rules = null;

    public List<Rule> getRules() {
        if(rules == null) {
            rules = getStorageImpl().load();
        }

        return rules;
    }

    public Rule getRule(String id) {

        Optional<Rule> rule = getRules().stream()
                .filter(rule1 -> rule1.getId().equals(id))
                .findFirst();

        return rule.orElse(null);
    }

    public void setRules(List<Rule> rules) {
        RuleRegistry.rules = rules;
    }

    private RuleRegistryStorage getStorageImpl() {
        return RuleRegistryStorage.make();
    }

}
