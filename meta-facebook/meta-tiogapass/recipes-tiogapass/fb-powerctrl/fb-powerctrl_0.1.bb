LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"

SRC_URI += " \
    file://setup_gpio \
    file://power-util \
    file://host-gpio.service \
    file://host-poweroff.service \
    file://host-poweron.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    host-gpio.service host-poweron.service \
    host-poweroff.service \
    "

S = "${WORKDIR}"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${S}/setup_gpio ${D}/${sbindir}/
    install -m 0755 ${S}/power-util ${D}/${sbindir}/
}
