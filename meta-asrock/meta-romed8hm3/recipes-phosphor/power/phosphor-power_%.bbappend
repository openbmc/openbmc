FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI += "file://config.json"

inherit obmc-phosphor-systemd
SYSTEMD_LINK:${PN}-regulators += " ../${REGS_CONF_SVC}:xyz.openbmc_project.Chassis.Control.Power@0.service.requires/${REGS_CONF_SVC}"

do_install:append() {
    PR_CFGDIR=${D}/${datadir}/phosphor-regulators
    install -d "$PR_CFGDIR"
    install -m 0644 ${UNPACKDIR}/config.json "$PR_CFGDIR"
}
