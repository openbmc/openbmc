# TODO - add LED support to system1
IMAGE_FEATURES:remove = " \
    obmc-leds \
"
OBMC_IMAGE_EXTRA_INSTALL:append:system1 = " phosphor-ipmi-blobs smbios-mdr"
