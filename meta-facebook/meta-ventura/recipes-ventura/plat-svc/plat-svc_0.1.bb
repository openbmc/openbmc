LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "fb-common-functions"

SRC_URI += " \
    file://ventura-sys-init.service \
    file://ventura-early-sys-init \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    ventura-sys-init.service \
    "

do_install() {
    VENTURA_LIBEXECDIR="${D}${libexecdir}/ventura"
    install -d ${VENTURA_LIBEXECDIR}

    install -m 0755 ${UNPACKDIR}/ventura-early-sys-init ${VENTURA_LIBEXECDIR}
}
