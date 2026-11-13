DESCRIPTION = "udev rules for Raspberry Pi Boards"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
	git://github.com/RPi-Distro/raspberrypi-sys-mods;protocol=https;branch=master \
	file://can.rules \
	"
SRCREV = "5ce3ef2b7f377c23fea440ca9df0e30f3f8447cf"

inherit useradd

INHIBIT_DEFAULT_DEPS = "1"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system i2c; --system spi; --system gpio"

do_install () {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc.armhf/udev/rules.d/99-com.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/can.rules ${D}${sysconfdir}/udev/rules.d/
}
