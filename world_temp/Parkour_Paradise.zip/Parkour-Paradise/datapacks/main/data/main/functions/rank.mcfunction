title @s[scores={win_timer=260}] times 0 100 20
title @s[scores={win_timer=260}] title {"text":" "}
title @s[scores={win_timer=261}] subtitle {"text":"R"}
title @s[scores={win_timer=263}] subtitle {"text":"Ra"}
title @s[scores={win_timer=265}] subtitle {"text":"Ran"}
title @s[scores={win_timer=267}] subtitle {"text":"Rank"}

playsound minecraft:block.note_block.hat master @s[scores={win_timer=261}] ~ ~ ~ 0.1 2
playsound minecraft:block.note_block.hat master @s[scores={win_timer=263}] ~ ~ ~ 0.1 2
playsound minecraft:block.note_block.hat master @s[scores={win_timer=265}] ~ ~ ~ 0.1 2
playsound minecraft:block.note_block.hat master @s[scores={win_timer=267}] ~ ~ ~ 0.1 2

#14 minutes = S rank
title @s[scores={win_timer=300,finaltime=..840}] title {"text":"S","color":"gold"}
tag @s[scores={win_timer=300,finaltime=..840}] add setRank
playsound minecraft:entity.player.levelup master @s[scores={win_timer=300,finaltime=..840}] ~ ~ ~ 1 1

#20 minutes = A rank
title @s[scores={win_timer=300,finaltime=..1200},tag=!setRank] title {"text":"A","color":"red"}
playsound minecraft:entity.player.levelup master @s[scores={win_timer=300,finaltime=..1200},tag=!setRank] ~ ~ ~ 1 0.8
tag @s[scores={win_timer=300,finaltime=..1200}] add setRank

#45 minutes = B rank
title @s[scores={win_timer=300,finaltime=..2700},tag=!setRank] title {"text":"B","color":"green"}
playsound minecraft:entity.player.levelup master @s[scores={win_timer=300,finaltime=..2700},tag=!setRank] ~ ~ ~ 1 0.7
tag @s[scores={win_timer=300,finaltime=..2700}] add setRank

#90 minutes = C rank
title @s[scores={win_timer=300,finaltime=..5399},tag=!setRank] title {"text":"C","color":"blue"}
playsound minecraft:entity.player.levelup master @s[scores={win_timer=300,finaltime=..5399},tag=!setRank] ~ ~ ~ 1 0.6
tag @s[scores={win_timer=300,finaltime=..5399}] add setRank

#90+ = D rank
title @s[scores={win_timer=300,finaltime=5400..},tag=!setRank] title {"text":"D","color":"light_purple"}
playsound minecraft:entity.player.levelup master @s[scores={win_timer=300,finaltime=5400..},tag=!setRank] ~ ~ ~ 1 0.52

title @s[scores={win_timer=300}] subtitle {"text":"Rank"}
tag @s[scores={win_timer=300}] remove setRank