# San Miguel Image configuration.

# San Miguel platform services
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"

# Monitor gpio status
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"
