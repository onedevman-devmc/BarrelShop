package mc.barrelshop.shop.ui.icon;

import mc.barrelshop.shop.ui.icon.config.EmptyIconConfig;
import mc.compendium.chestinterface.components.AbstractChestIcon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackIcon extends AbstractChestIcon<EmptyIconConfig> {

    protected static class EmptyItem extends ItemStack {

        public EmptyItem() {
            super(Material.AIR);
        }

    }

    //

    private ItemStack itemStack;

    //

    public ItemStackIcon() {
        super(new EmptyIconConfig());
    }

    //

    public ItemStack getItemStack() { return this.itemStack; }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    //

    @Override
    public ItemStack toBukkit() {
        ItemStack itemStack = this.getItemStack();
        return itemStack == null ? new EmptyItem() : itemStack.clone();
    }

}
