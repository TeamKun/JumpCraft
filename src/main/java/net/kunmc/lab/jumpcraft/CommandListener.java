package net.kunmc.lab.jumpcraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

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
            if(JumpCraft.getINSTANCE().isStart()) {
                if(!JumpCraft.getINSTANCE().isFinish()) {
                    sender.sendMessage("§c" + "ゲームは開始しています");
                    return true;
                }
            }
            BoxGenerator.getINSTANCE().destroyBox();
            JumpCraft.getINSTANCE().setStart(false);
            JumpCraft.getINSTANCE().setPause(false);
            JumpCraft.getINSTANCE().setPreparing(false);
            JumpCraft.getINSTANCE().startGame(((Player) sender).getLocation());
            return true;
        }
        if(args[0].equals("finish")) {
            if(!JumpCraft.getINSTANCE().isStart()) {
                sender.sendMessage("§c"+ "ゲームは開始していません");
                return true;
            }
            BoxGenerator.getINSTANCE().destroyBox();
            JumpCraft.getINSTANCE().setStart(false);
            JumpCraft.getINSTANCE().setPause(false);
            JumpCraft.getINSTANCE().setPreparing(false);
            sender.sendMessage("§a" + "ゲームを終了しました");
            return true;
        }
        if(args[0].equals("pause")) {
            if(!JumpCraft.getINSTANCE().isStart()) {
                sender.sendMessage("§c"+ "ゲームは開始していません");
                return true;
            }
            if(JumpCraft.getINSTANCE().isPause()) {
                sender.sendMessage("§c"+ "一時停止中です");
                return true;
            }
            JumpCraft.getINSTANCE().setPause(true);
            sender.sendMessage("§a" + "一時停止しました");
            return true;
        }
        if(args[0].equals("unpause")) {
            if(!JumpCraft.getINSTANCE().isStart()) {
                sender.sendMessage("§c"+ "ゲームは開始していません");
                return true;
            }
            if(!JumpCraft.getINSTANCE().isPause()) {
                sender.sendMessage("§c"+ "一時停止していません");
                return true;
            }
            JumpCraft.getINSTANCE().unPause();
            sender.sendMessage("§a" + "一時停止を解除しました");
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
                sender.sendMessage("§a" + "barSpeed: " +ConfigData.getINSTANCE().getBarSpeed());
                sender.sendMessage("§a" + "boxLength: " +ConfigData.getINSTANCE().getWidthZ());
                sender.sendMessage("§a" + "battleRoyalMode: " +ConfigData.getINSTANCE().isBattleRoyalMode());
                sender.sendMessage("§a" + "zFixMode: " +ConfigData.getINSTANCE().isZFixMode());
                return true;
            }
            if(args[1].equals("set")) {
                if(args.length < 4) {
                    sender.sendMessage("§c"+ "引数が足りません");
                    sender.sendMessage("§c"+ "barSpeed 0 < n < boxLength");
                    sender.sendMessage("§c"+ "boxLength 5以上/ゲーム進行中は変更不可");
                    sender.sendMessage("§c"+ "battleRoyalMode true or false");
                    sender.sendMessage("§c"+ "zFixMode true or false");
                    return true;
                }
                if(args[2].equals("battleRoyalMode")) {
                    if(args[3].equals("true") || args[3].equals("false")) {
                        ConfigData.getINSTANCE().setBattleRoyalMode(Boolean.parseBoolean(args[3]));
                        sender.sendMessage("§a"+ "battleRoyalModeを" + args[3] + "にしました");
                        return true;
                    }
                    sender.sendMessage("§c"+ "引数は true or false にしてください");
                    return true;
                }
                if(args[2].equals("zFixMode")) {
                    if(args[3].equals("true") || args[3].equals("false")) {
                        ConfigData.getINSTANCE().setZFixMode(Boolean.parseBoolean(args[3]));
                        sender.sendMessage("§a"+ "zFixModeを" + args[3] + "にしました");
                        return true;
                    }
                    sender.sendMessage("§c"+ "引数は true or false にしてください");
                    return true;
                }
                if(args[2].equals("barSpeed")) {
                    try {
                        double speed = Double.parseDouble(args[3]);
                        if(speed <= 0 || speed >= ConfigData.getINSTANCE().getWidthZ()) {
                            sender.sendMessage("§c"+ "barSpeedは  0 < n < boxLength にしてください");
                            return true;
                        }
                        ConfigData.getINSTANCE().setBarSpeed(speed);
                        sender.sendMessage("§a"+ "barSpeedを" + speed + "にしました");
                        return true;
                    } catch (Exception e) {
                        sender.sendMessage("§c"+ "引数は数値を入力してください");
                        return true;
                    }
                }
                if(args[2].equals("boxLength")) {
                    if(JumpCraft.getINSTANCE().isPreparing() || JumpCraft.getINSTANCE().isStart()) {
                        if(!JumpCraft.getINSTANCE().isFinish()) {
                            sender.sendMessage("§c" + "ゲーム進行中に変更することはできません");
                            return true;
                        }
                    }
                    try {
                        double boxLength = Double.parseDouble(args[3]);
                        if(boxLength < 5) {
                            sender.sendMessage("§c"+ "boxLengthは 5以上 にしてください");
                            return true;
                        }
                        ConfigData.getINSTANCE().setWidthZ((int) boxLength);
                        sender.sendMessage("§a"+ "boxLengthを" + boxLength + "にしました");
                        return true;
                    } catch (Exception e) {
                        sender.sendMessage("§c"+ "引数は数値を入力してください");
                        return true;
                    }
                }
            }
        }
        sender.sendMessage("§c"+ "引数が間違っています");
        sender.sendMessage("§c"+ "start ゲームの開始");
        sender.sendMessage("§c"+ "finish ゲームの強制終了");
        sender.sendMessage("§c"+ "pause 一時停止");
        sender.sendMessage("§c"+ "unpause 一時停止の解除");
        sender.sendMessage("§c"+ "config [set/show] コンフィグの設定・確認");
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
                return Stream.of("battleRoyalMode","zFixMode","barSpeed","boxLength").filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("battleRoyalMode") || args[2].equals("zFixMode")) {
                return Stream.of("true","false").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("barSpeed")) {
                return Stream.of("1","1.5","2","3","4").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
            if(args.length == 4 && args[2].equals("boxLength")) {
                return Stream.of("5","10","20","30","50").filter(e -> e.startsWith(args[3])).collect(Collectors.toList());
            }
        }
        return null;
    }
}
