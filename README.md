# Simple CTF
This is a Spigot plugin for playing "Capture The Flag".

WARNING: This plugin is beta.  

## Download
### [Releaces](https://github.com/Seaoftrees08/SimpleCTF/releases) 
for Spigot 1.19.3

## License
### [MIT License](https://github.com/Seaoftrees/SimpleCTF/blob/master/LICENSE)

## Player Commands
All commands can use "/sctf" instead of "/simplectf"
### /simplectf
You can view command list.
- CanUse: Player, Console 
- Permission: none(Anybody can use this command)
### /simplectf join \<arena\>
You join \<arena\>  
- CanUse: Player  
- Permission: simplectf.play
### /simplectf leave
You leave from arena.  
- CanUse: Player
- Permission: none(Anybody can use this command)
### /simplectf list
You can view arena list.
- CanUse: Player, Console
- Permission: none(Anybody can use this command)
### /simplectf watch \<arena\>
You can watch \<arena\> game.  
- CanUse: Player
- Permission: none(Anybody can use this command)
### /simplectf back
You back to WorldSpawnPoint from watching game.  
- CanUse: Player
- Permission: none(Anybody can use this command)
### /simplectf version
You can view this plugin infomation.
- CanUse: Player, Console
- Permission: none(Anybody can use this command)
### /simplectf reset
You can Reset Scoreboard. (debug)
- CanUse: Player, Console
- Permission: none(Anybody can use this command)
### /simplectf start \<arena\>
You can countdown within 10 seconds remaining after countdown started
 - CanUse: Player, Console
 - Permission: simplectf.force
### /simplectf admin
Start creation arena process as arena name is \<arena\>
- CanUse: Player, Console
- Permission: none(Anybody can use this command)

## Admin Commands
All commands can use "/sctf" instead of "/simplectf"  
A Player who has "simplectf.admin" can use admin commands.
### /simplectf admin create \<arena\>
Start creation arena process as arena name is \<arena\>
- CanUse: Player
- Permission: simplectf.admin
### /simplectf admin remove \<arena\> 
Remove \<arena\>
- CanUse: Player
- Permission: simplectf.admin
### /simplectf admin setTeam
Set Inventory and SpawnLocation. 
You can use this command when you create arena.
- CanUse: Player
- Permission: simplectf.admin
### /simplectf admin enable \<arena\>
You enable \<arena\>
- CanUse: Player, Console
- Permission: simplectf.admin
### /simplectf admin disable \<arena\> 
You disable \<arena\>
- CanUse: Player, Console
- Permission: simplectf.admin
### /simplectf admin addCmd \<cmd\> 
You add usable cmd while playing the game.
- CanUse: Player, Console
- Permission: simplectf.admin
### /simplectf admin rmCmd \<cmd\> 
You remove usable cmd while playing the game.
- CanUse: Player, Console
- Permission: simplectf.admin
### /simplectf admin cmdList
You can view usable all cmds while playing the game.
- CanUse: Player, Console
- Permission: simplectf.admin

## Permissions
### simplectf.*
Give access to all SimpleCTF commands
- childlen
  - simplectf.admin
  - simplectf.rate
  - simplectf.simplectf
  - simplectf.play
  - simplectf.force
### simplectf.admin
Allows to control SimpleCTF
- Default: op
### simplectf.simplectf
Allows to control commands
- Default: true
### simplectf.play
Allows to play ctf
- Default: true
### simplectf.force
Allows to force playing ctf
- Default: op

## version info
- 1.0.0
 stable version.(some bugs fixed from 0.4.0)
- 0.4.0
  totally from scratch for mc1.19.3 (some function unimplemented) again.
- 0.3.0
  totally from scratch for mc1.19.3 (some function unimplemented)
- 0.2.1
  bug fixed from 0.2.0(fixed get flag when any item) for Spigot1.8.8
- 0.2.0
  add function and bugfixed from 0.1.5 for Spigot1.8.8
  - fatal bug fixed (item throw away bug)
  - fatal bug fixed (Cannot throw FinishCTF.java)
  - bug fixed (item clear when player return on the way)
  - specification change(cannot throw away other than flag)
  - specification change(item clear when player join arena)
  - add cmds
  - add function(watch game)
- 0.1.5  
  bug fixed from 0.1.4(fixed not to paid items) for Spigot1.8.8
- 0.1.4 
  bug fixed from 0.1.3(return bug fixed) for Spigot1.8.8
- 0.1.3
  bug fixed from 0.1.2(nullPointExep. at FlingItem.java) for Spigot1.8.8
- 0.1.2 
  bug fixed from 0.1.1(permissions) for Spigot1.8.8]
- 0.1.1 
  bug fixed from 0.1.0(Timer is faster) for Spigot1.8.8
- 0.1.0
  First Releace for Spigot1.8.8






