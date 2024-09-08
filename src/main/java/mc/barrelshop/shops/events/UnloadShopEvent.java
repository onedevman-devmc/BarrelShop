package mc.barrelshop.shops.events;

import mc.barrelshop.shops.BarrelShop;

public class UnloadShopEvent extends PersistentBarrelShopRegistryEvent {

    private final BarrelShop shop;

    //

    public UnloadShopEvent(BarrelShop shop) {
        this.shop = shop;
    }

    //

    public BarrelShop getShop() { return this.shop; }

}
