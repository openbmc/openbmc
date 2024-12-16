LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "yosemite4-common-functions"

SRC_URI += " \
    file://yosemite4-sys-init.service \
    file://yosemite4-early-sys-init \
    file://yosemite4-schematic-init.service \
    file://yosemite4-schematic-init \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    yosemite4-sys-init.service \
    yosemite4-schematic-init.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/yosemite4-early-sys-init ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/yosemite4-schematic-init ${D}${libexecdir}
}

