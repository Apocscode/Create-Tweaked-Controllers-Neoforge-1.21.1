package com.getitemfromblock.create_tweaked_controllers.item;

import com.getitemfromblock.create_tweaked_controllers.block.ModBlocks;
import com.getitemfromblock.create_tweaked_controllers.controller.TweakedLinkedControllerClientHandler;
import com.getitemfromblock.create_tweaked_controllers.controller.TweakedLinkedControllerMenu;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.platform.CatnipServices;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TweakedLinkedControllerItem extends Item implements MenuProvider
{

    public TweakedLinkedControllerItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
    {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);

        if (player.mayBuild())
        {
            if (player.isShiftKeyDown())
            {
                if (ModBlocks.TWEAKED_LECTERN_CONTROLLER.has(hitState))
                {
                    if (!world.isClientSide)
                        ModBlocks.TWEAKED_LECTERN_CONTROLLER.get().withBlockEntityDo(world, pos, be ->
                                be.swapControllers(stack, player, ctx.getHand(), hitState));
                    return InteractionResult.SUCCESS;
                }
            }
            else
            {
                if (AllBlocks.REDSTONE_LINK.has(hitState))
                {
                    if (world.isClientSide)
                        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.toggleBindMode(ctx.getClickedPos()));
                    player.getCooldowns()
                            .addCooldown(this, 2);
                    return InteractionResult.SUCCESS;
                }

                if (hitState.is(Blocks.LECTERN) && !hitState.getValue(LecternBlock.HAS_BOOK))
                {
                    if (!world.isClientSide)
                    {
                        ItemStack lecternStack = player.isCreative() ? stack.copy() : stack.split(1);
                        ModBlocks.TWEAKED_LECTERN_CONTROLLER.get().replaceLectern(hitState, world, pos, lecternStack);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (ModBlocks.TWEAKED_LECTERN_CONTROLLER.has(hitState))
                    return InteractionResult.PASS;
            }
        }

        return use(world, player, ctx.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack heldItem = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND)
        {
            if (!world.isClientSide && player instanceof ServerPlayer sp && player.mayBuild())
                sp.openMenu(this, buf -> {
                    ItemStack.STREAM_CODEC.encode(buf, heldItem);
                });
            return InteractionResultHolder.success(heldItem);
        }

        if (!player.isShiftKeyDown())
        {
            if (world.isClientSide)
                CatnipServices.PLATFORM.executeOnClientOnly(() -> this::toggleActive);
            player.getCooldowns()
                .addCooldown(this, 2);
        }

        return InteractionResultHolder.pass(heldItem);
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleBindMode(BlockPos pos)
    {
        TweakedLinkedControllerClientHandler.toggleBindMode(pos);
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleActive()
    {
        TweakedLinkedControllerClientHandler.toggle();
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack, HolderLookup.Provider registries)
    {
        ItemStackHandler newInv = new ItemStackHandler(50);
        if (ModItems.TWEAKED_LINKED_CONTROLLER.get() != stack.getItem())
            throw new IllegalArgumentException("Cannot get frequency items from non-controller: " + stack);
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag invNBT = customData.copyTag().getCompound("Items");
        if (!invNBT.isEmpty())
            newInv.deserializeNBT(registries, invNBT);
        return newInv;
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack)
    {
        return getFrequencyItems(stack, net.minecraft.core.RegistryAccess.EMPTY);
    }

    public static Couple<Frequency> toFrequency(ItemStack controller, int slot, HolderLookup.Provider registries)
    {
        ItemStackHandler frequencyItems = getFrequencyItems(controller, registries);
        return Couple.create(Frequency.of(frequencyItems.getStackInSlot(slot * 2)),
            Frequency.of(frequencyItems.getStackInSlot(slot * 2 + 1)));
    }

    public static Couple<Frequency> toFrequency(ItemStack controller, int slot)
    {
        ItemStackHandler frequencyItems = getFrequencyItems(controller);
        return Couple.create(Frequency.of(frequencyItems.getStackInSlot(slot * 2)),
            Frequency.of(frequencyItems.getStackInSlot(slot * 2 + 1)));
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player)
    {
        ItemStack heldItem = player.getMainHandItem();
        return TweakedLinkedControllerMenu.create(id, inv, heldItem);
    }

    @Override
    public Component getDisplayName()
    {
        return getDescription();
    }

    // TODO: In 1.21, custom item rendering is registered via RegisterClientExtensionsEvent
    // or Registrate's .customRenderer() instead of initializeClient/IClientItemExtensions

}
