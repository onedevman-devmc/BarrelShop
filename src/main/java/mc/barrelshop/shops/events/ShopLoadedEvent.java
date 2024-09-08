package mc.barrelshop.shops.events;

import mc.barrelshop.shops.BarrelShop;
import org.bukkit.Location;

public class ShopLoadedEvent extends PersistentBarrelShopRegistryEvent {

    private final BarrelShop shop;

    //

    public ShopLoadedEvent(BarrelShop shop) {
        super(false);

        //

        this.shop = shop;
    }

    //

    public BarrelShop getShop() { return this.shop; }

}
