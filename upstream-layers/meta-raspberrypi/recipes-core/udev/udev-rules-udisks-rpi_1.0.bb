DESCRIPTION = "add udisk/udev rule to hide boot partition from udev"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "file://80-udisks-rpi.rules"

S = "${UNPACKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

do_install () {
	install -d ${D}${base_libdir}/udev/rules.d
	install -m 644 ${UNPACKDIR}/80-udisks-rpi.rules ${D}${base_libdir}/udev/rules.d
}

FILES:${PN} = "${base_libdir}/udev/rules.d"
