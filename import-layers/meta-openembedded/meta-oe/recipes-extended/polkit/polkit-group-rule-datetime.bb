DESCRIPTION = "Create usergroup datetime. All members off this group are allowed set date/time/timezone via system dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require polkit-group-rule.inc

SRC_URI = "file://50-org.freedesktop.timedate1.rules"

do_install() {
        install -m 0755 ${WORKDIR}/50-org.freedesktop.timedate1.rules ${D}${sysconfdir}/polkit-1/rules.d
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system datetime"
