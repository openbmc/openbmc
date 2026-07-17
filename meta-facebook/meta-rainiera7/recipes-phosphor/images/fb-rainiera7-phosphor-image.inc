# Rainier Image configuration.

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

# Enable phosphor-ipmi-blobs for IPMI blob transfer
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-ipmi-blobs \
"

# Enable SMBIOS MDR to parse host SMBIOS data
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    smbios-mdr \
"