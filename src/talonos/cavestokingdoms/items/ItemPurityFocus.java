package talonos.cavestokingdoms.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import talonos.cavestokingdoms.lib.DEFS;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintVillager;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPurityFocus extends ItemFocusBasic
{
	private String name = "purityFocus";

	public ItemPurityFocus() 
	{
		setUnlocalizedName(DEFS.MODID + "_" + name );
		GameRegistry.registerItem(this, name);
		setCreativeTab(Thaumcraft.tabTC);
		setTextureName(DEFS.MODID + ":" + name);
	}

	public String getSortingHelper(ItemStack itemstack)
	{
		return "BE" + super.getSortingHelper(itemstack);
	}

	private static final AspectList cost = new AspectList().add(Aspect.EARTH, 100).add(Aspect.ORDER, 150);

	public boolean isVisCostPerTick()
	{
		return false;
	}

	public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop)
	{
		ItemWandCasting wand = (ItemWandCasting)itemstack.getItem();
		int potency = wand.getFocusEnlarge(itemstack);

		if (!wand.consumeAllVis(itemstack, p, getBigVisCost(potency), false, false))
		{
			return itemstack;
		}
		
		Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.0D, 32.0D, 32.0F);
	    if (pointedEntity != null && !world.isRemote)
	    {
	    	//if target is a tainted sheep, convert to sheep
			if (pointedEntity instanceof EntityTaintSheep)
			{
				EntityTaintSheep entityTaintSheep = (EntityTaintSheep)pointedEntity;
				EntitySheep entitysheep = new EntitySheep(entityTaintSheep.worldObj);
				convertFromTo(entityTaintSheep, entitysheep);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}

			//if target is a tainted cow, convert to cow
			if (pointedEntity instanceof EntityTaintCow)
			{
				EntityTaintCow entityTaintCow = (EntityTaintCow)pointedEntity;
				EntityCow entityCow = new EntityCow(entityTaintCow.worldObj);
				convertFromTo(entityTaintCow, entityCow);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}

			//if target is a tainted chicken, convert to chicken
			if (pointedEntity instanceof EntityTaintChicken)
			{
				EntityTaintChicken entityTaintChicken = (EntityTaintChicken)pointedEntity;
				EntityChicken chicken = new EntityChicken(entityTaintChicken.worldObj);
				convertFromTo(entityTaintChicken, chicken);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}
				
			//if target is a tainted villager, convert to villager
			if (pointedEntity instanceof EntityTaintVillager)
			{
				EntityTaintVillager entityTaintVillager = (EntityTaintVillager)pointedEntity;
				EntityVillager villager = new EntityVillager(entityTaintVillager.worldObj);
				convertFromTo(entityTaintVillager, villager);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}

			//if target is a tainted pig, convert to pig
			if (pointedEntity instanceof EntityTaintPig)
			{
				EntityTaintPig entityTaintPig = (EntityTaintPig)pointedEntity;
				EntityPig pig = new EntityPig(entityTaintPig.worldObj);
				convertFromTo(entityTaintPig, pig);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}
			//if target is a tainted creeper, convert to creeper
			if (pointedEntity instanceof EntityTaintCreeper)
			{
				EntityTaintCreeper entityTaintCreeper = (EntityTaintCreeper)pointedEntity;
				EntityCreeper creeper  = new EntityCreeper(entityTaintCreeper.worldObj);
				convertFromTo(entityTaintCreeper, creeper);
				wand.consumeAllVis(itemstack, p, getVisCost(itemstack), true, false);
			}
			return itemstack;
	    }
		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			for (int xOffset = 0 - potency; xOffset < 1+potency; xOffset++)
			{
				for (int zOffset = 0 - potency; zOffset < 1+potency; zOffset++)
				{
					cleanUpLand(mop.blockX+xOffset, mop.blockZ+zOffset, world, itemstack, p);
				}
			}
	    }
		return itemstack;
	}
	
	/*
	 * converts original entity to convertTo entity and subtracts one from player held item stack
	 * requires the following lines before calling (replace EntityConvertTo with the convertTo entity and EntityOriginal with original entity):
	 * EntityOriginal original = (EntityOriginal)pointedEntity;
	 * EntityConvertTo convertTo = new EntityConvertTo(original.worldObj);
	 */
	public void convertFromTo(Entity original, EntityLivingBase convertTo)
	{
		//new entity copies original entity location
		convertTo.copyLocationAndAnglesFrom(original);
		//original entity spawns new entity into the world
		if (!original.worldObj.isRemote)
		{
			original.worldObj.spawnEntityInWorld(convertTo);
		}
		//new entity removes the old entity
		convertTo.worldObj.removeEntity(original);
	}

	private AspectList getBigVisCost(int potency) 
	{
		AspectList toReturn = new AspectList().add(Aspect.EARTH, 100*(potency*2+1)*(potency*2+1)).add(Aspect.ORDER, 150*(potency*2+1)*(potency*2+1));
		return toReturn;
	}

	private void cleanUpLand(int x, int z, World world, ItemStack itemStack, EntityPlayer p) 
	{
		if(!p.worldObj.isRemote)
		{
			if (((world.getBiomeGenForCoords(x, z).biomeID == Config.biomeTaintID) || 
				 (world.getBiomeGenForCoords(x, z).biomeID == Config.biomeEerieID) || 
				 (world.getBiomeGenForCoords(x, z).biomeID == Config.biomeMagicalForestID)))
			{
				ItemWandCasting wand = (ItemWandCasting)itemStack.getItem();
				wand.consumeAllVis(itemStack, p, getVisCost(itemStack), true, false);
				BiomeGenBase[] biomesForGeneration = null;
				biomesForGeneration = world.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, x, z, 1, 1);
				if ((biomesForGeneration != null) && (biomesForGeneration[0] != null)) 
				{
					Utils.setBiomeAt(world, x, z, biomesForGeneration[0]);
				}
			}
		}
		for (int y = 0; y < 256; y++)
		{
			if (world.getBlock(x, y, z)==ConfigBlocks.blockTaintFibres)
			{
				world.setBlock(x, y, z, Blocks.air);
			}
			if (world.getBlock(x, y, z)==ConfigBlocks.blockTaint)
			{
				if (world.getBlockMetadata(x, y, z)==0)
				{
					world.setBlock(x, y, z, ConfigBlocks.blockFluxGoo, ((BlockFluxGoo)ConfigBlocks.blockFluxGoo).getQuanta(), 3);
				}
				else if (world.getBlockMetadata(x, y, z)==1)
				{
					world.setBlock(x, y, z, Blocks.dirt);
				}
				else
				{
					world.setBlock(x, y, z, Blocks.air);
				}
			}
		}
	}
	
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(this.getIconString());
        this.icon = register.registerIcon(this.getIconString());
    }

	@Override
	public int getFocusColor(ItemStack arg0) 
	{
		return 0x32f8c5;
	}

	public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank)
	{
	    switch (rank)
	    {
	    case 1: 
	        return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
	    case 2: 
	        return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
	    case 3: 
	        return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
	    case 4: 
	        return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
	    case 5: 
            return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
	    }
	    return null;
	}

	@Override
	public AspectList getVisCost(ItemStack arg0) 
	{
		return cost;
	}
}