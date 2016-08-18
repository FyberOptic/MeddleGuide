package net.fybertech.meddleguide;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class GuideCommand implements ICommand
{

	@Override
	public int compareTo(ICommand o) {
		return this.getCommandName().compareTo(o.getCommandName());
	}

	
	@Override
	public List<String> addTabCompletionOptions(MinecraftServer arg0, ICommandSender arg1, String[] arg2, BlockPos arg3) {
		return Collections.emptyList();
	}

	
	@Override
	public boolean canCommandSenderUseCommand(MinecraftServer server, ICommandSender sender) {
		//return sender.canCommandSenderUseCommand(1, this.getCommandName());
		return true;
	}

	
	@Override
	public List<String> getCommandAliases() {
		return Collections.emptyList();
	}

	
	@Override
	public String getCommandName() {
		return "recipe";
	}

	
	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "recipe [search]";
	}

	
	@Override
	public boolean isUsernameIndex(String[] arg0, int arg1) {
		return false;
	}
	

	@Override
	public void processCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if (sender.getCommandSenderEntity() instanceof EntityPlayer) {			
			((EntityPlayer)sender).displayGUIChest(new InventoryGuide("Recipe Guide", true, 9 * 6, args));			
		}
	}
	
}