import com.github.alexthe666.iceandfire.entity.util.IafItemListing;
import com.github.alexthe666.iceandfire.entity.util.IafOffers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public class IafOffersTest {
    public static void main(String[] args) {
        IafItemListing unused = (trader, random) -> null;
        if (unused.getClass() == null) {
            throw new AssertionError();
        }
        // Compile-check 1.18 offer arities against 26.1 ItemCost constructors.
        MerchantOffer one = IafOffers.of(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.BOOK, 4), 25, 2, 0.05F);
        MerchantOffer two = IafOffers.of(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.PAPER, 15), new ItemStack(Items.BOOK, 1), 4, 4, 0.05F);
        if (one.getMaxUses() != 25 || one.getXp() != 2) {
            throw new AssertionError("single-cost offer uses/xp");
        }
        if (two.getMaxUses() != 4 || two.getXp() != 4) {
            throw new AssertionError("dual-cost offer uses/xp");
        }
        System.out.println("IafOffersTest ok");
    }
}
