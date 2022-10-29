package me.color.commands;

import me.color.Main;
import me.color.database.Database;
import me.color.events.LoginEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class LoginCommand implements CommandExecutor {

    public static int fails = 0;
    public static String playername;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        playername = sender.getName();
        Player p = (Player) sender;

        Database db = new Database();

        if(LoginEvents.isLogged){
            p.sendMessage(Main.chat("&cYou are already logged in!"));
            return true;
        }

        try {
            if(!db.exist("SELECT * FROM creds WHERE NickName = '" + sender.getName() + "';")){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(args.length != 1){
            p.sendMessage(Main.chat("&cCorrect usage /login [password]"));
            return true;
        }

        try {
            if(db.exist("SELECT * FROM creds WHERE NickName = '" + p.getName() +"' && Password = '" + Main.passwordHash(args[0]) + "';")){
                LoginEvents.isLogged = true;
                p.sendMessage(Main.chat("&aSuccessfully logged in!"));
            } else{
                if(fails == 3){
                    p.kickPlayer(Main.chat("&cYou have been kicked. \n Too many incorrect tries!"));
                }
                System.out.println(fails);
                fails += 1;

                p.sendMessage(Main.chat("&cInvalid password..."));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
