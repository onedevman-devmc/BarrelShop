package mc.barrelshop.shop.ui.management;

import mc.barrelshop.shop.AbstractShopTrade;
import mc.barrelshop.shop.BarrelShop;
import mc.barrelshop.shop.ui.InventoryHistory;
import mc.barrelshop.shop.ui.icon.BackIcon;
import mc.barrelshop.shop.ui.icon.SeparatorIcon;
import mc.barrelshop.shop.ui.management.icon.ChangeShopNameIcon;
import mc.barrelshop.shop.ui.management.icon.ShopTadeIcon;
import mc.barrelshop.shop.ui.management.icon.ToggleTradesAnimationIconIcon;
import mc.compendium.chestinterface.components.AbstractChestIcon;
import mc.compendium.chestinterface.components.ChestMenu;
import mc.compendium.chestinterface.components.configurations.ChestMenuConfig;
import mc.compendium.chestinterface.events.ChestMenuClickEvent;
import mc.compendium.events.EventHandler;
import mc.compendium.events.EventHandlerPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ShopManagementInterface extends ChestMenu<AbstractChestIcon<?>> {

    @EventHandler(priority = EventHandlerPriority.NORMAL)
    public void onClick(ChestMenuClickEvent event) {
        if(
            ( event.isInterfaceClick() && event.isClickedItemEmpty() )
            || ( event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) )
        ) {
            event.setCancelled(true);
            return;
        }

        //

        AbstractChestIcon<?> icon = event.getIcon();
        if(icon instanceof BackIcon) { event.getPlayer().closeInventory(); }
    }

    //

    private InventoryHistory history = new InventoryHistory();
    private final BarrelShop shop;

    //

    public ShopManagementInterface(BarrelShop shop) {
        super(new ChestMenuConfig("Shop Management", false, 4));

        //

        this.addListener(this);

        //

        this.shop = shop;

        //

        for(int i = 0; i < 9; i++) this.setIcon(0, i, SeparatorIcon.getInstance());
        for(int i = 0; i < 9; i++) this.setIcon(1, i, SeparatorIcon.getInstance());

        for(int i = 0; i < 8; i++) this.setIcon(3, i, SeparatorIcon.getInstance());

        this.setIcon(3, 8, new BackIcon(this.history));
    }

    //

    public InventoryHistory getHistory() { return this.history; }

    public void setHistory(InventoryHistory history) { this.history = history; }

    public BarrelShop getShop() { return this.shop; }

    //

    @Override
    public Inventory toBukkit() {
        this.setIcon(0, 3, new ChangeShopNameIcon(this.shop));
        this.setIcon(0, 5, new ToggleTradesAnimationIconIcon(this.shop));

        //

        List<AbstractShopTrade> trades = this.shop.trades();
        int tradeCount = trades.size();
        for(int i = 0; i < tradeCount; i++) {
            ShopTadeIcon tradeIcon = new ShopTadeIcon(this.shop, trades.get(i), this.history);
            tradeIcon.config().setName("§a§lTrade §7§ln°§5§l" + (i+1));
            this.setIcon(2, i, tradeIcon);
        }

        //

        return super.toBukkit();
    }
}
