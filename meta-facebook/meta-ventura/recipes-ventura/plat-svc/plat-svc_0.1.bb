LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI += " \
    file://ventura-sys-init.service \
    file://ventura-early-sys-init \
    file://ventura-init-tray-sgpio-status.service \
    file://ventura-init-tray-sgpio-status \
    file://ventura-schematic-init \
    file://ventura-schematic-init.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    ventura-sys-init.service \
    ventura-init-tray-sgpio-status.service \
    ventura-schematic-init.service \
    "

do_install() {
    VENTURA_LIBEXECDIR="${D}${libexecdir}/ventura"
    install -d ${VENTURA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/ventura-early-sys-init ${VENTURA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/ventura-init-tray-sgpio-status ${VENTURA_LIBEXECDIR}
    install -m 0755 ${UNPACKDIR}/ventura-schematic-init ${VENTURA_LIBEXECDIR}
}
