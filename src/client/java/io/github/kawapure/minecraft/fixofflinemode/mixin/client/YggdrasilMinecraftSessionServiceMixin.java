package io.github.kawapure.minecraft.fixofflinemode.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Hook the Yggdrasil session service to override the method for fetching the
 * profile.
 */
@Mixin(YggdrasilMinecraftSessionService.class)
public class YggdrasilMinecraftSessionServiceMixin
{
	private static final Logger LOGGER = LoggerFactory.getLogger("fix-offline-mode");

	/**
	 * Redirect profile fetching to report information from offline mode.
	 */
	@Inject(at = @At("HEAD"), method = "fetchProfile", cancellable = true, remap = false)
	private void fetchProfileHook(UUID profileId, boolean requireSecure, CallbackInfoReturnable<ProfileResult> info)
	{
		// The current session can be used to reliably retrieve the username of the
		// current offline player, but it still reports the wrong UUID.
		// If you use this same UUID, then same-account LAN multiplayer will fail,
		// reporting that the user is logged in at a different location.
		Session currentSession = MinecraftClient.getInstance().getSession();

		String username = currentSession.getUsername();

		// Generate the offline mode UUID like such:
		UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

		UUID onlineSessionUuid = currentSession.getUuidOrNull();
		if (onlineSessionUuid != null)
		{
			LOGGER.info("Current true session UUID is " + onlineSessionUuid);
		}

		LOGGER.info("Current offline session UUID is " + offlineUuid);

		// Compose the new GameProfile object with the offline session username and UUID.
		GameProfile newGameProfile = new GameProfile(
			offlineUuid, currentSession.getUsername()
		);

		ProfileResult result = new ProfileResult(newGameProfile);

		info.setReturnValue(result);
		info.cancel();
	}
}