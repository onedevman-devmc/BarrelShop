package mc.barrelshop.shops.ui.trading;

import mc.compendium.chestinterface.components.TradeInterface;
import mc.compendium.chestinterface.components.configurations.TradeInterfaceConfig;
import mc.compendium.chestinterface.events.InterfaceEventListener;
import mc.compendium.chestinterface.events.TradeInterfaceCloseEvent;
import mc.compendium.events.EventHandler;
import mc.barrelshop.shops.AbstractShopTrade;
import mc.barrelshop.shops.BarrelShop;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;

public class ShopTradeInterface extends TradeInterface<AbstractShopTrade> implements InterfaceEventListener {

    @EventHandler
    private void onClose(TradeInterfaceCloseEvent event) {
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 1, 1);
    }

    //

    private final BarrelShop shop;

    //

    public ShopTradeInterface(BarrelShop shop) {
        super(new TradeInterfaceConfig(shop.getName(), false));

        //

        this.shop = shop;

        //

        this.addListener(this);
    }

    //

    public BarrelShop getShop() { return this.shop; }

    //

    @Override
    public Merchant toBukkit() {
        this.config().setName(this.shop.getName());

        //

        return super.toBukkit();
    }
}
