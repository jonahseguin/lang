package com.jonahseguin.lang;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class stores language definitions for a specific module
 * and provides methods for formatting, defining and getting raw definitions.
 */
public class LangDefinitions {

    private final String name;
    private final ConcurrentMap<String, String> definitions = new ConcurrentHashMap<>();

    LangDefinitions(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
    }

    /**
     * Get the raw unformatted definition value for a language key
     *
     * @param key The Language key to get the raw definition for
     * @return The raw definition for the given key
     */
    public String getDefinition(String key) {
        Preconditions.checkNotNull(key);
        return definitions.getOrDefault(key.toLowerCase(), "Unknown lang definition for module '" + name + "': '" + key + "'");
    }

    /**
     * Check if a definition is stored for a language key
     *
     * @param key The language key to check
     * @return True if defined, else False
     */
    public boolean isDefined(String key) {
        Preconditions.checkNotNull(key);
        return definitions.containsKey(key.toLowerCase());
    }

    /**
     * Formats a given language key with the provided ordered parameters
     * Also colours the formatted language definition via {@link #coloured(String)}
     *
     * @param key       The language key
     * @param arguments The ordered parameters to pass to the definition
     * @return The formatted, coloured language definition
     */
    public String format(String key, Object... arguments) {
        Preconditions.checkNotNull(key);
        String definition = populateArguments(getDefinition(key), arguments);
        return coloured(definition);
    }

    private String populateArguments(String definition, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            for (int i = 0; i < arguments.length; i++) {
                String argLine = String.format("{%d}", i);
                Object argument = arguments[i];
                if (argument == null) {
                    argument = "";
                }
                definition = definition.replace(argLine, argument.toString());
            }
        }
        return definition;
    }

    /**
     * Colours a string using {@link ChatColor}
     *
     * @param s The {@link String} to colour
     * @return The coloured String
     * @see ChatColor#translateAlternateColorCodes(char, String)
     */
    public String coloured(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Define a language definition for this module
     * @param key The key to define for
     * @param format The language definition format to provide for the given key
     */
    public void define(String key, String format) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(format);
        this.definitions.putIfAbsent(key.toLowerCase(), format);
    }

    public void set(String key, String format) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(format);
        this.definitions.put(key, format);
    }

    public void extend(String key, String newKey, Object... args) {
        Preconditions.checkNotNull(key);
        define(newKey, populateArguments(getDefinition(key), args));
    }

    public ImmutableMap<String, String> definitions() {
        return ImmutableMap.copyOf(this.definitions);
    }

    public String name() {
        return this.name;
    }

}
