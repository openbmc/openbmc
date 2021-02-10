PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS_${PN} += "sslh"

SRC_URI_append = " \
    file://sslh.service \
    file://sslh.socket \
"

SYSTEMD_SERVICE_${PN} += "sslh.service"
SYSTEMD_SERVICE_${PN} += "sslh.socket"

do_install() {
    # Install service definitions
    install -d -m 0755 ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sslh.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sslh.socket ${D}${systemd_system_unitdir}
}
