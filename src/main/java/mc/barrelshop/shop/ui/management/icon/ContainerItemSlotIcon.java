package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.ContainerItemList;
import mc.barrelshop.shop.ui.icon.SlotIcon;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import mc.compendium.events.EventHandlerPriority;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ContainerItemSlotIcon extends SlotIcon {

    @EventHandler(priority = EventHandlerPriority.HIGHEST)
    protected void onLeftClick(ChestIconClickEvent event) {
        ItemStack cursorItem = event.getCursorItem();
        if(cursorItem == null || cursorItem.getType().equals(Material.AIR)) return;

        try { ContainerItemList.checkContainerItemValidity(cursorItem); }
        catch (ContainerItemList.InvalidContainerItemException e) { event.setCancelled(true); }
    }

    @EventHandler(ignoreCancelled = true)
    protected void onRightClick(ChestIconClickEvent event) {
        if(!event.getClickType().isRightClick()) return;
        event.setCancelled(true);

        Inventory containerInventory = this.getContainerInventory();
        if(containerInventory != null) event.getPlayer().openInventory(containerInventory);
    }

    //

    private final ContainerItemList containerItemList;
    private final int containerItemIndex;

    //

    public ContainerItemSlotIcon(ContainerItemList containerItemList, int containerItemIndex) {
        this(containerItemList, containerItemIndex, null);
    }

    public ContainerItemSlotIcon(ContainerItemList containerItemList, int containerItemIndex, ItemStack placeHolder) {
        super(1, 1, placeHolder);

        this.containerItemList = containerItemList;
        this.containerItemIndex = containerItemIndex;
    }

    //

    public List<ItemStack> getContainerItemList() { return this.containerItemList; }

    public int getContainerItemIndex() { return this.containerItemIndex; }

    public Inventory getContainerInventory() {
        List<Inventory> containerInventoryList = this.containerItemList.getInventoryList();
        return containerInventoryList.size() > this.containerItemIndex ? containerInventoryList.get(this.containerItemIndex) : null;
    }

    //


    @Override
    public ItemStack getItemStack() {
        return this.containerItemList.size() > this.containerItemIndex ? this.containerItemList.get(this.containerItemIndex) : null;
    }

    @Override
    public void setItemStack(ItemStack itemStack) {
        if(this.containerItemList.size() > this.containerItemIndex)
            this.containerItemList.set(this.containerItemIndex, itemStack);
        else
            this.containerItemList.add(this.containerItemIndex, itemStack);
    }

}
