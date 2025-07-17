LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += " bash libgpiod-tools"

FILES:${PN} += "${systemd_system_unitdir}/*"

SRC_URI += " \
    file://yosemite4-common-functions \
    file://FanboardPowerOff@.service \
    file://FanboardPowerOn@.service \
    file://fanboardpower \
    "

SYSTEMD_SERVICE:${PN} += " \
    FanboardPowerOff@.service \
    FanboardPowerOn@.service \
"

do_install() {
    install -d ${D}${libexecdir}/plat-tool
    install -m 0755 ${UNPACKDIR}/yosemite4-common-functions ${D}${libexecdir}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/FanboardPowerOff@.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/FanboardPowerOn@.service ${D}${systemd_system_unitdir}/
    install -m 0755 ${UNPACKDIR}/fanboardpower ${D}${libexecdir}/plat-tool/
}