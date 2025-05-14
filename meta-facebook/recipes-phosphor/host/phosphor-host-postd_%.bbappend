FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#Enable multi-host support in the phosphor-host-postd
EXTRA_OEMESON += "-Dhost-instances='${OBMC_HOST_INSTANCES}'"
EXTRA_OEMESON:append:fb-compute-multihost = " -Dsnoop=enabled"
