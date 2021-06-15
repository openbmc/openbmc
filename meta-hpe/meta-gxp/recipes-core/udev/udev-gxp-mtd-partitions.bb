SUMMARY = "udev rules for MTD partitions"
DESCRIPTION = "udev rules for MTD partitions"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}"
SRC_URI += "file://76-gxp-mtd-partitions.rules"

RDEPENDS_${PN} += "udev"

do_install() {
    install -d ${D}/${base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/76-gxp-mtd-partitions.rules ${D}/${base_libdir}/udev/rules.d
}
