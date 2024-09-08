package mc.barrelshop.shop.ui.management;

import mc.barrelshop.shop.AdminShopTrade;
import mc.barrelshop.shop.BarrelShop;

public class AdminShopTradeManagementMenu extends AbstractShopTradeManagementMenu {

    public AdminShopTradeManagementMenu(BarrelShop shop, AdminShopTrade trade) {
        super(shop, trade);
    }

    //

    @Override
    public AdminShopTrade getTrade() { return (AdminShopTrade) super.getTrade(); }

}
