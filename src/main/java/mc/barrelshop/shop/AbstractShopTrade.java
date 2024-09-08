package mc.barrelshop.shop;

import mc.compendium.chestinterface.components.Trade;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractShopTrade extends Trade {

    public static AbstractShopTrade deserialize(ConfigurationSection section) { return null; }

    //

    public static final ItemStack EMPTY_ITEM;

    //

    static {
        EMPTY_ITEM = new ItemStack(Material.STRUCTURE_VOID, 1);
        ItemMeta meta = Objects.requireNonNull(EMPTY_ITEM.getItemMeta());

        meta.setDisplayName("§rEmpty");

        EMPTY_ITEM.setItemMeta(meta);
    }

    //

    public AbstractShopTrade() {
        super(EMPTY_ITEM, EMPTY_ITEM);
    }

    //

    @Override
    public synchronized ItemStack getPrice() {
        ItemStack price = super.getPrice();
        return EMPTY_ITEM.equals(price) ? null : price;
    }

    @Override
    public synchronized ItemStack getComplementaryPrice() {
        ItemStack complementaryPrice = super.getComplementaryPrice();
        return EMPTY_ITEM.equals(complementaryPrice) ? null : complementaryPrice;
    }

    @Override
    @Nullable
    public synchronized ItemStack getResult() {
        ItemStack result = super.getResult();
        return EMPTY_ITEM.equals(result) ? null : result;
    }

    //

    @Override
    public synchronized void setPrice(ItemStack price) {
        super.setPrice(price == null ? EMPTY_ITEM : price);
    }

    @Override
    public synchronized void setResult(ItemStack result) {
        super.setResult(result == null ? EMPTY_ITEM : result);
    }

    //

    public abstract void serialize(ConfigurationSection section);
}
