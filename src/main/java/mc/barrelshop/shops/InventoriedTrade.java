package mc.barrelshop.shops;

import mc.compendium.types.Pair;
import mc.compendium.types.Trio;
import mc.compendium.utils.bukkit.Inventories;
import mc.compendium.utils.bukkit.ItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

public class InventoriedTrade extends AbstractShopTrade {

    public static InventoriedTrade deserialize(ConfigurationSection section) {
        InventoriedTrade trade = new InventoriedTrade();

        //

        trade.setPrice(ItemStacks.deserialize(section.getString("price")));
        trade.setComplementaryPrice(ItemStacks.deserialize(section.getString("complementary-price")));
        trade.setResult(ItemStacks.deserialize(section.getString("result")));

        ConfigurationSection inventoriesSection = Objects.requireNonNull(section.getConfigurationSection("inventories"));

        ConfigurationSection stocksInventoryListSection = Objects.requireNonNull(inventoriesSection.getConfigurationSection("stocks"));
        ConfigurationSection earningsInventoryListSection = Objects.requireNonNull(inventoriesSection.getConfigurationSection("earnings"));

        int stocksInventoryCount = stocksInventoryListSection.getKeys(false).size();
        List<Inventory> stocksInventoryList = new ArrayList<>(stocksInventoryCount);
        trade.setStocksInventoryList(stocksInventoryList);

        for(int index = 0; index < stocksInventoryCount; index++) {
            ItemStack[] content = Inventories.deserializeContent(stocksInventoryListSection.getString("" + index));
            stocksInventoryList.add(Inventories.copyContentInto(content, Bukkit.createInventory(null, content.length)));
        }

        int earningsInventoryCount = earningsInventoryListSection.getKeys(false).size();
        List<Inventory> earningsInventoryList = new ArrayList<>(earningsInventoryCount);
        trade.setEarningsInventoryList(earningsInventoryList);

        for(int index = 0; index < earningsInventoryCount; index++) {
            ItemStack[] content = Inventories.deserializeContent(earningsInventoryListSection.getString("" + index));
            earningsInventoryList.add(Inventories.copyContentInto(content, Bukkit.createInventory(null, content.length)));
        }

        return trade;
    }

    //

    private List<Inventory> stocksInventoryList = Collections.synchronizedList(new ArrayList<>());
    private List<Inventory> earningsInventoryList = Collections.synchronizedList(new ArrayList<>());

    //

    public InventoriedTrade() {
        super();
    }

    //

    @Override
    public synchronized int getStocks() {
        ItemStack tradeResult = this.getResult();
        if(tradeResult == null) return 0;

        int storedTradeResultAmount = 0;

        for(Inventory stocksInventory : this.stocksInventoryList) {
            if(stocksInventory != null) {
                for (ItemStack itemStack : stocksInventory.getContents()) {
                    if (itemStack != null && itemStack.isSimilar(this.getResult()))
                        storedTradeResultAmount += itemStack.getAmount();
                }
            }
        }

        return storedTradeResultAmount / tradeResult.getAmount();
    }

    @Override
    public synchronized int getMaxStocks() {
        ItemStack tradeResult = this.getResult();
        if(tradeResult == null) return 0;
        int maxStocks = 0;

        for(Inventory stocksInventory : this.stocksInventoryList) {
            if(stocksInventory != null) {
                for(ItemStack itemStack : stocksInventory.getContents()) {
                    if(itemStack == null || itemStack.isSimilar(tradeResult)) {
                        maxStocks += 1;
                    }
                }
            }
        }

        maxStocks = maxStocks * tradeResult.getMaxStackSize() / tradeResult.getAmount();
        return maxStocks;
    }

    //

    @Override
    public synchronized int getEarnings() {
        ItemStack tradePrice = this.getPrice();
        if(tradePrice == null) return 0;

        ItemStack tradeComplementaryPrice = this.getComplementaryPrice();

        int storedTradePriceAmount = 0;
        int storedTradeComplementaryPriceAmount = 0;

        for(Inventory earningsInventory : this.earningsInventoryList) {
            if(earningsInventory != null) {
                for(ItemStack itemStack : earningsInventory.getContents()) {
                    if(itemStack != null) {
                        if(itemStack.isSimilar(tradePrice)) storedTradePriceAmount += itemStack.getAmount();
                        else if(itemStack.isSimilar(tradeComplementaryPrice)) storedTradeComplementaryPriceAmount += itemStack.getAmount();
                    }
                }
            }
        }

        int storedTradePrices = storedTradePriceAmount / tradePrice.getAmount();

        return (
            tradeComplementaryPrice == null
            ? storedTradePrices
            : Math.min(storedTradePrices, storedTradeComplementaryPriceAmount / tradeComplementaryPrice.getAmount())
        );
    }

    @Override
    public synchronized int getMaxEarnings() {
        ItemStack tradePrice = this.getPrice();
        if(tradePrice == null) return 0;

        ItemStack tradeComplementaryPrice = this.getComplementaryPrice();

        int ingredientCount = 1 + (tradeComplementaryPrice == null ? 0 : 1);
        int candidateSlotCount = 0;

        for(Inventory earningsInventory : this.earningsInventoryList) {
            if(earningsInventory != null) {
                for(ItemStack itemStack : earningsInventory.getContents()) {
                    if(itemStack == null || itemStack.isSimilar(tradePrice)) {
                        candidateSlotCount += 1;
                    }
                }
            }
        }

        int eachIngredientSlotCount = candidateSlotCount / ingredientCount;
        int maxTradePrices = eachIngredientSlotCount * tradePrice.getMaxStackSize() / tradePrice.getAmount();

        return (
            tradeComplementaryPrice == null
            ? maxTradePrices
            : Math.min(
                maxTradePrices,
                eachIngredientSlotCount * tradeComplementaryPrice.getMaxStackSize() / tradeComplementaryPrice.getAmount()
            )
        );
    }

    //

    public synchronized List<Inventory> getStocksInventoryList() {
        return this.stocksInventoryList;
    }

    public synchronized List<Inventory> getEarningsInventoryList() {
        return this.earningsInventoryList;
    }

    //

    @Override
    public synchronized void setStocks(int stocks) {
        if(this.stocksInventoryList == null) throw new IllegalStateException("Shop trade haven't stocks inventory.");
        if(stocks < 0) throw new IllegalArgumentException("Stocks should be a number greater than 0.");

        if(this.getResult() == null) return;

        int currentStocks = this.getStocks();

        ItemStack itemStack = this.getResult().clone();
        itemStack.setAmount(Math.abs(stocks - currentStocks) * itemStack.getAmount());

        Function<Pair<Inventory, ItemStack>, Map<Integer, ItemStack>> stocksAction = null;

        if(stocks > currentStocks) stocksAction = pair -> pair.first().addItem(pair.last());
        else if(stocks < currentStocks) stocksAction = pair -> pair.first().removeItem(pair.last());

        if(stocksAction == null) return;

        int stocksInventoryCount = this.stocksInventoryList.size();
        for(int i = 0; i < stocksInventoryCount && itemStack != null && itemStack.getAmount() > 0; i++) {
            Inventory stocksInventory = this.stocksInventoryList.get(i);
            if(stocksInventory != null) {
                Map<Integer, ItemStack> rest = stocksAction.apply(Pair.of(stocksInventory, itemStack));
                itemStack = rest.get(0);
            }
        }
    }

    @Override
    public synchronized void setEarnings(int earnings) {
        if(this.earningsInventoryList == null) throw new IllegalStateException("Shop trade haven't earnings inventory.");
        if(earnings < 0) throw new IllegalArgumentException("Earnings should be a number greater than 0.");

        if(this.getPrice() == null) return;

        int currentEarnings = this.getEarnings();

        ItemStack priceItem = this.getPrice().clone();
        priceItem.setAmount(Math.abs(earnings - currentEarnings) * priceItem.getAmount());

        ItemStack complementaryPrice = this.getComplementaryPrice();
        ItemStack complementaryPriceItem = new ItemStack(Material.AIR);
        if(complementaryPrice != null) {
            complementaryPriceItem = complementaryPrice.clone();
            complementaryPriceItem.setAmount(Math.abs(earnings - currentEarnings) * complementaryPriceItem.getAmount());
        }

        Function<Trio<Inventory, ItemStack, ItemStack>, Map<Integer, ItemStack>> earningsAction = null;

        if(earnings > currentEarnings) earningsAction = trio -> trio.one().addItem(trio.two(), trio.three());
        else if(earnings < currentEarnings) earningsAction = trio -> trio.one().removeItem(trio.two(), trio.three());

        if(earningsAction == null) return;

        int earningsInventoryCount = this.earningsInventoryList.size();
        for(int i = 0; i < earningsInventoryCount && priceItem != null && priceItem.getAmount() > 0; i++) {
            Inventory earningsInventory = this.earningsInventoryList.get(i);
            if(earningsInventory != null) {
                Map<Integer, ItemStack> rest = earningsAction.apply(Trio.of(earningsInventory, priceItem, complementaryPriceItem));
                priceItem = rest.get(0);
                complementaryPriceItem = rest.get(1);
            }
        }
    }

    //

    public synchronized void setStocksInventoryList(List<Inventory> stocksInventoryList) {
        this.stocksInventoryList = Collections.synchronizedList(stocksInventoryList);
    }

    public synchronized void setEarningsInventoryList(List<Inventory> earningsInventoryList) {
        this.earningsInventoryList = Collections.synchronizedList(earningsInventoryList);
    }

    //

    @Override
    public synchronized void serialize(ConfigurationSection section) {
        section.set("price", ItemStacks.serialize(this.getPrice()));
        section.set("complementary-price", ItemStacks.serialize(this.getComplementaryPrice()));
        section.set("result", ItemStacks.serialize(this.getResult()));

        ConfigurationSection inventoriesSection = section.createSection("inventories");

        ConfigurationSection stocksInventorySection = inventoriesSection.createSection("stocks");
        ConfigurationSection earningsInventorySection = inventoriesSection.createSection("earnings");

        int stocksInventoryCount = this.getStocksInventoryList().size();
        for(int i = 0; i < stocksInventoryCount; i++) {
            stocksInventorySection.set("" + i, Inventories.serializeContent(stocksInventoryList.get(i)));
        }

        int earningsInventoryCount = this.getEarningsInventoryList().size();
        for(int i = 0; i < earningsInventoryCount; i++) {
            earningsInventorySection.set("" + i, Inventories.serializeContent(earningsInventoryList.get(i)));
        }
    }

}
