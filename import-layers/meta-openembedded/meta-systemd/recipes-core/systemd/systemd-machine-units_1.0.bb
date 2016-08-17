SUMMARY = "Machine specific systemd units"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r18"

inherit systemd
SYSTEMD_SERVICE_${PN} = ""
NATIVE_SYSTEMD_SUPPORT = "1"
ALLOW_EMPTY_${PN} = "1"
