DESCRIPTION = "Transfer BMC IP address to the FPGA"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI = " file://ip-to-fpga.sh \
            file://ip-to-fpga.service \
          "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SYSTEMD_SERVICE:${PN} = "ip-to-fpga.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/ip-to-fpga.sh ${D}${bindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/ip-to-fpga.service ${D}${systemd_system_unitdir}
}
