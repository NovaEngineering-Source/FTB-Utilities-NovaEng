package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import com.feed_the_beast.ftbu.JourneyMapIntegration;
import com.feed_the_beast.ftbu.client.CachedClientData;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class MessageUpdateJM extends MessageToClient<MessageUpdateJM>
{

    private Map<ChunkPos, CachedClientData.ChunkData> types;

    public MessageUpdateJM()
    {
    }

    public MessageUpdateJM(int x, int z, int d, int sx, int sz)
    {
        types = new HashMap<>();

        for(int x1 = x; x1 < x + sx; x1++)
        {
            for(int z1 = z; z1 < z + sz; z1++)
            {
                //ChunkDimPos pos = new ChunkDimPos(x1, z1, d);
                //types.put(pos, FTBUWorldData.chunks.getChunk(pos));
            }
        }
    }

    @Override
    public LMNetworkWrapper getWrapper()
    {
        return FTBUNetHandler.NET;
    }

    @Override
    public void fromBytes(ByteBuf io)
    {
        /*
        dim = io.readInt();
        types = new HashMap<>();
        
        int s = io.readInt();

        for(int i = 0; i < s; i++)
        {
            byte flags = io.readByte();
            int x = io.readInt();
            int z = io.readInt();
            
            ChunkDimPos pos = new ChunkDimPos(x, z, dim);
            boolean b = io.readBoolean();

            if(b)
            {
                IForgePlayer owner = null;//FIXME: FTBLibAPI.INSTANCE.getWorld() == null ? null : ForgeWorldSP.inst.getPlayer(LMNetUtils.readUUID(io));

                if(owner != null)
                {
                    ClaimedChunk chunk = new ClaimedChunk(owner, pos);
                    chunk.setLoaded(io.readBoolean());
                    types.put(pos, chunk);
                }
            }
            else
            {
                types.put(pos, null);
            }
        }
        */
    }

    @Override
    public void toBytes(ByteBuf io)
    {
        /*
        io.writeInt(dim);
        io.writeInt(types.size());

        for(Map.Entry<ChunkDimPos, IClaimedChunk> e : types.entrySet())
        {
            io.writeInt(e.getKey().posX);
            io.writeInt(e.getKey().posZ);

            if(e.getValue() == null)
            {
                io.writeBoolean(false);
            }
            else
            {
                io.writeBoolean(true);
                LMNetUtils.writeUUID(io, e.getValue().getOwner().getProfile().getId());
                io.writeBoolean(e.getValue().isLoaded());
            }
        }
        */
    }

    @Override
    public void onMessage(MessageUpdateJM m)
    {
        if(JourneyMapIntegration.INST != null)
        {
            for(Map.Entry<ChunkPos, CachedClientData.ChunkData> e : m.types.entrySet())
            {
                ChunkPos pos = e.getKey();
                CachedClientData.ChunkData chunk = e.getValue();

                JourneyMapIntegration.INST.chunkChanged(pos, chunk);
            }
        }
    }
}