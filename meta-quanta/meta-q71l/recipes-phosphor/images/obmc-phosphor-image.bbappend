OBMC_IMAGE_EXTRA_INSTALL:append:quanta-q71l = " quanta-powerctrl"

# dts for q71l includes snooping, so let's leverage that.
OBMC_IMAGE_EXTRA_INSTALL:append:quanta-q71l = " phosphor-host-postd"

