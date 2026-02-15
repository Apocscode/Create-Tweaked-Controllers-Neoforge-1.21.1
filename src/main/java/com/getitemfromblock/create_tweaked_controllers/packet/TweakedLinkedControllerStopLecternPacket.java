package com.getitemfromblock.create_tweaked_controllers.packet;

import com.getitemfromblock.create_tweaked_controllers.block.TweakedLecternControllerBlockEntity;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class TweakedLinkedControllerStopLecternPacket extends TweakedLinkedControllerPacketBase
{
    public static final StreamCodec<FriendlyByteBuf, TweakedLinkedControllerStopLecternPacket> STREAM_CODEC =
        StreamCodec.ofMember(TweakedLinkedControllerStopLecternPacket::toBytes, TweakedLinkedControllerStopLecternPacket::new);

    public TweakedLinkedControllerStopLecternPacket(FriendlyByteBuf buffer)
    {
        super(buffer);
    }

    public TweakedLinkedControllerStopLecternPacket(BlockPos lecternPos)
    {
        super(lecternPos);
    }

    @Override
    public BasePacketPayload.PacketTypeProvider getTypeProvider()
    {
        return ModPackets.TWEAKED_LINKED_CONTROLLER_USE_LECTERN;
    }

    @Override
    protected void handleLectern(ServerPlayer player, TweakedLecternControllerBlockEntity lectern)
    {
        lectern.tryStopUsing(player);
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem)
    {
    }

}
