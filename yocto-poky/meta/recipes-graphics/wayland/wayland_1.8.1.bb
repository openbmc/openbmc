SUMMARY = "Wayland, a protocol between a compositor and clients"
DESCRIPTION = "Wayland is a protocol for a compositor to talk to its clients \
as well as a C library implementation of that protocol. The compositor can be \
a standalone display server running on Linux kernel modesetting and evdev \
input devices, an X application, or a wayland client itself. The clients can \
be traditional applications, X servers (rootless or fullscreen) or other \
display servers."
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1d4476a7d98dd5691c53d4d43a510c72 \
                    file://src/wayland-server.c;endline=21;md5=079ae21dbf98ada52ec23744851b0a5c"

SRC_URI = "http://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://always-build-scanner.patch"
SRC_URI[md5sum] = "6e877877c3e04cfb865cfcd0733c9ab1"
SRC_URI[sha256sum] = "f17c938d1c24fd0a10f650a623a2775d329db3168b5732e498b08388ec776fc8"

SRC_URI_append_class-native = " \
    file://disable-macro-checks-not-used-for-scanner.patch \
    "
EXTRA_OECONF_class-native = "--disable-documentation"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"

DEPENDS = "expat libffi wayland-native"

EXTRA_OECONF = "--disable-documentation --with-host-scanner"

# Wayland installs a M4 macro for other projects to use, which uses the target
# pkg-config to find files.  Replace pkg-config with pkg-config-native.
do_install_append_class-native() {
  sed -e 's,PKG_CHECK_MODULES(.*),,g' \
      -e 's,$PKG_CONFIG,pkg-config-native,g' \
      -i ${D}/${datadir}/aclocal/wayland-scanner.m4
}

sysroot_stage_all_append_class-target () {
	rm ${SYSROOT_DESTDIR}/${datadir}/aclocal/wayland-scanner.m4
	cp ${STAGING_DATADIR_NATIVE}/aclocal/wayland-scanner.m4 ${SYSROOT_DESTDIR}/${datadir}/aclocal/
}

FILES_${PN} = "${libdir}/*${SOLIBS}"
FILES_${PN}-dev += "${bindir} ${datadir}/wayland"
