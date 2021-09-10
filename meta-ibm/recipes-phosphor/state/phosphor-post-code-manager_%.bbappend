# Set MaxBootCycleCount to 5 on IBM systems
EXTRA_OECMAKE:append:p10bmc = "-DMAX_BOOT_CYCLE_COUNT=5"
EXTRA_OECMAKE:append:witherspoon-tacoma = "-DMAX_BOOT_CYCLE_COUNT=5"

