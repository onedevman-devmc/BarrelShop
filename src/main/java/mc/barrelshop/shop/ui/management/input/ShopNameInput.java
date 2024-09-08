package mc.barrelshop.shop.ui.management.input;

import mc.barrelshop.shop.BarrelShop;
import mc.compendium.chestinterface.ChestInterfaceApi;
import mc.compendium.chestinterface.components.AnvilInput;
import mc.compendium.chestinterface.components.configurations.AnvilInputConfig;
import mc.compendium.chestinterface.events.AnvilInputSubmitEvent;
import mc.compendium.chestinterface.events.AnvilInputWritingEvent;
import mc.compendium.events.EventHandler;

import java.util.List;

public class ShopNameInput extends AnvilInput {

    @EventHandler
    public void onWriting(AnvilInputWritingEvent event) {
        event.setResultText(event.getPartialText().replaceAll("&", "§"));
    }

    @EventHandler
    public void onSubmit(AnvilInputSubmitEvent event) {
        String rawName = event.getText();
        String formattedName = rawName.replaceAll("&", "§");
        this.shop.setName(formattedName);
    }

    //

    private final BarrelShop shop;

    //

    public ShopNameInput(BarrelShop shop) {
        super(
            ChestInterfaceApi.getInstance().getPlugin(),
            new AnvilInputConfig(
                "Shop Name",
                List.of("By renaming this item, you can change the name of the shop."),
                shop.getName(),
                false
            )
        );

        this.shop = shop;
    }

    //

    public BarrelShop getShop() { return this.shop; }

}
