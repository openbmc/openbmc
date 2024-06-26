FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils}"
SYSTEMD_SERVICE:${PN} = "${BPN}.service"
SRC_URI:append = " \
    file://${BPN}-set-device-id.sh \
    file://${BPN}.service \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
do_install:append() {
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}
    install ${BPN}-set-device-id.sh ${D}${bindir}/
    install -m 0644 ${BPN}.service ${D}${systemd_system_unitdir}/
}

FILES:${PN}:append = " \
    ${bindir}/${BPN}-set-device-id.sh \
"
