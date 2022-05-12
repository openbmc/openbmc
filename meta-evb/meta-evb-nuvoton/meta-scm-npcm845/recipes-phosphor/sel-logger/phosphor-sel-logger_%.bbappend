inherit entity-utils

PACKAGECONFIG:append:scm-npcm845 = " log-watchdog clears-sel"
PACKAGECONFIG:append:scm-npcm845 = " ${@entity_enabled(d, 'log-threshold', 'send-to-logger log-alarm')}"
