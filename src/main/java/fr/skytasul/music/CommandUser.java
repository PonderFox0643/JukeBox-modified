package fr.skytasul.music;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.model.Song;

import fr.skytasul.music.utils.Playlists;
public class CommandUser implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c你必须是玩家才能使用此命令。");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§c语法错误。使用 /comusic <random|volume|loop|next|play|toggle|stop>");
            return false;
        }

        PlayerData pdata = JukeBox.getInstance().datas.getDatas(player);

        switch (args[0]) {
            case "random":
                sender.sendMessage(random(player));
                break;

            case "volume":
                if (args.length < 2) {
                    sender.sendMessage("§c语法错误。使用 /comusic volume <+|-|value>");
                    return false;
                }

                try {
                    int volume;
                    if (args[1].equals("+")) {
                        volume = pdata.getVolume() + 10;
                    } else if (args[1].equals("-")) {
                        volume = pdata.getVolume() - 10;
                    } else {
                        volume = Integer.parseInt(args[1]);
                    }

                    pdata.setVolume(volume);
                    sender.sendMessage("§a音量： " + pdata.getVolume());
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§c无效数字。");
                }
                break;

            case "loop":
                sender.sendMessage(loop(player));
                break;

            case "next":
                pdata.nextSong();
                sender.sendMessage("§a下一首歌曲。");
                break;

            case "play":
                if (args.length < 2) {
                    sender.sendMessage("§c语法错误。使用 /comusic play <songId|songName>");
                    return false;
                }
                String msg = play(player, args);
                if (!msg.isEmpty()) sender.sendMessage(msg);
                break;

            case "toggle":
                pdata.togglePlaying();
                sender.sendMessage("§a音乐播放状态已切换。");
                break;

            case "stop":
                pdata.stopPlaying(true);
                sender.sendMessage("§a音乐已停止。");
                break;

            default:
                sender.sendMessage("§c未知命令。使用 /comusic <random|volume|loop|next|play|toggle|stop>");
                break;
        }

        return true;
    }

    private String random(Player cp) {
        PlayerData pdata = JukeBox.getInstance().datas.getDatas(cp);
        Song song = pdata.playRandom();
        if (song == null) return "§a随机： §c没有可播放的歌曲";
        return "§a随机： " + song.getTitle();
    }

    private String play(Player cp, String[] args) {
        if (JukeBox.worlds && !JukeBox.worldsEnabled.contains(cp.getWorld().getName()))
            return "§c当前所在世界未开启音乐功能。";

        Song song;
        try {
            int id = Integer.parseInt(args[1]);
            try {
                song = JukeBox.getSongs().get(id);
            } catch (IndexOutOfBoundsException ex) {
                return "§c错误： §l" + id + " §r§c(不存在)";
            }
        } catch (NumberFormatException ex) {
            String fileName = args[1];
            for (int i = 2; i < args.length; i++) {
                fileName = fileName + args[i] + (i == args.length - 1 ? "" : " ");
            }
            song = JukeBox.getSongByFile(fileName);
            if (song == null) return "§c无效的歌曲： " + fileName;
        }

        PlayerData pdata = JukeBox.getInstance().datas.getDatas(cp);
        pdata.setPlaylist(Playlists.PLAYLIST, false);
        pdata.playSong(song);
        pdata.songPlayer.adminPlayed = false;
        return "";
    }

    private String loop(Player cp) {
        PlayerData pdata = JukeBox.getInstance().datas.getDatas(cp);
        return "§a循环模式： " + pdata.setRepeat(!pdata.isRepeatEnabled());
    }
}