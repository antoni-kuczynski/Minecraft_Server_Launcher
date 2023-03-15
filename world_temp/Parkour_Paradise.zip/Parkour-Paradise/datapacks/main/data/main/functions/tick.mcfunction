function main:firetick/tick

#PLAYSOUNDS
execute as @a[tag=playsound_levelup] at @s run playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 0.5 1
tag @a remove playsound_levelup

execute as @a[tag=playsound_warp] at @s run playsound minecraft:entity.enderman.teleport master @s ~ ~ ~ 1 1
tag @a remove playsound_warp

execute as @a[tag=playsound_portal] at @s run playsound minecraft:block.portal.travel master @s ~ ~ ~ 0.5 1 1
tag @a remove playsound_portal

spawnpoint @a -772 5 -44
effect give @a saturation 100000 255 true
effect give @a resistance 100000 255 true

#maps
execute as @e[type=armor_stand,name=console,limit=1] run function main:maps/tick

#On join
execute as @a[tag=!joined] run function main:on_join

#dont go out of spawn
execute positioned ~ 45 ~ as @a[distance=..15] if entity @s[x=-772,y=5,z=-44,distance=..100] run function main:tp/to_spawn

#Reset scores at spawn
scoreboard players reset @a[x=-772,y=5,z=-44,distance=..5] level
scoreboard players reset @a[x=-772,y=5,z=-44,distance=..5] time
scoreboard players reset @a[x=-772,y=5,z=-44,distance=..5] time_tick
tag @a[x=-772,y=5,z=-44,distance=..5] remove ingame

#time
scoreboard players add @a[tag=ingame] time_tick 1
scoreboard players add @a[tag=ingame,scores={time_tick=20}] time 1
scoreboard players set @a[scores={time_tick=20}] time_tick 0

#timers
execute as @e[type=armor_stand,name=console,limit=1] run function main:timer/tick

#level
execute as @a[tag=ingame] run function main:setlevel

#level display
execute as @e[type=armor_stand,name=console] run function main:set_display

#tp to start
gamemode adventure @a[x=-732,y=9,z=-44,distance=..15,gamemode=!adventure] 
execute as @a[x=-732,y=9,z=-44,distance=..15] at @s if block ~0.2999 ~ ~ minecraft:nether_portal run function main:tp/to_start
execute as @a[x=-732,y=9,z=-44,distance=..15] at @s if block ~ ~ ~ minecraft:nether_portal run function main:tp/to_start
execute as @a[x=-732,y=9,z=-44,distance=..15] at @s if block ~-0.2999 ~ ~ minecraft:nether_portal run function main:tp/to_start

execute if block -14 2 -32 minecraft:slime_block run setblock -14 0 -33 air

#tp back to spawn
execute as @a[x=-178,y=9,z=-147,distance=..10] at @s if block ~ ~ ~ minecraft:end_gateway run function main:tp/to_spawn

execute as @a[scores={level=42}] at @s if block ~ ~1 ~ minecraft:water run function main:tp/level42
execute as @a[x=-83,y=1,z=-51,distance=..3] at @s if block ~ ~-1.1 ~ minecraft:quartz_pillar run function main:tp/level59

#END
execute as @a[tag=ingame,x=-168,y=8,z=-147,distance=..15] at @s run function main:win/main
execute unless entity @a[tag=win_timer] run kill @e[type=area_effect_cloud,name=firework]

#waving armor stand :)
function main:waving_armor_stand

#reset trigger
scoreboard players enable @a reset
tag @a[scores={reset=1}] remove joined
scoreboard players set @a reset 0

#reset PB trigger
scoreboard players enable @a reset_PB
scoreboard players set @a[scores={reset_PB=1}] time_pb 2147483647
tellraw @a[scores={reset_PB=1}] [{"text":"PB: ","color":"gold","bold":true},{"text":"Your personal best has been reset","bold":false,"color":"gray"}]
scoreboard players set @a reset_PB 0

#restart trigger
scoreboard players enable @a restart
execute as @a[scores={restart=1}] run function main:restart
scoreboard players set @a restart 0