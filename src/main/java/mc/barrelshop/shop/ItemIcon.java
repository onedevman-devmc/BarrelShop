package mc.barrelshop.shop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemIcon {

    private Location location;

    private final Item item;
    private final BlockDisplay blockDisplay;

    //

    ItemIcon(Location location) {
        this.setLocation(location, false);

        World world = Objects.requireNonNull(this.location.getWorld());

        this.item = world.dropItem(this.location, new ItemStack(Material.STONE));

        this.item.setVelocity(this.item.getVelocity().zero());
        this.item.setPickupDelay(Integer.MAX_VALUE);
        this.item.setUnlimitedLifetime(true);

        this.blockDisplay = world.spawn(this.location, BlockDisplay.class);
        this.blockDisplay.addPassenger(this.item);

        this.update();
    }

    //

    public Item item() { return item; }

    public Location getLocation() { return this.location; }

    public void setLocation(Location location) {
        this.setLocation(location, true);
    }

    private void setLocation(Location location, boolean update) {
        this.location = location.clone();
        if(update) this.update();
    }

    public ItemStack getItemStack() { return this.item.getItemStack(); }

    public void setItemStack(ItemStack itemStack) { this.item.setItemStack(itemStack); }

    public boolean isCustomNameVisible() { return this.item.isCustomNameVisible(); }

    public void setCustomNameVisible(boolean isCustomNameVisible) { this.item.setCustomNameVisible(isCustomNameVisible); }

    public String getCustomName() { return this.item.getCustomName(); }

    public void setCustomName(String customName) { this.item.setCustomName(customName); }

    //

    public void update() {
        if(!this.blockDisplay.getLocation().equals(this.location))
            this.blockDisplay.teleport(this.location);

        if(!this.item.getLocation().equals(this.location)) {
            this.item.teleport(this.location);
            this.blockDisplay.removePassenger(this.item);
            this.blockDisplay.addPassenger(this.item);
        }

        this.item.setPickupDelay(Integer.MAX_VALUE);
    }

    //

    public void remove() {
        this.item.remove();
        this.blockDisplay.remove();
    }

}
