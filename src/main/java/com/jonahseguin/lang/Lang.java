package com.jonahseguin.lang;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sun.istack.internal.NotNull;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The main class for the Lang library.
 * This controls language definitions for a whole plugin, split into modules.
 */
public class Lang {

    private final Plugin plugin;
    private final LangFile file;
    private final ConcurrentMap<String, LangDefinitions> modules = new ConcurrentHashMap<>();
    private String language;

    /**
     * Create a Lang instance for managing language definitions for your plugin
     * Cannot be null
     * @param plugin {@link NotNull} {@link Plugin} {@link org.bukkit.plugin.java.JavaPlugin} your plugin's main class
     */
    public Lang(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.file = new LangFile(this);
        this.language("en");
    }

    /**
     * Sets the active language for this plugin
     * @param language The name of the language.  The default is "en" and is called in the constructor above. {@link #Lang(Plugin)}
     * @return True if successfully loads & saves the language file by name (will create & write defaults), False if it fails in any way
     */
    public boolean language(@NotNull String language) {
        Preconditions.checkNotNull(language);
        if (this.language == null || !this.language.equalsIgnoreCase(language)) {
            this.language = language;
            return this.load() && this.save();
        }
        return true;
    }

    @NotNull
    public String language() {
        return this.language;
    }

    /**
     * Get a specific module from the language definitions for this plugin
     * @param name The name of the module
     * @return {@link LangDefinitions} the module
     */
    @NotNull
    public LangDefinitions module(String name) {
        return modules.computeIfAbsent(name.toLowerCase(), LangDefinitions::new);
    }

    /**
     *
     * @param module
     */
    public void register(@NotNull LangModule module) {
        module.define(this.module(module.langModule()));
    }

    @NotNull
    public Plugin plugin() {
        return plugin;
    }

    @NotNull
    public Set<LangDefinitions> modules() {
        return ImmutableSet.copyOf(this.modules.values());
    }

    public boolean load() {
        return file.load();
    }

    public boolean save() {
        return file.save();
    }

}
