FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://blacklist.json \
    file://device-driver-probe \
    file://xyz.openbmc_project.EntityManager.conf \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.EntityManager.service.d/xyz.openbmc_project.EntityManager.conf"

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${BPN}/blacklist.json

    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${UNPACKDIR}/device-driver-probe ${D}${libexecdir}/${BPN}/

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.EntityManager.service.d
    install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.EntityManager.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.EntityManager.service.d/xyz.openbmc_project.EntityManager.conf
}
