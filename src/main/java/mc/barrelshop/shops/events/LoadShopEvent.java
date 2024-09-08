package mc.barrelshop.shops.events;

import org.bukkit.Location;

public class LoadShopEvent extends PersistentBarrelShopRegistryEvent {

    private final Location shopLocation;

    //

    public LoadShopEvent(Location shopLocation) {
        this.shopLocation = shopLocation;
    }

    //

    public Location getShopLocation() { return shopLocation.clone(); }

}
