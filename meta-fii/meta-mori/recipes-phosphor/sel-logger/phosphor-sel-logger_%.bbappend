# Enable threshold, pulse, and watchdog monitoring
PACKAGECONFIG:append:mori = " \
    log-threshold log-alarm log-pulse log-watchdog  \
"
