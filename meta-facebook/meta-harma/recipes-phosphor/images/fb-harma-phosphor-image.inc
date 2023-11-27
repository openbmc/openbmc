# Harma Image configuration.

# Monitor power status, for example, add handling for power good and post-end signals to manage power status.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"

# It is for lsusb app.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    usbutils \
"
