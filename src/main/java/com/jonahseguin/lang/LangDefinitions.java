package com.jonahseguin.lang;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.bukkit.ChatColor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LangDefinitions {

    private final String name;
    private final ConcurrentMap<String, String> definitions = new ConcurrentHashMap<>();

    LangDefinitions(@NotNull String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
    }

    @NotNull
    public String getDefinition(@NotNull String key) {
        return definitions.getOrDefault(key, "Unknown lang definition for module '" + name + "': '" + key + "'");
    }

    @NotNull
    public String format(@NotNull String key, @Nullable Object... values) {
        Preconditions.checkNotNull(key);
        String definition = getDefinition(key);
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                String argLine = String.format("{%d}", i);
                Object argument = values[i];
                if (argument == null) {
                    argument = "";
                }
                definition = definition.replace(argLine, argument.toString());
            }
        }
        return coloured(definition);
    }

    @NotNull
    public String coloured(@NotNull String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Define a language definition for this module
     * @param key
     * @param format
     */
    public void define(@NotNull String key, @NotNull String format) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(format);
        this.definitions.put(key, format);
    }

    @NotNull
    public ImmutableMap<String, String> definitions() {
        return ImmutableMap.copyOf(this.definitions);
    }

    @NotNull
    public String name() {
        return this.name;
    }

}
