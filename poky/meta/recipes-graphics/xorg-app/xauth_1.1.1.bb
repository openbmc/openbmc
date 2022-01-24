require xorg-app-common.inc
SUMMARY = "X authority utilities"
DESCRIPTION = "X application to edit and display the authorization \
information used in connecting to the X server."

LIC_FILES_CHKSUM = "file://COPYING;md5=5ec74dd7ea4d10c4715a7c44f159a40b"

DEPENDS += "libxau libxext libxmu"
PE = "1"

SRC_URI[sha256sum] = "164ea0a29137b284a47b886ef2affcb0a74733bf3aad04f9b106b1a6c82ebd92"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
