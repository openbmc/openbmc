SUMMARY = "Public Suffix List library"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5437030d9e4fbe7267ced058ddb8a7f5 \
                    file://COPYING;md5=f41d10997a12da5ee3c24ceeb0148d18"

SRC_URI = "https://github.com/rockdaboot/${BPN}/releases/download/${BP}/${BP}.tar.gz \
           file://0001-gtk-doc-do-not-include-tree_index.sgml.patch \
           file://0001-Makefile.am-use-PYTHON-when-invoking-psl-make-dafsa.patch \
           "
SRC_URI[md5sum] = "171e96d887709e36a57f4ee627bf82d2"
SRC_URI[sha256sum] = "41bd1c75a375b85c337b59783f5deb93dbb443fb0a52d257f403df7bd653ee12"

UPSTREAM_CHECK_URI = "https://github.com/rockdaboot/libpsl/releases"

DEPENDS = "libidn2"

inherit autotools gettext gtk-doc manpages pkgconfig lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = "--enable-man,--disable-man,libxslt-native"

BBCLASSEXTEND = "native nativesdk"
