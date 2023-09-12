![VOXEL THING](doc/logo.png)
## Your generic barebones clone of a really popular block game
Nothing much here yet, huh? What will this be? Well, it is heading towards being some sort of standalone version of [Diverge](https://github.com/BlueStaggo/MCDiverge), a mod created by the same author as this random, uhh, voxel thing. This game might feature furry content, like how [DangerZone](https://orespawn.com) has anti-vax content, who knows? Voxel Thing will feature multiplayer and it will be free to play if it turns into a full on game.

## Controls
- WASD: Fly around
- Move mouse: Look around
- Space: Jump
- Double Space: Fly
- Shift: Fly down
- 1-9: Select block
- Left click: Place block
- Right click: Break block
- E: Inventory
- Q: No-clip
- R: Teleport to a random position
- F: Halve fog distance
- Shift + F: Double fog distance
- F1: Toggle GUI
- F3: Debug menu
- F4: Toggle view bobbing
- F5: Third person
- F6: Change skin
- Escape: Lock / unlock mouse
- ]: Increase GUI scale
- [: Decrease GUI scale

## Play
There are two ways to play Voxel Thing:
- Download [the latest release](https://github.com/BlueStaggo/VoxelThing/releases/latest) for your platform.
- Get the latest cross-platform artifact from the latest commit [here](https://github.com/BlueStaggo/VoxelThing/actions).

From there, it is as simple as running the jar! Java 17 or later is required to run Voxel Thing.

## Compiling
To compile the Voxel Thing client, simply run `./gradlew :client:build`. To create a jar with libraries for your current platform, run `./gradlew :client:shadowJar`. To switch to a different platform, change the `voxelthing.client.platform` option in [gradle.properties](/gradle.properties) to any platform that LWJGL supports or set it to `all` to allow all platforms in a single jar.

## Contribution
Feel free to contribute to Voxel Thing! Pull requests from forks can be made, as long as they don't change the game too much like adding in completely new blocks or items. Pull requests for objective improvements such as improved performance, documentation fixes and code cleanup are welcome.

## Credits
![STAGGO: Lead developer](doc/credits/staggo.png)
![FLOOF: Densigner](doc/credits/floof.png)

[//]: # (![TALON: Just a cool guy ig]&#40;doc/credits/talon.png&#41;)
