LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI += " \
    file://backend-nic-driver-bind \
    file://catalina-sys-init.service \
    file://catalina-early-sys-init \
    file://osfp-eeprom-driver-bind \
    file://standby-power-enable \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    backend-nic-driver-bind.service \
    catalina-sys-init.service \
    osfp-eeprom-driver-bind.service \
    "

do_install() {
    CATALINA_LIBEXECDIR="${D}${libexecdir}/catalina"
    install -d ${CATALINA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/backend-nic-driver-bind ${CATALINA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/catalina-early-sys-init ${CATALINA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/osfp-eeprom-driver-bind ${CATALINA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/standby-power-enable ${CATALINA_LIBEXECDIR}
}
