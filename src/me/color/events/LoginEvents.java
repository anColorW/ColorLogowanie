package me.color.events;

import me.color.Main;
import me.color.commands.LoginCommand;
import me.color.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LoginEvents implements Listener {

    public static boolean isLogged = false;
    int kicks = 0;

    @EventHandler
    public void onKick(PlayerKickEvent e) throws ParseException {
        if(e.getPlayer().getName().equalsIgnoreCase(LoginCommand.playername)){
            if(e.getReason().equalsIgnoreCase(Main.chat("&cYou have been kicked. \n Too many incorrect tries!"))){
                kicks += 1;

                LocalDateTime myDateObj = LocalDateTime.now().plusHours(1L);
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = myDateObj.format(myFormatObj);

                Database db = new Database();

                if(kicks == 1){
                    try {
                        db.insertQuery("INSERT INTO blacklist (ip, date) VALUES ('"+ e.getPlayer().getAddress().getHostString() +"', STR_TO_DATE('"+ formattedDate +"', '%Y-%m-%d %H:%i:%s') );");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println(kicks + "<--------");

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        isLogged = false;
        LoginCommand.fails = 0;
        Database db = new Database();

        try {
           if(db.exist("SELECT * FROM blacklist WHERE ip = '"+ e.getPlayer().getAddress().getHostString() +"';")){

            String dater = db.printdata("SELECT date FROM `blacklist` WHERE ip = '"+ e.getPlayer().getAddress().getHostString() +"'");
            dater = dater.substring(0, dater.length() - 2);
            dater = dater.replace(" ", "T");
            LocalDateTime das = LocalDateTime.now();
            das = das.withNano(0);

            if(LocalDateTime.parse(dater).compareTo(das) >= 0){
                long gap = ChronoUnit.MILLIS.between(das ,LocalDateTime.parse(dater));
                long gapinmin = gap / 1000 / 60;

                e.getPlayer().kickPlayer(Main.chat("&cYou have been blacklisted! \n Time left: " + gapinmin + "min"));
            }
           }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            int sec = 0;
            @Override
            public void run() {
                if(isLogged){
                    return;
                }
                if(sec == 20){
                    e.getPlayer().kickPlayer("Login time ended...");
                }
                try {
                    if(db.exist("SELECT * FROM creds WHERE NickName = '" + e.getPlayer().getName() +"';")){
                        e.getPlayer().sendMessage(Main.chat("&7/login [password]"));

                    } else{
                        e.getPlayer().sendMessage(Main.chat("&7/register [password] [passsword]"));
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                sec += 2;
            }

        }, 0L, 40L); //2sec

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){

       if(!isLogged){
           if(e.getFrom() != e.getTo()){
               e.setFrom(e.getFrom());
               e.setTo(e.getFrom());
           }
       }

    }

}
