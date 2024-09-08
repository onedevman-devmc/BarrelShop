package mc.barrelshop.shop.ui.icon;

import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class CopyItemSlotIcon extends SlotIcon {

    @Override
    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);

        ClickType clickType = event.getClickType();

        ItemStack cursorItem = event.getCursorItem();
        ItemStack cursorItemClone = cursorItem == null ? null : cursorItem.clone();
        ItemStack storedItem = this.getItemStack();

        if(cursorItemClone != null && cursorItemClone.getType().equals(Material.AIR)) cursorItemClone = null;

        if(
            (!clickType.isLeftClick() || cursorItemClone == null)
            && !clickType.isRightClick()
        ) return;

        this.getChangeCallback().accept(storedItem, cursorItemClone);
        this.setItemStack(cursorItemClone);
        event.getClickedInventory().setItem(event.getSlot(), this.toBukkit());
    }

    //

    public CopyItemSlotIcon(BiConsumer<ItemStack, ItemStack> changeItemCallback) {
        this(changeItemCallback, null);
    }

    public CopyItemSlotIcon(BiConsumer<ItemStack, ItemStack> changeItemCallback, ItemStack placeHolder) {
        super(0, Integer.MAX_VALUE, changeItemCallback, placeHolder);
    }

    //

}
