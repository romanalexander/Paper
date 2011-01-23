package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerItemEvent;
// CraftBukkit end

public class ItemHoe extends Item {

    public ItemHoe(int i, EnumToolMaterial enumtoolmaterial) {
        super(i);
        bb = 1;
        bc = enumtoolmaterial.a();
    }

    public boolean a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l) {
        int i1 = world.a(i, j, k);
        Material material = world.c(i, j + 1, k);

        if (!material.a() && i1 == Block.u.bi || i1 == Block.v.bi) {
            // CraftBukkit start - Hoes
            CraftWorld craftWorld = ((WorldServer) world).getWorld();
            CraftServer craftServer = ((WorldServer) world).getServer();
            
            Type eventType = Type.PLAYER_ITEM;
            Player who = (entityplayer == null)?null:(Player)entityplayer.getBukkitEntity();
            org.bukkit.inventory.ItemStack itemInHand = new CraftItemStack(itemstack);
            org.bukkit.block.Block blockClicked = craftWorld.getBlockAt(i, j, k);
            BlockFace blockFace = CraftBlock.notchToBlockFace(1);
            
            PlayerItemEvent pie = new PlayerItemEvent(eventType, who, itemInHand, blockClicked, blockFace);
            craftServer.getPluginManager().callEvent(pie);
            
            if (pie.isCancelled()) {
                return false;
            }
            // CraftBukkit end

            Block block = Block.aA;

            world.a((float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F, block.br.c(), (block.br.a() + 1.0F) / 2.0F, block.br.b() * 0.8F);
            if (world.z) {
                return true;
            }
            world.e(i, j, k, block.bi);
            itemstack.b(1);
            if (world.l.nextInt(8) == 0 && i1 == Block.u.bi) {
                int j1 = 1;

                for (int k1 = 0; k1 < j1; k1++) {
                    float f = 0.7F;
                    float f1 = world.l.nextFloat() * f + (1.0F - f) * 0.5F;
                    float f2 = 1.2F;
                    float f3 = world.l.nextFloat() * f + (1.0F - f) * 0.5F;
                    EntityItem entityitem = new EntityItem(world, (float) i + f1, (float) j + f2, (float) k + f3, new ItemStack(Item.Q));

                    entityitem.c = 10;
                    world.a(((Entity) (entityitem)));
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
