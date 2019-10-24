package com.jonahseguin.lang;

/**
 * To be implemented by objects/classes which want to have their own language definitions
 */
public interface LangModule {

    /**
     * Define language definitions
     * @see LangDefinitions#define(String, String)
     * @param l The {@link LangDefinitions} object to define language entries to, via {@link LangDefinitions#define(String, String)}
     */
    void define(LangDefinitions l);

    /**
     * Provide the name of this lang module
     * Could be a class name, service name, or just a section name for the language definitions config file for this module.
     * @return {@link String} Name of this lang module
     */
    String langModule();

}
