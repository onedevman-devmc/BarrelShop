package mc.barrelshop.shops.ui.icon;

import mc.compendium.chestinterface.ChestInterfaceApi;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class SlotIcon extends ItemStackIcon {

    private static class PlacerHolderItem extends ItemStack {

        public static PlacerHolderItem from(ItemStack itemStack) {
            return new PlacerHolderItem(itemStack);
        }

        //

        private PlacerHolderItem(ItemStack itemStack) {
            super(itemStack);
        }

    }

    //

    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        ClickType clickType = event.getClickType();
        if(clickType.isRightClick()) {
            event.setCancelled(true);
            return;
        }

        ItemStack cursorItem = event.getCursorItem();
        ItemStack cursorItemClone = cursorItem == null ? null : cursorItem.clone();
        ItemStack storedItem = this.getItemStack();

        if(cursorItemClone == null || cursorItemClone.getType().equals(Material.AIR)) {
            if(storedItem == null) event.setCancelled(true);
            else {
                if(this.changeItemCallback != null) this.changeItemCallback.accept(storedItem, null);
                this.setItemStack(null);
                Bukkit.getScheduler().runTaskLater(ChestInterfaceApi.getInstance().getPlugin(), () -> {
                    event.getClickedInventory().setItem(event.getSlot(), this.toBukkit());
                }, 1L);
            }
        }
        else {
            int cursorItemCloneAmount = cursorItemClone.getAmount();
            if(cursorItemCloneAmount < this.getMinAcceptedAmount()) {
                event.setCancelled(true);
                return;
            }
            else if(cursorItemClone.isSimilar(storedItem)) {
                int storedItemAmount = storedItem.getAmount();

                if(cursorItemClone.getMaxStackSize() - cursorItemClone.getAmount() >= storedItemAmount) {
                    if(this.changeItemCallback != null) this.changeItemCallback.accept(storedItem, null);
                    this.setItemStack(null);

                    ItemStack finalPlayerCursorItem = cursorItemClone.clone();
                    finalPlayerCursorItem.setAmount(finalPlayerCursorItem.getAmount() + storedItemAmount);

                    event.getPlayer().setItemOnCursor(finalPlayerCursorItem);
                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(ChestInterfaceApi.getInstance().getPlugin(), () -> {
                        event.getClickedInventory().setItem(event.getSlot(), this.toBukkit());
                    }, 1L);
                }
            }
            else {
                int maxAcceptedAmount = this.getMaxAcceptedAmount();
                int amountToTake = Math.min(cursorItemCloneAmount, maxAcceptedAmount);

                ItemStack newStoredItem = cursorItemClone.clone();
                newStoredItem.setAmount(amountToTake);

                if (this.changeItemCallback != null) this.changeItemCallback.accept(storedItem, newStoredItem);
                this.setItemStack(newStoredItem);

                if (storedItem == null)
                    event.getClickedInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));

                if (cursorItemCloneAmount > maxAcceptedAmount) {
                    ItemStack finalPlayerCursorItem = cursorItemClone.clone();
                    finalPlayerCursorItem.setAmount(finalPlayerCursorItem.getAmount() - maxAcceptedAmount);

                    event.getPlayer().setItemOnCursor(finalPlayerCursorItem);
                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(ChestInterfaceApi.getInstance().getPlugin(), () -> {
                        event.getClickedInventory().setItem(event.getSlot(), this.toBukkit());
                    }, 1L);
                }
            }
        }
    }

    //

    private BiConsumer<ItemStack, ItemStack> changeItemCallback;
    private ItemStack placeholder;

    //

    private int minAcceptedAmount;
    private int maxAcceptedAmount;

    //

    public SlotIcon(int minAcceptedAmount, int maxAcceptedAmount) {
        this(minAcceptedAmount, maxAcceptedAmount, (ItemStack) null);
    }

    public SlotIcon(int minAcceptedAmount, int maxAcceptedAmount, BiConsumer<ItemStack, ItemStack> changeItemCallback) {
        this(minAcceptedAmount, maxAcceptedAmount, changeItemCallback, null);
    }

    public SlotIcon(int minAcceptedAmount, int maxAcceptedAmount, ItemStack placeholder) {
        this(minAcceptedAmount, maxAcceptedAmount, null, placeholder);
    }

    public SlotIcon(int minAcceptedAmount, int maxAcceptedAmount, BiConsumer<ItemStack, ItemStack> changeItemCallback, ItemStack placeholder) {
        this.minAcceptedAmount = minAcceptedAmount;
        this.maxAcceptedAmount = maxAcceptedAmount;
        this.changeItemCallback = changeItemCallback;
        this.placeholder = placeholder;
    }

    //

    private int getMinAcceptedAmount() { return this.minAcceptedAmount; }

    private void setMinAcceptedAmount(int minAcceptedAmount) {
        if(minAcceptedAmount < 1 || minAcceptedAmount > 64) throw new IllegalArgumentException("Amount can't must be a number between 1 and 64.");
        this.minAcceptedAmount = minAcceptedAmount;
    }

    private int getMaxAcceptedAmount() { return this.maxAcceptedAmount; }

    private void setMaxAcceptedAmount(int maxAcceptedAmount) {
        if(maxAcceptedAmount < 1 || maxAcceptedAmount > 64) throw new IllegalArgumentException("Amount can't must be a number between 1 and 64.");
        this.maxAcceptedAmount = maxAcceptedAmount;
    }

    public BiConsumer<ItemStack, ItemStack> getChangeCallback() { return this.changeItemCallback; }

    public void setChangeCallback(BiConsumer<ItemStack, ItemStack> changeItemCallback) { this.changeItemCallback = changeItemCallback; }

    public ItemStack getPlaceholder() { return this.placeholder; }

    public void setPlaceholder(ItemStack placeholder) { this.placeholder = placeholder; }

    //

    @Override
    public ItemStack toBukkit() {
        ItemStack finalItemStack = super.toBukkit();
        if(finalItemStack instanceof ItemStackIcon.EmptyItem)
            return PlacerHolderItem.from(this.placeholder == null ? new ItemStackIcon.EmptyItem() : this.placeholder.clone());
        else
            return finalItemStack;
    }

}
