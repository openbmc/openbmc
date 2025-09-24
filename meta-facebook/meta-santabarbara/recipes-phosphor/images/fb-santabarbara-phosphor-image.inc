# Santabarbara Image configuration.

# Enable obmc-phosphor-buttons which needed by debug card function.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
"

# Monitor presence gpio status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor-presence \
"

# Allow scripted access to SITV BMC
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    sshpass \
"

# Monitor power status, for example, add handling for power good and post-end signals to manage power status.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"

# Monitor the chassis power status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-power-control \
"

# Santabarbara platform service
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"
