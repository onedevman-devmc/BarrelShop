package mc.barrelshop.shop.ui.management;

import mc.barrelshop.shop.BarrelShop;
import mc.barrelshop.shop.ShopTrade;
import mc.barrelshop.shop.ui.management.icon.ContainerItemSlotIcon;

public class ShopTradeManagementMenu extends AbstractShopTradeManagementMenu {

    public ShopTradeManagementMenu(BarrelShop shop, ShopTrade trade) {
        super(shop, trade);

        //

        this.setIcon(1, 6, new ContainerItemSlotIcon(trade.getEarningsContainerItemList(), 0, ShopTrade.EMPTY_ITEM));
        this.setIcon(1, 7, new ContainerItemSlotIcon(trade.getStocksContainerItemList(), 0, ShopTrade.EMPTY_ITEM));
    }

    //

    @Override
    public ShopTrade getTrade() { return (ShopTrade) super.getTrade(); }

}
