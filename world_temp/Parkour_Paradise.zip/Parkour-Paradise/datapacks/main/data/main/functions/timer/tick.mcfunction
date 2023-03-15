#start timer
execute as @a[tag=start_timer] at @s run function main:timer/start

#win timer
execute as @a[tag=win_timer] at @s run function main:timer/win

#firework
execute if entity @e[type=area_effect_cloud,name=firework] run function main:timer/firework

