/*
 * Copyright 2022-2025 Noah Ross
 *
 * This file is part of PerPlayerKit.
 *
 * PerPlayerKit is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * PerPlayerKit is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PerPlayerKit. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.noah.perplayerkit.gui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import dev.noah.perplayerkit.ItemFilter;
import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.KitRoomDataManager;
import dev.noah.perplayerkit.PublicKit;
import static dev.noah.perplayerkit.gui.ItemUtil.addHideFlags;
import static dev.noah.perplayerkit.gui.ItemUtil.createItem;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.IDUtil;
import dev.noah.perplayerkit.util.PlayerUtil;
import dev.noah.perplayerkit.util.SoundManager;
import net.md_5.bungee.api.ChatColor;

public class GUI {
    private final Plugin plugin;
    private final boolean filterItemsOnImport;
    private static final Set<UUID> kitDeletionFlag = new HashSet<>();

    public GUI(Plugin plugin) {
        this.plugin = plugin;
        this.filterItemsOnImport = plugin.getConfig().getBoolean("anti-exploit.import-filter", false);
    }

    public static void addLoadPublicKit(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            KitManager.get().loadPublicKit(player, id);
            info.getClickedMenu().close();
        });
    }

    public static Menu createPublicKitMenu() {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Public Kit Room").redraw(true).build();
    }

    public static boolean removeKitDeletionFlag(Player player) {
        return kitDeletionFlag.remove(player.getUniqueId());
    }

    public void OpenKitMenu(Player p, int slot) {
        Menu menu = createKitMenu(slot);

        if (KitManager.get().getItemStackArrayById(p.getUniqueId().toString() + slot) != null) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId().toString() + slot);
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 0; i < 41; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "&7Boots"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "&7Leggings"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "&7Chestplate"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "&7Helmet"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "&7Offhand"));

        menu.getSlot(51).setItem(createItem(Material.ARMOR_STAND, 1, "&aɪᴍᴘᴏʀᴛ", "&a● &7Import from inventory"));
        menu.getSlot(52).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ", "&c● &7Shift click to clear"));
        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cʙᴀᴄᴋ"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52));
        addImport(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);

        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void OpenPublicKitEditor(Player p, String kitId) {
        Menu menu = createPublicKitMenu(kitId);

        if (KitManager.get().getItemStackArrayById(IDUtil.getPublicKitId(kitId)) != null) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(IDUtil.getPublicKitId(kitId));
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 0; i < 41; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "&7Boots"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "&7Leggings"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "&7Chestplate"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "&7Helmet"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "&7Offhand"));

        menu.getSlot(51).setItem(createItem(Material.ARMOR_STAND, 1, "&aɪᴍᴘᴏʀᴛ", "&a● &7Import from inventory"));
        menu.getSlot(52).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ", "&c● &7Shift click to clear"));
        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cʙᴀᴄᴋ"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52));
        addImport(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);

        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void OpenECKitKenu(Player p, int slot) {
        Menu menu = createECMenu(slot);

        for (int i = 0; i < 9; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));

        }
        for (int i = 36; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));

        }
        if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot) != null) {

            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot);
            for (int i = 9; i < 36; i++) {
                menu.getSlot(i).setItem(kit[i - 9]);
            }
        }
        for (int i = 9; i < 36; i++) {
            allowModification(menu.getSlot(i));
        }
        menu.getSlot(51).setItem(createItem(Material.ENDER_CHEST, 1, "&aɪᴍᴘᴏʀᴛ", "&a● &7Import from enderchest"));
        menu.getSlot(52).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ ᴋɪᴛ", "&c● &7Shift click to clear"));
        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cʙᴀᴄᴋ"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52), 9, 36);
        addImportEC(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void InspectKit(Player p, UUID target, int slot) {
        String playerName = getPlayerName(target);
        Menu menu = createInspectMenu(slot, playerName);

        if (KitManager.get().hasKit(target, slot)) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(target.toString() + slot);
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "&7Boots"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "&7Leggings"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "&7Chestplate"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "&7Helmet"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "&7Offhand"));

        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cᴄʟᴏꜱᴇ"));
        menu.getSlot(53).setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            info.getClickedMenu().close();
            SoundManager.playCloseGui(player);
        });

        if (p.hasPermission("perplayerkit.admin")) {
            for (int i = 0; i < 41; i++) {
                allowModification(menu.getSlot(i));
            }
            menu.getSlot(52).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ ᴋɪᴛ", "&c● &7Shift click to delete kit"));
            addClearKit(menu.getSlot(52), target, slot);
        }

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void InspectEc(Player p, UUID target, int slot) {
        String playerName = getPlayerName(target);
        Menu menu = createInspectEcMenu(slot, playerName);

        for (int i = 0; i < 9; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));

        }
        for (int i = 36; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));

        }
        if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot) != null) {

            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot);
            for (int i = 9; i < 36; i++) {
                menu.getSlot(i).setItem(kit[i - 9]);
            }
        }

        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cᴄʟᴏꜱᴇ"));
        menu.getSlot(53).setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            info.getClickedMenu().close();
            SoundManager.playCloseGui(player);
        });

        if (p.hasPermission("perplayerkit.admin")) {
            for (int i = 9; i < 36; i++) {
                allowModification(menu.getSlot(i));
            }
            menu.getSlot(52).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ ᴇɴᴅᴇʀᴄʜᴇꜱᴛ", "&c● &7Shift click to delete enderchest"));
            addClearEnderchest(menu.getSlot(52), target, slot);
        }

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void OpenMainMenu(Player p) {
        Menu menu = createMainMenu(p);
        for (int i = 0; i < 45; i++) {
            menu.getSlot(i).setItem(createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }
        int[] kitSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};

        for (int i = 0; i < 10; i++) {
            int slot = kitSlots[i];
            int kitIndex = i + 1;
        
            if (KitManager.get().getItemStackArrayById(p.getUniqueId().toString() + kitIndex) != null) {
                menu.getSlot(slot).setItem(createItem(Material.LIME_SHULKER_BOX, 1, "&aKit " + kitIndex, "&a● &7Left click to load kit", "&9● &7Right click to load kit"));
            } else {
                menu.getSlot(slot).setItem(createItem(Material.RED_SHULKER_BOX, 1, "&cKit " + kitIndex + " &c(empty)", "&a● &7Click to create"));
            }
        
            addEditLoad(menu.getSlot(slot), kitIndex);
        }
        for (int i = 0; i < 5; i++) {
            int slot = 29 + i;
            int ecIndex = i + 1;
        
            if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + ecIndex) != null) {
                menu.getSlot(slot).setItem(createItem(Material.ENDER_CHEST, 1, "&3Enderchest " + ecIndex,
                    "&a● &7Left click to load kit", "&9● &7Right click to edit kit"));
                addEditLoadEC(menu.getSlot(slot), ecIndex);
            } else {
                menu.getSlot(slot).setItem(createItem(Material.ENDER_EYE, 1, "&3Enderchest " + ecIndex,
                    "&a● &7Click to create"));
                addEditEC(menu.getSlot(slot), ecIndex);
            }
        }

        for (int i = 37; i < 54; i++) {
            menu.getSlot(i).setItem(createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }

        menu.getSlot(38).setItem(createItem(Material.BARREL, 1, "&9ᴋɪᴛ ʀᴏᴏᴍ"));
        menu.getSlot(39).setItem(createItem(Material.BOOKSHELF, 1, "&eᴘʀᴇᴍᴀᴅᴇ ᴋɪᴛꜱ"));
        menu.getSlot(40).setItem(createItem(Material.LAVA_BUCKET, 1, "&cᴄʟᴇᴀʀ ɪɴᴠᴇɴᴛᴏʀʏ", "&c● &7Shift click to clear"));
        menu.getSlot(41).setItem(createItem(Material.ENCHANTED_BOOK, 1, "&aʀᴇᴘᴀɪʀ ɪᴛᴇᴍꜱ"));
        menu.getSlot(42).setItem(createItem(Material.CAT_SPAWN_EGG, 1, "&eᴍᴇᴏᴡ"));
        
        addKitRoom(menu.getSlot(38));
        addPublicKitMenu(menu.getSlot(39));
        addClearButton(menu.getSlot(40));
        addRepairButton(menu.getSlot(41));
        addNiMeowMeow(menu.getSlot(42));

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void OpenKitRoom(Player p) {
        OpenKitRoom(p, 0);
    }

    public void OpenKitRoom(Player p, int page) {
        Menu menu = createKitRoom();
        for (int i = 0; i < 45; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 45; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }
        if (KitRoomDataManager.get().getKitRoomPage(page) != null) {
            for (int i = 0; i < 45; i++) {
                menu.getSlot(i).setItem(KitRoomDataManager.get().getKitRoomPage(page)[i]);
            }
        }

        menu.getSlot(45).setItem(createItem(Material.DISPENSER, 1, "&7ʀᴇꜰɪʟʟ"));
        addKitRoom(menu.getSlot(45), page);

        if (!p.hasPermission("perplayerkit.editkitroom")) {
            menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cʙᴀᴄᴋ"));
            addMainButton(menu.getSlot(53));
        } else {
            menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS_PANE, page + 1, "&c&lEDIT MENU", "&cSHIFT RIGHT CLICK TO SAVE"));
        }
        addKitRoom(menu.getSlot(47), 0);
        addKitRoom(menu.getSlot(48), 1);
        addKitRoom(menu.getSlot(49), 2);
        addKitRoom(menu.getSlot(50), 3);
        addKitRoom(menu.getSlot(51), 4);
        addKitRoom(menu.getSlot(52), 5);

        for (int i = 1; i < 6; i++) {
            menu.getSlot(46 + i).setItem(addHideFlags(createItem(Material.valueOf(plugin.getConfig().getString("kitroom.items." + i + ".material")), "&r" + plugin.getConfig().getString("kitroom.items." + i + ".name"))));
        }

        menu.getSlot(page + 47).setItem(ItemUtil.addEnchantLook(menu.getSlot(page + 47).getItem(p)));

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public Menu ViewPublicKitMenu(Player p, String id) {
        ItemStack[] kit = KitManager.get().getPublicKit(id);

        if (kit == null) {
            p.sendMessage(ChatColor.RED + "Kit not found");
            if (p.hasPermission("perplayerkit.admin")) {
                p.sendMessage(ChatColor.RED + "To assign a kit to this publickit use /savepublickit <id>");
            }
            return null;
        }
        Menu menu = ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Viewing Public Kit: " + id).redraw(true).build();

        for (int i = 0; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
        }

        for (int i = 9; i < 36; i++) {
            menu.getSlot(i).setItem(kit[i]);
        }
        for (int i = 0; i < 9; i++) {
            menu.getSlot(i + 36).setItem(kit[i]);
        }
        for (int i = 36; i < 41; i++) {
            menu.getSlot(i + 9).setItem(kit[i]);
        }

        menu.getSlot(52).setItem(createItem(Material.APPLE, 1, "&aʟᴏᴀᴅ ᴋɪᴛ"));
        menu.getSlot(53).setItem(createItem(Material.RED_STAINED_GLASS, 1, "&cʙᴀᴄᴋ"));
        addPublicKitMenu(menu.getSlot(53));
        addLoadPublicKit(menu.getSlot(52), id);

        menu.open(p);
        SoundManager.playOpenGui(p);

        return menu;
    }

    public void OpenPublicKitMenu(Player player) {
        Menu menu = createPublicKitMenu();
        for (int i = 0; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.AIR, 1, " "));
        }

        for (int i = 18; i < 36; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.BOOK, 1, "&7&lᴍᴏʀᴇ ᴋɪᴛꜱ ᴄᴏᴍɪɴɢ ꜱᴏᴏɴ"));
        }

        List<PublicKit> publicKitList = KitManager.get().getPublicKitList();

        for (int i = 0; i < publicKitList.size(); i++) {
            if (KitManager.get().hasPublicKit(publicKitList.get(i).id)) {
                if (player.hasPermission("perplayerkit.admin")) {
                    menu.getSlot(i + 18).setItem(createItem(publicKitList.get(i).icon, 1, ChatColor.RESET + publicKitList.get(i).name, "&7● [ADMIN] Shift click to edit"));
                } else {
                    menu.getSlot(i + 18).setItem(createItem(publicKitList.get(i).icon, 1, ChatColor.RESET + publicKitList.get(i).name));
                }
                addPublicKitButton(menu.getSlot(i + 18), publicKitList.get(i).id);
            } else {
                if (player.hasPermission("perplayerkit.admin")) {
                    menu.getSlot(i + 18).setItem(createItem(publicKitList.get(i).icon, 1, ChatColor.RESET + publicKitList.get(i).name + " &c&l[UNASSIGNED]", "&7● Admins have not yet setup this kit yet", "&7● [ADMIN] Shift click to edit"));
                } else {
                    menu.getSlot(i + 18).setItem(createItem(publicKitList.get(i).icon, 1, ChatColor.RESET + publicKitList.get(i).name + " &c&l[UNASSIGNED]", "&7● Admins have not yet setup this kit yet"));
                }
            }

            if (player.hasPermission("perplayerkit.admin")) {
                addAdminPublicKitButton(menu.getSlot(i + 18), publicKitList.get(i).id);
            }
        }

        addMainButton(menu.getSlot(45));

        menu.getSlot(45).setItem(createItem(Material.RED_STAINED_GLASS_PANE, 1, "&cʙᴀᴄᴋ"));
        menu.open(player);
        SoundManager.playOpenGui(player);
    }

    public void addClear(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                Menu m = info.getClickedMenu();
                for (int i = 0; i < 41; i++) {
                    m.getSlot(i).setItem((org.bukkit.inventory.ItemStack) null);
                }
            }
        });
    }

    public void addClear(Slot slot, int start, int end) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                Menu m = info.getClickedMenu();
                for (int i = start; i < end; i++) {
                    m.getSlot(i).setItem((org.bukkit.inventory.ItemStack) null);
                }
            }
        });
    }

    public void addClearKit(Slot slot, UUID target, int slotNum) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                KitManager.get().deleteKit(target, slotNum);
                player.sendMessage(ChatColor.GREEN + "Kit " + slotNum + " deleted for player!");
                SoundManager.playSuccess(player);
                kitDeletionFlag.add(player.getUniqueId());
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addClearEnderchest(Slot slot, UUID target, int slotNum) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                KitManager.get().deleteEnderchest(target, slotNum);
                player.sendMessage(ChatColor.GREEN + "Enderchest " + slotNum + " deleted for player!");
                SoundManager.playSuccess(player);
                kitDeletionFlag.add(player.getUniqueId());
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addPublicKitButton(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT) {
                KitManager.get().loadPublicKit(player, id);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT) {
                Menu m = ViewPublicKitMenu(player, id);
                if (m != null) {
                    m.open(player);
                }
            }
        });
    }

    public void addAdminPublicKitButton(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                OpenPublicKitEditor(player, id);
                return;
            }
            if (info.getClickType() == ClickType.LEFT) {
                KitManager.get().loadPublicKit(player, id);
            } else if (info.getClickType() == ClickType.RIGHT) {
                Menu m = ViewPublicKitMenu(player, id);
                if (m != null) {
                    m.open(player);
                }
            }
        });
    }

    public void addMainButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenMainMenu(player);
        });
    }


    public void addNiMeowMeow(Slot slot) {
        slot.setClickHandler((player, info) -> {
            if (info.getClickType() == ClickType.RIGHT) {
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_STRAY_AMBIENT, 1f, 1f);
                player.sendMessage(ChatColor.GREEN + "ᴍᴇᴏᴡ ᴍᴇᴏᴡ...");
            } else if (info.getClickType() == ClickType.LEFT) {
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1f, 1f);
                player.sendMessage(ChatColor.GREEN + "ɢʀʀʀ ɢʀʀʀ...");
            } else if (info.getClickType() == ClickType.SHIFT_LEFT || info.getClickType() == ClickType.SHIFT_RIGHT) {
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_DEATH, 1f, 1f);
                player.sendMessage(ChatColor.RED + "ᴅᴇᴀᴛʜ...");
            }
        });
    }
    public void addKitRoom(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenKitRoom(player);
            BroadcastManager.get().broadcastPlayerOpenedKitRoom(player);
        });
    }

    public void addKitRoom(Slot slot, int page) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenKitRoom(player, page);
        });
    }

    public void addPublicKitMenu(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenPublicKitMenu(player);
        });
    }

    public void addKitRoomSaveButton(Slot slot, int page) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isRightClick() && info.getClickType().isShiftClick()) {
                ItemStack[] data = new ItemStack[45];
                for (int i = 0; i < 41; i++) {
                    data[i] = player.getInventory().getContents()[i];
                }
                KitRoomDataManager.get().setKitRoom(page, data);
                player.sendMessage("saved menu");
                SoundManager.playSuccess(player);
            }
        });
    }

    public void addRepairButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            BroadcastManager.get().broadcastPlayerRepaired(player);
            PlayerUtil.repairAll(player);
            player.updateInventory();
            SoundManager.playSuccess(player);
        });
    }

    public void addClearButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                player.getInventory().clear();
                player.sendMessage(ChatColor.GREEN + "Inventory cleared");
                SoundManager.playSuccess(player);
            }
        });
    }

    public void addImport(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            Menu m = info.getClickedMenu();
            ItemStack[] inv;
            if (filterItemsOnImport) {
                inv = ItemFilter.get().filterItemStack(player.getInventory().getContents());
            } else {
                inv = player.getInventory().getContents();
            }
            for (int i = 0; i < 41; i++) {
                m.getSlot(i).setItem(inv[i]);
            }
        });
    }

    public void addImportEC(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            Menu m = info.getClickedMenu();
            ItemStack[] inv;
            if (filterItemsOnImport) {
                inv = ItemFilter.get().filterItemStack(player.getEnderChest().getContents());
            } else {
                inv = player.getEnderChest().getContents();
            }
            for (int i = 0; i < 27; i++) {
                m.getSlot(i + 9).setItem(inv[i]);
            }
        });
    }

    public void addEdit(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isLeftClick() || info.getClickType().isRightClick()) {
                OpenKitMenu(player, i);
            }
        });
    }

    public void addEditEC(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isLeftClick() || info.getClickType().isRightClick()) {
                OpenECKitKenu(player, i);
            }
        });
    }

    public void addLoad(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadKit(player, i);
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addEditLoad(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadKit(player, i);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT || info.getClickType() == ClickType.SHIFT_RIGHT) {
                OpenKitMenu(player, i);
            }
        });
    }

    public void addEditLoadEC(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadEnderchest(player, i);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT || info.getClickType() == ClickType.SHIFT_RIGHT) {
                OpenECKitKenu(player, i);
            }
        });
    }

    public Menu createKitMenu(int slot) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Kit: " + slot).build();
    }

    public Menu createPublicKitMenu(String id) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Public Kit: " + id).build();
    }

    public Menu createECMenu(int slot) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Enderchest: " + slot).build();
    }

    public Menu createInspectMenu(int slot, String playerName) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Inspecting " + playerName + "'s kit " + slot).build();
    }

    public Menu createInspectEcMenu(int slot, String playerName) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Inspecting " + playerName + "'s enderchest " + slot).build();
    }

    public Menu createMainMenu(Player p) {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + p.getName() + "'s Kits").build();
    }

    public Menu createKitRoom() {
        return ChestMenu.builder(6).title(ChatColor.DARK_GRAY + "Kit Room").redraw(true).build();
    }

    public void allowModification(Slot slot) {
        ClickOptions options = ClickOptions.ALLOW_ALL;
        slot.setClickOptions(options);
    }

    private String getPlayerName(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            return onlinePlayer.getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String name = offlinePlayer.getName();
        return name != null ? name : uuid.toString();
    }
}