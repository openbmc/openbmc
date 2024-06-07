# Harma Image configuration.

# Monitor power status, for example, add handling for power good and post-end signals to manage power status.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"

# Monitor gpio presence status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor-presence \
"

# Monitor Chassis power status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-power-control \
"

# Allow scripted access to SITV BMC
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    sshpass \
"

# Enable obmc-phosphor-buttons which needed by debug card function.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
"
