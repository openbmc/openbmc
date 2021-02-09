PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS_${PN} += "iperf3"

SRC_URI += "file://iperf3.service"

SYSTEMD_SERVICE_${PN} += "iperf3.service"

do_install() {
    # Install service definitions
    install -d -m 0755 ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/iperf3.service ${D}${systemd_system_unitdir}
}
