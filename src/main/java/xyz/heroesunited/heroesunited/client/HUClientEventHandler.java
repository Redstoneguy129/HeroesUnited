package xyz.heroesunited.heroesunited.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.client.render.HULayerRenderer;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerOpenAccessoriesInv;
import xyz.heroesunited.heroesunited.common.networking.server.ServerToggleKey;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HURichPresence;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HUClientEventHandler {

    public static final KeyBinding ABILITIES_SCREEN = new KeyBinding(HeroesUnited.MODID + ".key.abilities_screen", GLFW.GLFW_KEY_H, "key.categories." + HeroesUnited.MODID);
    public static final KeyBinding ACCESSORIES_SCREEN = new KeyBinding(HeroesUnited.MODID + ".key.accessories_screen", GLFW.GLFW_KEY_J, "key.categories." + HeroesUnited.MODID);
    private final List<String> playerBones = Arrays.asList("bipedHead", "bipedBody", "bipedRightArm", "bipedLeftArm", "bipedRightLeg", "bipedLeftLeg");
    private final ArrayList<LivingRenderer> entitiesWithLayer = Lists.newArrayList();
    public static List<AbilityKeyBinding> ABILITY_KEYS = Lists.newArrayList();
    public static Map<Integer, Boolean> KEY_STATE = Maps.newHashMap();

    public HUClientEventHandler() {
        if (Minecraft.getInstance() != null) {
            ClientRegistry.registerKeyBinding(ABILITIES_SCREEN);
            ClientRegistry.registerKeyBinding(ACCESSORIES_SCREEN);

            for (int i = 1; i <= 5; i++) {
                int key = i == 1 ? GLFW.GLFW_KEY_Z : i == 2 ? GLFW.GLFW_KEY_R : i == 3 ? GLFW.GLFW_KEY_G : i == 4 ? GLFW.GLFW_KEY_V : i == 5 ? GLFW.GLFW_KEY_B : -1;
                AbilityKeyBinding keyBinding = new AbilityKeyBinding(HeroesUnited.MODID + ".key.ability_" + i, key, i);
                ClientRegistry.registerKeyBinding(keyBinding);
                ABILITY_KEYS.add(keyBinding);
            }
        }
    }

    @SubscribeEvent
    public void renderEntityPre(RenderLivingEvent.Pre e) {
        if (entitiesWithLayer.contains(e.getRenderer())) return;
        e.getRenderer().addLayer(new HULayerRenderer(e.getRenderer()));
        entitiesWithLayer.add(e.getRenderer());
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent event) {
        for (Ability a : AbilityHelper.getAbilities(event.getPlayer())) {
            if (a instanceof SizeChangeAbility && ((SizeChangeAbility) a).changeSizeInRender()) {
                if (event instanceof RenderPlayerEvent.Pre) {
                    event.getMatrixStack().pushPose();
                    float size = ((SizeChangeAbility) a).getSize();
                    event.getMatrixStack().scale(size, size, size);
                    event.getRenderer().shadowRadius = 0.5F * size;
                }
                if (event instanceof RenderPlayerEvent.Post) {
                    event.getMatrixStack().popPose();
                }
            } else {
                if (event.getRenderer().shadowRadius != 0.5F) {
                    event.getRenderer().shadowRadius = 0.5F;
                }
            }
            if (AbilityHelper.getAbilities(event.getPlayer()).isEmpty()) {
                event.getRenderer().getDispatcher().setRenderShadow(true);
            }
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc == null || mc.screen != null) return;
        if (ABILITIES_SCREEN.consumeClick()) {
            mc.player.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundCategory.NEUTRAL, 1, 0);
            mc.setScreen(new AbilitiesScreen());
        } else if (ACCESSORIES_SCREEN.consumeClick()) {
            HUNetworking.INSTANCE.sendToServer(new ServerOpenAccessoriesInv());
        }

        for (AbilityKeyBinding key : ABILITY_KEYS) {
            sendToggleKey(e.getKey(), e.getAction(), key, key.index);
        }
        sendToggleKey(e.getKey(), e.getAction(), mc.options.keyJump, 7);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.RawMouseEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc == null || mc.screen != null) return;
        sendToggleKey(e.getButton(), e.getAction(), mc.options.keyAttack, 8);
        sendToggleKey(e.getButton(), e.getAction(), mc.options.keyUse, 9);
    }

    public static void sendToggleKey(int key, int action, KeyBinding keyBind, int index) {
        if (action < GLFW.GLFW_REPEAT && key == keyBind.getKey().getValue()) {
            if (!KEY_STATE.containsKey(key)) KEY_STATE.put(key, false);
            if (KEY_STATE.get(key) != (action == GLFW.GLFW_PRESS)) {
                HUNetworking.INSTANCE.sendToServer(new ServerToggleKey(index, action == GLFW.GLFW_PRESS));
                Minecraft.getInstance().player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.toggle(index, action == GLFW.GLFW_PRESS));
            }
            KEY_STATE.put(key, action == GLFW.GLFW_PRESS);
        }
    }

    @SubscribeEvent
    public void onRenderAccessories(HURenderLayerEvent.Accessories event) {
        hideAllBodyParts(event, event.getLivingEntity());
    }

    @SubscribeEvent
    public void onRenderHULayer(HURenderLayerEvent.Pre event) {
        hideAllBodyParts(event, event.getLivingEntity());
    }

    @SubscribeEvent
    public void onArmorLayer(HURenderLayerEvent.Armor.Pre event) {
        hideAllBodyParts(event, event.getLivingEntity());
    }

    public void hideAllBodyParts(Event event, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            for (Ability ability : AbilityHelper.getAbilities(entity)) {
                if (ability instanceof HideBodyPartsAbility && ability.getJsonObject().has("visibility_parts")) {
                    for (Map.Entry<String, JsonElement> yep : JSONUtils.getAsJsonObject(ability.getJsonObject(), "visibility_parts").entrySet()) {
                        if (yep.getKey().equals("all")) {
                            event.setCanceled(!JSONUtils.convertToBoolean(yep.getValue(), yep.getKey()));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void setDiscordPresence(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) || Minecraft.getInstance().player == null || Minecraft.getInstance().player.getUUID() != event.getEntity().getUUID())
            return;
        HURichPresence.getPresence().setDiscordRichPresence("Playing Heroes United", null, HURichPresence.MiniLogos.NONE, null);
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent e) {
        if (e.getGui() instanceof CustomizeSkinScreen) {
            e.addWidget(new Button(e.getGui().width / 2 - 100 + 25, e.getGui().height / 6 + 24 * (12 >> 1) - 28 + 4, 150, 20, new TranslationTextComponent("gui.heroesunited.changehead"), (button) -> HUConfig.CLIENT.renderHead.set(!HUConfig.CLIENT.renderHead.get())));
        }
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.renderPlayerPre(event));
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            AnimationEvent animationEvent = new AnimationEvent(cap, 0.0F, 0.0F, 0.0F, false, Arrays.asList(event.getPlayer().getUUID()));
            animationEvent.setController(cap.getController());
            cap.getAnimatedModel().setLivingAnimations(cap, event.getPlayer().getUUID().hashCode(), animationEvent);
        });

        if (HUPlayer.getCap(event.getPlayer()).isFlying() && !event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
            boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).renderFlying(event.getPlayer());
            if (renderFlying && !(event.getPlayer().getFallFlyingTicks() > 4) && !event.getPlayer().isVisuallySwimming()) {
                event.getMatrixStack().pushPose();
                event.getMatrixStack().mulPose(new Quaternion(0, -event.getPlayer().yRot, 0, true));
                event.getMatrixStack().mulPose(new Quaternion(90F + event.getPlayer().xRot, 0, 0, true));
                event.getMatrixStack().mulPose(new Quaternion(0, event.getPlayer().yRot, 0, true));
            }
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.renderPlayerPost(event));
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isFlying() && !event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).renderFlying(event.getPlayer());
                if (renderFlying && !(event.getPlayer().getFallFlyingTicks() > 4) && !event.getPlayer().isVisuallySwimming()) {
                    event.getMatrixStack().popPose();
                }
            }
        });
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isIntangible()) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        PlayerEntity player = event.getPlayer();
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.setRotationAngles(event));
        for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
            if (Suit.getSuitItem(equipmentSlot, player) != null) {
                Suit.getSuitItem(equipmentSlot, player).getSuit().setRotationAngles(event, equipmentSlot);
            }
        }

        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            for (Ability ability : cap.getAbilities().values()) {
                if (ability instanceof IAbilityAlwaysRenderer) {
                    ((IAbilityAlwaysRenderer) ability).setAlwaysRotationAngles(event);
                }
            }

            for (String s : playerBones) {
                GeoBone bone = cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap)).getBone(s).get();
                ModelRenderer renderer = HUClientUtil.getModelRendererById(event.getPlayerModel(), s);
                if (cap.getController().getCurrentAnimation() != null && cap.getController().getAnimationState() == AnimationState.Running) {
                    for (BoneAnimation boneAnimation : cap.getController().getCurrentAnimation().boneAnimations) {
                        if (boneAnimation.boneName.equals(s)) {
                            renderer.xRot = -bone.getRotationX();
                            renderer.yRot = -bone.getRotationY();
                            renderer.zRot = bone.getRotationZ();
                        }
                    }
                    HUClientUtil.copyAnglesToWear(event.getPlayerModel());
                }
            }

            for (int slot = 0; slot <= 8; ++slot) {
                ItemStack stack = cap.getInventory().getItem(slot);
                if (stack != null && stack.getItem() instanceof IAccessory) {
                    IAccessory accessory = ((IAccessory) stack.getItem());
                    if (accessory.getHiddenParts() != null) {
                        for (PlayerPart part : accessory.getHiddenParts()) {
                            part.setVisibility(event.getPlayerModel(), false);
                        }
                    }
                }

            }

            if (cap.isFlying() && !player.isOnGround() && !player.isSwimming() && player.isSprinting()) {
                PlayerModel model = event.getPlayerModel();
                boolean renderFlying = IFlyingAbility.getFlyingAbility(event.getPlayer()) == null || IFlyingAbility.getFlyingAbility(event.getPlayer()).setDefaultRotationAngles(event.getPlayer());
                if (renderFlying) {
                    model.head.xRot = (-(float)Math.PI / 4F);
                    model.rightArm.xRot = IFlyingAbility.getFlyingAbility(event.getPlayer()) != null && IFlyingAbility.getFlyingAbility(event.getPlayer()).rotateArms(event.getPlayer()) ? (float) Math.toRadians(180F) : (float) Math.toRadians(0F);
                    model.leftArm.xRot = model.rightArm.xRot;
                    model.rightArm.yRot = model.rightArm.zRot =
                            model.leftArm.yRot = model.leftArm.zRot =
                                    model.rightLeg.xRot = model.leftLeg.xRot = (float) Math.toRadians(0F);
                }
                HUClientUtil.copyAnglesToWear(model);
            }
        });
    }

    @SubscribeEvent
    public void renderPlayerHand(HURenderPlayerHandEvent event) {
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            for (Ability ability : a.getAbilities().values()) {
                if (ability instanceof IAbilityAlwaysRenderer) {
                    ((IAbilityAlwaysRenderer) ability).renderAlwaysFirstPersonArm(event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getPlayer(), event.getSide());
                }
            }
        });
    }

    @SubscribeEvent
    public void renderPlayerLayers(HURenderLayerEvent.Player event) {
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            for (Ability ability : a.getAbilities().values()) {
                if (ability instanceof IAbilityAlwaysRenderer) {
                    ((IAbilityAlwaysRenderer) ability).renderAlways(event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getPartialTicks(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch());
                }
            }
        });
    }

    public static class AbilityKeyBinding extends KeyBinding {

        public final int index;

        public AbilityKeyBinding(String description, int keyCode, int index) {
            super(description, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, keyCode, "key.categories." + HeroesUnited.MODID);
            this.index = index;
        }
    }
}
