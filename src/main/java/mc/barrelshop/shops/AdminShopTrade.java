package mc.barrelshop.shops;

import mc.compendium.utils.bukkit.ItemStacks;
import org.bukkit.configuration.ConfigurationSection;

public class AdminShopTrade extends AbstractShopTrade {

    public static AdminShopTrade deserialize(ConfigurationSection section) {
        AdminShopTrade trade = new AdminShopTrade();

        //

        trade.setPrice(ItemStacks.deserialize(section.getString("price")));
        trade.setComplementaryPrice(ItemStacks.deserialize(section.getString("complementary-price")));
        trade.setResult(ItemStacks.deserialize(section.getString("result")));

        //

        return trade;
    }

    //

    public AdminShopTrade() {
        super();
    }

    //

    private boolean isComplete() {
        return this.getPrice() != null && this.getResult() != null;
    }

    //

    @Override
    public synchronized int getStocks() { return this.isComplete() ? Integer.MAX_VALUE : 0; }

    @Override
    public synchronized int getMaxStocks() { return this.isComplete() ? Integer.MAX_VALUE : 0; }

    @Override
    public synchronized int getEarnings() { return 0; }

    @Override
    public synchronized int getMaxEarnings() { return this.isComplete() ? Integer.MAX_VALUE : 0; }

    //

    @Override
    public synchronized void setStocks(int stocks) {}

    @Override
    public synchronized void setMaxStocks(int maxStocks) {}

    @Override
    public synchronized void setEarnings(int earnings) {}

    @Override
    public synchronized void setMaxEarnings(int maxEarnings) {}

    //

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("price", ItemStacks.serialize(this.getPrice()));
        section.set("complementary-price", ItemStacks.serialize(this.getComplementaryPrice()));
        section.set("result", ItemStacks.serialize(this.getResult()));
    }

}
