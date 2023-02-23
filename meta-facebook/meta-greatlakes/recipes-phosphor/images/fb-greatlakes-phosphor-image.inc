OBMC_IMAGE_EXTRA_INSTALL:append = " \
    greatlakes-sysinit \
    greatlakes-gpio-monitor-register \
    phosphor-gpio-monitor \
"

# Currently, the led related services couldn't complete
# causing that multi-user.target never complete.
# If multi-user.target doesn't complete,
# the obmc-host-startmin@.target couldn't start.
# In that case, BMC couldn't set current host state to "Running".
IMAGE_FEATURES:remove = " \
    obmc-leds \
"
