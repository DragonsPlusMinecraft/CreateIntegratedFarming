package plus.dragons.createintegratedfarming.integration.mynethersdelight.farming.harvest;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCannonBlock;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class PowderyCannonArmInteractionPoint extends ArmInteractionPoint {
    public PowderyCannonArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public Mode getMode() {
        return Mode.TAKE;
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        BlockState state = level.getBlockState(pos);
        if((state.getBlock() instanceof PowderyCaneBlock || state.getBlock() instanceof PowderyCannonBlock) && state.getValue(BlockStateProperties.LIT)) {
            if(!simulate) {
                state = state.setValue(BlockStateProperties.LIT, false);
                level.setBlockAndUpdate(pos, state);
            }
            int j = 1 + level.random.nextInt(2);
            return new ItemStack(MNDItems.BULLET_PEPPER.get(),j);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 1;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof PowderyCaneBlock || state.getBlock() instanceof PowderyCannonBlock;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new PowderyCannonArmInteractionPoint(this, level, pos, state);
        }
    }
}
