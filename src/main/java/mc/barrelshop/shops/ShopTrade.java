package mc.barrelshop.shops;

import mc.compendium.utils.bukkit.ItemStacks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShopTrade extends InventoriedTrade {

    public static ShopTrade deserialize(ConfigurationSection section) {
        InventoriedTrade inventoriedTrade = InventoriedTrade.deserialize(section);
        ShopTrade trade = new ShopTrade();

        //

        trade.setPrice(inventoriedTrade.getPrice());
        trade.setComplementaryPrice(inventoriedTrade.getComplementaryPrice());
        trade.setResult(inventoriedTrade.getResult());

        //

        ConfigurationSection containerItemListsSection = Objects.requireNonNull(section.getConfigurationSection("container-item-lists"));

        ConfigurationSection stocksContainerItemListSection = Objects.requireNonNull(containerItemListsSection.getConfigurationSection("stocks"));
        ConfigurationSection earningsContainerItemListSection = Objects.requireNonNull(containerItemListsSection.getConfigurationSection("earnings"));

        ContainerItemList stocksContainerItemList = new ContainerItemList(inventoriedTrade.getStocksInventoryList());

        for(String indexKey : stocksContainerItemListSection.getKeys(false)) {
            String serializedContainerItem = stocksContainerItemListSection.getString(indexKey);
            stocksContainerItemList.add(ItemStacks.deserialize(serializedContainerItem));
        }

        ContainerItemList earningsContainerItemList = new ContainerItemList(inventoriedTrade.getEarningsInventoryList());

        for(String indexKey : earningsContainerItemListSection.getKeys(false)) {
            String serializedContainerItem = earningsContainerItemListSection.getString(indexKey);
            earningsContainerItemList.add(ItemStacks.deserialize(serializedContainerItem));
        }

        trade.setStocksContainerItemList(stocksContainerItemList);
        trade.setEarningsContainerItemList(earningsContainerItemList);

        return trade;
    }

    //

    private ContainerItemList stocksContainerItemList = new ContainerItemList(this.getInternalStocksInventoryList());
    private ContainerItemList earningsContainerItemList = new ContainerItemList(this.getInternalEarningsInventoryList());

    //

    public ShopTrade() {
        super();
    }

    //

    public synchronized List<Inventory> getInternalStocksInventoryList() {
        return super.getStocksInventoryList();
    }

    @Override
    public synchronized List<Inventory> getStocksInventoryList() {
        return Collections.unmodifiableList(this.getInternalStocksInventoryList());
    }

    public synchronized List<Inventory> getInternalEarningsInventoryList() {
        return super.getEarningsInventoryList();
    }

    @Override
    public synchronized List<Inventory> getEarningsInventoryList() {
        return Collections.unmodifiableList(this.getInternalEarningsInventoryList());
    }

    //

    public synchronized ContainerItemList getStocksContainerItemList() {
        return this.stocksContainerItemList;
    }

    public synchronized ContainerItemList getEarningsContainerItemList() {
        return this.earningsContainerItemList;
    }

    //

    @Override
    public synchronized void setStocksInventoryList(List<Inventory> stocksInventoryList) {
        throw new UnsupportedOperationException("Managed by container item lists.");
    }

    @Override
    public synchronized void setEarningsInventoryList(List<Inventory> earningsInventoryList) {
        throw new UnsupportedOperationException("Managed by container item lists.");
    }

    //

    private void setStocksContainerItemList(ContainerItemList stocksContainerItemList) {
        this.stocksContainerItemList = stocksContainerItemList;
        super.setStocksInventoryList(stocksContainerItemList.getInventoryList());
    }

    private void setEarningsContainerItemList(ContainerItemList earningsContainerItemList) {
        this.earningsContainerItemList = earningsContainerItemList;
        super.setEarningsInventoryList(earningsContainerItemList.getInventoryList());
    }

    //

    @Override
    public synchronized void serialize(ConfigurationSection section) {
        super.serialize(section);

        ConfigurationSection containerItemListsSection = section.createSection("container-item-lists");

        ConfigurationSection stocksContainerItemListSection = containerItemListsSection.createSection("stocks");
        ConfigurationSection earningsContainerItemListSection = containerItemListsSection.createSection("earnings");

        int stocksContainerItemCount = this.stocksContainerItemList.size();
        for(int i = 0; i < stocksContainerItemCount; i++) {
            stocksContainerItemListSection.set("" + i, ItemStacks.serialize(this.stocksContainerItemList.get(i)));
        }

        int earningsContainerItemCount = this.earningsContainerItemList.size();
        for(int i = 0; i < earningsContainerItemCount; i++) {
            earningsContainerItemListSection.set("" + i, ItemStacks.serialize(this.earningsContainerItemList.get(i)));
        }
    }

}
