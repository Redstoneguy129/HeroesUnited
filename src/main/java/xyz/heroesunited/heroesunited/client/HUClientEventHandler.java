package xyz.heroesunited.heroesunited.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.gui.screen.CustomizeSkinScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.*;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.client.render.HULayerRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.space.CelestialBodyRenderer;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerKeyInput;
import xyz.heroesunited.heroesunited.common.networking.server.ServerOpenAccessoriesInv;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.mixin.client.InvokerKeyBinding;
import xyz.heroesunited.heroesunited.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HUClientEventHandler {

    public static final KeyBinding ABILITIES_SCREEN = new KeyBinding(HeroesUnited.MODID + ".key.abilities_screen", GLFW.GLFW_KEY_H, "key.categories." + HeroesUnited.MODID);
    public static final KeyBinding ACCESSORIES_SCREEN = new KeyBinding(HeroesUnited.MODID + ".key.accessories_screen", GLFW.GLFW_KEY_J, "key.categories." + HeroesUnited.MODID);
    private final List<String> playerBones = Arrays.asList("bipedHead", "bipedBody", "bipedRightArm", "bipedLeftArm", "bipedRightLeg", "bipedLeftLeg");
    private final ArrayList<LivingRenderer> entitiesWithLayer = Lists.newArrayList();
    public static List<AbilityKeyBinding> ABILITY_KEYS = Lists.newArrayList();
    public static KeyMap KEY_MAP = new KeyMap();

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
    public void mouseScroll(InputEvent.MouseScrollEvent e) {
        if (InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT) ||
                InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT)) {
            if (e.getScrollDelta() > 0) {
                AbilitiesScreen.INDEX--;
            } else {
                AbilitiesScreen.INDEX++;
            }
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (e.getModifiers() == GLFW.GLFW_MOD_ALT) {
            List<Ability> abilities = AbilitiesScreen.getCurrentDisplayedAbilities(mc.player);
            for (int i = 0; i < abilities.size(); i++) {
                int key = GLFW.GLFW_KEY_1 + i;
                if (key == e.getKey() && e.getAction() == GLFW.GLFW_PRESS) {
                    Ability ability = abilities.get(i);
                    if (!ability.alwaysActive(mc.player)) {
                        if (AbilityHelper.getEnabled(ability.name, mc.player)) {
                            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerDisableAbility(ability.name));
                        } else {
                            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerEnableAbility(ability.name, ability.serializeNBT()));
                        }
                        mc.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    ((InvokerKeyBinding) mc.options.keyHotbarSlots[i]).releaseKey();
                }
            }
        }

        if (ABILITIES_SCREEN.consumeClick()) {
            if (!HUPackSuperpowers.hasSuperpowers(mc.player) || !JSONUtils.getAsBoolean(HUPackSuperpowers.getSuperpowersJSONS().get(HUPackSuperpowers.getSuperpower(mc.player)), "block_screen", false)) {
                mc.player.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundCategory.NEUTRAL, 1, 0);
                mc.setScreen(new AbilitiesScreen());
            }
        }
        if (ACCESSORIES_SCREEN.consumeClick()) {
            HUNetworking.INSTANCE.sendToServer(new ServerOpenAccessoriesInv());
        }

        for (AbilityKeyBinding key : ABILITY_KEYS) {
            sendToggleKey(key, key.index);
        }
        sendToggleKey(mc.options.keyJump, 7);
    }

    @SubscribeEvent
    public void huRender(HUChangeRendererEvent event) {
        for (Ability ability : AbilityHelper.getAbilities(event.getPlayer())) {
            ability.huRenderPlayer(event);
        }
    }

    @SubscribeEvent
    public void onWorldLastRender(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().level.dimension().equals(HeroesUnited.SPACE)) {
            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.pushPose();

            IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();

            Vector3d view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStack.translate(-view.x(), -view.y(), -view.z());

            for (CelestialBody celestialBody : CelestialBody.CELESTIAL_BODIES.getValues()) {
                matrixStack.pushPose();
                matrixStack.translate(celestialBody.getCoordinates().x, celestialBody.getCoordinates().y, celestialBody.getCoordinates().z);
                matrixStack.mulPose(new Quaternion(0, 0, 180, true));
                CelestialBodyRenderer celestialBodyRenderer = CelestialBodyRenderer.getRenderer(celestialBody);
                celestialBodyRenderer.render(matrixStack, buffers, WorldRenderer.getLightColor(Minecraft.getInstance().level, new BlockPos(celestialBody.getCoordinates())), event.getPartialTicks());

                IVertexBuilder buffer = buffers.getBuffer(RenderType.LINES);
                matrixStack.popPose();
                if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
                    WorldRenderer.renderLineBox(matrixStack, buffer,  celestialBody.getHitbox(), 1, 1, 1, 1);
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
    }

    public static void sendToggleKey(KeyBinding keyBind, int index) {
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
            event.getMatrixStack().pushPose();
            if (event.getEntity() instanceof PlayerEntity && event.getEntity().isCrouching()) {
                event.getMatrixStack().translate(0,0.125D,0);
            }
            event.getMatrixStack().scale(0.01F,0.01F,0.01F);
        }
        if (entitiesWithLayer.contains(event.getRenderer())) return;
        event.getRenderer().addLayer(new HULayerRenderer(event.getRenderer()));
        entitiesWithLayer.add(event.getRenderer());
    }

    @SubscribeEvent
    public void renderEntityPost(RenderLivingEvent.Post event) {
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.getMatrixStack().popPose();
        }
    }


    @SubscribeEvent
    public void renderShadowSize(HUChangeShadowSizeEvent event) {
        for (Ability a : AbilityHelper.getAbilities(event.getEntity())) {
            if (a instanceof SizeChangeAbility && ((SizeChangeAbility) a).changeSizeInRender()) {
                event.setNewSize(event.getDefaultSize() * ((SizeChangeAbility) a).getSize());
            }
        }
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent event) {
        for (Ability a : AbilityHelper.getAbilities(event.getPlayer())) {
            if (a instanceof SizeChangeAbility && ((SizeChangeAbility) a).changeSizeInRender()) {
                if (event instanceof RenderPlayerEvent.Pre) {
                    event.getMatrixStack().pushPose();
                    float size = ((SizeChangeAbility) a).getRenderSize(event.getPartialRenderTick());
                    event.getMatrixStack().scale(size, size, size);
                }
                if (event instanceof RenderPlayerEvent.Post) {
                    event.getMatrixStack().popPose();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderHULayer(HURenderLayerEvent.Accessories event) {
        for (Ability ability : AbilityHelper.getAbilities(event.getPlayer())) {
            if (ability instanceof HideLayerAbility && ability.getEnabled()) {
                String layer = JSONUtils.getAsString(ability.getJsonObject(), "layer");
                if (layer.equals("accessories")) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void hideLayers(HUHideLayerEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            for (Ability ability : AbilityHelper.getAbilities(event.getEntity())) {
                if (ability instanceof HideLayerAbility) {
                    HideLayerAbility a = (HideLayerAbility) ability;
                    if (a.layerNameIs("armor")) {
                        event.blockLayers(BipedArmorLayer.class, ElytraLayer.class);
                    }
                    if (a.layerNameIs("head")) {
                        event.blockLayer(HeadLayer.class);
                    }
                    if (a.layerNameIs("held_item")) {
                        event.blockLayer(HeldItemLayer.class);
                    }
                    if (a.layerNameIs("heroesunited")) {
                        event.blockLayer(HULayerRenderer.class);
                    }
                    if (a.layerNameIs("arrow")) {
                        event.blockLayer(ArrowLayer.class);
                    }
                    if (a.layerNameIs("player")) {
                        event.blockLayers(BipedArmorLayer.class, HeldItemLayer.class, Deadmau5HeadLayer.class, CapeLayer.class, HeadLayer.class, ElytraLayer.class, ParrotVariantLayer.class);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void setDiscordPresence(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) || Minecraft.getInstance().player == null || Minecraft.getInstance().player.getUUID() != event.getEntity().getUUID())
            return;
        if (!HURichPresence.isHiddenRPC()) {
            HURichPresence.getPresence().setDiscordRichPresence("Playing Heroes United", null, HURichPresence.MiniLogos.NONE, null);
        }
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
            AnimationEvent animationEvent = new AnimationEvent(cap, 0.0F, 0.0F, event.getPartialRenderTick(), false, Arrays.asList(event.getPlayer(), event.getPlayer().getUUID()));
            animationEvent.setController(cap.getController());
            if (!(Minecraft.getInstance().getOverlay() instanceof ResourceLoadProgressGui)) {
                cap.getAnimatedModel().setLivingAnimations(cap, event.getPlayer().getUUID().hashCode(), animationEvent);
            }
            IFlyingAbility ability = IFlyingAbility.getFlyingAbility(event.getPlayer());
            if ((ability != null && ability.isFlying(event.getPlayer()) && ability.renderFlying(event.getPlayer())) || cap.isFlying()) {
                if (!event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                    if (!(event.getPlayer().getFallFlyingTicks() > 4) && !event.getPlayer().isVisuallySwimming()) {
                        event.getMatrixStack().pushPose();
                        event.getMatrixStack().mulPose(new Quaternion(0, -event.getPlayer().yRot, 0, true));
                        event.getMatrixStack().mulPose(new Quaternion(90F + event.getPlayer().xRot, 0, 0, true));
                        event.getMatrixStack().mulPose(new Quaternion(0, event.getPlayer().yRot, 0, true));
                    }
                }
            }
        });
        event.getPlayer().getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
            for (Ability ability : cap.getAbilities().values()) {
                if (ability instanceof IAlwaysRenderer) {
                    ((IAlwaysRenderer) ability).renderPlayerPreAlways(event);
                }
            }
        });
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.renderPlayerPost(event));
        event.getPlayer().getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
            for (Ability ability : cap.getAbilities().values()) {
                if (ability instanceof IAlwaysRenderer) {
                    ((IAlwaysRenderer) ability).renderPlayerPostAlways(event);
                }
            }
        });
        IFlyingAbility ability = IFlyingAbility.getFlyingAbility(event.getPlayer());
        event.getPlayer().getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if ((ability != null && ability.isFlying(event.getPlayer()) && ability.renderFlying(event.getPlayer())) || cap.isFlying()) {
                if (!event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                    if (!(event.getPlayer().getFallFlyingTicks() > 4) && !event.getPlayer().isVisuallySwimming()) {
                        event.getMatrixStack().popPose();
                    }
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
        player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
            for (Ability ability : cap.getAbilities().values()) {
                if (ability instanceof IAlwaysRenderer) {
                    ((IAlwaysRenderer) ability).setAlwaysRotationAngles(event);
                }
            }
        });

        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            for (String s : playerBones) {
                GeoBone bone = cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap)).getBone(s).get();
                ModelRenderer renderer = HUClientUtil.getModelRendererById(event.getPlayerModel(), s);
                if (cap.getController().getCurrentAnimation() != null && cap.getController().getAnimationState() == AnimationState.Running) {
                    for (BoneAnimation boneAnimation : cap.getController().getCurrentAnimation().boneAnimations) {
                        if (boneAnimation.boneName.equals(s)) {
                            renderer.xRot = -bone.getRotationX();
                            renderer.yRot = -bone.getRotationY();
                            renderer.zRot = bone.getRotationZ();

                            renderer.x = -(bone.getPivotX() + bone.getPositionX());
                            renderer.y = (24 - bone.getPivotY()) - bone.getPositionY();
                            renderer.z = bone.getPivotZ() + bone.getPositionZ();

                            if (bone.name.endsWith("Leg")) {
                                renderer.y = ((24 - bone.getPivotY()) - bone.getPositionY()) - bone.getScaleY()*2;
                            }
                            ((IHUModelRenderer) renderer).setSize(new Vector3f(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()));
                        }
                    }
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

            IFlyingAbility ability = IFlyingAbility.getFlyingAbility(event.getPlayer());
            if ((ability != null && ability.isFlying(event.getPlayer()) && ability.renderFlying(event.getPlayer())) || cap.isFlying()) {
                if (!event.getPlayer().isOnGround() && !event.getPlayer().isSwimming() && event.getPlayer().isSprinting()) {
                    PlayerModel model = event.getPlayerModel();
                    model.head.xRot = (-(float) Math.PI / 4F);
                    model.rightArm.xRot = IFlyingAbility.getFlyingAbility(event.getPlayer()) != null && IFlyingAbility.getFlyingAbility(event.getPlayer()).rotateArms(event.getPlayer()) ? (float) Math.toRadians(180F) : (float) Math.toRadians(0F);
                    model.leftArm.xRot = model.rightArm.xRot;
                    model.rightArm.yRot = model.rightArm.zRot =
                            model.leftArm.yRot = model.leftArm.zRot =
                                    model.rightLeg.xRot = model.leftLeg.xRot = (float) Math.toRadians(0F);
                }
            }
        });
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        AbilityHelper.getAbilities(event.getPlayer()).forEach(ability -> ability.inputUpdate(event));
    }
    
    @SubscribeEvent
    public void renderPlayerHandPost(HURenderPlayerHandEvent.Post event) {
        event.getPlayer().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
            for (Ability ability : a.getAbilities().values()) {
                if (ability instanceof IAlwaysRenderer) {
                    ((IAlwaysRenderer) ability).renderAlwaysFirstPersonArm(event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getPlayer(), event.getSide());
                }
            }
        });
    }

    @SubscribeEvent
    public void renderPlayerLayers(HURenderLayerEvent.Player event) {
        event.getPlayer().getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
            for (Ability ability : a.getAbilities().values()) {
                if (ability instanceof IAlwaysRenderer) {
                    ((IAlwaysRenderer) ability).renderAlways(event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getPartialTicks(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch());
                }
            }
        });
    }

    @SubscribeEvent
    public void renderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().player == null) return;
        AbstractClientPlayerEntity player = Minecraft.getInstance().player;
        for (Ability a : AbilityHelper.getAbilities(player)) {
            if (a instanceof JSONAbility && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                double distance = Minecraft.getInstance().hitResult.getLocation().distanceTo(player.position().add(0, player.getEyeHeight(), 0));
                AxisAlignedBB box = new AxisAlignedBB(0.1F, -0.25, 0, 0, -0.25, -distance).inflate(0.03125D);
                Color color = HUJsonUtils.getColor(a.getJsonObject());
                if (a instanceof EnergyLaserAbility && a.getEnabled()) {
                    event.getMatrixStack().pushPose();
                    event.getMatrixStack().translate(player.getMainArm() == HandSide.RIGHT ? 0.3F : -0.3F, 0, 0);
                    HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, 1, event.getLight());
                    HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (color.getAlpha() / 255F) * 0.5F, event.getLight());
                    event.setCanceled(true);
                    event.getMatrixStack().popPose();
                    return;
                }
                if (a instanceof HeatVisionAbility) {
                    float alpha = (a.getDataManager().<Integer>getValue("prev_timer") + (a.getDataManager().<Integer>getValue("timer") - a.getDataManager().<Integer>getValue("prev_timer")) * event.getPartialTicks()) / JSONUtils.getAsInt(a.getJsonObject(), "maxTimer", 10);
                    if (a.getDataManager().<String>getValue("type").equals("cyclop")) {
                        AxisAlignedBB box1 = new AxisAlignedBB(-0.15F, -0.11F, 0, 0.15F, -0.11F, -distance).inflate(0.0625D);
                        HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box1.deflate(0.0625D / 2), 1F, 1F, 1F, alpha, event.getLight());
                        HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box1, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255F, alpha * 0.5F, event.getLight());
                        if (a.getEnabled()) {
                            event.setCanceled(true);
                        }
                        return;
                    }
                    if (a.getDataManager().<String>getValue("type").equals("default")) {
                        for (int i = 0; i < 2; i++) {
                            event.getMatrixStack().pushPose();
                            event.getMatrixStack().translate(i == 0 ? 0.2F : -0.3F, 0.25, 0);
                            HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box, 1F, 1F, 1F, alpha, event.getLight());
                            HUClientUtil.renderFilledBox(event.getMatrixStack(), event.getBuffers().getBuffer(HUClientUtil.HURenderTypes.LASER), box.inflate(0.03125D), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha * 0.5F, event.getLight());
                            if (a.getEnabled()) {
                                event.setCanceled(true);
                            }
                            event.getMatrixStack().popPose();
                        }
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGameOverlayPost(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && mc.player != null && mc.player.isAlive()) {
            List<Ability> abilities = AbilitiesScreen.getCurrentDisplayedAbilities(mc.player, a ->
                    !a.isHidden(mc.player) && a.getConditionManager().isEnabled(mc.player, "canActivate") && a.getConditionManager().isEnabled(mc.player, "canBeEnabled"));
            if (abilities.size() > 0) {
                final ResourceLocation widgets = new ResourceLocation(HeroesUnited.MODID, "textures/gui/widgets.png");
                int y = event.getWindow().getGuiScaledHeight() / 3;

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                mc.getTextureManager().bind(widgets);
                AbstractGui.blit(event.getMatrixStack(), 0, y, 0, 0, 22, 102, 64, 128);

                for (int i = 0; i < abilities.size(); i++) {
                    Ability ability = AbilityHelper.getAnotherAbilityFromMap(AbilityHelper.getAbilities(mc.player), abilities.get(i));
                    int abilityY = y + 3 + i * 20;
                    event.getMatrixStack().pushPose();
                    event.getMatrixStack().translate(0, 0, 500D);
                    ability.drawIcon(event.getMatrixStack(), 3, abilityY);
                    event.getMatrixStack().popPose();
                    mc.getTextureManager().bind(widgets);

                    if (ability.getMaxCooldown(mc.player) != 0) {
                        int progress = (int) ((ability.getDataManager().<Integer>getValue("prev_cooldown") + (ability.getDataManager().<Integer>getValue("cooldown") - ability.getDataManager().<Integer>getValue("prev_cooldown")) * event.getPartialTicks()) / ability.getMaxCooldown(mc.player) * 16);
                        if (progress > 0) {
                            AbstractGui.blit(event.getMatrixStack(), 3, abilityY, 46, 0, progress, 16, 64, 128);
                        }
                    }
                    if (ability.getEnabled()) {
                        AbstractGui.blit(event.getMatrixStack(), -1, abilityY - 4, 22, 0, 24, 24, 64, 128);
                    }
                }
                RenderSystem.color4f(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }
        }
    }

    public static class AbilityKeyBinding extends KeyBinding {

        public final int index;

        public AbilityKeyBinding(String description, int keyCode, int index) {
            super(description, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, keyCode, "key.categories." + HeroesUnited.MODID);
            this.index = index;
        }
    }
}
