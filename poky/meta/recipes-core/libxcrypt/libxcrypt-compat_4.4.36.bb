#
# This provides libcrypto.so.1 which contains obsolete APIs, needed for uninative in particular
#

require libxcrypt.inc

PROVIDES = ""
AUTO_LIBNAME_PKGS = ""
EXCLUDE_FROM_WORLD = "1"

API = "--enable-obsolete-api"

do_install:append () {
	rm -rf ${D}${includedir}
	rm -rf ${D}${libdir}/pkgconfig
	rm -rf ${D}${libdir}/libcrypt.so
	rm -rf ${D}${datadir}
}
