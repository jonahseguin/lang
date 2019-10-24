package com.jonahseguin.lang;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

class LangFile {

    private final Lang controller;
    private File file;
    private File directory;
    private YamlConfiguration config;

    LangFile(@NotNull Lang controller) {
        this.controller = controller;
    }

    private String fileName() {
        return this.controller.language() + ".yml";
    }

    private boolean ensureDirectory() {
        if (this.directory == null) {
            this.directory = new File(this.controller.plugin().getDataFolder(), "lang");
            return this.directory.mkdirs();
        }
        return true;
    }

    private boolean ensureFile() {
        Preconditions.checkNotNull(this.directory);
        if (this.file == null || !this.file.getName().equalsIgnoreCase(fileName())) {
            this.file = new File(this.directory, fileName());
        }
        if (!file.exists()) {
            try {
                return file.createNewFile();
            }
            catch (IOException ex) {
                this.controller.plugin().getLogger().severe("Failed to create language file " + fileName());
            }
        }
        return file.exists();
    }

    private boolean loadConfig() {
        Preconditions.checkNotNull(this.file);
        try {
            if (this.config == null) {
                this.config = new YamlConfiguration();
            }
            this.config.load(this.file);
            return true;
        }
        catch (IOException | InvalidConfigurationException ex) {
            this.controller.plugin().getLogger().severe("Failed to load language file " + fileName());
            return false;
        }
    }

    private boolean saveConfig() {
        Preconditions.checkNotNull(this.config);
        Preconditions.checkNotNull(this.file);
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            this.controller.plugin().getLogger().severe("Failed to save language file " + fileName());
            return false;
        }
    }

    private void loadDefinitions() {
        Preconditions.checkNotNull(config);
        this.controller.modules().forEach(module -> {
            ConfigurationSection section = config.contains(module.name()) ? config.getConfigurationSection(module.name()) : config.createSection(module.name());
            module.definitions().forEach((key, definition) -> module.define(key, section.getString(key, definition)));
        });
    }

    private void writeDefinitions() {
        Preconditions.checkNotNull(config);
        this.controller.modules().forEach(module -> {
            ConfigurationSection section = config.contains(module.name()) ? config.getConfigurationSection(module.name()) : config.createSection(module.name());
            module.definitions().forEach(section::set);
        });
    }

    boolean load() {
        if (this.ensureDirectory() && this.ensureFile()) {
            if (this.loadConfig()) {
                this.loadDefinitions();
                return true;
            }
        }
        return false;
    }

    boolean save() {
        if (this.ensureDirectory() && this.ensureFile()) {
            this.writeDefinitions();
            return this.saveConfig();
        }
        return false;
    }

}
