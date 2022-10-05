SUMMARY = "DHCP Check"
DESCRIPTION = "Script to check and restart network interface if DHCP fails to get an address"

S = "${WORKDIR}"
SRC_URI = "file://dhcp-check.sh \
           file://dhcp-check.service \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
RDEPENDS_${PN} += "bash"

inherit obmc-phosphor-systemd

FILES_${PN} += "${systemd_system_unitdir}/dhcp-check.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/dhcp-check.sh ${D}/${bindir}/dhcp-check.sh
}

SYSTEMD_SERVICE_${PN} += " dhcp-check.service"