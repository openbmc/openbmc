FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
RDEPENDS:${PN}-discover:append = " bletchley-common-tool"

# Because Bletchley does not have IPMI between Bmc & Host, the Host init
# state will set to Off after Bmc booted. We require an extra service to
# check and set Host state & Chassis power state to correct state before
# doing any power action or power policy restore.

BLETCHLEY_SYS_ST_INIT_CONF_FMT = "bletchley-system-state-init.conf:phosphor-discover-system-state@{0}.service.d/bletchley-system-state-init.conf"
SYSTEMD_OVERRIDE:${PN}-discover:bletchley += "${@compose_list_zip(d, 'BLETCHLEY_SYS_ST_INIT_CONF_FMT', 'OBMC_HOST_INSTANCES')}"
