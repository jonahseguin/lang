# lang
simple language handling &amp; ordered parameter formatting for Spigot plugins

## Usage
I recommend using something like Google's Guice for injecting the Lang class across your project.
```java

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.GregorianCalendar;

public class MyPlugin extends JavaPlugin implements LangModule {

    private Lang lang;

    @Override
    public void define(LangDefinitions l) {
        l.define("no-permission-specific", "&cYou don't have permission to {0}."); // With an argument.  Arguments are optional
        l.extend("no-permission-specific", "no-permission-generic", "do this"); // extend an existing definition with arguments
        l.define("hi", "&aHi, {0}!  The year is {1}.  Your name is {0}."); // duplicate parameters, can be placed in any order and will maintain their values
    }

    @Override
    public String langModule() {
        return "main";
    }

    @Override
    public void onEnable() {
        this.lang = new Lang(this);
        
        lang.register(this); // important call to register this LangModule.  This registers the definitions.
        // You must register all of your LangModule implementations using this!
        
        if (!lang.language("en")) {
            // unnecessary call but this is just to show the functionality.  This call loads the file 'MyPlugin/lang/en.yml'
            // the above call also will create the file if it doesn't exist, and writes defaults/missing definitions to the file.
            getLogger().severe("Language file failed to init!  Aborting");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        getCommand("hi").setExecutor(this);
    }

    @Override
    public void onDisable() {
        this.lang.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hi")) {
            if (sender.hasPermission("hi.use")) {
                int year = new GregorianCalendar().get(GregorianCalendar.YEAR);
                sender.sendMessage(lang.module(this).format("hi", sender.getName(), year));
                // or
                sender.sendMessage(lang.module("main").format("hi", sender.getName(), year));
                // Will send them (in green, example player's name is jo19):  "Hi, jo19!  The year is 2019.  Your name is jo19."
            }
            else {
                sender.sendMessage(lang.module(this).format("no-permission-specific", "use the /hi command"));
                // Will send them (in red): "You don't have permission to use the /hi command."
            }
            return true;
        }
        return false;
    }
    
}

```


## Installation
1. `git clone git@github.com:jonahseguin/lang.git`
2. `cd lang`
3. `mvn package install`


## Maven Dependency
```
<dependency>
  <groupId>com.jonahseguin</groupId>
  <artifactId>lang</artifactId>
  <version>1.0.0</version>
</dependency>
```
