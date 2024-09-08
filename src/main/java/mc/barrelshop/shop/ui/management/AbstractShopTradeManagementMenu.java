package mc.barrelshop.shop.ui.management;

import mc.barrelshop.shop.AbstractShopTrade;
import mc.barrelshop.shop.BarrelShop;
import mc.barrelshop.shop.ShopTrade;
import mc.barrelshop.shop.ui.InventoryHistory;
import mc.barrelshop.shop.ui.icon.*;
import mc.barrelshop.shop.ui.management.icon.*;
import mc.compendium.chestinterface.components.AbstractChestIcon;
import mc.compendium.chestinterface.components.ChestMenu;
import mc.compendium.chestinterface.components.configurations.ChestMenuConfig;
import mc.compendium.chestinterface.events.ChestMenuClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

public abstract class AbstractShopTradeManagementMenu extends ChestMenu<AbstractChestIcon<?>> {

    @EventHandler
    public void onClick(ChestMenuClickEvent event) {
        if(
            ( event.isInterfaceClick() && event.isClickedItemEmpty() )
            || ( event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) )
        ) event.setCancelled(true);
    }

    //

    private InventoryHistory history = new InventoryHistory();
    private final BarrelShop shop;
    private final AbstractShopTrade trade;

    //

    protected AbstractShopTradeManagementMenu(BarrelShop shop, AbstractShopTrade trade) {
        super(new ChestMenuConfig("Trade Management", false, 3));

        //

        this.addListener(this);

        //

        this.shop = shop;
        this.trade = trade;

        //

        AbstractShopTradeManagementMenu _this = this;

        for(int i = 0; i < 3; i++) {
            this.setIcon(i, 0, SeparatorIcon.getInstance());
            this.setIcon(i, 3, SeparatorIcon.getInstance());
            this.setIcon(i, 5, SeparatorIcon.getInstance());
            this.setIcon(i, 8, SeparatorIcon.getInstance());
        }

        //

        this.setIcon(0, 1, PriceSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.DOWN));
        this.setIcon(0, 2, ComplementaryPriceSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.DOWN));
        this.setIcon(0, 4, ResultSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.DOWN));
        this.setIcon(0, 6, EarningsContainerItemIndicatorIcon.get(SlotIndicatorIcon.Direction.DOWN));
        this.setIcon(0, 7, StocksContainerItemIndicatorIcon.get(SlotIndicatorIcon.Direction.DOWN));

        this.setIcon(2, 1, PriceSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.UP));
        this.setIcon(2, 2, ComplementaryPriceSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.UP));
        this.setIcon(2, 4, ResultSlotIndicatorIcon.get(SlotIndicatorIcon.Direction.UP));
        this.setIcon(2, 6, EarningsContainerItemIndicatorIcon.get(SlotIndicatorIcon.Direction.UP));
        this.setIcon(2, 7, StocksContainerItemIndicatorIcon.get(SlotIndicatorIcon.Direction.UP));

        //

        this.setIcon(1, 1, new CopyItemSlotIcon((itemBefore, itemAfter) -> {
            trade.setPrice(itemAfter);
        }, ShopTrade.EMPTY_ITEM));
        this.setIcon(1, 2, new CopyItemSlotIcon((itemBefore, itemAfter) -> {
            trade.setComplementaryPrice(itemAfter);
        }, ShopTrade.EMPTY_ITEM));
        this.setIcon(1, 4, new CopyItemSlotIcon((itemBefore, itemAfter) -> {
            trade.setResult(itemAfter);
            this.shop.requestUpdateToManagers();
        }, ShopTrade.EMPTY_ITEM));

        //

        this.setIcon(1, 6, DisabledIcon.getInstance());
        this.setIcon(1, 7, DisabledIcon.getInstance());
    }

    //

    public InventoryHistory getHistory() { return this.history; }

    public void setHistory(InventoryHistory history) { this.history = history; }

    public BarrelShop getShop() { return this.shop; }

    public AbstractShopTrade getTrade() { return this.trade; }

    //

    protected void load() {
        ((ItemStackIcon) this.getIcon(1, 1)).setItemStack(this.trade.getPrice());
        ((ItemStackIcon) this.getIcon(1, 2)).setItemStack(this.trade.getComplementaryPrice());
        ((ItemStackIcon) this.getIcon(1, 4)).setItemStack(this.trade.getResult());

        //

        this.setIcon(2, 8, new BackIcon(this.history));
    }

    //

    @Override
    public Inventory toBukkit() {
        this.load();

        //

        return super.toBukkit();
    }
}
