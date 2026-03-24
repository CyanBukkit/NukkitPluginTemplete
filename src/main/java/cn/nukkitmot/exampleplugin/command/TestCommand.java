package cn.nukkitmot.exampleplugin.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.LangCode;
import cn.nukkit.utils.TextFormat;
import cn.nukkitmot.exampleplugin.ExamplePlugin;
import cn.nukkitmot.exampleplugin.camera.CameraManager;
import cn.nukkitmot.exampleplugin.camera.CameraPreset;
import cn.nukkitmot.exampleplugin.nbs.NBSSoundManager;

import java.io.File;

public class TestCommand extends PluginCommand<ExamplePlugin> {

    private static final String NBS_FILE_NAME = "Joy To The World.nbs";
    private final ExamplePlugin plugin;

    public TestCommand() {
        super("testcmd", ExamplePlugin.getInstance());
        this.plugin = ExamplePlugin.getInstance();

        this.setDescription("Test command for NBS playback and Camera control");
        this.setAliases(new String[]{"test"});

        this.getCommandParameters().clear();

        this.getCommandParameters().put("nbs", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("nbaction", "play", "stop", "pause", "resume"))
        });

        this.getCommandParameters().put("camera", new CommandParameter[]{
                CommandParameter.newEnum("cammode", false, new CommandEnum("cammode", "fixed", "follow", "facing", "2d", "topdown", "isometric", "clear"))
        });

        this.getCommandParameters().put("camera2d", new CommandParameter[]{
                CommandParameter.newEnum("cammode", false, new CommandEnum("cammode2", "2d")),
                CommandParameter.newEnum("side", false, new CommandEnum("camside", "left", "right"))
        });

        this.getCommandParameters().put("cameracoords", new CommandParameter[]{
                CommandParameter.newEnum("cammode", false, new CommandEnum("cammodec", "fixed", "facing")),
                CommandParameter.newType("x", false, CommandParamType.FLOAT),
                CommandParameter.newType("y", false, CommandParamType.FLOAT),
                CommandParameter.newType("z", false, CommandParamType.FLOAT)
        });
    }

    private NBSSoundManager getNbsManager() {
        return NBSSoundManager.getInstance();
    }

    private CameraManager getCameraManager() {
        return CameraManager.getInstance();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "nbs", "music" -> {
                return handleNbsCommand(sender, args);
            }
            case "camera", "cam" -> {
                return handleCameraCommand(sender, args);
            }
            case "help" -> {
                sendHelp(sender);
                return true;
            }
            default -> {
                sendHelp(sender);
                return false;
            }
        }
    }

    private boolean handleNbsCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "This command must be used by a player!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(TextFormat.YELLOW + "Usage: /testcmd nbs <play|stop|pause|resume>");
            return false;
        }

        String action = args[1].toLowerCase();
        File nbsFile = new File(plugin.getDataFolder(), NBS_FILE_NAME);

        if (!nbsFile.exists()) {
            player.sendMessage(TextFormat.RED + "NBS file not found: " + NBS_FILE_NAME);
            return false;
        }

        switch (action) {
            case "play" -> {
                getNbsManager().playNBS(player, nbsFile);
                player.sendMessage(TextFormat.GREEN + "Playing: " + NBS_FILE_NAME);
            }
            case "stop" -> {
                getNbsManager().stopNBSForPlayer(player);
                player.sendMessage(TextFormat.GREEN + "Stopped NBS playback");
            }
            case "pause" -> {
                getNbsManager().pauseNBSForPlayer(player);
                player.sendMessage(TextFormat.GREEN + "Paused NBS playback");
            }
            case "resume" -> {
                getNbsManager().resumeNBSForPlayer(player);
                player.sendMessage(TextFormat.GREEN + "Resumed NBS playback");
            }
            default -> {
                player.sendMessage(TextFormat.YELLOW + "Unknown action: " + action);
                return false;
            }
        }
        return true;
    }

    private boolean handleCameraCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "This command must be used by a player!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(TextFormat.YELLOW + "Usage: /testcmd camera <fixed|follow|facing|2d|topdown|isometric|clear>");
            return false;
        }

        String mode = args[1].toLowerCase();

        switch (mode) {
            case "fixed" -> {
                if (args.length >= 5) {
                    try {
                        double x = Double.parseDouble(args[2]);
                        double y = Double.parseDouble(args[3]);
                        double z = Double.parseDouble(args[4]);
                        float easeTime = args.length >= 6 ? Float.parseFloat(args[5]) : 2.0f;
                        getCameraManager().setFixedCamera(player, x, y, z, easeTime);
                        player.sendMessage(TextFormat.GREEN + String.format("Fixed camera set to (%.2f, %.2f, %.2f)", x, y, z));
                    } catch (NumberFormatException e) {
                        player.sendMessage(TextFormat.RED + "Invalid coordinates!");
                        return false;
                    }
                } else {
                    player.sendMessage(TextFormat.YELLOW + "Usage: /testcmd camera fixed <x> <y> <z> [easeTime]");
                }
            }
            case "follow" -> {
                double offsetX = args.length >= 3 ? Double.parseDouble(args[2]) : 0;
                double offsetY = args.length >= 4 ? Double.parseDouble(args[3]) : 2;
                double offsetZ = args.length >= 5 ? Double.parseDouble(args[4]) : -5;
                float easeTime = args.length >= 6 ? Float.parseFloat(args[5]) : 1.5f;
                getCameraManager().setFollowCamera(player, offsetX, offsetY, offsetZ, easeTime);
                player.sendMessage(TextFormat.GREEN + "Follow camera set");
            }
            case "facing" -> {
                if (args.length >= 5) {
                    try {
                        double x = Double.parseDouble(args[2]);
                        double y = Double.parseDouble(args[3]);
                        double z = Double.parseDouble(args[4]);
                        float easeTime = args.length >= 6 ? Float.parseFloat(args[5]) : 2.0f;
                        getCameraManager().setFacingCamera(player, x, y, z, easeTime);
                        player.sendMessage(TextFormat.GREEN + String.format("Facing camera set at (%.2f, %.2f, %.2f)", x, y, z));
                    } catch (NumberFormatException e) {
                        player.sendMessage(TextFormat.RED + "Invalid coordinates!");
                        return false;
                    }
                } else {
                    player.sendMessage(TextFormat.YELLOW + "Usage: /testcmd camera facing <x> <y> <z> [easeTime]");
                }
            }
            case "2d" -> {
                String side = args.length >= 3 ? args[2].toLowerCase() : "left";
                boolean isLeft = side.equals("left");
                getCameraManager().set2DCamera(player, isLeft ? CameraManager.CameraSide.LEFT : CameraManager.CameraSide.RIGHT, 8.0, 2.0f);
                player.sendMessage(TextFormat.GREEN + "2D camera set (" + side + " side)");
            }
            case "topdown" -> {
                double height = args.length >= 3 ? Double.parseDouble(args[2]) : 10.0;
                getCameraManager().setTopDownCamera(player, height, 2.0f);
                player.sendMessage(TextFormat.GREEN + "Top-down camera set");
            }
            case "isometric" -> {
                double distance = args.length >= 3 ? Double.parseDouble(args[2]) : 8.0;
                getCameraManager().setIsometricCamera(player, distance, 2.0f);
                player.sendMessage(TextFormat.GREEN + "Isometric camera set");
            }
            case "clear" -> {
                getCameraManager().clearCamera(player);
                player.sendMessage(TextFormat.GREEN + "Camera cleared, back to default");
            }
            default -> {
                player.sendMessage(TextFormat.YELLOW + "Unknown camera mode: " + mode);
                return false;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextFormat.GOLD + "===== TestCommand Help =====");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd nbs <play|stop|pause|resume>" + TextFormat.WHITE + " - NBS music control");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera fixed <x> <y> <z> [time]" + TextFormat.WHITE + " - Set fixed camera at coordinates");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera follow [ox] [oy] [oz] [time]" + TextFormat.WHITE + " - Set follow camera");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera facing <x> <y> <z> [time]" + TextFormat.WHITE + " - Set camera facing player");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera 2d <left|right>" + TextFormat.WHITE + " - Set 2D side view");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera topdown [height]" + TextFormat.WHITE + " - Set top-down view");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera isometric [distance]" + TextFormat.WHITE + " - Set isometric view");
        sender.sendMessage(TextFormat.YELLOW + "/testcmd camera clear" + TextFormat.WHITE + " - Clear camera and restore default");
    }
}