FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI += " \
    file://setup-devices-eid \
"

PACKAGECONFIG:append = "\
    nvmesensor \
"

RDEPENDS:${PN} += " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    install -d ${D}${libexecdir}/${PN}

    install -m 0755 ${UNPACKDIR}/setup-devices-eid \
              ${D}${libexecdir}/${PN}
}

SYSTEMD_OVERRIDE:${PN} += "setup-mctpreactor.conf:xyz.openbmc_project.mctpreactor.service.d/setup-mctpreactor.conf"

