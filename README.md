# Simple CTF
This is a Spigot plugin for playing "Capture The Flag".

WARNING: This plugin is beta.  

## Download and Version info
- [0.1.0](https://github.com/Seaoftrees/SimpleCTF/tree/master/jar/0.1.0)  
  First Releace for Spigot1.8.8
- [0.1.1](https://github.com/Seaoftrees/SimpleCTF/tree/master/jar/0.1.1)  
  bug fixed from 0.1.0(Timer is faster) for Spigot1.8.8
- [0.1.2](https://github.com/Seaoftrees/SimpleCTF/tree/master/jar/0.1.2)  
  bug fixed from 0.1.1(permissions) for Spigot1.8.8
- [0.1.3](https://github.com/Seaoftrees/SimpleCTF/tree/master/jar/0.1.3)  
  bug fixed from 0.1.2(nullPointExep. at FlingItem.java) for Spigot1.8.8
- [0.1.4](https://github.com/Seaoftrees/SimpleCTF/tree/master/jar/0.1.4)  
  bug fixed from 0.1.3(return bug fixed) for Spigot1.8.8

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
### /simplectf rate \[player\]
You can view \[player\] rate.  
If you don't write \[player\], you can view your rate.  
WARNING: This function is implemented in future version.  
- CanUse: Player, Console
- Permission: simplectf.rate
### /simplectf list
You can view arena list.
- CanUse: Player, Console
- Permission: none(Anybody can use this command)
### /simplectf watch \<arena\>
You can watch \<arena\> game.  
WARNING: This function is implemented in future version.
- CanUse: Player
- Permission: none(Anybody can use this command)
### /simplectf back
You back to WorldSpawnPoint from watching game.  
WARNING: This function is implemented in future version.
- CanUse: Player
- Permission: none(Anybody can use this command)
### /simplectf version
You can view this plugin infomation.
- CanUse: Player, Console
- Permission: none(Anybody can use this command)
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
### /simplectf admin setInv
Set Inventory team1 and team2.  
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