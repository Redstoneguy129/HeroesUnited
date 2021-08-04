package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.util.HUCalendarHelper;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class SnowWidget {
    private static Identifier snow = new Identifier(HeroesUnited.MODID, "textures/gui/snow.png");
    private static Random rand = new Random();
    private int height, x, y, index, rotation, friction, frictionTemp;
    private boolean dead;
    private static SnowWidget[] cloud = new SnowWidget[150];

    public SnowWidget(int width, int height) {
        this.index = rand.nextInt(6);
        this.x = rand.nextInt(width + 16) - 16;
        this.height = height;
        this.friction = this.frictionTemp = rand.nextInt(5);
        this.y = -16;
    }

    public void drawSnowflake(MatrixStack stack) {
        if (this.isDead()) return;
        stack.push();
        stack.translate((float) this.x, (float) this.y, 0);
        stack.multiply(new Quaternion(0, 0, this.rotation, true));
        DrawableHelper.drawTexture(stack, 0, 0, 16 * this.index, 0, 16, 16, 96, 16);
        stack.pop();
        if (this.frictionTemp-- <= 0) {
            this.frictionTemp = this.friction;
            this.y++;
        }
        this.rotation++;
        if (this.y > height)
            this.setDead();
    }

    public static void drawSnowOnScreen(MatrixStack stack, int width, int height) {
        if (!HUCalendarHelper.isSnowTime()) return;
        MinecraftClient.getInstance().getTextureManager().bind(snow);
        RenderSystem.color4f(1, 1, 1, 1);
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
