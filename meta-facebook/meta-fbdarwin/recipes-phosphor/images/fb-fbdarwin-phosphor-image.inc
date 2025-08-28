# Lfdarwin Image configuration.

# Monitor presence gpio status
#OBMC_IMAGE_EXTRA_INSTALL:append = " \
#    phosphor-gpio-monitor \
#    phosphor-gpio-monitor-presence \
#"

# Enable obmc-phosphor-buttons which needed by debug card function.
#OBMC_IMAGE_EXTRA_INSTALL:append = " \
#    obmc-phosphor-buttons-signals \
#    obmc-phosphor-buttons-handler \
#"
