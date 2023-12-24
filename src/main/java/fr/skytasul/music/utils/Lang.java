package fr.skytasul.music.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.skytasul.music.JukeBox;

public class Lang{
	
	public static String NEXT_PAGE = ChatColor.AQUA + "下一页";
	public static String LATER_PAGE = ChatColor.AQUA + "上一页";
	public static String CURRENT_PAGE = ChatColor.DARK_AQUA + "§o第 %d 页，共 %d 页";
	public static String PLAYER = ChatColor.RED + "您必须是玩家才能执行此命令。";
	public static String RELOAD_MUSIC = ChatColor.GREEN + "音乐已重新加载。";
	public static String INV_NAME = ChatColor.LIGHT_PURPLE + "§l点歌机！";
	public static String TOGGLE_PLAYING = ChatColor.GOLD + "暂停/播放";
	public static String VOLUME = ChatColor.BLUE + "音乐音量：§b";
	public static String RIGHT_CLICK = "§e右键点击：减少 10%";
	public static String LEFT_CLICK = "§e左键点击：增加 10%";
	public static String RANDOM_MUSIC = ChatColor.DARK_AQUA + "随机音乐";
	public static String STOP = ChatColor.RED + "停止音乐";
	public static String MUSIC_STOPPED = ChatColor.GREEN + "音乐已停止。";
	public static String ENABLE = "启用";
	public static String DISABLE = "禁用";
	public static String ENABLED = "已启用";
	public static String DISABLED = "已禁用";
	public static String TOGGLE_SHUFFLE_MODE = "{TOGGLE} 随机播放模式";
	public static String TOGGLE_LOOP_MODE = "{TOGGLE} 循环模式";
	public static String TOGGLE_CONNEXION_MUSIC = "{TOGGLE} 连接时播放音乐";
	public static String TOGGLE_PARTICLES = "{TOGGLE} 粒子效果";
	public static String MUSIC_PLAYING = ChatColor.GREEN + "播放音乐中：";
	public static String INCORRECT_SYNTAX = ChatColor.RED + "语法错误。";
	public static String RELOAD_LAUNCH = ChatColor.GREEN + "尝试重新加载。";
	public static String RELOAD_FINISH = ChatColor.GREEN + "重新加载完成。";
	public static String AVAILABLE_COMMANDS = ChatColor.GREEN + "可用命令：";
	public static String INVALID_NUMBER = ChatColor.RED + "无效的数字。";
	public static String PLAYER_MUSIC_STOPPED = ChatColor.GREEN + "玩家停止了音乐：§b";
	public static String IN_PLAYLIST = ChatColor.BLUE + "§o在播放列表中";
	public static String PLAYLIST_ITEM = ChatColor.LIGHT_PURPLE + "播放列表";
	public static String OPTIONS_ITEM = ChatColor.AQUA + "选项";
	public static String MENU_ITEM = ChatColor.GOLD + "返回菜单";
	public static String CLEAR_PLAYLIST = ChatColor.RED + "清空当前播放列表";
	public static String NEXT_ITEM = ChatColor.YELLOW + "下一首歌曲";
	public static String CHANGE_PLAYLIST = ChatColor.GOLD + "§l切换播放列表：§r";
	public static String CHANGE_PLAYLIST_LORE = ChatColor.YELLOW + "中键点击音乐唱片\n§e 添加/移除歌曲";
	public static String PLAYLIST = ChatColor.DARK_PURPLE + "播放列表";
	public static String FAVORITES = ChatColor.DARK_RED + "收藏夹";
	public static String RADIO = ChatColor.DARK_AQUA + "广播电台";
	public static String UNAVAILABLE_RADIO = ChatColor.RED + "在收听广播电台时无法执行此操作。";
	public static String NONE = ChatColor.RED + "无";

	public static void saveFile(YamlConfiguration cfg, File file) throws ReflectiveOperationException, IOException {
		for (Field f : Lang.class.getDeclaredFields()){
			if (f.getType() != String.class) continue;
			if (!cfg.contains(f.getName())) cfg.set(f.getName(), f.get(null));
		}
		cfg.save(file);
	}
	
	public static void loadFromConfig(File file, YamlConfiguration cfg) {
		List<String> inexistant = new ArrayList<>();
		for (String key : cfg.getValues(false).keySet()){
			try {
				String str = cfg.getString(key);
				str = ChatColor.translateAlternateColorCodes('&', str);
				if (JukeBox.version >= 16) str = translateHexColorCodes("(&|§)#", "", str);
				try {
					Lang.class.getDeclaredField(key).set(key, str);
				}catch (NoSuchFieldException ex) {
					inexistant.add(key);
				}
			}catch (Exception e) {
				JukeBox.getInstance().getLogger().warning("Error when loading language value \"" + key + "\".");
				e.printStackTrace();
				continue;
			}
		}
		if (!inexistant.isEmpty())
			JukeBox.getInstance().getLogger().warning("Found " + inexistant.size() + " inexistant string(s) in " + file.getName() + ": " + String.join(" ", inexistant));
	}
	
	private static final char COLOR_CHAR = '\u00A7';
	
	private static String translateHexColorCodes(String startTag, String endTag, String message) {
		final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(2);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
		}
		return matcher.appendTail(buffer).toString();
	}
	
}
