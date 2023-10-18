# Currently, the led related services couldn't complete
# causing that multi-user.target never complete.
# If multi-user.target doesn't complete,
# the obmc-host-startmin@.target couldn't start.
# In that case, BMC couldn't set current host state to "Running".
IMAGE_FEATURES:remove = " \
    obmc-leds \
"

# Temporarily add libusb1 to allow side-loading of a BIOS/BIC update
# application which need libusb1.  Should be removed when BIOS and BIC update
# is finished.
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    libusb1 \
"
# Temporarily add plat-svc for gpio initialization
OBMC_IMAGE_EXTRA_INSTALL:append = " \
    plat-svc \
"

OBMC_IMAGE_EXTRA_INSTALL:append = " \
    phosphor-gpio-monitor \
"

