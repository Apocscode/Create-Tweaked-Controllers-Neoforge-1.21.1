package com.getitemfromblock.create_tweaked_controllers.packet;


import com.getitemfromblock.create_tweaked_controllers.block.TweakedLecternControllerBlockEntity;
import com.getitemfromblock.create_tweaked_controllers.item.TweakedLinkedControllerItem;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TweakedLinkedControllerBindPacket extends TweakedLinkedControllerPacketBase
{
    public static final StreamCodec<FriendlyByteBuf, TweakedLinkedControllerBindPacket> STREAM_CODEC =
        StreamCodec.ofMember(TweakedLinkedControllerBindPacket::toBytes, TweakedLinkedControllerBindPacket::new);

    private int button;
    private BlockPos linkLocation;

    public TweakedLinkedControllerBindPacket(int button, BlockPos linkLocation)
    {
        super((BlockPos) null);
        this.button = button;
        this.linkLocation = linkLocation;
    }

    public TweakedLinkedControllerBindPacket(FriendlyByteBuf buffer)
    {
        super(buffer);
        this.button = buffer.readVarInt();
        this.linkLocation = buffer.readBlockPos();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer)
    {
        super.toBytes(buffer);
        buffer.writeVarInt(button);
        buffer.writeBlockPos(linkLocation);
    }

    @Override
    public BasePacketPayload.PacketTypeProvider getTypeProvider()
    {
        return ModPackets.TWEAKED_LINKED_CONTROLLER_BIND;
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem)
    {
        if (player.isSpectator())
            return;

        ItemStackHandler frequencyItems = TweakedLinkedControllerItem.getFrequencyItems(heldItem);
        LinkBehaviour linkBehaviour = BlockEntityBehaviour.get(player.level(), linkLocation, LinkBehaviour.TYPE);
        if (linkBehaviour == null)
            return;

        linkBehaviour.getNetworkKey()
            .forEachWithContext((f, first) -> frequencyItems.setStackInSlot(button * 2 + (first ? 0 : 1), f.getStack()
                .copy()));

        // TODO: Port to DataComponents system - heldItem.getTag() is removed in 1.21
        // heldItem.getTag().put("Items", frequencyItems.serializeNBT());
    }

    @Override
    protected void handleLectern(ServerPlayer player, TweakedLecternControllerBlockEntity lectern) {}

}
