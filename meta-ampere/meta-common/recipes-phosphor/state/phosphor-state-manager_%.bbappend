FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPS_CFG = "resetreason.conf"
DEPS_TGT = "phosphor-discover-system-state@.service"
SYSTEMD_OVERRIDE_${PN}-discover_append = "${DEPS_CFG}:${DEPS_TGT}.d/${DEPS_CFG}"

SRC_URI += " \
             file://ampere-reset-host-check@.service \
           "

do_install_append() {
    install -m 0644 ${WORKDIR}/ampere-reset-host-check@.service ${D}${systemd_unitdir}/system/phosphor-reset-host-check@.service
}