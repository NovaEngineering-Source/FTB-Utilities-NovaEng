package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class CmdAdminDelWarp extends SubCommand
{
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		return null;
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		throw new FeatureDisabledException();
		/*
		 * checkArgs(args, 1);
			
			if(EnkiData.Warps.remWarp(args[0]))
				return FINE + "Warp '" + args[0] + "' removed!";
			return "Warp '" + args[0] + "' doesn't exist!";
		 */
	}
}