package xyz.heroesunited.heroesunited.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.client.render.HULayerRenderer;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerOpenAccesoireInv;
import xyz.heroesunited.heroesunited.common.networking.server.ServerToggleKey;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HUClientEventHandler {

    public static final KeyBinding ABILITIES_SCREEN = new KeyBinding(HeroesUnited.MODID+".key.abilities_screen", GLFW.GLFW_KEY_H, "key.categories."+ HeroesUnited.MODID);
    public static final KeyBinding ACCESSOIRES_SCREEN = new KeyBinding(HeroesUnited.MODID+".key.accessoires_screen", GLFW.GLFW_KEY_J, "key.categories."+ HeroesUnited.MODID);
    public static List<AbilityKeyBinding> ABILITY_KEYS = Lists.newArrayList();

    public HUClientEventHandler() {
        if (Minecraft.getInstance() != null) {
            ClientRegistry.registerKeyBinding(ABILITIES_SCREEN);
            ClientRegistry.registerKeyBinding(ACCESSOIRES_SCREEN);

            for (int i = 1; i <= 5; i++) {
                int key = i == 1 ? GLFW.GLFW_KEY_Z : i == 2 ? GLFW.GLFW_KEY_R : i == 3 ? GLFW.GLFW_KEY_G : i == 4 ? GLFW.GLFW_KEY_V : i == 5 ? GLFW.GLFW_KEY_B : -1;
                AbilityKeyBinding keyBinding = new AbilityKeyBinding(HeroesUnited.MODID+".key.ability_" + i, key, i);
                ClientRegistry.registerKeyBinding(keyBinding);
                ABILITY_KEYS.add(keyBinding);
            }
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().currentScreen != null) return;
        PlayerEntity player = Minecraft.getInstance().player;
        ABILITY_KEYS.forEach(key -> {
            if (event.getKey() == key.getKey().getKeyCode() && key.isKeyDown()) {
                HUNetworking.INSTANCE.sendToServer(new ServerToggleKey(key.index, event.getAction()));
            }
        });
        if (ABILITIES_SCREEN.isPressed()) {
            player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.NEUTRAL, 1, 0);
            Minecraft.getInstance().displayGuiScreen(new AbilitiesScreen());
        } else if (ACCESSOIRES_SCREEN.isPressed()) {
            HUNetworking.INSTANCE.sendToServer(new ServerOpenAccesoireInv());
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent e) {
        if (e.getGui() instanceof CustomizeSkinScreen) {
            e.addWidget(new Button(e.getGui().width / 2 - 100 + 25, e.getGui().height / 6 + 24 * (12 >> 1) - 28 + 4, 150, 20, new TranslationTextComponent("gui.heroesunited.changehead"), (button) -> HUConfig.CLIENT.renderHead.set(!HUConfig.CLIENT.renderHead.get())));
        }
    }

    private final ArrayList<LivingRenderer> entitiesWithLayer = new ArrayList<>();

    @SubscribeEvent
    public void renderEntityPre(RenderLivingEvent.Pre e) {
        if(entitiesWithLayer.contains(e.getRenderer())) return;
        e.getRenderer().addLayer(new HULayerRenderer(e.getRenderer()));
        entitiesWithLayer.add(e.getRenderer());
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        for (AbilityType type : AbilityHelper.getAbilities(event.getPlayer())) {
            type.create().renderPlayerPre(event);
        }
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isFlying() && !event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).renderFlying(event.getPlayer());
                if (renderFlying) {
                    event.getMatrixStack().push();
                    event.getMatrixStack().rotate(new Quaternion(0,-event.getPlayer().rotationYaw,0, true));
                    event.getMatrixStack().rotate(new Quaternion(event.getPlayer().rotationPitch,0,0, true));
                    event.getMatrixStack().rotate(new Quaternion(0,event.getPlayer().rotationYaw,0, true));
                }
            }
        });
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        for (AbilityType type : AbilityHelper.getAbilities(event.getPlayer())) {
            type.create().renderPlayerPost(event);
        }
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isFlying() && !event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).renderFlying(event.getPlayer());
                if (renderFlying) {
                    event.getMatrixStack().pop();
                }
            }
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity pl = event.player;
        if (event.phase == TickEvent.Phase.END) {
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                if (a.isFlying() && !pl.isOnGround() && pl.isSprinting()) {
                    pl.setPose(Pose.SWIMMING);
                }
            });
        }
    }

    @SubscribeEvent
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        PlayerEntity player = event.getPlayer();
        for (AbilityType type : AbilityHelper.getAbilities(player)) {
            type.create().setRotationAngles(event);
        }
        if (Suit.getSuit(player) != null) {
            Suit.getSuit(player).setRotationAngles(event);
        }

        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            if (a.isFlying() && !player.isOnGround() && !player.isSwimming() && player.isSprinting()) {
                PlayerModel model = event.getPlayerModel();
                boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).renderFlying(event.getPlayer());
                if (renderFlying) {
                    model.bipedRightArm.rotateAngleX = IFlyingAbility.getFlyingAbility(event.getPlayer()) != null && IFlyingAbility.getFlyingAbility(event.getPlayer()).rotateArms() ? (float) Math.toRadians(180F) : (float) Math.toRadians(0F);
                    model.bipedLeftArm.rotateAngleX = model.bipedRightArm.rotateAngleX;
                    model.bipedRightArm.rotateAngleY = model.bipedRightArm.rotateAngleZ =
                            model.bipedLeftArm.rotateAngleY = model.bipedLeftArm.rotateAngleZ =
                                    model.bipedRightLeg.rotateAngleX = model.bipedLeftLeg.rotateAngleX = (float) Math.toRadians(0F);
                }
                HUClientUtil.copyAnglesToWear(model);
            }
        });
    }

    public static class AbilityKeyBinding extends KeyBinding {

        public final int index;

        public AbilityKeyBinding(String description, int keyCode, int index) {
            super(description, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, keyCode, "key.categories."+ HeroesUnited.MODID);
            this.index = index;
        }
    }
}
