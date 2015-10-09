package net.fybertech.meddleguide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.IInteractionObject;

public class InventoryGuide extends InventoryBasic implements IInteractionObject
{
	String[] search = null;

	public InventoryGuide(String param0, boolean param1, int param2) {
		super(param0, param1, param2);
	}
	
	public InventoryGuide(String param0, boolean param1, int param2, String[] search) {
		super(param0, param1, param2);
		this.search = search;
	}

	@Override
	public Container createContainer(InventoryPlayer arg0, EntityPlayer arg1) {
		return new ContainerGuide(arg0, this, arg1, search);
	}

	@Override
	public String getGuiID() {
		return "minecraft:container";
	}

}
