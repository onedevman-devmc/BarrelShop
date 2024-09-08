package mc.barrelshop.shops.ui.management;

import mc.barrelshop.shops.AdminShopTrade;
import mc.barrelshop.shops.BarrelShop;

public class AdminShopTradeManagementMenu extends AbstractShopTradeManagementMenu {

    public AdminShopTradeManagementMenu(BarrelShop shop, AdminShopTrade trade) {
        super(shop, trade);
    }

    //

    @Override
    public AdminShopTrade getTrade() { return (AdminShopTrade) super.getTrade(); }

}
