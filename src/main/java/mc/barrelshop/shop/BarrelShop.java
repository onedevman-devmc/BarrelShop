package mc.barrelshop.shop;

import mc.barrelshop.PluginMain;
import mc.barrelshop.shop.ui.management.ShopManagementInterface;
import mc.barrelshop.shop.ui.trading.ShopTradeInterface;
import mc.compendium.chestinterface.events.InterfaceEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.function.Function;

public class BarrelShop implements InterfaceEventListener {

    private static final String SHOP_SIGN_IDENTIFIER = "[shop]";
    private static final String ADMIN_SHOP_SIGN_IDENTIFIER = "[admin shop]";

    //

    public static class InvalidBarrelException extends Exception {
        public InvalidBarrelException() { super(); }
        public InvalidBarrelException(String message) { super(message); }
        public InvalidBarrelException(String message, Throwable cause) { super(message, cause); }
        public InvalidBarrelException(Throwable cause) { super(cause); }
    }

    public static class InvalidSignException extends Exception {
        public InvalidSignException() { super(); }
        public InvalidSignException(String message) { super(message); }
        public InvalidSignException(String message, Throwable cause) { super(message, cause); }
        public InvalidSignException(Throwable cause) { super(cause); }
    }
    
    public static class InvalidOwnerException extends Exception {
        public InvalidOwnerException() { super(); }
        public InvalidOwnerException(String message) { super(message); }
        public InvalidOwnerException(String message, Throwable cause) { super(message, cause); }
        public InvalidOwnerException(Throwable cause) { super(cause); }
    }

    //

    public static Sign getAssociatedSign(Barrel barrel) {
        Sign sign = null;

        Location barrelLocation = barrel.getLocation();

        for(int checkX = 0; checkX <= 1; checkX+=1) {
            for(int i = -1; i <= 1; i+=2) {
                int checkZ = (checkX + 1) % 2;

                Location checkPos = barrelLocation.clone().add(i * checkX, 0, i * checkZ);
                BlockState checkState = checkPos.getBlock().getState();

                if(isValidSign(checkState)) sign = (Sign) checkState;
            }
        }

        return sign;
    }

    public static Barrel getAssociatedBarrel(Sign sign) {
        if(!(sign.getBlockData() instanceof WallSign data)) return null;

        BlockState barrelCandidate = sign.getBlock().getLocation().add(data.getFacing().getOppositeFace().getDirection()).getBlock().getState();
        return barrelCandidate.getType().equals(Material.BARREL) ? (Barrel) barrelCandidate : null;
    }

    //

    public static boolean hasShopIdentifier(Sign sign) {
        return sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(SHOP_SIGN_IDENTIFIER);
    }

    public static boolean hasAdminShopIdentifier(Sign sign) {
        return sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(ADMIN_SHOP_SIGN_IDENTIFIER);
    }

    public static boolean isValidSign(BlockState sign) {
        return (sign instanceof Sign _sign) && isValidSign(_sign);
    }

    public static boolean isValidSign(Sign sign) {
        return (
            sign != null
            && ( hasShopIdentifier(sign) || hasAdminShopIdentifier(sign) )
            && getAssociatedBarrel(sign) != null
        );
    }

    //

    private static void checkIntegrity(BarrelShop shop) throws InvalidOwnerException, InvalidSignException {
        if(!isValidSign(shop.getSign())) throw new InvalidSignException("Sign must be placed against a barrel and have a shop identifier (\"[shop]\" or \"[admin shop]\") on the first line.");
        if(!shop.getSign().isPlaced()) throw new InvalidSignException("Sign doesn't exists.");
        if(shop.isAdmin() && shop.getOwner() != null) throw new InvalidOwnerException("This is an admin shop, so it shouldn't have an owner.");
    }

    //

    public static BarrelShop deserialize(ConfigurationSection section) throws InvalidBarrelException, InvalidSignException, InvalidOwnerException {
        Location location = Objects.requireNonNull(section.getLocation("location"));
        if(!(location.getBlock().getState() instanceof Barrel barrel))
            throw new InvalidBarrelException("Unable to find the shop associated barrel.");

        String ownerUuidStr = section.getString("owner");
        OfflinePlayer owner = ownerUuidStr == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(ownerUuidStr));

        boolean isAdmin = section.getBoolean("admin");

        BarrelShop shop = isAdmin ? new BarrelAdminShop(barrel) : new BarrelShop(barrel, owner);

        shop.setName(section.getString("name", shop.getName()));
        shop.setTradesCanBeAnimated(section.getBoolean("trades-animation", shop.doesTradesCanBeAnimated()));

        ConfigurationSection tradesSection = Objects.requireNonNull(section.getConfigurationSection("trades"));

        Function<ConfigurationSection, AbstractShopTrade> tradeDeserializationFunction = isAdmin ? AdminShopTrade::deserialize : ShopTrade::deserialize;

        for(String tradeIndexKey : tradesSection.getKeys(false)) {
            int index = Integer.parseInt(tradeIndexKey);
            AbstractShopTrade trade = tradeDeserializationFunction.apply(Objects.requireNonNull(tradesSection.getConfigurationSection(tradeIndexKey)));
            shop.trades().set(index, trade);
        }

        return shop;
    }

    //

    private final Barrel barrel;
    private final Sign sign;

    private String name;
    private OfflinePlayer owner;
    private boolean tradesCanBeAnimated = false;

    private final List<AbstractShopTrade> trades = Collections.synchronizedList(new ArrayList<>());

    private ShopManagementInterface managementInterface;
    private ShopTradeInterface tradeInterface;

    private final List<BarrelShopManager> managers = new ArrayList<>();

    //

    public BarrelShop(Sign sign, OfflinePlayer owner) throws InvalidSignException, InvalidOwnerException {
        this.barrel = getAssociatedBarrel(sign);
        this.sign = sign;

        this.init(owner);
    }

    public BarrelShop(Barrel barrel, OfflinePlayer owner) throws InvalidSignException, InvalidOwnerException {
        this.barrel = barrel;
        this.sign = getAssociatedSign(barrel);

        this.init(owner);
    }

    //

    private void init(OfflinePlayer owner) throws InvalidSignException, InvalidOwnerException {
        BarrelShop.checkIntegrity(this);

        this.name = "Shop";
        this.owner = owner;

        //

        this.initTrades();

        //

        this.managementInterface = new ShopManagementInterface(this);

        this.tradeInterface = new ShopTradeInterface(this);
        this.tradeInterface.addListener(this);
        this.tradeInterface.setTradeList(this.trades);

        //

        if(!this.isAdmin()) this.getSign().getSide(Side.FRONT).setLine(1, this.getOwner().getName());

        Bukkit.getScheduler().runTaskLater(PluginMain.instance(), () -> {
            this.getSign().getSide(Side.FRONT).setGlowingText(true);
            this.getSign().update(true);
        }, 1);
        this.sign.setWaxed(true);

        this.sign.update(true);
    }

    protected void initTrades() {
        for(int i = 0; i < 9; i++) this.trades.add(new ShopTrade());
    }

    //

    public Barrel getBarrel() { return this.barrel; }

    public Location getLocation() { return this.barrel.getLocation(); }

    public Sign getSign() { return this.sign; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public OfflinePlayer getOwner() { return this.owner; }

    public boolean doesTradesCanBeAnimated() { return this.tradesCanBeAnimated; }

    public void setTradesCanBeAnimated(boolean tradesCanBeAnimated) { this.tradesCanBeAnimated = tradesCanBeAnimated; }

    public void setOwner(OfflinePlayer owner) { this.owner = owner; }

    public boolean isAdmin() { return false; }

    public ShopManagementInterface getManagementInterface() { return this.managementInterface; }

    public ShopTradeInterface getTradeInterface() { return this.tradeInterface; }

    //

    public List<AbstractShopTrade> trades() { return this.trades; }

    //

    public void registerManager(BarrelShopManager manager) { if(!this.managers.contains(manager)) this.managers.add(manager); }

    public void unregisterManager(BarrelShopManager manager) { this.managers.remove(manager); }


    public void requestUpdateToManagers() {
        for(BarrelShopManager manager : this.managers)
            manager.updateShopTradeAnimationIcon(this);
    }

    //

    public void serialize(ConfigurationSection section) {
        section.set("location", this.getLocation());

        section.set("name", this.getName());

        OfflinePlayer owner = this.getOwner();
        section.set("owner", owner == null ? null : owner.getUniqueId().toString());

        section.set("trades-animation", this.doesTradesCanBeAnimated());

        section.set("admin", this.isAdmin());

        ConfigurationSection tradesSection = section.createSection("trades");

        for(int i = 0; i < this.trades.size(); i++) {
            AbstractShopTrade trade = this.trades.get(i);
            ConfigurationSection tradeSection = tradesSection.createSection("" + i);
            trade.serialize(tradeSection);
        }
    }

    //

    @Override
    public int hashCode() {
        return barrel.getLocation().hashCode();
    }

}
