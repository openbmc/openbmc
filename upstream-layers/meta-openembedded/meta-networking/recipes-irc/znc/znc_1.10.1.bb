SUMMARY = "ZNC, an advanced IRC bouncer"
SECTION = "net"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "zlib"
SRC_URI = "gitsm://github.com/znc/znc.git;branch=master;protocol=https;tag=${BP}"

SRCREV = "29694fd26f5e9ec46731ee13bf66224181984966"

inherit cmake pkgconfig

PACKAGECONFIG ??= "openssl zlib icu ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "-DWANT_IPV6=YES, -DWANT_IPV6=NO"
PACKAGECONFIG[openssl] = "-DWANT_OPENSSL=YES, -DWANT_OPENSSL=NO, openssl"
PACKAGECONFIG[zlib] = "-DWANT_ZLIB=YES, -DWANT_ZLIB=NO"
PACKAGECONFIG[icu] = "-DWANT_ICU=YES, -DWANT_ICU=NO, icu"
PACKAGECONFIG[i18n] = "-DWANT_I18N=YES, -DWANT_I18N=NO, boost gettext-native"

do_install:append() {
	sed -i -e 's|${DEBUG_PREFIX_MAP}||g; s|--sysroot=${STAGING_DIR_TARGET}||g; s|${STAGING_BINDIR_NATIVE}||g' ${D}${libdir}/pkgconfig/*.pc
}
