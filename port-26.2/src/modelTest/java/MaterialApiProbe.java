import com.github.alexthe666.iceandfire.block.IafMaterials;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Compile-only probe of 26.1 block properties and the 1.18 Material helper. */
public final class MaterialApiProbe {
    static final BlockBehaviour.Properties METAL = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.0F, 5.0F).requiresCorrectToolForDrops();
    static final BlockBehaviour.Properties STONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5F, 10.0F);
    static final BlockBehaviour.Properties WOOD = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).ignitedByLava();
    static final BlockBehaviour.Properties DIRT = BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.GRAVEL).strength(0.5F);
    static final BlockBehaviour.Properties SAND = BlockBehaviour.Properties.of().mapColor(MapColor.SAND).sound(SoundType.SAND);
    static final BlockBehaviour.Properties ICE = BlockBehaviour.Properties.of().mapColor(MapColor.ICE).sound(SoundType.GLASS).friction(0.98F);
    static final BlockBehaviour.Properties PLANT = BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().instabreak().replaceable();
    static final BlockBehaviour.Properties GLASS = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).sound(SoundType.GLASS).noOcclusion();
    static final BlockBehaviour.Properties PORTAL = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollision().strength(-1.0F);

    static boolean classify(BlockState state) {
        return IafMaterials.isAir(state)
            || IafMaterials.isSolid(state)
            || IafMaterials.isLiquid(state)
            || IafMaterials.blocksMotion(state)
            || IafMaterials.isReplaceable(state)
            || IafMaterials.isWater(state)
            || IafMaterials.isFire(state)
            || IafMaterials.isSand(state)
            || IafMaterials.isDirt(state)
            || IafMaterials.isGrass(state)
            || IafMaterials.isStone(state)
            || IafMaterials.isWood(state)
            || IafMaterials.isLeaves(state)
            || IafMaterials.isPlant(state)
            || IafMaterials.isSnow(state)
            || IafMaterials.isIce(state)
            || IafMaterials.isIceSolid(state);
    }

    public static void main(String[] args) {
        System.out.println(METAL);
        System.out.println(MapColor.STONE);
        System.out.println("material helpers compiled");
    }
}
