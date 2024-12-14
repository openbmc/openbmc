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

SRC_URI = "https://gitlab.freedesktop.org/wayland/wayland/-/releases/${PV}/downloads/${BPN}-${PV}.tar.xz \
           file://run-ptest \
           file://0001-build-Fix-strndup-detection-on-MinGW.patch \
           "
SRC_URI[sha256sum] = "864fb2a8399e2d0ec39d56e9d9b753c093775beadc6022ce81f441929a81e5ed"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/wayland/wayland/-/tags"
UPSTREAM_CHECK_REGEX = "releases/(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

inherit meson pkgconfig ptest

PACKAGECONFIG ??= "dtd-validation"
PACKAGECONFIG[dtd-validation] = "-Ddtd_validation=true,-Ddtd_validation=false,libxml2,,"

EXTRA_OEMESON = "-Ddocumentation=false"
EXTRA_OEMESON:class-native = "-Ddocumentation=false"

# Wayland installs a M4 macro for other projects to use, which uses the target
# pkg-config to find files.  Replace pkg-config with pkg-config-native.
do_install:append:class-native() {
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

sysroot_stage_all:append:class-target () {
	rm ${SYSROOT_DESTDIR}/${datadir}/aclocal/wayland-scanner.m4
	cp ${STAGING_DATADIR_NATIVE}/aclocal/wayland-scanner.m4 ${SYSROOT_DESTDIR}/${datadir}/aclocal/
}

PACKAGES =+ "${PN}-tools"

FILES:${PN}-tools = "${bindir}/wayland-scanner"
FILES:${PN}-dev += "${datadir}/${BPN}/wayland-scanner.mk"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-ptest += "binutils sed ${PN}-tools"
