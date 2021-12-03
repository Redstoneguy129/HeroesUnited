package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerHorasPlayerSetDimension;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HorasScreen extends Screen {

    enum TabEnum {
        MENU(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/horas.png")),
        ALIEN(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/aliens.png")),
        EVO(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/evos.png")),
        GHOST(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/ghosts.png")),
        PLANET(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/planets.png")),
        DIMENSION(new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/dimensions.png"));

        private final ResourceLocation location;

        TabEnum(ResourceLocation location) {
            this.location = location;
        }

        public ResourceLocation getLocation() {
            return location;
        }
    }

    private final ResourceLocation info = new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/info.png");
    private final ResourceLocation left_arrow = new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/left_arrow.png");
    private final ResourceLocation right_arrow = new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/right_arrow.png");
    private final ResourceLocation esc = new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/esc.png");
    private final ResourceLocation travel = new ResourceLocation(HeroesUnited.MODID, "textures/gui/horas/menu/travel.png");
    private final HashMap<HorasInfo.AlienInfo, LivingEntity> alienInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.EvoInfo, LivingEntity> evoInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.GhostInfo, LivingEntity> ghostInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.PlanetInfo, ResourceLocation> planetInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.DimensionInfo, ResourceLocation> dimensionInfoHUEntityHashMap = new HashMap<>();
    private final Horas horas;

    private int xSize, ySize, x, y;
    private int alienMaxPages, evoMaxPages, ghostMaxPages, planetMaxPages, dimensionMaxPages;
    private TabEnum currentTab;

    public HorasScreen(Horas horas) {
        super(new TranslatableComponent("screen.heroesunited.horasscreen"));
        this.horas = horas;
    }

    @Override
    public void init() {
        xSize = 247;
        ySize = 253;
        x = (this.width - xSize) / 2;
        y = (this.height - ySize) / 2;
        currentTab = TabEnum.MENU;
        HorasInfo.getAliens().forEach(alienInfo -> alienInfoHUEntityHashMap.put(alienInfo, alienInfo.getEntityType().create(this.getMinecraft().level)));
        alienMaxPages = alienInfoHUEntityHashMap.size() / 3;
        HorasInfo.getEvos().forEach(evoInfo -> evoInfoHUEntityHashMap.put(evoInfo, evoInfo.getEntityType().create(this.getMinecraft().level)));
        evoMaxPages = evoInfoHUEntityHashMap.size() / 3;
        HorasInfo.getGhosts().forEach(ghostInfo -> ghostInfoHUEntityHashMap.put(ghostInfo, ghostInfo.getEntityType().create(this.getMinecraft().level)));
        ghostMaxPages = ghostInfoHUEntityHashMap.size() / 3;
        HorasInfo.getPlanets().forEach(planetInfo -> planetInfoHUEntityHashMap.put(planetInfo, planetInfo.getPlanetImage()));
        planetMaxPages = planetInfoHUEntityHashMap.size() / 3;
        HorasInfo.getDimensions().forEach(dimensionInfo -> dimensionInfoHUEntityHashMap.put(dimensionInfo, dimensionInfo.getDimensionImage()));
        dimensionMaxPages = dimensionInfoHUEntityHashMap.size() / 3;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        SnowWidget.drawSnowOnScreen(matrixStack, this.width, this.height);
        this.renderBackground(matrixStack);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, TabEnum.MENU.getLocation());
        this.blit(matrixStack, x, y, 256, 256, xSize, ySize);
        this.renderables.forEach(widget -> {
            if (widget instanceof Button) {
                ((Button) widget).active = false;
                ((Button) widget).visible = false;
            }
        });
        this.renderables.clear();
        switch (currentTab) {
            case MENU -> MenuRender();
            case ALIEN -> {
                ESCButton();
                AlienRender(matrixStack);
            }
            case EVO -> {
                ESCButton();
                EvoRender(matrixStack);
            }
            case GHOST -> {
                ESCButton();
                GhostRender(matrixStack);
            }
            case PLANET -> {
                ESCButton();
                PlanetRender(matrixStack);
            }
            case DIMENSION -> {
                ESCButton();
                DimensionRender(matrixStack);
            }
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void MenuRender() {
        this.addRenderableWidget(new ImageButton(x + 6, y + 6, 75, 79, 0, 0, 0, TabEnum.ALIEN.getLocation(), (button) -> this.currentTab = TabEnum.ALIEN));
        this.addRenderableWidget(new ImageButton(x + 88, y + 6, 75, 79, 0, 0, 0, TabEnum.EVO.getLocation(), (button) -> this.currentTab = TabEnum.EVO));
        this.addRenderableWidget(new ImageButton(x + 163, y + 6, 75, 79, 0, 0, 0, TabEnum.GHOST.getLocation(), (button) -> this.currentTab = TabEnum.GHOST));
        this.addRenderableWidget(new ImageButton(x + 6, y + 90, 90, 56, 0, 0, 0, TabEnum.PLANET.getLocation(), (button) -> this.currentTab = TabEnum.PLANET));
        this.addRenderableWidget(new ImageButton(x + 99, y + 90, 90, 56, 0, 0, 0, TabEnum.DIMENSION.getLocation(), (button) -> this.currentTab = TabEnum.DIMENSION));
    }


    private int alienPage = 0;

    private void AlienRender(PoseStack matrixStack) {
        if (alienPage != 0) {
            this.addRenderableWidget(new ImageButton(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> alienPage--));
        }
        if (alienPage != alienMaxPages && alienInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addRenderableWidget(new ImageButton(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> alienPage++));
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3) {
            HorasInfo.AlienInfo alien1 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(alien1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 6 + (75 / 2), y + 114, 40, alienInfoHUEntityHashMap.get(alien1));
            List<Character> alien1CharacterList = new ArrayList<>();
            for (char i : alien1.getDescription().toCharArray()) {
                alien1CharacterList.add(i);
            }
            int lineCount = 0;
            while (!alien1CharacterList.isEmpty()) {
                List<Character> characters;
                if (alien1CharacterList.size() > 13) {
                    characters = alien1CharacterList.subList(0, 13);
                } else {
                    characters = alien1CharacterList.subList(0, alien1CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3 + 1) {
            HorasInfo.AlienInfo alien2 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(alien2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 84 + (75 / 2), y + 114, 40, alienInfoHUEntityHashMap.get(alien2));
            List<Character> alien2CharacterList = new ArrayList<>();
            for (char i : alien2.getDescription().toCharArray()) {
                alien2CharacterList.add(i);
            }
            int lineCount2 = 0;
            while (!alien2CharacterList.isEmpty()) {
                List<Character> characters;
                if (alien2CharacterList.size() > 13) {
                    characters = alien2CharacterList.subList(0, 13);
                } else {
                    characters = alien2CharacterList.subList(0, alien2CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3 + 2) {
            HorasInfo.AlienInfo alien3 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(alien3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 163 + (75 / 2), y + 114, 40, alienInfoHUEntityHashMap.get(alien3));
            List<Character> alien3CharacterList = new ArrayList<>();
            for (char i : alien3.getDescription().toCharArray()) {
                alien3CharacterList.add(i);
            }
            int lineCount3 = 0;
            while (!alien3CharacterList.isEmpty()) {
                List<Character> characters;
                if (alien3CharacterList.size() > 13) {
                    characters = alien3CharacterList.subList(0, 13);
                } else {
                    characters = alien3CharacterList.subList(0, alien3CharacterList.size());
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int evoPage = 0;

    private void EvoRender(PoseStack matrixStack) {
        if (evoPage != 0) {
            this.addRenderableWidget(new ImageButton(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> evoPage--));
        }
        if (evoPage != evoMaxPages && evoInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addRenderableWidget(new ImageButton(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> evoPage++));
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3) {
            HorasInfo.EvoInfo evo1 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(evo1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 6 + (75 / 2), y + 114, 40, evoInfoHUEntityHashMap.get(evo1));
            List<Character> evo1CharacterList = new ArrayList<>();
            for (char i : evo1.getDescription().toCharArray()) {
                evo1CharacterList.add(i);
            }
            int lineCount = 0;
            while (!evo1CharacterList.isEmpty()) {
                List<Character> characters;
                if (evo1CharacterList.size() > 13) {
                    characters = evo1CharacterList.subList(0, 13);
                } else {
                    characters = evo1CharacterList.subList(0, evo1CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3 + 1) {
            HorasInfo.EvoInfo evo2 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(evo2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 84 + (75 / 2), y + 114, 40, evoInfoHUEntityHashMap.get(evo2));
            List<Character> evo2CharacterList = new ArrayList<>();
            for (char i : evo2.getDescription().toCharArray()) {
                evo2CharacterList.add(i);
            }
            int lineCount2 = 0;
            while (!evo2CharacterList.isEmpty()) {
                List<Character> characters;
                if (evo2CharacterList.size() > 13) {
                    characters = evo2CharacterList.subList(0, 13);
                } else {
                    characters = evo2CharacterList.subList(0, evo2CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3 + 2) {
            HorasInfo.EvoInfo evo3 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(evo3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 163 + (75 / 2), y + 114, 40, evoInfoHUEntityHashMap.get(evo3));
            List<Character> evo3CharacterList = new ArrayList<>();
            for (char i : evo3.getDescription().toCharArray()) {
                evo3CharacterList.add(i);
            }
            int lineCount3 = 0;
            while (!evo3CharacterList.isEmpty()) {
                List<Character> characters;
                if (evo3CharacterList.size() > 13) {
                    characters = evo3CharacterList.subList(0, 13);
                } else {
                    characters = evo3CharacterList.subList(0, evo3CharacterList.size());
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int ghostPage = 0;

    private void GhostRender(PoseStack matrixStack) {
        if (ghostPage != 0) {
            this.addRenderableWidget(new ImageButton(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> ghostPage--));
        }
        if (ghostPage != ghostMaxPages && ghostInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addRenderableWidget(new ImageButton(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> ghostPage++));
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3) {
            HorasInfo.GhostInfo ghost1 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(ghost1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 6 + (75 / 2), y + 114, 40, ghostInfoHUEntityHashMap.get(ghost1));
            List<Character> ghost1CharacterList = new ArrayList<>();
            for (char i : ghost1.getDescription().toCharArray()) {
                ghost1CharacterList.add(i);
            }
            int lineCount = 0;
            while (!ghost1CharacterList.isEmpty()) {
                List<Character> characters;
                if (ghost1CharacterList.size() > 13) {
                    characters = ghost1CharacterList.subList(0, 13);
                } else {
                    characters = ghost1CharacterList.subList(0, ghost1CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3 + 1) {
            HorasInfo.GhostInfo ghost2 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(ghost2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 84 + (75 / 2), y + 114, 40, ghostInfoHUEntityHashMap.get(ghost2));
            List<Character> ghost2CharacterList = new ArrayList<>();
            for (char i : ghost2.getDescription().toCharArray()) {
                ghost2CharacterList.add(i);
            }
            int lineCount2 = 0;
            while (!ghost2CharacterList.isEmpty()) {
                List<Character> characters;
                if (ghost2CharacterList.size() > 13) {
                    characters = ghost2CharacterList.subList(0, 13);
                } else {
                    characters = ghost2CharacterList.subList(0, ghost2CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3 + 2) {
            HorasInfo.GhostInfo ghost3 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(ghost3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            this.drawEntity(x + 163 + (75 / 2), y + 114, 40, ghostInfoHUEntityHashMap.get(ghost3));
            List<Character> ghost3CharacterList = new ArrayList<>();
            for (char i : ghost3.getDescription().toCharArray()) {
                ghost3CharacterList.add(i);
            }
            int lineCount3 = 0;
            while (!ghost3CharacterList.isEmpty()) {
                List<Character> characters;
                if (ghost3CharacterList.size() > 13) {
                    characters = ghost3CharacterList.subList(0, 13);
                } else {
                    characters = ghost3CharacterList.subList(0, ghost3CharacterList.size());
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int planetPage = 0;

    private void PlanetRender(PoseStack matrixStack) {
        if (planetPage != 0) {
            this.addRenderableWidget(new ImageButton(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> planetPage--));
        }
        if (planetPage != planetMaxPages && planetInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addRenderableWidget(new ImageButton(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> planetPage++));
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3) {
            HorasInfo.PlanetInfo planet1 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(planet1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet1));
            this.blit(matrixStack, x + 8, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 6 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet1.getDimensionID(), this.horas.getId()))));
            List<Character> planet1CharacterList = new ArrayList<>();
            for (char i : planet1.getDescription().toCharArray()) {
                planet1CharacterList.add(i);
            }
            int lineCount = 0;
            while (!planet1CharacterList.isEmpty()) {
                List<Character> characters;
                if (planet1CharacterList.size() > 13) {
                    characters = planet1CharacterList.subList(0, 13);
                } else {
                    characters = planet1CharacterList.subList(0, planet1CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 9, y + 73 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3 + 1) {
            HorasInfo.PlanetInfo planet2 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(planet2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet2));
            this.blit(matrixStack, x + 86, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 84 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet2.getDimensionID(), this.horas.getId()))));
            List<Character> planet2CharacterList = new ArrayList<>();
            for (char i : planet2.getDescription().toCharArray()) {
                planet2CharacterList.add(i);
            }
            int lineCount2 = 0;
            while (!planet2CharacterList.isEmpty()) {
                List<Character> characters;
                if (planet2CharacterList.size() > 13) {
                    characters = planet2CharacterList.subList(0, 13);
                } else {
                    characters = planet2CharacterList.subList(0, planet2CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 87, y + 73 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3 + 2) {
            HorasInfo.PlanetInfo planet3 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(planet3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet3));
            this.blit(matrixStack, x + 165, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 163 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet3.getDimensionID(), this.horas.getId()))));
            List<Character> planet3CharacterList = new ArrayList<>();
            for (char i : planet3.getDescription().toCharArray()) {
                planet3CharacterList.add(i);
            }
            int lineCount3 = 0;
            while (!planet3CharacterList.isEmpty()) {
                List<Character> characters;
                if (planet3CharacterList.size() > 13) {
                    characters = planet3CharacterList.subList(0, 13);
                } else {
                    characters = planet3CharacterList.subList(0, planet3CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 166, y + 73 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int dimensionPage = 0;

    private void DimensionRender(PoseStack matrixStack) {
        if (dimensionPage != 0) {
            this.addRenderableWidget(new ImageButton(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> dimensionPage--));
        }
        if (dimensionPage != dimensionMaxPages && dimensionInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addRenderableWidget(new ImageButton(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> dimensionPage++));
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3) {
            HorasInfo.DimensionInfo dimension1 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(dimension1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension1));
            this.blit(matrixStack, x + 8, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 6 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension1.getDimensionID(), this.horas.getId()))));
            List<Character> dimension1CharacterList = new ArrayList<>();
            for (char i : dimension1.getDescription().toCharArray()) {
                dimension1CharacterList.add(i);
            }
            int lineCount = 0;
            while (!dimension1CharacterList.isEmpty()) {
                List<Character> characters;
                if (dimension1CharacterList.size() > 13) {
                    characters = dimension1CharacterList.subList(0, 13);
                } else {
                    characters = dimension1CharacterList.subList(0, dimension1CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 9, y + 73 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3 + 1) {
            HorasInfo.DimensionInfo dimension2 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(dimension2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension2));
            this.blit(matrixStack, x + 86, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 84 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension2.getDimensionID(), this.horas.getId()))));
            List<Character> dimension2CharacterList = new ArrayList<>();
            for (char i : dimension2.getDescription().toCharArray()) {
                dimension2CharacterList.add(i);
            }
            int lineCount2 = 0;
            while (!dimension2CharacterList.isEmpty()) {
                List<Character> characters;
                if (dimension2CharacterList.size() > 13) {
                    characters = dimension2CharacterList.subList(0, 13);
                } else {
                    characters = dimension2CharacterList.subList(0, dimension2CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 87, y + 73 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3 + 2) {
            HorasInfo.DimensionInfo dimension3 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.blit(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredString(matrixStack, this.font, new TextComponent(dimension3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension3));
            this.blit(matrixStack, x + 165, y + 25, 256, 256, 71, 45);
            this.addRenderableWidget(new ImageButton(x + 163 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension3.getDimensionID(), this.horas.getId()))));
            List<Character> dimension3CharacterList = new ArrayList<>();
            for (char i : dimension3.getDescription().toCharArray()) {
                dimension3CharacterList.add(i);
            }
            int lineCount3 = 0;
            while (!dimension3CharacterList.isEmpty()) {
                List<Character> characters;
                if (dimension3CharacterList.size() > 13) {
                    characters = dimension3CharacterList.subList(0, 13);
                } else {
                    characters = dimension3CharacterList.subList(0, dimension3CharacterList.size());
                }
                if (characters.get(0).toString().equals(" ")) {
                    characters.remove(0);
                }
                StringBuilder newLine = new StringBuilder();
                characters.forEach(newLine::append);
                drawString(matrixStack, this.font, new TextComponent(newLine.toString()), x + 166, y + 73 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private void drawEntity(int posX, int posY, int scale, LivingEntity entity) {
        EntityRenderer<? super Entity> renderer = this.getMinecraft().getEntityRenderDispatcher().getRenderer(entity);
        if (renderer instanceof LivingEntityRenderer && ((LivingEntityRenderer) renderer).getModel() instanceof HeadedModel) {
            LivingEntityRenderer render = (LivingEntityRenderer) renderer;
            HeadedModel model = (HeadedModel) render.getModel();
            model.getHead().visible = true;
            float f = (float) Math.atan((double) (-242 / 40.0F));
            float f1 = (float) Math.atan((double) (-103 / 40.0F));
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate((double) posX, (double) posY, 1050.0D);
            posestack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            PoseStack posestack1 = new PoseStack();
            posestack1.translate(0.0D, 0.0D, 1000.0D);
            posestack1.scale((float) scale, (float) scale, (float) scale);
            Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
            Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
            posestack1.mulPose(quaternion);
            float f2 = entity.yBodyRot;
            float f3 = entity.getYRot();
            float f4 = entity.getXRot();
            float f5 = entity.yHeadRotO;
            float f6 = entity.yHeadRot;
            entity.yBodyRot = 180.0F + f * 20.0F;
            entity.setYRot(180.0F + f * 40.0F);
            entity.setXRot(-f1 * 20.0F);
            entity.yHeadRot = entity.getYRot();
            entity.yHeadRotO = entity.getYRot();
            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            quaternion1.conj();
            entityrenderdispatcher.overrideCameraOrientation(quaternion1);
            entityrenderdispatcher.setRenderShadow(false);
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(entity)));
            RenderSystem.runAsFancy(() -> model.getHead().render(posestack1, builder, OverlayTexture.NO_OVERLAY, 15728880));
            buffer.endBatch();
            entityrenderdispatcher.setRenderShadow(true);
            entity.yBodyRot = f2;
            entity.setYRot(f3);
            entity.setXRot(f4);
            entity.yHeadRotO = f5;
            entity.yHeadRot = f6;
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
        }
    }

    private void ESCButton() {
        this.addRenderableWidget(new ImageButton(x - 38, y + 2, 35, 25, 0, 0, 0, esc, (button) -> this.currentTab = TabEnum.MENU));
    }
}
