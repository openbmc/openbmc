FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "\
    file://blacklist.json \
    file://device-driver-probe \
    "

RDEPENDS:${PN}:append = " bash"

SYSTEMD_OVERRIDE:${PN} += "xyz.openbmc_project.EntityManager.conf:xyz.openbmc_project.EntityManager.service.d/xyz.openbmc_project.EntityManager.conf"

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/device-driver-probe ${D}${libexecdir}/${PN}/
}
