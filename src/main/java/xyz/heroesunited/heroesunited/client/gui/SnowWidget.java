package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.util.HUCalendarHelper;

import java.util.Random;

public class SnowWidget {
    private static final ResourceLocation snow = new ResourceLocation(HeroesUnited.MODID, "textures/gui/snow.png");
    private static final Random rand = new Random();
    private final int height;
    private final int x;
    private int y;
    private final int index;
    private int rotation;
    private final int friction;
    private int frictionTemp;
    private boolean dead;
    private static final SnowWidget[] cloud = new SnowWidget[150];

    public SnowWidget(int width, int height) {
        this.index = rand.nextInt(6);
        this.x = rand.nextInt(width + 16) - 16;
        this.height = height;
        this.friction = this.frictionTemp = rand.nextInt(5);
        this.y = -16;
    }

    public void drawSnowflake(PoseStack stack) {
        if (this.isDead()) return;
        stack.pushPose();
        stack.translate((float) this.x, (float) this.y, 0);
        stack.mulPose(new Quaternion(0, 0, this.rotation, true));
        GuiComponent.blit(stack, 0, 0, 16 * this.index, 0, 16, 16, 96, 16);
        stack.popPose();
        if (this.frictionTemp-- <= 0) {
            this.frictionTemp = this.friction;
            this.y++;
        }
        this.rotation++;
        if (this.y > height)
            this.setDead();
    }

    public static void drawSnowOnScreen(PoseStack stack, int width, int height) {
        if (!HUCalendarHelper.isSnowTime()) return;
        RenderSystem.setShaderTexture(0, snow);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int i = 0;
        for (SnowWidget snow : cloud) {
            if (snow != null && !snow.isDead()) {
                snow.drawSnowflake(stack);
                if (snow.isDead()) createSnowDropWithChance(i, width, height);
            } else {
                createSnowDropWithChance(i, width, height);
            }
            i++;
        }
    }

    private static void createSnowDropWithChance(int id, int width, int height) {
        if (rand.nextFloat() * 100F <= 0.8F)
            cloud[id] = new SnowWidget(width, height);
    }

    public void setDead() {
        this.dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
