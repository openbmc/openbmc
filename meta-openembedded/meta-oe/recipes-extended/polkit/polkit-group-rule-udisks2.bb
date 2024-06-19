DESCRIPTION = "Polkit rule to allow non-priviledged users mount/umount block devices via udisks2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require polkit-group-rule.inc

# The file originates from https://github.com/coldfix/udiskie/wiki/Permissions
SRC_URI = "file://50-org.freedesktop.udiskie.rules"

RDEPENDS:${PN} += "udisks2"

do_install() {
    install -m 0755 ${UNPACKDIR}/50-org.freedesktop.udiskie.rules ${D}${sysconfdir}/polkit-1/rules.d
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system plugdev"
