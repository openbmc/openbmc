# Yosemite5 Image configuration.

# Temporarily add plat-svc for gpio initialization
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"

# Monitor presence gpio status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
    phosphor-gpio-monitor-presence \
"

# Enable obmc-phosphor-buttons which needed by debug card function.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
"

# Enable dbus monitor for host power off policy
IMAGE_FEATURES:append = " obmc-dbus-monitor"
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    fan-policy \
    thermal-policy \
"