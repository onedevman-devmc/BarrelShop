package mc.barrelshop.shops.events;

import mc.barrelshop.shops.BarrelShop;

public class ShopUnloadedEvent extends PersistentBarrelShopRegistryEvent {

    private final BarrelShop shop;

    //

    public ShopUnloadedEvent(BarrelShop shop) {
        super(false);

        //

        this.shop = shop;
    }

    //

    public BarrelShop getShop() { return this.shop; }

}
