scoreboard players add @s start_timer 1

title @s[scores={start_timer=1}] times 5 100 20
title @s[scores={start_timer=1}] title {"text":" "}
title @s[scores={start_timer=1}] subtitle {"text":"P"}
title @s[scores={start_timer=3}] subtitle {"text":"Pa"}
title @s[scores={start_timer=5}] subtitle {"text":"Par"}
title @s[scores={start_timer=7}] subtitle {"text":"Park"}
title @s[scores={start_timer=9}] subtitle {"text":"Parko"}
title @s[scores={start_timer=11}] subtitle {"text":"Parkou"}
title @s[scores={start_timer=13}] subtitle {"text":"Parkour"}
title @s[scores={start_timer=15}] subtitle {"text":"Parkour "}
title @s[scores={start_timer=17}] subtitle {"text":"Parkour P"}
title @s[scores={start_timer=19}] subtitle {"text":"Parkour Pa"}
title @s[scores={start_timer=21}] subtitle {"text":"Parkour Par"}
title @s[scores={start_timer=23}] subtitle {"text":"Parkour Para"}
title @s[scores={start_timer=25}] subtitle {"text":"Parkour Parad"}
title @s[scores={start_timer=27}] subtitle {"text":"Parkour Paradi"}
title @s[scores={start_timer=30}] subtitle {"text":"Parkour Paradis"}
title @s[scores={start_timer=32}] subtitle {"text":"Parkour Paradise"}
title @s[scores={start_timer=34}] subtitle {"text":"Parkour Paradise"}

title @s[scores={start_timer=63}] subtitle {"text":"Parkour Paradise","bold":true}
tag @s[scores={start_timer=63}] remove start_timer
scoreboard players reset @s[scores={start_timer=63}] start_timer