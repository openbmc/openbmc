SUMMARY = "Public Suffix List library"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5437030d9e4fbe7267ced058ddb8a7f5 \
                    file://COPYING;md5=f41d10997a12da5ee3c24ceeb0148d18"

SRC_URI = "https://github.com/rockdaboot/${BPN}/releases/download/${BP}/${BP}.tar.gz"
SRC_URI[md5sum] = "f604f7d30d64bc673870ecf84b860a1e"
SRC_URI[sha256sum] = "f8fd0aeb66252dfcc638f14d9be1e2362fdaf2ca86bde0444ff4d5cc961b560f"

UPSTREAM_CHECK_URI = "https://github.com/rockdaboot/libpsl/releases"

DEPENDS = "libidn2"

inherit autotools gettext gtk-doc manpages pkgconfig lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native"

BBCLASSEXTEND = "native nativesdk"
