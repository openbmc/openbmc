SUMMARY = "Wayland, a protocol between a compositor and clients"
DESCRIPTION = "Wayland is a protocol for a compositor to talk to its clients \
as well as a C library implementation of that protocol. The compositor can be \
a standalone display server running on Linux kernel modesetting and evdev \
input devices, an X application, or a wayland client itself. The clients can \
be traditional applications, X servers (rootless or fullscreen) or other \
display servers."
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b31d8f53b6aaf2b4985d7dd7810a70d1 \
                    file://src/wayland-server.c;endline=24;md5=b8e046164a766bb1ede8ba38e9dcd7ce"

DEPENDS = "expat libxml2 libffi wayland-native"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
	   file://fixpathinpcfiles.patch \
           file://0001-scanner-Add-configure-check-for-strndup.patch \
           file://0002-Move-wl_priv_signal-to-wayland-server-private.h.patch \
           "
SRC_URI[md5sum] = "d91f970aea11fd549eae023d06f91af3"
SRC_URI[sha256sum] = "72aa11b8ac6e22f4777302c9251e8fec7655dc22f9d94ee676c6b276f95f91a4"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-documentation --with-host-scanner"
EXTRA_OECONF_class-native = "--disable-documentation --disable-libraries"

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

BBCLASSEXTEND = "native nativesdk"
