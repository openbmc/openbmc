DESCRIPTION = "Create usergroup datetime. All members off this group are allowed set date/time/timezone via system dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

require polkit-group-rule.inc

SRC_URI = "file://50-org.freedesktop.timedate1.rules"

do_install() {
        install -m 0755 ${WORKDIR}/50-org.freedesktop.timedate1.rules ${D}${sysconfdir}/polkit-1/rules.d
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system datetime"
