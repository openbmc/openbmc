# Ventura2 Image Configuration

# Enable mdio-tools for communication with the PHY chip
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    mdio-tools \
"

# Add plat-svc for system initialization
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"

# Enable for debug card function
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-ipmi-ipmb \
    fb-ipmi-oem \
    obmc-phosphor-buttons-signals \
    obmc-phosphor-buttons-handler \
"

# Create udev symbolic links for each Modbus device
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    persistent-rs485-port-mapping \
"

