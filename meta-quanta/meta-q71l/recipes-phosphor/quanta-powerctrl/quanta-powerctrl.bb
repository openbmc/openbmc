FILESEXTRAPATHS:append := "${THISDIR}/files:"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI = "file://init_once.sh \
           file://poweroff.sh \
           file://poweron.sh \
           file://host-gpio.service \
           file://host-poweroff.service \
           file://host-poweron.service"

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "host-gpio.service host-poweron.service host-poweroff.service"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/init_once.sh ${D}/${bindir}/
    install -m 0755 ${S}/poweroff.sh ${D}/${bindir}/
    install -m 0755 ${S}/poweron.sh ${D}/${bindir}/
}
