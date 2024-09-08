package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.AbstractShopTrade;
import mc.barrelshop.shop.AdminShopTrade;
import mc.barrelshop.shop.BarrelShop;
import mc.barrelshop.shop.ShopTrade;
import mc.barrelshop.shop.ui.InventoryHistory;
import mc.barrelshop.shop.ui.management.AbstractShopTradeManagementMenu;
import mc.barrelshop.shop.ui.management.AdminShopTradeManagementMenu;
import mc.barrelshop.shop.ui.management.ShopTradeManagementMenu;
import mc.compendium.chestinterface.components.AbstractChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.chestinterface.events.InterfaceEventListener;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopTadeIcon extends AbstractChestIcon<ShopTadeIcon.Config> implements InterfaceEventListener {

    public static class Config extends ChestIconConfig {

        private final AbstractShopTrade trade;

        //

        public Config(AbstractShopTrade trade) {
            super(
                Material.STRUCTURE_VOID,
                "",
                1,
                List.of("§7Click to manage this trade."),
                false
            );

            //

            this.trade = trade;
        }

        //

        public AbstractShopTrade getTrade() { return this.trade; }

        //

        private ItemStack getTradeResultRepresentation() {
            ItemStack result = this.trade.getResult();
            return (result == null ? ShopTrade.EMPTY_ITEM : result);
        }

        //

        @Override
        public Material getMaterial() {
            return this.getTradeResultRepresentation().getType();
        }

        @Override
        public int getAmount() {
            return this.getTradeResultRepresentation().getAmount();
        }

        @Override
        public boolean getEnchanted() { return !this.getTradeResultRepresentation().getEnchantments().isEmpty(); }

    }

    //

    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        if(this.managementMenu == null) return;

        this.managementMenu.getHistory().push(() -> event.getInterface().toBukkit());
        event.getPlayer().openInventory(this.managementMenu.toBukkit());
    }

    //

    private AbstractShopTradeManagementMenu managementMenu;

    //

    public ShopTadeIcon(BarrelShop shop, AbstractShopTrade trade, InventoryHistory history) {
        super(new Config(trade));

        //

        this.addListener(this);

        //

        if(trade instanceof ShopTrade shopTrade) this.managementMenu = new ShopTradeManagementMenu(shop, shopTrade);
        else if(trade instanceof AdminShopTrade adminShopTrade) this.managementMenu = new AdminShopTradeManagementMenu(shop, adminShopTrade);

        if(this.managementMenu != null) this.managementMenu.setHistory(history);
    }

    //

    public AbstractShopTradeManagementMenu getManagementMenu() { return this.managementMenu; }

}
