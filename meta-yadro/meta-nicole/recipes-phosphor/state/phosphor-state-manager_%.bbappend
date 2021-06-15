FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

DEPS_CFG = "resetreason.conf"
DEPS_TGT = "phosphor-discover-system-state@.service"
SYSTEMD_OVERRIDE_${PN}-discover_append = "${DEPS_CFG}:${DEPS_TGT}.d/${DEPS_CFG}"
