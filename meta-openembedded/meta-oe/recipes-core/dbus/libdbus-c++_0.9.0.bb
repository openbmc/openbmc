SUMMARY = "DBus-C++ Library"
DESCRIPTION = "DBus-c++ attempts to provide a C++ API for D-BUS. The library has a glib and an Ecore mainloop integration. It also offers an optional own main loop."
HOMEPAGE = "http://dbus-cplusplus.sourceforge.net"
SECTION = "base"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DEPENDS = "dbus expat glib-2.0 libpcre"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/dbus-cplusplus/dbus-c++/${PV}/${BP}.tar.gz \
           file://fix-missing-unistd.h-include.patch \
           file://remove-CXX_FOR_BUILD-stuff.patch \
           file://0001-src-eventloop.cpp-use-portable-method-for-initializi.patch \
           file://0002-tools-generate_proxy.cpp-avoid-possibly-undefined-ui.patch \
           file://0003-Fixed-undefined-ssize_t-for-clang-3.8.0-on-FreeBSD.patch \
           file://0004-use-POSIX-poll.h-instead-of-sys-poll.h.patch \
           file://0001-pipe.c-Use-a-string-instead-of-char.patch \
           "
SRC_URI[md5sum] = "e752116f523fa88ef041e63d3dee4de2"
SRC_URI[sha256sum] = "bc11ac297b3cb010be904c72789695543ee3fdf3d75cdc8225fd371385af4e61"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-ecore --disable-examples --disable-tests"
LDFLAGS += "-pthread"

PACKAGE_BEFORE_PN = "${PN}-tools"

FILES_${PN}-tools = "${bindir}"

BBCLASSEXTEND = "native"
