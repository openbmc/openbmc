OBMC_IMAGE_EXTRA_INSTALL_append = " google-ipmi-sys"
OBMC_IMAGE_EXTRA_INSTALL_append = " google-ipmi-i2c"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-blobs"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-ethstats"
OBMC_IMAGE_EXTRA_INSTALL_append = " phosphor-ipmi-flash"

OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " gbmc-systemd-config"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = " gbmc-iperf3"
OBMC_IMAGE_EXTRA_INSTALL_append_gbmc = \
  '${@"" if not d.getVar("GBMC_NCSI_IF_NAME") else " gbmc-ncsi-config"}'
