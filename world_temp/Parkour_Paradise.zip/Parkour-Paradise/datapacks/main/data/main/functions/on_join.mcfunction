tp @s -772 5 -44 270 10
gamemode adventure @s
team join main @s
tag @s remove ingame
scoreboard players reset @s time
scoreboard players reset @s time_tick
scoreboard players reset @s level
scoreboard players reset @s level_display

tag @s add joined

#if player has no PB
execute unless score @s time_pb = @s time_pb run scoreboard players set @s time_pb 2147483647