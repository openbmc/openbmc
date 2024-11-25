SUMMARY = "Miscellaneous OpenBMC functions"
HOMEPAGE = "https://github.com/openbmc/phosphor-misc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS = "systemd"
SRCREV = "abc613ef4df43e8eef9d9be1b3a01ddf64586d13"
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
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-misc;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_PACKAGES = "${PHOSPHOR_MISC_PACKAGES}"
SYSTEMD_SERVICE:${PN}-first-boot-set-hostname = "first-boot-set-hostname.service"
SYSTEMD_SERVICE:${PN}-first-boot-set-mac = "first-boot-set-mac@.service"
SYSTEMD_SERVICE:${PN}-http-redirect-awk = " \
    http-redirect@.service \
    http-redirect.socket \
    "

inherit meson
inherit pkgconfig
inherit systemd

PACKAGES = "${PHOSPHOR_MISC_PACKAGES}"

RDEPENDS:${PN}-http-redirect-awk = "${VIRTUAL-RUNTIME_base-utils}"
RDEPENDS:${PN}-first-boot-set-hostname += "bash"
RDEPENDS:${PN}-first-boot-set-mac += "bash"
RDEPENDS:${PN}-usb-ctrl += "bash"

# first-boot-set-hostname
FILES:${PN}-first-boot-set-hostname = "${bindir}/first-boot-set-hostname.sh"
# first-boot-set-mac
FILES:${PN}-first-boot-set-mac = "${bindir}/first-boot-set-mac.sh"
# http-redirect-awk
FILES:${PN}-http-redirect-awk = "${bindir}/http-redirect.awk"
# usb-ctrl
FILES:${PN}-usb-ctrl = "${bindir}/usb-ctrl"

PHOSPHOR_MISC_PACKAGES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'first-boot-set-hostname', '${PN}-first-boot-set-hostname', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'first-boot-set-mac', '${PN}-first-boot-set-mac', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'http-redirect-awk', '${PN}-http-redirect-awk', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'usb-ctrl', '${PN}-usb-ctrl', '', d)} \
    "
PACKAGE_BEFORE_PN += "${PHOSPHOR_MISC_PACKAGES}"
