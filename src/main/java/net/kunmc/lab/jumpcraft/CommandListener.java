package net.kunmc.lab.jumpcraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements TabExecutor {
    public CommandListener() {
        Bukkit.getPluginCommand("jumpCraft").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!command.getName().equals("jumpCraft")) {
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage("§c"+ "引数が足りません");
            sender.sendMessage("§c"+ "start ゲームの開始");
            sender.sendMessage("§c"+ "finish ゲームの強制終了");
            sender.sendMessage("§c"+ "pause 一時停止");
            sender.sendMessage("§c"+ "unpause 一時停止の解除");
            sender.sendMessage("§c"+ "config [set/show] コンフィグの設定・確認");
            return true;
        }
        if(args[0].equals("start")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("§c"+ "startはプレイヤーのみ実行可能です");
                return true;
            }
            JumpCraft.instance.startGame((Player) sender);
            return true;
        }
        if(args[0].equals("finish")) {
            if(JumpCraft.instance.finishGame()) {
                sender.sendMessage("§a" + "ゲームを終了しました");
            }
            return true;
        }
        if(args[0].equals("pause")) {
            if(JumpCraft.instance.pauseGame()) {
                sender.sendMessage("§a" + "一時停止しました");
            } else {
                sender.sendMessage("§c"+ "ゲームは開始していません");
            }
            return true;
        }
        if(args[0].equals("unpause")) {
            if(JumpCraft.instance.unpauseGame()) {
                sender.sendMessage("§a" + "一時停止を解除しました");
            } else {
                sender.sendMessage("§c"+ "ゲームは開始していません");
            }
            return true;
        }
        if(args[0].equals("config")) {
            if(args.length < 2) {
                sender.sendMessage("§c"+ "引数が足りません");
                sender.sendMessage("§c"+ "set コンフィグの設定");
                sender.sendMessage("§c"+ "show コンフィグの確認");
                return true;
            }
            if(args[1].equals("show")) {
                ConfigManager.instance.show(sender);
                return true;
            }
            if(args[1].equals("set")) {
                if(args.length < 4) {
                    sender.sendMessage("§c"+ "引数が足りません");
                    sender.sendMessage("§c"+ "barSpeed 0 < n < boxLength");
                    sender.sendMessage("§6" + "以下ゲーム進行中は変更不可");
                    sender.sendMessage("§c"+ "stageLength 5以上");
                    sender.sendMessage("§c"+ "shouldXFix true or false");
                    sender.sendMessage("§c"+ "shouldZFix true or false");
                    sender.sendMessage("§c"+ "teamMode true or false");
                    sender.sendMessage("§c"+ "battleRoyalMode true or false");
                    sender.sendMessage("§c"+ "blackList add Player or remove Player or removeAll");
                    return true;
                }
                if(args[2].equals("barSpeed")) {
                    if(ConfigManager.instance.set(args[2],args[3])) {
                        sender.sendMessage("§a" + args[2] + "を" + args[3] + "にしました");
                        return true;
                    }
                    sender.sendMessage("§c" + "正しい数値を入力してください");
                    sender.sendMessage("§c"+ "barSpeed 0 < n < boxLength");
                    return true;
                }
                if(args[2].equals("stageLength") || args[2].equals("shouldZFix") || args[2].equals("shouldXFix")
                        || args[2].equals("teamMode") || args[2].equals("battleRoyalMode")) {
                    if(!JumpCraft.instance.isNotStart()) {
                        sender.sendMessage("§c" + args[2] + "はゲーム進行中に変更することはできません");
                        return true;
                    }
                }
                if(args[2].equals("stageLength")) {
                    if(ConfigManager.instance.set(args[2],args[3])) {
                        sender.sendMessage("§a" + args[2] + "を" + args[3] + "にしました");
                        return true;
                    }
                    sender.sendMessage("§c" + "正しい数値を入力してください");
                    sender.sendMessage("§c"+ "stageLength 5以上");
                    return true;
                }
                if(args[2].equals("shouldZFix") || args[2].equals("shouldXFix")
                        || args[2].equals("teamMode") || args[2].equals("battleRoyalMode")) {
                    if(ConfigManager.instance.set(args[2],args[3])) {
                        sender.sendMessage("§a" + args[2] + "を" + args[3] + "にしました");
                        return true;
                    }
                    sender.sendMessage("§c"+ "trueかfalseを入力してください");
                    return true;
                }
                if(args[2].equals("blackList")) {
                    if(args[3].equals("removeAll")) {
                        ConfigManager.instance.setBlackList(new HashSet<>());
                        sender.sendMessage("§a" + args[2] + "を空にしました");
                        return true;
                    }
                    if(args.length < 5) {
                        sender.sendMessage("§c"+ "引数が足りません");
                        sender.sendMessage("§c"+ "blackList add Player or remove Player or removeAll");
                        return true;
                    }
                    if(args[3].equals("add")) {
                        if(ConfigManager.instance.getBlackList().add(args[4])) {
                            sender.sendMessage("§a" + args[2] + "に" + args[4] + "を追加しました");
                            return true;
                        }
                        sender.sendMessage("§c" + args[2] + "に" + args[4] + "はすでに追加されています");
                        return true;
                    }
                    if(args[3].equals("remove")) {
                        if(ConfigManager.instance.getBlackList().remove(args[4])) {
                            sender.sendMessage("§a" + args[2] + "から" + args[4] + "を取り除きました");
                            return true;
                        }
                        sender.sendMessage("§c" + args[2] + "に" + args[4] + "は追加されていません");
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("jumpCraft")) {
            if(args.length == 1) {
                return Stream.of("start","finish","pause","unpause","config").filter(e -> e.startsWith(args[0])).collect(Collectors.toList());
            }
            if(args.length >= 2 && !args[0].equals("config")) {
                return null;
            }
            if(args.length == 2) {
                return Stream.of("set", "show").filter(e -> e.startsWith(args[1])).collect(Collectors.toList());
            }
            if(args.length == 3 && args[1].equals("set")) {
                return Stream.of("teamMode","battleRoyalMode","shouldXFix","shouldZFix","barSpeed","stageLength", "blackList").filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("battleRoyalMode") || args[2].equals("shouldZFix") || args[2].equals("shouldXFix") || args[2].equals("teamMode")) {
                return Stream.of("true","false").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("barSpeed")) {
                return Stream.of("1","1.5","2","3","4").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("stageLength")) {
                return Stream.of("5","10","20","30","50").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("blackList")) {
                return Stream.of("add","remove","removeAll").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 5 && args[3].equals("add")) {
                return Bukkit.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> !ConfigManager.instance.getBlackList().contains(name))
                        .filter(e -> e.startsWith(args[4]))
                        .collect(Collectors.toList());
            }
            if(args.length == 5 && args[3].equals("remove")) {
                return Bukkit.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> ConfigManager.instance.getBlackList().contains(name))
                        .filter(e -> e.startsWith(args[4]))
                        .collect(Collectors.toList());
            }

        }
        return null;
    }
}