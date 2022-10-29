package me.color.commands;

import me.color.Main;
import me.color.database.Database;
import me.color.events.LoginEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;

        Database db = new Database();

        if(LoginEvents.isLogged){
            p.sendMessage(Main.chat("&cYou are already logged in!"));
        }

        try {
            if(db.exist("SELECT * FROM creds WHERE NickName = '" + sender.getName() + "';")){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(args[0] == null || args[1] == null || args.length != 2){
            p.sendMessage(Main.chat("&cCorrect usage /register [password] [password]"));
            return true;
        }

        if(!args[1].equals(args[0])){
            p.sendMessage(Main.chat("&cPasswords doesnt match"));
            return true;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        try {
            db.insertQuery("INSERT INTO creds (NickName, Password, RegDate, IP) VALUES " +
                    "('"+ sender.getName() +"', '"+ Main.passwordHash(args[0]) +"', '"+ formatter.format(date) +"', '"+ p.getAddress().getHostString() +"');");
            LoginEvents.isLogged = true;
            p.sendMessage(Main.chat("&aSuccessfully registered account!"));

        } catch (SQLException e) {
            e.printStackTrace();
        }




/*
-> sprawdzanie zarejstrowanej osboy przez exist database
-> sha256 hash password
-> max 4 proby logowania
-> max 3 proby wchodzenia na server z blednymi danymi (ban ip minuta, 5min, 30min itd)
 */

        return false;
    }
}
