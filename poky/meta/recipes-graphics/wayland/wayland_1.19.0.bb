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

DEPENDS = "expat libffi wayland-native"

SRC_URI = "https://wayland.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
           file://run-ptest \
           file://0002-meson.build-find-the-native-wayland-scanner-directly.patch \
           file://0002-Do-not-hardcode-the-path-to-wayland-scanner.patch \
           file://0001-build-Fix-strndup-detection-on-MinGW.patch \
           "
SRC_URI[sha256sum] = "baccd902300d354581cd5ad3cc49daa4921d55fb416a5883e218750fef166d15"

UPSTREAM_CHECK_URI = "https://wayland.freedesktop.org/releases.html"

inherit meson pkgconfig ptest

PACKAGECONFIG ??= "dtd-validation"
PACKAGECONFIG[dtd-validation] = "-Ddtd_validation=true,-Ddtd_validation=false,libxml2,,"

EXTRA_OEMESON = "-Ddocumentation=false"
EXTRA_OEMESON_class-native = "-Ddocumentation=false -Dlibraries=false"

# Wayland installs a M4 macro for other projects to use, which uses the target
# pkg-config to find files.  Replace pkg-config with pkg-config-native.
do_install_append_class-native() {
  sed -e 's,PKG_CHECK_MODULES(.*),,g' \
      -e 's,$PKG_CONFIG,pkg-config-native,g' \
      -i ${D}/${datadir}/aclocal/wayland-scanner.m4
}

do_install_ptest() {
    mkdir -p ${D}${PTEST_PATH}/tests/data
    cp -rf ${B}/tests/*-test ${B}/tests/*-checker ${D}${PTEST_PATH}/tests
    cp -rf ${B}/tests/*-checker ${D}${PTEST_PATH}
    cp -rf ${S}/tests/scanner-test.sh ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/data/* ${D}${PTEST_PATH}/tests/data/
    cp -rf ${S}/egl/wayland-egl-symbols-check ${D}${PTEST_PATH}/tests/
}

sysroot_stage_all_append_class-target () {
	rm ${SYSROOT_DESTDIR}/${datadir}/aclocal/wayland-scanner.m4
	cp ${STAGING_DATADIR_NATIVE}/aclocal/wayland-scanner.m4 ${SYSROOT_DESTDIR}/${datadir}/aclocal/
}

FILES_${PN} = "${libdir}/*${SOLIBS}"
FILES_${PN}-dev += "${bindir} ${datadir}/wayland"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}-ptest += "binutils sed"
