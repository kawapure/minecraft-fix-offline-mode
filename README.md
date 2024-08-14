# Fix Minecraft Offline Mode

This mod fixes a bug with Minecraft's offline mode. Basically, in vanilla Minecraft, if you start offline mode with a different username than what the game expects you to have, then singleplayer mode is quite broken (multiplayer will generally work fine though).

The following issues are fixed by this mod:
- Worlds owned by the offline player not saving
- Disconnection of both player upon the disconnection of one in certain multiplayer scenarios
- Commands not working in singleplayer

The issues seem to come from Minecraft expecting your usual online-mode username in some cases instead of the offline-mode username that you had changed to. I think it's entirely related to the authentication server and `MinecraftClient.getGameProfile()`.

## Notice

This mod should only be installed on clients that you expect to run in offline mode, at the moment. I'm not sure how to detect offline mode at the moment, but I intend on cleaning this up when possible.