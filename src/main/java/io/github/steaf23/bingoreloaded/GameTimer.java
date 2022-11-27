package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer {
    private int time;
    private BingoScoreboard scoreboard;
    private BingoGame game;
    private BukkitRunnable runnable;

    public GameTimer(BingoScoreboard scoreboard, BingoGame game) {
        this.scoreboard = scoreboard;
        this.game = game;
        this.time = 0;
    }

    public void start() {
        time = 0;
        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                setTime(getTime() + 1);

                // Check if the game has exceeded the max time
                if (getTime() >= ConfigData.getConfig().maxGameTime) {
                    // End the game
                    game.end();
                }
            }
        };
        runnable.runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), 0, 20);
    }

    public void stop() {
        try {
            if (runnable != null)
                runnable.cancel();
        } catch (IllegalStateException e) {
            Message.log(ChatColor.RED + "Timer couldn't be stopped since it never started!");
        }
    }

    public static String getTimeAsString(int seconds) {
        if (seconds >= 60) {
            int minutes = (seconds / 60) % 60;
            if (seconds >= 3600) {
                int hours = seconds / 3600;
                return String.format("%02d:%02d:%02d", hours, minutes, seconds % 60);
            }
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
        return String.format("00:%02d", seconds % 60);
    }

    public void setTime(int seconds) {
        time = seconds;
        scoreboard.updateGameTime(this);
    }

    public int getTime() {
        return time;
    }

    public int getDuration() {
        return ConfigData.getConfig().maxGameTime;
    }
}
