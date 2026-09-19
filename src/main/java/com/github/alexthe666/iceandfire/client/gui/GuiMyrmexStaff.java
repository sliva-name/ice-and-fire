package com.github.alexthe666.iceandfire.client.gui;

import net.minecraft.network.chat.Component;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.ClientProxy;
import com.github.alexthe666.iceandfire.client.gui.bestiary.ChangePageButton;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageGetMyrmexHive;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiMyrmexStaff extends Screen {
    private static final Identifier JUNGLE_TEXTURE = Identifier.parse("iceandfire:textures/gui/myrmex_staff_jungle.png");
    private static final Identifier DESERT_TEXTURE = Identifier.parse("iceandfire:textures/gui/myrmex_staff_desert.png");
    private static final WorldGenMyrmexHive.RoomType[] ROOMS = {WorldGenMyrmexHive.RoomType.FOOD, WorldGenMyrmexHive.RoomType.NURSERY, WorldGenMyrmexHive.RoomType.EMPTY};
    private static final int ROOMS_PER_PAGE = 5;
    private final List<Room> allRoomPos = Lists.newArrayList();
    private final List<MyrmexDeleteButton> allRoomButtonPos = Lists.newArrayList();
    public ChangePageButton previousPage;
    public ChangePageButton nextPage;
    int ticksSinceDeleted = 0;
    int currentPage = 0;
    private final boolean jungle;
    private int hiveCount;

    public GuiMyrmexStaff(ItemStack staff) {
        super(Component.translatable("myrmex_staff_screen"));
        this.jungle = staff.getItem() == IafItemRegistry.MYRMEX_JUNGLE_STAFF.get();
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.allRoomButtonPos.clear();
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        int x_translate = 193;
        int y_translate = 37;
        if (ClientProxy.getReferedClientHive() == null) {
            return;
        }
        populateRoomMap();
        this.addRenderableWidget(Button.builder(ClientProxy.getReferedClientHive().reproduces ? Component.translatable("myrmex.message.disablebreeding") : Component.translatable("myrmex.message.enablebreeding"), (p_214132_1_) -> {
            boolean opposite = !ClientProxy.getReferedClientHive().reproduces;
            ClientProxy.getReferedClientHive().reproduces = opposite;
        }).bounds(i + 124, j + 15, 120, 20).build());
        this.addRenderableWidget(
            this.previousPage = new ChangePageButton(i + 5, j + 150, false, this.jungle ? 2 : 1, (p_214132_1_) -> {
                if (this.currentPage > 0) {
                    this.currentPage--;
                }
            }));
        this.addRenderableWidget(
            this.nextPage = new ChangePageButton(i + 225, j + 150, true, this.jungle ? 2 : 1, (p_214132_1_) -> {
                if (this.currentPage < this.allRoomButtonPos.size() / ROOMS_PER_PAGE) {
                    this.currentPage++;
                }
            }));
        int totalRooms = allRoomPos.size();
        for (int rooms = 0; rooms < allRoomPos.size(); rooms++) {
            int yIndex = rooms % ROOMS_PER_PAGE;
            BlockPos pos = allRoomPos.get(rooms).pos;
            MyrmexDeleteButton button = new MyrmexDeleteButton(i + x_translate, j + y_translate + (yIndex) * 22, pos, Component.translatable("myrmex.message.delete"), (p_214132_1_) -> {
                if (ticksSinceDeleted <= 0) {
                    ClientProxy.getReferedClientHive().removeRoom(pos);
                    ticksSinceDeleted = 5;
                }
            });
            button.visible = rooms < ROOMS_PER_PAGE * (this.currentPage + 1) && rooms >= ROOMS_PER_PAGE * this.currentPage;
            this.addRenderableWidget(button);
            this.allRoomButtonPos.add(button);
        }
        if (totalRooms <= ROOMS_PER_PAGE * (this.currentPage) && this.currentPage > 0) {
            this.currentPage--;
        }
    }

    private void populateRoomMap() {
        allRoomPos.clear();

        for (WorldGenMyrmexHive.RoomType type : ROOMS) {
            List<BlockPos> roomPos = ClientProxy.getReferedClientHive().getRooms(type);
            for (BlockPos pos : roomPos) {
                String name = type == WorldGenMyrmexHive.RoomType.FOOD ? "food" : type == WorldGenMyrmexHive.RoomType.NURSERY ? "nursery" : "misc";
                allRoomPos.add(new Room(pos, name));
            }
        }
        for (BlockPos pos : ClientProxy.getReferedClientHive().getEntrances().keySet()) {
            allRoomPos.add(new Room(pos, "enterance_surface"));
        }
        for (BlockPos pos : ClientProxy.getReferedClientHive().getEntranceBottoms().keySet()) {
            allRoomPos.add(new Room(pos, "enterance_bottom"));
        }
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, jungle ? JUNGLE_TEXTURE : DESERT_TEXTURE, i, j, 0, 0, 248, 166, 256, 256);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        init();
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        int color = this.jungle ? 0X35EA15 : 0XFFBF00;
        if (ticksSinceDeleted > 0) {
            ticksSinceDeleted--;
        }
        hiveCount = 0;
        for (int rooms = 0; rooms < this.allRoomButtonPos.size(); rooms++) {
            if (rooms < ROOMS_PER_PAGE * (this.currentPage + 1) && rooms >= ROOMS_PER_PAGE * this.currentPage) {
                this.drawRoomInfo(graphics, this.allRoomPos.get(rooms).string, this.allRoomPos.get(rooms).pos, i, j, color);
            }
        }
        if (ClientProxy.getReferedClientHive() != null) {
            if (!ClientProxy.getReferedClientHive().colonyName.isEmpty()) {
                String title = I18n.get("myrmex.message.colony_named", ClientProxy.getReferedClientHive().colonyName);
                graphics.text(this.font, title, i + 40 - title.length() / 2, j - 3, color);
            } else {
                graphics.text(this.font, I18n.get("myrmex.message.colony"), i + 80, j - 3, color);
            }
            int opinion = ClientProxy.getReferedClientHive().getPlayerReputation(Minecraft.getInstance().player.getUUID());
            graphics.text(this.font, I18n.get("myrmex.message.hive_opinion", opinion), i, j + 12, color);
            graphics.text(this.font, I18n.get("myrmex.message.rooms"), i, j + 25, color);
        }
    }

    @Override
    public void removed() {
        if (ClientProxy.getReferedClientHive() != null) {
            IceAndFire.sendMSGToServer(new MessageGetMyrmexHive(ClientProxy.getReferedClientHive().toNBT()));
        }
    }

    private void drawRoomInfo(GuiGraphicsExtractor graphics, String type, BlockPos pos, int i, int j, int color) {
        String translate = "myrmex.message.room." + type;
        graphics.text(this.font, I18n.get(translate, pos.getX(), pos.getY(), pos.getZ()), i, j + 36 + hiveCount * 22, color);
        hiveCount++;
    }

    private class Room {
        public BlockPos pos;
        public String string;

        public Room(BlockPos pos, String string) {
            this.pos = pos;
            this.string = string;
        }
    }
}
