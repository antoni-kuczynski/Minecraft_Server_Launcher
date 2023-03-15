execute if block -770 6 -5 oak_button[powered=true] run function main:maps/previous
execute if block -774 6 -5 oak_button[powered=true] run function main:maps/next

execute as @e[tag=mapFrame] run data merge entity @s {ItemRotation:0b}

function main:maps/animated_logo