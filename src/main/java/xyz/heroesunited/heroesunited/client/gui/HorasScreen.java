package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerHorasPlayerSetDimension;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class HorasScreen extends Screen {

    enum TabEnum {
        MENU(new Identifier(HeroesUnited.MODID, "textures/gui/horas/horas.png")),
        ALIEN(new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/aliens.png")),
        EVO(new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/evos.png")),
        GHOST(new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/ghosts.png")),
        PLANET(new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/planets.png")),
        DIMENSION(new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/dimensions.png"));

        private final Identifier location;

        TabEnum(Identifier location) {
            this.location = location;
        }

        public Identifier getLocation() {
            return location;
        }
    }

    private final Identifier info = new Identifier(HeroesUnited.MODID, "textures/gui/horas/info.png");
    private final Identifier left_arrow = new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/left_arrow.png");
    private final Identifier right_arrow = new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/right_arrow.png");
    private final Identifier esc = new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/esc.png");
    private final Identifier travel = new Identifier(HeroesUnited.MODID, "textures/gui/horas/menu/travel.png");
    private final HashMap<HorasInfo.AlienInfo, LivingEntity> alienInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.EvoInfo, LivingEntity> evoInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.GhostInfo, LivingEntity> ghostInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.PlanetInfo, Identifier> planetInfoHUEntityHashMap = new HashMap<>();
    private final HashMap<HorasInfo.DimensionInfo, Identifier> dimensionInfoHUEntityHashMap = new HashMap<>();
    private final Horas horas;

    private int xSize, ySize, x, y;
    private int alienMaxPages, evoMaxPages, ghostMaxPages, planetMaxPages, dimensionMaxPages;
    private TabEnum currentTab;

    public HorasScreen(Horas horas) {
        super(new TranslatableText("screen.heroesunited.horasscreen"));
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        SnowWidget.drawSnowOnScreen(matrixStack, this.width, this.height);
        this.renderBackground(matrixStack);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, TabEnum.MENU.getLocation());
        this.drawTexture(matrixStack, x, y, 256, 256, xSize, ySize);
        this.drawables.forEach(widget -> {
            if (widget instanceof ButtonWidget) {
                ((ButtonWidget) widget).active = false;
                ((ButtonWidget) widget).visible = false;
            }
        });
        this.clearChildren();
        switch (currentTab) {
            case MENU:
                MenuRender();
                break;
            case ALIEN:
                ESCButton();
                AlienRender(matrixStack);
                break;
            case EVO:
                ESCButton();
                EvoRender(matrixStack);
                break;
            case GHOST:
                ESCButton();
                GhostRender(matrixStack);
                break;
            case PLANET:
                ESCButton();
                PlanetRender(matrixStack);
                break;
            case DIMENSION:
                ESCButton();
                DimensionRender(matrixStack);
                break;
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void MenuRender() {
        this.addDrawableChild(new TexturedButtonWidget(x + 6, y + 6, 75, 79, 0, 0, 0, TabEnum.ALIEN.getLocation(), (button) -> this.currentTab = TabEnum.ALIEN));
        this.addDrawableChild(new TexturedButtonWidget(x + 88, y + 6, 75, 79, 0, 0, 0, TabEnum.EVO.getLocation(), (button) -> this.currentTab = TabEnum.EVO));
        this.addDrawableChild(new TexturedButtonWidget(x + 163, y + 6, 75, 79, 0, 0, 0, TabEnum.GHOST.getLocation(), (button) -> this.currentTab = TabEnum.GHOST));
        this.addDrawableChild(new TexturedButtonWidget(x + 6, y + 90, 90, 56, 0, 0, 0, TabEnum.PLANET.getLocation(), (button) -> this.currentTab = TabEnum.PLANET));
        this.addDrawableChild(new TexturedButtonWidget(x + 99, y + 90, 90, 56, 0, 0, 0, TabEnum.DIMENSION.getLocation(), (button) -> this.currentTab = TabEnum.DIMENSION));
    }


    private int alienPage = 0;

    private void AlienRender(MatrixStack matrixStack) {
        if (alienPage != 0) {
            this.addDrawableChild(new TexturedButtonWidget(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> alienPage--));
        }
        if (alienPage != alienMaxPages && alienInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addDrawableChild(new TexturedButtonWidget(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> alienPage++));
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3) {
            HorasInfo.AlienInfo alien1 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(alien1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3 + 1) {
            HorasInfo.AlienInfo alien2 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(alien2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (alienInfoHUEntityHashMap.keySet().toArray().length - 1 >= alienPage * 3 + 2) {
            HorasInfo.AlienInfo alien3 = (HorasInfo.AlienInfo) alienInfoHUEntityHashMap.keySet().toArray()[alienPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(alien3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int evoPage = 0;

    private void EvoRender(MatrixStack matrixStack) {
        if (evoPage != 0) {
            this.addDrawableChild(new TexturedButtonWidget(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> evoPage--));
        }
        if (evoPage != evoMaxPages && evoInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addDrawableChild(new TexturedButtonWidget(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> evoPage++));
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3) {
            HorasInfo.EvoInfo evo1 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(evo1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3 + 1) {
            HorasInfo.EvoInfo evo2 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(evo2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (evoInfoHUEntityHashMap.keySet().toArray().length - 1 >= evoPage * 3 + 2) {
            HorasInfo.EvoInfo evo3 = (HorasInfo.EvoInfo) evoInfoHUEntityHashMap.keySet().toArray()[evoPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(evo3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int ghostPage = 0;

    private void GhostRender(MatrixStack matrixStack) {
        if (ghostPage != 0) {
            this.addDrawableChild(new TexturedButtonWidget(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> ghostPage--));
        }
        if (ghostPage != ghostMaxPages && ghostInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addDrawableChild(new TexturedButtonWidget(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> ghostPage++));
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3) {
            HorasInfo.GhostInfo ghost1 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(ghost1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 9, y + 76 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3 + 1) {
            HorasInfo.GhostInfo ghost2 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(ghost2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 87, y + 76 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (ghostInfoHUEntityHashMap.keySet().toArray().length - 1 >= ghostPage * 3 + 2) {
            HorasInfo.GhostInfo ghost3 = (HorasInfo.GhostInfo) ghostInfoHUEntityHashMap.keySet().toArray()[ghostPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(ghost3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 166, y + 76 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int planetPage = 0;

    private void PlanetRender(MatrixStack matrixStack) {
        if (planetPage != 0) {
            this.addDrawableChild(new TexturedButtonWidget(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> planetPage--));
        }
        if (planetPage != planetMaxPages && planetInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addDrawableChild(new TexturedButtonWidget(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> planetPage++));
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3) {
            HorasInfo.PlanetInfo planet1 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(planet1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet1));
            this.drawTexture(matrixStack, x + 8, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 6 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet1.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 9, y + 73 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3 + 1) {
            HorasInfo.PlanetInfo planet2 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(planet2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet2));
            this.drawTexture(matrixStack, x + 86, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 84 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet2.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 87, y + 73 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (planetInfoHUEntityHashMap.keySet().toArray().length - 1 >= planetPage * 3 + 2) {
            HorasInfo.PlanetInfo planet3 = (HorasInfo.PlanetInfo) planetInfoHUEntityHashMap.keySet().toArray()[planetPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(planet3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, planetInfoHUEntityHashMap.get(planet3));
            this.drawTexture(matrixStack, x + 165, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 163 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(planet3.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 166, y + 73 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private int dimensionPage = 0;

    private void DimensionRender(MatrixStack matrixStack) {
        if (dimensionPage != 0) {
            this.addDrawableChild(new TexturedButtonWidget(x - 29, y + 49, 26, 26, 0, 0, 0, left_arrow, (button) -> dimensionPage--));
        }
        if (dimensionPage != dimensionMaxPages && dimensionInfoHUEntityHashMap.keySet().toArray().length > 3) {
            this.addDrawableChild(new TexturedButtonWidget(x + 247, y + 49, 26, 26, 0, 0, 0, right_arrow, (button) -> dimensionPage++));
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3) {
            HorasInfo.DimensionInfo dimension1 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 6, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(dimension1.getName()), x + 6 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension1));
            this.drawTexture(matrixStack, x + 8, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 6 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension1.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 9, y + 73 + (lineCount * 12), 16777215);
                lineCount++;
                characters.clear();
            }
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3 + 1) {
            HorasInfo.DimensionInfo dimension2 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3 + 1];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 84, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(dimension2.getName()), x + 84 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension2));
            this.drawTexture(matrixStack, x + 86, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 84 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension2.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 87, y + 73 + (lineCount2 * 12), 16777215);
                lineCount2++;
                characters.clear();
            }
        }
        if (dimensionInfoHUEntityHashMap.keySet().toArray().length - 1 >= dimensionPage * 3 + 2) {
            HorasInfo.DimensionInfo dimension3 = (HorasInfo.DimensionInfo) dimensionInfoHUEntityHashMap.keySet().toArray()[dimensionPage * 3 + 2];
            RenderSystem.setShaderTexture(0, info);
            this.drawTexture(matrixStack, x + 163, y + 7, 256, 256, 75, 138);
            drawCenteredText(matrixStack, this.textRenderer, new LiteralText(dimension3.getName()), x + 163 + (75 / 2), y + 14, 16777215);
            RenderSystem.setShaderTexture(0, dimensionInfoHUEntityHashMap.get(dimension3));
            this.drawTexture(matrixStack, x + 165, y + 25, 256, 256, 71, 45);
            this.addDrawableChild(new TexturedButtonWidget(x + 163 + 20, y + 127, 33, 11, 0, 0, 0, travel, (buttons) -> HUNetworking.INSTANCE.sendToServer(new ServerHorasPlayerSetDimension(dimension3.getDimensionID(), this.horas.getId()))));
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
                drawTextWithShadow(matrixStack, this.textRenderer, new LiteralText(newLine.toString()), x + 166, y + 73 + (lineCount3 * 12), 16777215);
                lineCount3++;
                characters.clear();
            }
        }
    }


    private void drawEntity(int posX, int posY, int scale, LivingEntity entity) {
        EntityRenderer<? super Entity> renderer = this.getMinecraft().getEntityRenderDispatcher().getRenderer(entity);
        if (renderer instanceof LivingEntityRenderer && ((LivingEntityRenderer) renderer).getModel() instanceof ModelWithHead) {
            LivingEntityRenderer render = (LivingEntityRenderer) renderer;
            ModelWithHead model = (ModelWithHead) render.getModel();
            model.getHead().visible = true;
            float f = (float) Math.atan(-242 / 40.0F);
            float f1 = (float) Math.atan(-103 / 40.0F);
            MatrixStack posestack = RenderSystem.getModelViewStack();
            posestack.push();
            posestack.translate(posX, posY, 1050.0D);
            posestack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            MatrixStack posestack1 = new MatrixStack();
            posestack1.translate(0.0D, 0.0D, 1000.0D);
            posestack1.scale(scale, scale, scale);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(f1 * 20.0F);
            quaternion.hamiltonProduct(quaternion1);
            posestack1.multiply(quaternion);
            float f2 = entity.bodyYaw;
            float f3 = entity.getYaw();
            float f4 = entity.getPitch();
            float f5 = entity.prevHeadYaw;
            float f6 = entity.headYaw;
            entity.bodyYaw = 180.0F + f * 20.0F;
            entity.setYaw(180.0F + f * 40.0F);
            entity.setPitch(-f1 * 20.0F);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            DiffuseLighting.setupForEntityInInventory();
            EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion1.conjugate();
            VertexConsumerProvider.Immediate buffer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            VertexConsumer builder = buffer.getBuffer(RenderLayer.getEntityTranslucent(renderer.getTexture(entity)));
            RenderSystem.runAsFancy(() -> model.getHead().render(posestack1, builder, OverlayTexture.DEFAULT_UV, 15728880));
            buffer.draw();
            manager.setRenderShadows(true);
            entity.bodyYaw = f2;
            entity.setYaw(f3);
            entity.setPitch(f4);
            entity.prevHeadYaw = f5;
            entity.headYaw = f6;
            posestack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();
        }
    }

    private void ESCButton() {
        this.addDrawableChild(new TexturedButtonWidget(x - 38, y + 2, 35, 25, 0, 0, 0, esc, (button) -> this.currentTab = TabEnum.MENU));
    }
}
