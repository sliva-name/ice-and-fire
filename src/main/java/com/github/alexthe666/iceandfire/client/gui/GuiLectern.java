package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.inventory.ContainerLectern;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class GuiLectern extends AbstractContainerScreen<ContainerLectern> {
    private static final Identifier ENCHANTMENT_TABLE_GUI_TEXTURE = Identifier.parse("iceandfire:textures/gui/lectern.png");
    private static final Identifier ENCHANTMENT_TABLE_BOOK_TEXTURE = Identifier.parse("iceandfire:textures/models/lectern_book.png");
    private static BookModel bookModel;
    private final Random random = new Random();
    private final Component nameable;
    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;
    private int flapTimer = 0;

    public GuiLectern(ContainerLectern container, Inventory inv, Component name) {
        super(container, inv, name);
        this.nameable = name;
    }

    @Override
    protected void init() {
        super.init();
        bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.nameable.getString(), 12, 4, 4210752);
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.menu.onUpdate();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubled) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        double mouseX = event.x();
        double mouseY = event.y();

        for (int k = 0; k < 3; ++k) {
            double l = mouseX - (i + 60);
            double i1 = mouseY - (j + 14 + 19 * k);

            if (l >= 0 && i1 >= 0 && l < 108 && i1 < 19 && this.menu.clickMenuButton(this.minecraft.player, k)) {
                flapTimer = 5;
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }
        return super.mouseClicked(event, doubled);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        float f1 = Mth.lerp(partialTicks, this.oOpen, this.open);
        float f3 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.25F;
        float f4 = Mth.lerp(partialTicks, this.oFlip, this.flip) + 0.75F;
        f3 = (f3 - (float) Mth.floor(f3)) * 1.6F - 0.3F;
        f4 = (f4 - (float) Mth.floor(f4)) * 1.6F - 0.3F;
        f3 = Mth.clamp(f3, 0.0F, 1.0F);
        f4 = Mth.clamp(f4, 0.0F, 1.0F);
        bookModel.setupAnim(new BookModel.State(f1, f3, f4));
        graphics.book(bookModel, ENCHANTMENT_TABLE_BOOK_TEXTURE, f1, f3, f4, i + 14, j + 14, 40, 40);
        this.menu.getManuscriptAmount();

        for (int i1 = 0; i1 < 3; ++i1) {
            int j1 = i + 60;
            int k1 = j1 + 20;
            int l1 = this.menu.getPossiblePages()[i1] == null ? -1 : this.menu.getPossiblePages()[i1].ordinal();
            if (l1 == -1) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 185, 108, 19, 256, 256);
            } else {
                String s = "" + 3;
                Font fontrenderer = this.font;
                String s1 = "";
                float textScale = 1.0F;
                EnumBestiaryPages enchantment = this.menu.getPossiblePages()[i1];
                if (enchantment != null) {
                    s1 = I18n.get("bestiary." + enchantment.toString().toLowerCase());
                    if (fontrenderer.width(s1) > 80) {
                        textScale = 1.0F - (fontrenderer.width(s1) - 80) * 0.01F;
                    }
                }
                int j2 = 6839882;
                if (IceAndFire.PROXY.getRefrencedTE() instanceof TileEntityLectern) {
                    if (menu.getSlot(0).getItem().getItem() == IafItemRegistry.BESTIARY.get()) {
                        int k2 = mouseX - (i + 60);
                        int l2 = mouseY - (j + 14 + 19 * i1);
                        int j3 = 0X9F988C;
                        if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                            graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 204, 108, 19, 256, 256);
                            j2 = 16777088;
                            j3 = 16777088;
                        } else {
                            graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 166, 108, 19, 256, 256);
                        }

                        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1 + 1, j + 15 + 19 * i1, 16 * i1, 223, 16, 16, 256, 256);
                        graphics.pose().pushMatrix();
                        graphics.pose().translate(width / 2F - 10, height / 2F - 83 + (1.0F - textScale) * 55);
                        graphics.pose().scale(textScale, textScale);
                        graphics.text(fontrenderer, s1, 0, 20 + 19 * i1, j2);
                        graphics.pose().popMatrix();
                        graphics.text(this.font, s, k1 + 84 - this.font.width(s), j + 13 + 19 * i1 + 7, j3, true);
                    } else {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1, j + 14 + 19 * i1, 0, 185, 108, 19, 256, 256);
                        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_TABLE_GUI_TEXTURE, j1 + 1, j + 15 + 19 * i1, 16 * i1, 239, 16, 16, 256, 256);
                    }
                }
            }
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);
        boolean flag = this.minecraft.player.isCreative();
        int i = this.menu.getManuscriptAmount();

        for (int j = 0; j < 3; ++j) {
            int k = 1;
            EnumBestiaryPages enchantment = this.menu.getPossiblePages()[j];
            int i1 = 3;

            if (this.isHovering(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0) {
                List<FormattedCharSequence> list = Lists.newArrayList();

                if (enchantment == null) {
                    list.add(Component.literal(ChatFormatting.RED + I18n.get("container.lectern.no_bestiary")).getVisualOrderText());
                } else if (!flag) {
                    list.add(Component.literal("" + ChatFormatting.WHITE + ChatFormatting.ITALIC + I18n.get(enchantment == null ? "" : "bestiary." + enchantment.name().toLowerCase())).getVisualOrderText());
                    ChatFormatting textformatting = i >= i1 ? ChatFormatting.GRAY : ChatFormatting.RED;
                    list.add(Component.literal(textformatting + "" + I18n.get("container.lectern.costs")).getVisualOrderText());
                    String s = I18n.get("container.lectern.manuscript.many", i1);
                    list.add(Component.literal(textformatting + "" + s).getVisualOrderText());
                }

                graphics.setTooltipForNextFrame(list, mouseX, mouseY);
                break;
            }
        }
    }

    public void tickBook() {
        ItemStack itemstack = this.menu.getSlot(0).getItem();

        if (!ItemStack.matches(itemstack, this.last)) {
            this.last = itemstack;

            while (true) {
                this.flipT += this.random.nextInt(4) - this.random.nextInt(4);

                if (this.flip > this.flipT + 1.0F || this.flip < this.flipT - 1.0F) {
                    break;
                }
            }
        }

        ++this.ticks;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean flag = false;

        for (int i = 0; i < 3; ++i) {
            if (this.menu.getPossiblePages()[i] != null) {
                flag = true;
            }
        }

        if (flag) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float f1 = (this.flipT - this.flip) * 0.4F;
        if (flapTimer > 0) {
            f1 = (ticks + this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false)) * 0.5F;
            flapTimer--;
        }
        f1 = Mth.clamp(f1, -0.2F, 0.2F);
        this.flipA += (f1 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }
}
