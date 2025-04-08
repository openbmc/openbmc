LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "libgpiod-tools"
RDEPENDS:${PN} += "minerva-common-functions"

SRC_URI += " \
    file://minerva-sys-init.service \
    file://minerva-early-sys-init \
    file://minerva-fan-status-monitor \
    file://minerva-fan-status-monitor.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    minerva-sys-init.service \
    minerva-fan-status-monitor.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/minerva-early-sys-init ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/minerva-fan-status-monitor ${D}${libexecdir}
}

