require recipes-phosphor/images/fb-yosemite5-phosphor-image.inc

# rainiera6 Image configuration.

# Enable phosphor-ipmi-blobs for IPMI blob transfer
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-ipmi-blobs \
"

# Enable SMBIOS MDR to parse host SMBIOS data
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    smbios-mdr \
"
