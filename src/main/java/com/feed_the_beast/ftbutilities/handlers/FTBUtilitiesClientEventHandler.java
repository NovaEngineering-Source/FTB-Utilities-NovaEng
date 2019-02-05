package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import com.feed_the_beast.ftbutilities.gui.GuiClaimedChunks;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import com.feed_the_beast.ftbutilities.net.MessageLeaderboardList;
import com.feed_the_beast.ftbutilities.net.MessageRequestBadge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
public class FTBUtilitiesClientEventHandler
{
	private static final Map<UUID, Icon> BADGE_CACHE = new HashMap<>();
	public static int currentBackupFile = 0;
	public static int totalBackupFiles = 0;
	public static long shutdownTime = 0L;

	public static void readSyncData(NBTTagCompound nbt)
	{
		shutdownTime = System.currentTimeMillis() + nbt.getLong("ShutdownTime");
	}

	public static Icon getBadge(UUID id)
	{
		Icon tex = BADGE_CACHE.get(id);

		if (tex == null)
		{
			tex = Icon.EMPTY;
			BADGE_CACHE.put(id, tex);
			new MessageRequestBadge(id).sendToServer();
		}

		return tex;
	}

	public static void setBadge(UUID id, String url)
	{
		BADGE_CACHE.put(id, Icon.getIcon(url));
	}

	@SubscribeEvent
	public static void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		BADGE_CACHE.clear();
		currentBackupFile = 0;
		totalBackupFiles = 0;
		shutdownTime = 0L;
	}

	@SubscribeEvent
	public static void onDebugInfoEvent(RenderGameOverlayEvent.Text event)
	{
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
		{
			return;
		}

		if (shutdownTime > 0L && FTBUtilitiesClientConfig.general.show_shutdown_timer)
		{
			long timeLeft = Math.max(0L, shutdownTime - System.currentTimeMillis());

			if (timeLeft > 0L && timeLeft <= FTBUtilitiesClientConfig.general.getShowShutdownTimer())
			{
				event.getLeft().add(TextFormatting.DARK_RED + I18n.format("ftbutilities.lang.timer.shutdown", StringUtils.getTimeString(timeLeft)));
			}
		}

		if (totalBackupFiles > 0 && totalBackupFiles > currentBackupFile && FTBUtilitiesClientConfig.general.show_backup_progress)
		{
			event.getLeft().add(TextFormatting.LIGHT_PURPLE + I18n.format("ftbutilities.lang.timer.backup_progress", currentBackupFile * 100 / totalBackupFiles, currentBackupFile, totalBackupFiles));
		}
	}

	@SubscribeEvent
	public static void onKeyEvent(InputEvent.KeyInputEvent event)
	{
		if (FTBUtilitiesClient.KEY_NBT.isPressed())
		{
			MessageEditNBTRequest.editNBT();
		}
	}

	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getNamespace().equals(FTBUtilities.MOD_ID))
		{
			switch (event.getID().getPath())
			{
				case "toggle_gamemode":
					ClientUtils.execClientCommand("/gamemode " + (Minecraft.getMinecraft().player.capabilities.isCreativeMode ? "survival" : "creative"));
					break;
				case "daytime":
					ClientUtils.execClientCommand("/time add " + (24000L - (Minecraft.getMinecraft().world.getWorldTime() % 24000L) + 6000) % 24000L);
					break;
				case "nighttime":
					ClientUtils.execClientCommand("/time add " + (24000L - (Minecraft.getMinecraft().world.getWorldTime() % 24000L) + 18000) % 24000L);
					break;
				case "claims_gui":
					GuiClaimedChunks.instance = new GuiClaimedChunks();
					GuiClaimedChunks.instance.openGui();
					break;
				case "leaderboards_gui":
					new MessageLeaderboardList().sendToServer();
					break;
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onClientWorldTick(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.START && mc.world != null && mc.world.provider.getDimension() == FTBUtilitiesConfig.world.spawn_dimension)
		{
			if (FTBUtilitiesConfig.world.forced_spawn_dimension_time != -1)
			{
				mc.world.setWorldTime(FTBUtilitiesConfig.world.forced_spawn_dimension_time);
			}

			if (FTBUtilitiesConfig.world.forced_spawn_dimension_weather != -1)
			{
				mc.world.getWorldInfo().setRaining(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 1);
				mc.world.getWorldInfo().setThundering(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 2);
			}
		}
	}
}