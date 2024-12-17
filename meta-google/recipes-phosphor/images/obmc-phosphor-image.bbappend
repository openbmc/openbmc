OBMC_IMAGE_EXTRA_INSTALL:append = " google-ipmi-sys"
OBMC_IMAGE_EXTRA_INSTALL:append = " google-ipmi-i2c"
OBMC_IMAGE_EXTRA_INSTALL:append = " phosphor-ipmi-blobs"
OBMC_IMAGE_EXTRA_INSTALL:append = " phosphor-ipmi-ethstats"
OBMC_IMAGE_EXTRA_INSTALL:append = " phosphor-ipmi-flash"
OBMC_IMAGE_EXTRA_INSTALL:append = \
  '${@bb.utils.contains_any("MACHINE_FEATURES", "glome", \
  " glome-login", "", d)}'

# Google BMC (gBMC) specific installs
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " iproute2 iproute2-ss"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " tzdata-core"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " gbmc-systemd-config"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " gbmc-iperf3"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " authorized-keys-comp"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc:dev = " gbmc-dev-ssh-key"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = \
  '${@"" if not d.getVar("GBMC_NCSI_IF_NAME") else " gbmc-ncsi-config"}'
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = \
  '${@"" if not d.getVar("GBMC_EXT_NICS") else " gbmc-nic-config"}'
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = \
  '${@"" if not d.getVar("GBMC_MAC_EEPROM_OF_NAME") else " gbmc-mac-config"}'
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = \
  '${@"" if not d.getVar("GBMC_ETHER_MAP") else " gbmc-nic-rename"}'

# Include these useful utilities for all gbmc platforms
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " ipmitool"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " lrzsz"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " tcpdump"

# Add gBMC update recipes
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " dummy-gbmc-update"
OBMC_IMAGE_EXTRA_INSTALL:append:gbmc = " ${VIRTUAL-RUNTIME_bmc-update}"

# Jettison the cracklib package to save space.
PACKAGE_INSTALL:remove:gbmc = "cracklib libpwquality"
PACKAGE_EXCLUDE:gbmc = "cracklib libpwquality"
