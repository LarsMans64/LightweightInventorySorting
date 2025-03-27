package borknbeans.lightweightinventorysorting.mixin.client;

import borknbeans.lightweightinventorysorting.ContainerSortButton;
import borknbeans.lightweightinventorysorting.LightweightInventorySortingClient;
import borknbeans.lightweightinventorysorting.config.LightweightInventorySortingConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends HandledScreen<PlayerScreenHandler> {

    @Unique
    private ContainerSortButton inventorySortButton;


    public MixinInventoryScreen(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        int size = LightweightInventorySortingConfig.buttonSize.getButtonSize();

        inventorySortButton = new ContainerSortButton(0, 0, size, size, Text.literal("S"), 9, 35, this);

        setButtonCoordinates(size);

        // Add button to the screen
        this.addDrawableChild(inventorySortButton);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (inventorySortButton != null) {
            setButtonCoordinates(LightweightInventorySortingConfig.buttonSize.getButtonSize());

            inventorySortButton.render(context, mouseX, mouseY, delta);
        }
    }

    // This injection is failing now in 1.21.2
    /*
    @Inject(method = "keyPressed", at = @At("RETURN"))
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (LightweightInventorySortingClient.sortKeyBind.matchesKey(keyCode, scanCode)) {
            inventorySortButton.onClick(0f, 0f); // Simulate a click
        }
        return cir.getReturnValue();
    }
     */

    // This override is NOT an ideal solution as it could lead to conflicts with other mods
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (LightweightInventorySortingClient.sortKeyBind.matchesKey(keyCode, scanCode)) {
            inventorySortButton.onClick(0f, 0f); // Simulate a click
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void setButtonCoordinates(int size) {
        inventorySortButton.setX(this.x + this.backgroundWidth - 20 + LightweightInventorySortingConfig.xOffsetInventory + 12 - size);
        inventorySortButton.setY(this.height / 2 - 15 + LightweightInventorySortingConfig.yOffsetInventory + 12 - size);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (LightweightInventorySortingClient.sortKeyBind.matchesMouse(button)) {
            inventorySortButton.onClick(0f, 0f); // Simulate a click
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
