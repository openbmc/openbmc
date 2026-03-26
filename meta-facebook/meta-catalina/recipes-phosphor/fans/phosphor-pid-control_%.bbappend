FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://config.json \
                           "

FILES:${PN}:append = " ${datadir}/swampd/config.json"

do_install:append() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${UNPACKDIR}/config.json ${D}${datadir}/swampd/config.json
}

#========================================================
# Changes for catalina
#========================================================
RDEPENDS:${PN}:append:catalina = " bash"

SRC_URI:append:catalina = " \
    file://config-pdb-brick.json \
    file://check-fsc-config \
    file://check-fsc-config.conf \
    "

FILES:${PN}:append:catalina = "\
    ${libexecdir}/phosphor-pid-control \
    ${datadir}/swampd/config-pdb-brick.json \
    ${systemd_system_unitdir}/phosphor-pid-control.service.d/check-fsc-config.conf \
    "

do_install:append:catalina() {
    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${UNPACKDIR}/config-pdb-brick.json ${D}${datadir}/swampd/config-pdb-brick.json

    LIBEXECDIR="${D}${libexecdir}/phosphor-pid-control"
    install -d ${LIBEXECDIR}
    install -m 0755 -D ${UNPACKDIR}/check-fsc-config ${LIBEXECDIR}/check-fsc-config

    install -d ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d
    install -m 0644 ${UNPACKDIR}/check-fsc-config.conf \
        ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d/check-fsc-config.conf
}
