require xorg-app-common.inc
SUMMARY = "X authority utilities"
DESCRIPTION = "X application to edit and display the authorization \
information used in connecting to the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=5ec74dd7ea4d10c4715a7c44f159a40b"

DEPENDS += "libxau libxext libxmu"
PE = "1"

SRC_URI[md5sum] = "f3f76cb10f69b571c43893ea6a634aa4"
SRC_URI[sha256sum] = "5afe42ce3cdf4f60520d1658d2b17face45c74050f39af45dccdc95e73fafc4d"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
