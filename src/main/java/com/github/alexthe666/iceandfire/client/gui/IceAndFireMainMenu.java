package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IceAndFireMainMenu extends TitleScreen {
    public static final int LAYER_COUNT = 2;
    public static final Identifier splash = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "splashes.txt");
    private static final Identifier MINECRAFT_TITLE_TEXTURES = Identifier.withDefaultNamespace("textures/gui/title/minecraft.png");
    private static final Identifier BESTIARY_TEXTURE = Identifier.parse("iceandfire:textures/gui/main_menu/bestiary_menu.png");
    private static final Identifier TABLE_TEXTURE = Identifier.parse("iceandfire:textures/gui/main_menu/table.png");
    public static Identifier[] pageFlipTextures;
    public static Identifier[] drawingTextures = new Identifier[22];
    private int layerTick;
    private String splashText;
    private boolean isFlippingPage = false;
    private int pageFlip = 0;
    private Picture[] drawnPictures;
    private Enscription[] drawnEnscriptions;
    private float globalAlpha = 1F;

    public IceAndFireMainMenu() {
        pageFlipTextures = new Identifier[]{Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_1.png"),
            Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_2.png"),
            Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_3.png"),
            Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_4.png"),
            Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_5.png"),
            Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/page_6.png")};
        for (int i = 0; i < drawingTextures.length; i++) {
            drawingTextures[i] = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "textures/gui/main_menu/drawing_" + (i + 1) + ".png");
        }
        resetDrawnImages();
        final String branch = "1.17";
        try (final BufferedReader reader = getURLContents("https://raw.githubusercontent.com/Alex-the-666/Ice_and_Fire/"
            + branch + "/src/main/resources/assets/iceandfire/splashes.txt", "assets/iceandfire/splashes.txt")) {
            List<String> list = IOUtils.readLines(reader);

            if (!list.isEmpty()) {
                do {
                    this.splashText = list.get(ThreadLocalRandom.current().nextInt(list.size()));
                } while (this.splashText.hashCode() == 125780783);
            }
        } catch (IOException e) {
            IceAndFire.LOGGER.error("Exception trying to collect splash screen lines: ", e);
        }
    }

    public static BufferedReader getURLContents(String urlString, String backupFileLoc) {
        BufferedReader reader = null;
        boolean useBackup = false;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            url = null;
            useBackup = true;
        }
        if (url != null) {
            URLConnection connection = null;
            try {
                connection = url.openConnection();
                connection.setConnectTimeout(200);
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
            } catch (IOException e) {
                IceAndFire.LOGGER.warn("Ice and Fire couldn't download splash texts for main menu");
                useBackup = true;
            }
        }
        if (useBackup) {
            InputStream is = IceAndFireMainMenu.class.getClassLoader().getResourceAsStream(backupFileLoc);
            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        }
        return reader;
    }


    private void resetDrawnImages() {
        globalAlpha = 0;
        Random random = java.util.concurrent.ThreadLocalRandom.current();
        drawnPictures = new Picture[1 + random.nextInt(2)];
        boolean left = random.nextBoolean();
        for (int i = 0; i < drawnPictures.length; i++) {
            left = !left;
            int x;
            int y = random.nextInt(25);
            if (left) {
                x = -15 - random.nextInt(20) - 128;
            } else {
                x = 30 + random.nextInt(20);
            }
            drawnPictures[i] = new Picture(random.nextInt(drawingTextures.length - 1), x, y, 0.5F, random.nextFloat() * 0.5F + 0.5F);
        }
        drawnEnscriptions = new Enscription[4 + random.nextInt(8)];
        for (int i = 0; i < drawnEnscriptions.length; i++) {
            left = !left;
            int x;
            int y = 10 + random.nextInt(130);
            if (left) {
                x = -30 - random.nextInt(30) - 50;
            } else {
                x = 30 + random.nextInt(30);
            }
            String s1 = "missingno";
            drawnEnscriptions[i] = new Enscription(s1, x, y, random.nextFloat() * 0.5F + 0.5F, 0X9C8B7B);
        }
    }

    @Override
    public void tick() {
        super.tick();
        float flipTick = layerTick % 40;
        if (globalAlpha < 1 && !isFlippingPage && flipTick < 30) {
            globalAlpha += 0.1F;
        }

        if (globalAlpha > 0 && flipTick > 30) {
            globalAlpha -= 0.1F;
        }
        if (flipTick == 0 && !isFlippingPage) {
            isFlippingPage = true;
        }
        if (isFlippingPage) {
            if (layerTick % 2 == 0) {
                pageFlip++;
            }
            if (pageFlip == 6) {
                pageFlip = 0;
                isFlippingPage = false;
                resetDrawnImages();
            }
        }

        this.layerTick++;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.text(this.font, "Ice and Fire " + ChatFormatting.YELLOW + IceAndFire.VERSION, 2, this.height - 10, 0xFFFFFFFF);
        if (this.splashText != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate((this.width / 2f + 90f), 70.0F);
            graphics.pose().rotate((float) Math.toRadians(-20.0F));
            float f2 = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
            f2 = f2 * 100.0F / (float) (this.font.width(this.splashText) + 32);
            graphics.pose().scale(f2, f2);
            graphics.centeredText(this.font, this.splashText, 0, -8, 16776960);
            graphics.pose().popMatrix();
        }
        String copyright = "Copyright Mojang AB. Do not distribute!";
        graphics.text(this.font, copyright, this.width - this.font.width(copyright) - 2, this.height - 10, 0xFFFFFFFF);
    }

    private class Picture {
        int image;
        int x;
        int y;
        float alpha;

        public Picture(int image, int x, int y, float alpha, float scale) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.alpha = alpha;
        }
    }

    private class Enscription {
        public Enscription(String text, int x, int y, float alpha, int color) {
        }
    }
}

