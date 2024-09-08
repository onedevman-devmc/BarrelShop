package mc.barrelshop.shop;

import mc.barrelshop.shop.events.ShopLoadedEvent;
import mc.barrelshop.shop.events.ShopUnloadedEvent;
import mc.compendium.events.EventListener;
import mc.compendium.utils.bukkit.ItemNames;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BarrelShopManager implements Listener, EventListener {

    public static final ItemStack EMPTY_SHOP_TRADES_ANIMATION_ICON_ITEMSTACK;

    //

    static {
        EMPTY_SHOP_TRADES_ANIMATION_ICON_ITEMSTACK = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = Objects.requireNonNull(EMPTY_SHOP_TRADES_ANIMATION_ICON_ITEMSTACK.getItemMeta());

        meta.setDisplayName("§r ");
        EMPTY_SHOP_TRADES_ANIMATION_ICON_ITEMSTACK.setItemMeta(meta);
    }

    //

    private static boolean hasCreationPermission(Player player, boolean isAdminShop) {
        if(isAdminShop) {
            return player.hasPermission("mc.barrelshop.shop.admin.create");
        }
        else return player.hasPermission("mc.barrelshop.shop.self.create");
    }

    //

    private static boolean hasDeletionPermission(Player player, OfflinePlayer shopOwner, boolean isAdminShop) {
        if(isAdminShop) {
            return player.hasPermission("mc.barrelshop.shop.admin.delete");
        }
        else {
            if(shopOwner.getUniqueId().equals(player.getUniqueId()))
                return player.hasPermission("mc.barrelshop.shop.self.delete");
            else return player.hasPermission("mc.barrelshop.shop.other.delete");
        }
    }

    //

    private static boolean hasManagementPermission(Player player, OfflinePlayer shopOwner, boolean isAdminShop) {
        if(isAdminShop) {
            return player.hasPermission("mc.barrelshop.shop.admin.manage");
        }
        else {
            if(shopOwner.getUniqueId().equals(player.getUniqueId()))
                return player.hasPermission("mc.barrelshop.shop.self.manage");
            else return player.hasPermission("mc.barrelshop.shop.other.manage");
        }
    }

    //

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerOpenShopContainer(InventoryOpenEvent event) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        if(!(event.getPlayer() instanceof Player player)) return;

        Location barrelCandidateLocation = event.getInventory().getLocation();
        if(barrelCandidateLocation == null) return;

        BlockState barrelCandidate = barrelCandidateLocation.getBlock().getState();
        if(!(barrelCandidate instanceof Barrel barrel)) return;

        BarrelShop shop = this.shopRegistry.get(barrel.getLocation());
        if(shop == null) return;

        //

        event.setCancelled(true);
        player.openMerchant(shop.getTradeInterface().toBukkit(), true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerOpenShopManagementInterface(PlayerInteractEvent event) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        if(!Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) return;

        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if(block == null || !(block.getState() instanceof Sign sign)) return;

        Barrel barrel = BarrelShop.getAssociatedBarrel(sign);
        if(barrel == null) return;

        BarrelShop shop = this.shopRegistry.get(barrel.getLocation());
        if(shop == null) return;

        //

        if(!hasManagementPermission(player, shop.getOwner(), shop.isAdmin())) {
            player.sendMessage("§cYou don't have permission to manage this shop.");
            event.setCancelled(true);
            return;
        }

        //

        player.openInventory(shop.getManagementInterface().toBukkit());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCreateShop(SignChangeEvent event) throws IOException, BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        Player player = event.getPlayer();

        Sign sign = (Sign) event.getBlock().getState();

        String[] incomingLines = event.getLines();
        for(int i = 0; i < incomingLines.length; i++) sign.getSide(event.getSide()).setLine(i, incomingLines[i]);
        sign.update(true);

        if(!BarrelShop.isValidSign(sign)) return;

        Barrel barrel = BarrelShop.getAssociatedBarrel(sign);
        if(barrel == null) return;

        //

        if(this.shopRegistry.get(barrel.getLocation()) != null) return;

        //

        if(!hasCreationPermission(player, BarrelShop.hasAdminShopIdentifier(sign))) {
            player.sendMessage("§cYou don't have permission to create this kind of shop.");
            event.setCancelled(true);
            return;
        }

        //

        BarrelShop shop = this.createShopFrom(sign, player);
        this.shopRegistry.register(shop);

        //

        SignSide signSide = sign.getSide(event.getSide());

        String[] finalLines = signSide.getLines();
        for(int i = 0; i < finalLines.length; i++) event.setLine(i, finalLines[i]);

        sign.update(true);

        //

        player.sendMessage("§aShop successfully created.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeleteShop(BlockBreakEvent event) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException, IOException {
        Player player = event.getPlayer();
        Block targetBlock = event.getBlock();
        BlockState targetBlockState = targetBlock.getState();
        Barrel barrel = null;
        BarrelShop shop = null;

        //

        if(targetBlockState instanceof Sign sign) { barrel = BarrelShop.getAssociatedBarrel(sign); }
        else if(targetBlockState instanceof Barrel barrelCandidate) { barrel = barrelCandidate; }

        if(barrel != null) shop = this.shopRegistry.get(barrel.getLocation());

        if(shop == null) return;

        //

        if(!hasDeletionPermission(player, shop.getOwner(), shop.isAdmin())) {
            player.sendMessage("§cYou don't have permission to delete this shop.");
            event.setCancelled(true);
            return;
        }

        //

        this.deleteShop(shop, targetBlock.getLocation());
        player.sendMessage("§aShop successfully deleted.");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPistonBreakShop(BlockPistonExtendEvent event) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        List<Block> affectedBlocks = event.getBlocks();
        int affectedBlockCount = affectedBlocks.size();

        BarrelShop shop = null;

        for(int i = 0; i < affectedBlockCount && shop == null; i++) {
            Block block = affectedBlocks.get(i);
            BlockState blockState = block.getState();
            Barrel barrel = null;

            if(blockState instanceof Sign sign) barrel = BarrelShop.getAssociatedBarrel(sign);
            else if(blockState instanceof Barrel barrelCandidate) barrel = barrelCandidate;

            if(barrel != null) shop = this.shopRegistry.get(barrel.getLocation(), true);
        }

        if(shop == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplodeShop(EntityExplodeEvent event) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        List<Block> affectedBlocks = event.blockList();
        List<Block> affectedBlocksCopy = new ArrayList<>(affectedBlocks);

        for (Block block : affectedBlocksCopy) {
            BlockState blockState = block.getState();
            Barrel barrel = null;

            if (blockState instanceof Sign sign) barrel = BarrelShop.getAssociatedBarrel(sign);
            else if (blockState instanceof Barrel barrelCandidate) barrel = barrelCandidate;

            if (barrel != null) {
                BarrelShop shop = this.shopRegistry.get(barrel.getLocation(), true);
                if (shop != null) affectedBlocks.remove(block);
            }
        }
    }

    //

    @EventHandler
    public void onShopTradeAnimationIconPickedUpByContainer(InventoryPickupItemEvent event) {
        event.setCancelled(shopTradeAnimationIconMap.containsValue(event.getItem()));
    }

    //

    @mc.compendium.events.EventHandler
    public void onShopLoaded(ShopLoadedEvent event) {
        BarrelShop shop = event.getShop();
        shop.registerManager(this);
    }

    @mc.compendium.events.EventHandler
    public void onShopUnloaded(ShopUnloadedEvent event) {
        BarrelShop shop = event.getShop();
        this.clearShopTradeAnimationIcon(shop);
        shop.unregisterManager(this);
    }

    //

    private final Plugin plugin;
    private final PersistentBarrelShopRegistry shopRegistry;

    private BukkitTask shopTradesAnimationLoopTask = null;
    private final Map<Location, Integer> shopTradesAnimationLoopIndexMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<Location, ItemIcon> shopTradeAnimationIconMap = Collections.synchronizedMap(new HashMap<>());

    //

    public BarrelShopManager(Plugin plugin, File registryStorageDirectory) {
        this.plugin = plugin;

        this.shopRegistry = new PersistentBarrelShopRegistry(plugin, registryStorageDirectory);
        this.shopRegistry.addListener(this);

        //

        for(World world : Bukkit.getWorlds()) {
            for(Chunk chunk : world.getLoadedChunks())
                this.shopRegistry.loadChunk(chunk);
        }
    }

    //

    public Plugin getPlugin() { return this.plugin; }

    public PersistentBarrelShopRegistry getShopRegistry() { return this.shopRegistry; }

    //

    private synchronized BarrelShop createShopFrom(Sign sign, OfflinePlayer owner) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException {
        if(!BarrelShop.isValidSign(sign)) return null;

        BarrelShop shop = BarrelShop.hasAdminShopIdentifier(sign) ? new BarrelAdminShop(sign) : new BarrelShop(sign, owner);
        shop.registerManager(this);
        shop.requestUpdateToManagers();

        return shop;
    }

    private synchronized void deleteShop(BarrelShop shop, Location origin) throws IOException {
        Location shopLocation = shop.getLocation();
        World shopWorld = Objects.requireNonNull(shop.getLocation().getWorld());

        if(!shopLocation.equals(origin)) shopLocation.getBlock().breakNaturally();

        Location dropLocation = shopLocation.clone().add(0.5, 0.5, 0.5);
        for(AbstractShopTrade trade : shop.trades()) {
            if(trade instanceof ShopTrade shopTrade) {
                for(List<ItemStack> containerItems : Set.of(
                    shopTrade.getStocksContainerItemList(), shopTrade.getEarningsContainerItemList()
                )) {
                    for(ItemStack containerItem : containerItems)
                        shopWorld.dropItem(dropLocation, containerItem);
                }
            }
        }

        this.clearShopTradeAnimationIcon(shop);
        this.shopRegistry.unregister(shop);
    }

    //

    public void updateShopTradeAnimationIcon(BarrelShop shop) {
        if(shop.doesTradesCanBeAnimated()) this.internalUpdateShopTradeAnimationIcon(shop);
        else this.clearShopTradeAnimationIcon(shop);
    }

    private void internalUpdateShopTradeAnimationIcon(BarrelShop shop) {
        Location shopLocation = shop.getLocation();
        World shopWorld = Objects.requireNonNull(shop.getLocation().getWorld());

        List<AbstractShopTrade> trades = shop.trades();
        int tradeCount = trades.size();
        this.shopTradesAnimationLoopIndexMap.putIfAbsent(shopLocation, 0);
        int tradeIndex = this.shopTradesAnimationLoopIndexMap.get(shopLocation) % tradeCount;

        AbstractShopTrade trade = null;
        ItemStack tradeResult = null;
        if(!trades.isEmpty()) {
            int i = 0;
            do {
                trade = trades.get((tradeIndex+i) % tradeCount);
                tradeResult = trade == null ? null : trade.getResult();
                ++i;
            } while (i < tradeCount && tradeResult == null);

            tradeIndex = (tradeIndex+i-1) % tradeCount;
        }

        ItemIcon animationIcon = this.shopTradeAnimationIconMap.get(shopLocation);
        Location animationIconLocation = shopLocation.clone().add(0.5, 1, 0.5);

        if(animationIcon != null && !animationIcon.item().isInWorld()) {
            animationIcon.remove();
            animationIcon = null;
        }

        if (animationIcon == null) {
            this.shopTradeAnimationIconMap.put(
                shopLocation,
                animationIcon = new ItemIcon(animationIconLocation)
            );
        }

        if(tradeResult == null) {
            animationIcon.setItemStack(EMPTY_SHOP_TRADES_ANIMATION_ICON_ITEMSTACK);
            animationIcon.setCustomNameVisible(false);
        }
        else {
            animationIcon.setItemStack(tradeResult.clone());

            ItemStack animationIconItemStack = animationIcon.getItemStack();
            ItemMeta meta = Objects.requireNonNull(animationIconItemStack.getItemMeta());

            String displayName = meta.hasDisplayName() ? meta.getDisplayName() : ItemNames.defaultName(animationIconItemStack);
            animationIcon.setCustomName(displayName + " §8(§7✕§5" + animationIconItemStack.getAmount() + "§8)§r");
            animationIcon.setCustomNameVisible(true);
        }

        this.shopTradesAnimationLoopIndexMap.remove(shopLocation);
        this.shopTradesAnimationLoopIndexMap.put(shopLocation, tradeIndex + 1);
    }

    private void shopTradesAnimationLoop() {
        for(BarrelShop shop : this.shopRegistry.getAllLoadedShops()) this.updateShopTradeAnimationIcon(shop);
    }

    private void clearShopTradeAnimationIcon(BarrelShop shop) {
        Location shopLocation = shop.getLocation();
        ItemIcon animationIcon = this.shopTradeAnimationIconMap.remove(shopLocation);
        if(animationIcon != null) animationIcon.remove();
        this.shopTradeAnimationIconMap.remove(shopLocation);
    }

    private void clearAllShopTradeAnimationIcons() {
        for(ItemIcon animationIcon : this.shopTradeAnimationIconMap.values()) animationIcon.remove();
        this.shopTradeAnimationIconMap.clear();
        this.shopTradesAnimationLoopIndexMap.clear();
    }

    public void runShopTradesAnimationLoop() {
        this.stopShopTradesAnimationLoop();
        this.shopTradesAnimationLoopTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this::shopTradesAnimationLoop, 0L, 5 * 20L);
    }

    public void stopShopTradesAnimationLoop() {
        if(this.shopTradesAnimationLoopTask != null) this.shopTradesAnimationLoopTask.cancel();
        this.shopTradesAnimationLoopTask = null;
        this.clearAllShopTradeAnimationIcons();
    }

}
