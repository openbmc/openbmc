DESCRIPTION = "Create usergroup network. All members off this group are allowed to modify networkmanager settings"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require polkit-group-rule.inc

SRC_URI = "file://50-org.freedesktop.NetworkManager.rules"

do_install() {
        install -m 0755 ${UNPACKDIR}/50-org.freedesktop.NetworkManager.rules ${D}${sysconfdir}/polkit-1/rules.d
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system network"
