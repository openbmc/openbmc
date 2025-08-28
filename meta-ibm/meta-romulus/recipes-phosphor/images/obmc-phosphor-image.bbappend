OBMC_IMAGE_EXTRA_INSTALL:append:romulus = " mboxd"

# Remove rsyslog, etc. to save space.
IMAGE_FEATURES:remove = "obmc-remote-logging-mgmt"
