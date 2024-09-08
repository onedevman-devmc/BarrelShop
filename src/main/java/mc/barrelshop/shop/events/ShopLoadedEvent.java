package mc.barrelshop.shop.events;

import mc.barrelshop.shop.BarrelShop;

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
