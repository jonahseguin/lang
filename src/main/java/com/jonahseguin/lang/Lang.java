package com.jonahseguin.lang;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
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
     * Makes a call to set the language by default to "en" (English) via {@link #language(String)}
     * @param plugin {@link Plugin} {@link org.bukkit.plugin.java.JavaPlugin} your plugin's main class
     */
    public Lang(Plugin plugin) {
        Preconditions.checkNotNull(plugin);
        this.plugin = plugin;
        this.file = new LangFile(this);
        this.language("en");
    }

    /**
     * Sets the active language for this plugin
     * @param language The name of the language.  The default is "en" and is called in the constructor above. {@link #Lang(Plugin)}
     * @return True if successfully loads & saves the language file by name (will create & write defaults), False if it fails in any way
     */
    public boolean language(String language) {
        Preconditions.checkNotNull(language);
        if (this.language == null || !this.language.equalsIgnoreCase(language)) {
            this.language = language;
            return this.load() && this.save();
        }
        return true;
    }

    /**
     * Gets the currently set language (the name of the language, default is "en")
     *
     * @return the currently set language name
     */
    public String language() {
        return this.language;
    }

    /**
     * Get a specific module from the language definitions for this plugin
     * @param name The name of the module
     * @return {@link LangDefinitions} the module
     */
    public LangDefinitions module(String name) {
        Preconditions.checkNotNull(name);
        return modules.computeIfAbsent(name.toLowerCase(), LangDefinitions::new);
    }

    public LangDefinitions module(LangModule module) {
        Preconditions.checkNotNull(module);
        return module(module.langModule());
    }

    /**
     * Register a module {@link LangModule} for language definitions
     * @param module the {@link LangModule} implementation to register
     */
    public void register(LangModule module) {
        Preconditions.checkNotNull(module);
        if (!modules.containsKey(module.langModule().toLowerCase())) {
            module.define(this.module(module.langModule()));
        }
    }

    public Plugin plugin() {
        return plugin;
    }

    public Set<LangDefinitions> modules() {
        return ImmutableSet.copyOf(this.modules.values());
    }

    /**
     * Load from the config file for this language
     *
     * @return True if successful, else False
     * @see #language()
     */
    public boolean load() {
        return file.load();
    }

    /**
     * Save to the config file for this language
     * @see #language()
     * @return True if successful, else False
     */
    public boolean save() {
        return file.save();
    }

}
