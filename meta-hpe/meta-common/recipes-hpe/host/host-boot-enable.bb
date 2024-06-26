SUMMARY = "Enable Host Boot"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${HPEBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

HOST_BOOT_SERVICE = "host-boot-enable.service"
SYSTEMD_SERVICE:${PN} += "${HOST_BOOT_SERVICE}"

HOST_BOOT_FMT = "../${HOST_BOOT_SERVICE}:multi-user.target.wants/${HOST_BOOT_SERVICE}"
SYSTEMD_LINK_${PN} += "${HOST_BOOT_FMT}"

SRC_URI += "file://host-boot-enable.service"
SRC_URI += "file://host-boot-enable.sh"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${UNPACKDIR}/host-boot-enable.sh ${D}${bindir}
}
