package mc.barrelshop.shops;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Barrel;
import org.bukkit.block.Sign;

public class BarrelAdminShop extends BarrelShop {

    public BarrelAdminShop(Sign sign) throws InvalidSignException, InvalidOwnerException {
        super(sign, null);
    }

    public BarrelAdminShop(Barrel barrel) throws InvalidSignException, InvalidOwnerException {
        super(barrel, null);
    }

    //

    @Override
    protected void initTrades() {
        for(int i = 0; i < 9; i++) this.trades().add(new AdminShopTrade());
    }


    //

    @Override
    public OfflinePlayer getOwner() { return null; }

    @Override
    public void setOwner(OfflinePlayer player) {}

    @Override
    public boolean isAdmin() { return true; }

    //

}
