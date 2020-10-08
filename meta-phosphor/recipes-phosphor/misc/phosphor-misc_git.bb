SUMMARY = "Miscellaneous OpenBMC functions"
HOMEPAGE = "https://github.com/openbmc/phosphor-misc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
PR = "r1"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/phosphor-misc"
SRCREV = "75d4734fb8bc390d97106544af8f2ffd2d9bf02c"

S = "${WORKDIR}/git"

inherit meson
inherit pkgconfig
inherit systemd

DEPENDS = "systemd"

PHOSPHOR_MISC_PACKAGES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'first-boot-set-hostname', '${PN}-first-boot-set-hostname', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'first-boot-set-mac', '${PN}-first-boot-set-mac', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'http-redirect-awk', '${PN}-http-redirect-awk', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'usb-ctrl', '${PN}-usb-ctrl', '', d)} \
    "

PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = ""

PACKAGE_BEFORE_PN += "${PHOSPHOR_MISC_PACKAGES}"
SYSTEMD_PACKAGES = "${PHOSPHOR_MISC_PACKAGES}"

PACKAGECONFIG ??= " \
    first-boot-set-hostname \
    first-boot-set-mac \
    http-redirect-awk \
    usb-ctrl \
    "

PACKAGECONFIG[first-boot-set-hostname] = "-Dfirst-boot-set-hostname=enabled, -Dfirst-boot-set-hostname=disabled"
PACKAGECONFIG[first-boot-set-mac] = "-Dfirst-boot-set-mac=enabled, -Dfirst-boot-set-mac=disabled"
PACKAGECONFIG[http-redirect-awk] = "-Dhttp-redirect=enabled, -Dhttp-redirect=disabled"
PACKAGECONFIG[usb-ctrl] = "-Dusb-ctrl=enabled, -Dusb-ctrl=disabled"

# first-boot-set-hostname
FILES_${PN}-first-boot-set-hostname = "${bindir}/first-boot-set-hostname.sh"
SYSTEMD_SERVICE_${PN}-first-boot-set-hostname = "first-boot-set-hostname.service"

# first-boot-set-mac
FILES_${PN}-first-boot-set-mac = "${bindir}/first-boot-set-mac.sh"
SYSTEMD_SERVICE_${PN}-first-boot-set-mac = "first-boot-set-mac@.service"

# http-redirect-awk
FILES_${PN}-http-redirect-awk = "${bindir}/http-redirect.awk"
SYSTEMD_SERVICE_${PN}-http-redirect-awk = " \
    http-redirect@.service \
    http-redirect.socket \
    "
RDEPENDS_${PN}-http-redirect-awk = "${VIRTUAL-RUNTIME_base-utils}"

# usb-ctrl
FILES_${PN}-usb-ctrl = "${bindir}/usb-ctrl"
