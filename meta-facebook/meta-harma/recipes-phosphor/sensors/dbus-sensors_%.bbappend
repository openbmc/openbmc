FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://setup-devices-eid \
    file://setup-mctpreactor.conf \
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

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.mctpreactor.service.d
    install -m 0644 ${UNPACKDIR}/setup-mctpreactor.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.mctpreactor.service.d/setup-mctpreactor.conf
}

