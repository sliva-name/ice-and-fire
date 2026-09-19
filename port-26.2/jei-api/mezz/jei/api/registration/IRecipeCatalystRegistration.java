package mezz.jei.api.registration;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IRecipeCatalystRegistration {
    void addRecipeCatalyst(ItemStack stack, Identifier... recipeCategoryUids);

    List<Catalyst> getCatalysts();

    record Catalyst(ItemStack stack, List<Identifier> categories) {
    }

    final class Recording implements IRecipeCatalystRegistration {
        private final List<Catalyst> catalysts = new ArrayList<>();

        @Override
        public void addRecipeCatalyst(ItemStack stack, Identifier... recipeCategoryUids) {
            catalysts.add(new Catalyst(stack, List.of(recipeCategoryUids)));
        }

        @Override
        public List<Catalyst> getCatalysts() {
            return List.copyOf(catalysts);
        }
    }
}
