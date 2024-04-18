# Minerva Image configuration

# Monitor the GPIO on the system and take action.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"

# Temporarily add plat-svc for gpio initialization
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"

# For console to the MTIA blade
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    obmc-console \
"

# Enable for debug card function
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-ipmi-ipmb \
    fb-ipmi-oem \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
"

# Enable logger for sensor threshold event monitor
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-sel-logger \
"

OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-state-manager \
"
