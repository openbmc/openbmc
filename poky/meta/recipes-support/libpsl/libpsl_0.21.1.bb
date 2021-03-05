SUMMARY = "Public Suffix List library"
DESCRIPTION = "The libpsl package provides a library for accessing and \
resolving information from the Public Suffix List (PSL). The PSL is a set of \
domain names beyond the standard suffixes, such as .com."

HOMEPAGE = "https://rockdaboot.github.io/libpsl/"
BUGTRACKER = "https://github.com/rockdaboot/libpsl/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5437030d9e4fbe7267ced058ddb8a7f5 \
                    file://COPYING;md5=f41d10997a12da5ee3c24ceeb0148d18"

SRC_URI = "https://github.com/rockdaboot/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           "
SRC_URI[sha256sum] = "ac6ce1e1fbd4d0254c4ddb9d37f1fa99dec83619c1253328155206b896210d4c"

UPSTREAM_CHECK_URI = "https://github.com/rockdaboot/libpsl/releases"

DEPENDS = "libidn2"

inherit autotools gettext gtk-doc manpages pkgconfig lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native"

BBCLASSEXTEND = "native nativesdk"
