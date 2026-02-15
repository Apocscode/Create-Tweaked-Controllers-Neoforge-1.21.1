package com.getitemfromblock.create_tweaked_controllers.packet;

import java.util.Locale;

import com.getitemfromblock.create_tweaked_controllers.CreateTweakedControllers;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum ModPackets implements BasePacketPayload.PacketTypeProvider
{
    TWEAKED_LINKED_CONTROLLER_INPUT(TweakedLinkedControllerButtonPacket.class, TweakedLinkedControllerButtonPacket.STREAM_CODEC),
    TWEAKED_LINKED_CONTROLLER_INPUT_AXIS(TweakedLinkedControllerAxisPacket.class, TweakedLinkedControllerAxisPacket.STREAM_CODEC),
    TWEAKED_LINKED_CONTROLLER_BIND(TweakedLinkedControllerBindPacket.class, TweakedLinkedControllerBindPacket.STREAM_CODEC),
    TWEAKED_LINKED_CONTROLLER_USE_LECTERN(TweakedLinkedControllerStopLecternPacket.class, TweakedLinkedControllerStopLecternPacket.STREAM_CODEC),
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> ModPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
            new CustomPacketPayload.Type<>(CreateTweakedControllers.asResource(name)),
            clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType()
    {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void registerPackets()
    {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreateTweakedControllers.ID, "1.21.1-1.3.0");
        for (ModPackets packet : ModPackets.values())
        {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
