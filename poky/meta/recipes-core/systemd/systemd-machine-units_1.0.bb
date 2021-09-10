SUMMARY = "Machine specific systemd units"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r19"

inherit systemd
SYSTEMD_SERVICE:${PN} = ""

ALLOW_EMPTY:${PN} = "1"
