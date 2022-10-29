package me.color;

import me.color.commands.LoginCommand;
import me.color.commands.RegisterCommand;
import me.color.database.Database;
import me.color.events.LoginEvents;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class Main extends JavaPlugin {


    static Main instance;

    @Override
    public void onEnable() {
        Database db = new Database();
        db.database();
        instance = this;
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new LoginEvents(), this);
        this.getCommand("register").setExecutor(new RegisterCommand());
        this.getCommand("login").setExecutor(new LoginCommand());
        System.out.println(LocalDate.now());

    }

    public static String passwordHash(String password){
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec("1234".getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(password.getBytes());
            String check = Hex.encodeHexString(hash);
            return new String(check);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String chat (String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static Main getInstance() {
        return instance;
    }
}
