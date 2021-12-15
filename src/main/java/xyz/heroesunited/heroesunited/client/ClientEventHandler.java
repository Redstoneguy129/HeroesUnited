package xyz.heroesunited.heroesunited.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.*;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;
import xyz.heroesunited.heroesunited.client.renderer.space.CelestialBodyRenderer;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.events.RegisterPlayerControllerEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerKeyInput;
import xyz.heroesunited.heroesunited.common.networking.server.ServerOpenAccessoriesInv;
import xyz.heroesunited.heroesunited.common.networking.server.ServerToggleAbility;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.common.space.CelestialBodies;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    public static final KeyMapping ABILITIES_SCREEN = new KeyMapping(HeroesUnited.MODID + ".key.abilities_screen", GLFW.GLFW_KEY_H, "key.categories." + HeroesUnited.MODID);
    public static final KeyMapping ACCESSORIES_SCREEN = new KeyMapping(HeroesUnited.MODID + ".key.accessories_screen", GLFW.GLFW_KEY_J, "key.categories." + HeroesUnited.MODID);
    public static final List<AbilityKeyBinding> ABILITY_KEYS = new ArrayList<>();
    public static final KeyMap KEY_MAP = new KeyMap();

    public ClientEventHandler() {
        if (Minecraft.getInstance() != null) {
            ClientRegistry.registerKeyBinding(ABILITIES_SCREEN);
            ClientRegistry.registerKeyBinding(ACCESSORIES_SCREEN);

            for (int i = 1; i <= 5; i++) {
                int key = i == 1 ? GLFW.GLFW_KEY_Z : i == 2 ? GLFW.GLFW_KEY_R : i == 3 ? GLFW.GLFW_KEY_G : i == 4 ? GLFW.GLFW_KEY_V : GLFW.GLFW_KEY_B;
                AbilityKeyBinding keyBinding = new AbilityKeyBinding(HeroesUnited.MODID + ".key.ability_" + i, key, i);
                ClientRegistry.registerKeyBinding(keyBinding);
                ABILITY_KEYS.add(keyBinding);
            }
        }
    }

    @SubscribeEvent
    public void mouseScroll(InputEvent.MouseScrollEvent e) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT) ||
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT)) {
            if (e.getScrollDelta() > 0) {
                AbilityOverlay.INDEX--;
            } else {
                AbilityOverlay.INDEX++;
            }
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (e.getModifiers() == GLFW.GLFW_MOD_ALT) {
            List<Ability> abilities = AbilityOverlay.getCurrentDisplayedAbilities(mc.player);
            for (int i = 0; i < abilities.size(); i++) {
                int key = GLFW.GLFW_KEY_1 + i;
                if (key == e.getKey() && e.getAction() == GLFW.GLFW_PRESS) {
                    Ability ability = abilities.get(i);
                    if (!ability.alwaysActive(mc.player)) {
                        HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerToggleAbility(ability.name));
                        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    mc.options.keyHotbarSlots[i].release();
                }
            }
        }

        if (ABILITIES_SCREEN.consumeClick()) {
            if (!HUPackSuperpowers.hasSuperpowers(mc.player) || !GsonHelper.getAsBoolean(HUPackSuperpowers.getSuperpowersJSONS().get(HUPackSuperpowers.getSuperpower(mc.player)), "block_screen", false)) {
                mc.player.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.NEUTRAL, 1, 0);
                mc.setScreen(new AbilitiesScreen());
            }
        }
        if (ACCESSORIES_SCREEN.consumeClick()) {
            HUNetworking.INSTANCE.sendToServer(new ServerOpenAccessoriesInv(mc.player.getId()));
        }

        for (AbilityKeyBinding key : ABILITY_KEYS) {
            sendToggleKey(key, key.index);
        }
        sendToggleKey(mc.options.keyJump, 7);
    }

    @SubscribeEvent
    public void huRender(RendererChangeEvent event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.getClientProperties().rendererChange(event));
    }

    @SubscribeEvent
    public void onWorldLastRender(RenderLevelLastEvent event) {
        if (Minecraft.getInstance().level.dimension().equals(HeroesUnited.SPACE)) {
            PoseStack matrixStack = event.getPoseStack();
            matrixStack.pushPose();

            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

            Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStack.translate(-view.x(), -view.y(), -view.z());

            for (CelestialBody celestialBody : CelestialBodies.REGISTRY.get().getValues()) {
                matrixStack.pushPose();
                matrixStack.translate(celestialBody.getCoordinates().x, celestialBody.getCoordinates().y, celestialBody.getCoordinates().z);
                matrixStack.mulPose(new Quaternion(0, 0, 180, true));
                CelestialBodyRenderer celestialBodyRenderer = CelestialBodyRenderer.getRenderer(celestialBody);
                celestialBodyRenderer.render(matrixStack, buffers, LevelRenderer.getLightColor(Minecraft.getInstance().level, new BlockPos(celestialBody.getCoordinates())), event.getPartialTick());

                VertexConsumer buffer = buffers.getBuffer(RenderType.LINES);
                matrixStack.popPose();
                if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
                    LevelRenderer.renderLineBox(matrixStack, buffer, celestialBody.getBoundingBox(), 1, 1, 1, 1);
            }

            matrixStack.popPose();
            RenderSystem.disableDepthTest();
            buffers.endBatch();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.MouseInputEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        sendToggleKey(mc.options.keyAttack, 8);
        sendToggleKey(mc.options.keyUse, 9);
        sendToggleKey(mc.options.keyPickItem, 10);
    }

    public static void sendToggleKey(KeyMapping keyBind, int index) {
        if (KEY_MAP.get(index) != keyBind.isDown()) {
            KEY_MAP.put(index, keyBind.isDown());
            HUNetworking.INSTANCE.sendToServer(new ServerKeyInput(KEY_MAP));
            Minecraft.getInstance().player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.onKeyInput(KEY_MAP));
        }
    }

    /*@SubscribeEvent
    public void updateFov(FOVUpdateEvent event) {
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.setNewfov(-200);
        }
    }*/

    @SubscribeEvent
    public void renderEntityPre(RenderLivingEvent.Pre event) {
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.getPoseStack().pushPose();
            if (event.getEntity() instanceof Player && event.getEntity().isCrouching()) {
                event.getPoseStack().translate(0, 0.125D, 0);
            }
            event.getPoseStack().scale(0.01F, 0.01F, 0.01F);
        }
    }

    @SubscribeEvent
    public void renderEntityPost(RenderLivingEvent.Post event) {
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public void renderShadowSize(ChangeShadowSizeEvent event) {
        for (SizeChangeAbility a : AbilityHelper.getListOfType(AbilityHelper.getAbilities(event.getEntity()), SizeChangeAbility.class)) {
            if (a.changeSizeInRender()) {
                event.setNewSize(event.getDefaultSize() * a.getSize());
            }
        }
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent event) {
        for (SizeChangeAbility a : AbilityHelper.getListOfType(AbilityHelper.getAbilities(event.getEntity()), SizeChangeAbility.class)) {
            if (a.changeSizeInRender()) {
                if (event instanceof RenderPlayerEvent.Pre) {
                    event.getPoseStack().pushPose();
                    float size = a.getRenderSize(event.getPartialTick());
                    event.getPoseStack().scale(size, size, size);
                }
                if (event instanceof RenderPlayerEvent.Post) {
                    event.getPoseStack().popPose();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderHULayer(RenderLayerEvent.Accessories<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> event) {
        for (HideLayerAbility ability : AbilityHelper.getListOfType(AbilityHelper.getAbilities(event.getLivingEntity()), HideLayerAbility.class)) {
            if (ability.layerNameIs("accessories")) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void hideLayers(HideLayerEvent event) {
        if (event.getEntity() instanceof Player) {
            for (HideLayerAbility a : AbilityHelper.getListOfType(AbilityHelper.getAbilities(event.getEntity()), HideLayerAbility.class)) {
                if (a.layerNameIs("armor")) {
                    event.blockLayers(HumanoidArmorLayer.class, ElytraLayer.class);
                }
                if (a.layerNameIs("head")) {
                    event.blockLayer(CustomHeadLayer.class);
                }
                if (a.layerNameIs("held_item")) {
                    event.blockLayer(ItemInHandLayer.class);
                }
                if (a.layerNameIs("heroesunited")) {
                    event.blockLayer(HULayerRenderer.class);
                }
                if (a.layerNameIs("arrow")) {
                    event.blockLayer(ArrowLayer.class);
                }
                if (a.layerNameIs("player")) {
                    event.blockLayers(HumanoidArmorLayer.class, ItemInHandLayer.class, Deadmau5EarsLayer.class, CapeLayer.class, CustomHeadLayer.class, ElytraLayer.class, ParrotOnShoulderLayer.class);
                }
            }
        }
    }

    @SubscribeEvent
    public void setDiscordPresence(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof Player) || Minecraft.getInstance().player == null || Minecraft.getInstance().player.getUUID() != event.getEntity().getUUID())
            return;
        HURichPresence.getPresence().setDiscordRichPresence("Playing Heroes United", null, HURichPresence.MiniLogos.NONE, null);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        AbilityHelper.getAbilities(player).forEach(ability -> ability.getClientProperties().renderPlayerPre(event));
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            IFlyingAbility ability = IFlyingAbility.getFlyingAbility(player);
            if (ability != null && ability.isFlying(player) && ability.renderFlying(player)) {
                if (!player.isOnGround() && !player.isSwimming()) {
                    if (!(player.getFallFlyingTicks() > 4) && !player.isVisuallySwimming()) {
                        float defaultRotation = Mth.clamp(Mth.sqrt((float) player.distanceToSqr(player.xo, player.yo, player.zo)), 0.0F, 1.0F) * ability.getDegreesForWalk(player);

                        event.getPoseStack().pushPose();
                        event.getPoseStack().mulPose(Vector3f.YP.rotationDegrees(-player.getYRot()));
                        event.getPoseStack().mulPose(Vector3f.XP.rotationDegrees(Mth.clamp(defaultRotation + (cap.getFlightAmount(event.getPartialTick()) * ability.getDegreesForSprint(player)), 0, ability.getDegreesForSprint(player))));
                        event.getPoseStack().mulPose(Vector3f.YP.rotationDegrees(player.getYRot()));
                    }
                }
            }
        });
        if (GlidingAbility.getInstance(event.getPlayer()) != null && GlidingAbility.getInstance(event.getPlayer()).canGliding(event.getPlayer())) {
            event.getPoseStack().pushPose();
            event.getPoseStack().mulPose(new Quaternion(0, -event.getPlayer().getYRot(), 0, true));
            event.getPoseStack().mulPose(new Quaternion(90F + event.getPlayer().getXRot(), 0, 0, true));
            event.getPoseStack().mulPose(new Quaternion(0, event.getPlayer().getYRot(), 0, true));
        }
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().renderPlayerPreAlways(event));
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.getClientProperties().renderPlayerPost(event));
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().renderPlayerPostAlways(event));
        IFlyingAbility ability = IFlyingAbility.getFlyingAbility(event.getPlayer());
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (ability != null && ability.isFlying(event.getPlayer()) && ability.renderFlying(event.getPlayer())) {
                if (!event.getPlayer().isOnGround() && !event.getPlayer().isSwimming()) {
                    if (!(event.getPlayer().getFallFlyingTicks() > 4) && !event.getPlayer().isVisuallySwimming()) {
                        event.getPoseStack().popPose();
                    }
                }
            }
        });
        if (GlidingAbility.getInstance(event.getPlayer()) != null && GlidingAbility.getInstance(event.getPlayer()).canGliding(event.getPlayer())) {
            event.getPoseStack().popPose();
        }
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
    public void registerControllers(RegisterPlayerControllerEvent event) {
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().registerPlayerControllers(event));
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void setupAnim(SetupAnimEvent event) {
        Player player = event.getPlayer();
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.getClientProperties().setupAnim(event));
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            SuitItem suitItem = Suit.getSuitItem(equipmentSlot, player);
            if (suitItem != null) {
                suitItem.getSuit().setupAnim(event, equipmentSlot);
            }
        }

        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().setAlwaysRotationAngles(event));
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            for (AnimationController<?> controller : cap.getFactory().getOrCreateAnimationData(player.getUUID().hashCode()).getAnimationControllers().values()) {
                if (controller.getCurrentAnimation() != null && controller.getAnimationState() == AnimationState.Running) {
                    for (String s : Arrays.asList("bipedHead", "bipedBody", "bipedRightArm", "bipedLeftArm", "bipedRightLeg", "bipedLeftLeg")) {
                        GeoBone bone = cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap)).getBone(s).get();
                        ModelPart renderer = HUClientUtil.getModelRendererById(event.getPlayerModel(), s);
                        for (BoneAnimation boneAnimation : controller.getCurrentAnimation().boneAnimations) {
                            if (boneAnimation.boneName.equals(s)) {
                                renderer.xRot = -bone.getRotationX();
                                renderer.yRot = -bone.getRotationY();
                                renderer.zRot = bone.getRotationZ();

                                if (bone.getPositionX() != 0) {
                                    renderer.x = -(bone.getPivotX() + bone.getPositionX());
                                }
                                if (bone.getPositionY() != 0) {
                                    renderer.y = (24 - bone.getPivotY()) - bone.getPositionY();
                                }
                                if (bone.getPositionZ() != 0) {
                                    renderer.z = bone.getPivotZ() + bone.getPositionZ();
                                }

                                if (bone.name.endsWith("Leg")) {
                                    renderer.y = renderer.y - bone.getScaleY() * 2;
                                }
                                ((IHUModelPart) (Object) renderer).setSize(new CubeDeformation(bone.getScaleX() - 1.0F, bone.getScaleY() - 1.0F, bone.getScaleZ() - 1.0F));
                            }
                        }
                    }
                }
            }

            for (int slot = 0; slot <= 8; ++slot) {
                ItemStack stack = cap.getInventory().getItem(slot);
                if (stack != null && stack.getItem() instanceof IAccessory accessory) {
                    if (accessory.getHiddenParts(false) != null) {
                        for (PlayerPart part : accessory.getHiddenParts(false)) {
                            part.setVisibility(event.getPlayerModel(), false);
                        }
                    }
                }
            }

            IFlyingAbility ability = IFlyingAbility.getFlyingAbility(event.getPlayer());
            if (ability != null && ability.isFlying(event.getPlayer()) && ability.renderFlying(event.getPlayer()) && ability.setDefaultRotationAngles(event)) {
                if (!event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                    PlayerModel<?> model = event.getPlayerModel();
                    float flightAmount = cap.getFlightAmount(event.getPartialTicks());
                    float armRotations = ability.rotateArms(event.getPlayer()) ? (float) Math.toRadians(180F) : 0F;

                    model.head.xRot = model.rotlerpRad(flightAmount, model.head.xRot, (-(float) Math.PI / 4F));
                    model.leftArm.xRot = model.rotlerpRad(flightAmount, model.leftArm.xRot, armRotations);
                    model.rightArm.xRot = model.rotlerpRad(flightAmount, model.rightArm.xRot, armRotations);

                    model.leftArm.yRot = model.rotlerpRad(flightAmount, model.leftArm.yRot, 0);
                    model.rightArm.yRot = model.rotlerpRad(flightAmount, model.rightArm.yRot, 0);

                    model.leftArm.zRot = model.rotlerpRad(flightAmount, model.leftArm.zRot, 0);
                    model.rightArm.zRot = model.rotlerpRad(flightAmount, model.rightArm.zRot, 0);

                    model.leftLeg.xRot = model.rotlerpRad(flightAmount, model.leftLeg.xRot, 0);
                    model.rightLeg.xRot = model.rotlerpRad(flightAmount, model.rightLeg.xRot, 0);
                }
            }
        });
    }

    @SubscribeEvent
    public void onInputUpdate(MovementInputUpdateEvent event) {
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().inputUpdate(event));
    }

    @SubscribeEvent
    public void renderPlayerHandPost(RenderPlayerHandEvent.Post event) {
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().renderAlwaysFirstPersonArm(Minecraft.getInstance().getEntityModels(), event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer(), event.getSide()));
    }

    @SubscribeEvent
    public void renderPlayerLayers(RenderLayerEvent.Armor.Post event) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            SuitItem suitItem = Suit.getSuitItem(slot, event.getLivingEntity());
            if (suitItem != null && event.getRenderer().getModel() instanceof HumanoidModel) {
                HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(suitItem.getSuit().getRegistryName());
                if (layer != null) {
                    if (layer.getTexture("cape") != null && slot.equals(EquipmentSlot.CHEST)) {
                        HUClientUtil.renderCape(event.getRenderer(), event.getLivingEntity(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTicks(), layer.getTexture("cape"));
                    }
                    if (layer.getTexture("lights") != null) {
                        ForgeHooksClient.getArmorModel(event.getLivingEntity(), event.getLivingEntity().getItemBySlot(slot), slot, (HumanoidModel) event.getRenderer().getModel()).renderToBuffer(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.getLight(layer.getTexture("lights"))), event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void renderPlayerLayers(RenderLayerEvent.Player event) {
        AbilityHelper.getAbilityMap(event.getPlayer()).values().forEach(ability -> ability.getClientProperties().renderAlways(event.getContext(), event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getPartialTicks(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch()));
    }

    @SubscribeEvent
    public void renderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().player == null) return;
        AbstractClientPlayer player = Minecraft.getInstance().player;
        boolean canceled = false;
        for (Ability a : AbilityHelper.getAbilities(player)) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                double distance = Minecraft.getInstance().hitResult.getLocation().distanceTo(player.position().add(0, player.getEyeHeight(), 0));
                AABB box = new AABB(0.1F, -0.25, 0, 0, -0.25, -distance).inflate(0.03125D);
                Color color = HUJsonUtils.getColor(a.getJsonObject());
                if (a instanceof EnergyLaserAbility && a.getEnabled()) {
                    event.getPoseStack().pushPose();
                    event.getPoseStack().translate(((EnergyLaserAbility) a).isLeftArm(player) ? -0.3F : 0.3F, 0, 0);
                    HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, 1, event.getPackedLight());
                    HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (color.getAlpha() / 255F) * 0.5F, event.getPackedLight());
                    canceled = true;
                    event.getPoseStack().popPose();
                }
                if (a instanceof HeatVisionAbility) {
                    float alpha = (a.getDataManager().<Integer>getValue("prev_timer") + (a.getDataManager().<Integer>getValue("timer") - a.getDataManager().<Integer>getValue("prev_timer")) * event.getPartialTicks()) / GsonHelper.getAsInt(a.getJsonObject(), "maxTimer", 10);
                    if (a.getDataManager().<String>getValue("type").equals("cyclop")) {
                        AABB box1 = new AABB(-0.15F, -0.11F, 0, 0.15F, -0.11F, -distance).inflate(0.0625D);
                        HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box1.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, event.getPackedLight());
                        HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box1, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, event.getPackedLight());
                        if (a.getEnabled()) {
                            event.setCanceled(true);
                        }
                        return;
                    }
                    if (a.getDataManager().<String>getValue("type").equals("default")) {
                        for (int i = 0; i < 2; i++) {
                            event.getPoseStack().pushPose();
                            event.getPoseStack().translate(i == 0 ? 0.2F : -0.3F, 0.25, 0);
                            HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, alpha, event.getPackedLight());
                            HUClientUtil.renderFilledBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha * 0.5F, event.getPackedLight());
                            if (a.getEnabled()) {
                                event.setCanceled(true);
                            }
                            event.getPoseStack().popPose();
                        }
                        return;
                    }
                }
            }
        }

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(equipmentSlot);
            if (stack.getItem() instanceof SuitItem suitItem) {
                if (suitItem.getSlot().equals(equipmentSlot)) {
                    if (suitItem.renderWithoutArm()) {
                        suitItem.renderFirstPersonArm(Minecraft.getInstance().getEntityModels(), null, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), player, player.getMainArm(), stack);
                    }
                }
            }
        }
        event.setCanceled(canceled);
    }

    public static class AbilityKeyBinding extends KeyMapping {

        public final int index;

        public AbilityKeyBinding(String description, int keyCode, int index) {
            super(description, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, keyCode, "key.categories." + HeroesUnited.MODID);
            this.index = index;
        }
    }
}
