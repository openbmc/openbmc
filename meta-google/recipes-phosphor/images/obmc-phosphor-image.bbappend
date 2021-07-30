OBMC_IMAGE_EXTRA_INSTALL_append = " google-ipmi-sys"
OBMC_IMAGE_EXTRA_INSTALL_append = " google-ipmi-i2c"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-blobs"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-ethstats"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-flash"

# Google BMC (gBMC) specific installs
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " iproute2 iproute2-ss"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " gbmc-systemd-config"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " gbmc-iperf3"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " authorized-keys-comp"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc_dev = " gbmc-dev-ssh-key"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = \
  '${@"" if not d.getVar("GBMC_NCSI_IF_NAME") else " gbmc-ncsi-config"}'
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = \
  '${@"" if not d.getVar("GBMC_MAC_EEPROM_OF_NAME") else " gbmc-mac-config"}'

# Include these useful utilities for all gbmc platforms
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " ipmitool"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " iotools"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " lrzsz"

# Add gBMC update recipes
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " dummy-gbmc-update"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " virtual/bmc-update"
