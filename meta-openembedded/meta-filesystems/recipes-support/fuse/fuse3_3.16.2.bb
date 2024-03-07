SUMMARY = "Implementation of a fully functional filesystem in a userspace program"
DESCRIPTION = "FUSE (Filesystem in Userspace) is a simple interface for userspace \
               programs to export a virtual filesystem to the Linux kernel. FUSE \
               also aims to provide a secure method for non privileged users to \
               create and mount their own filesystem implementations. \
              "
HOMEPAGE = "https://github.com/libfuse/libfuse"
SECTION = "libs"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://GPL2.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://LGPL2.txt;md5=4fbd65380cdd255951079008b364516c \
    file://LICENSE;md5=a55c12a2d7d742ecb41ca9ae0a6ddc66 \
"

SRC_URI = "https://github.com/libfuse/libfuse/releases/download/fuse-${PV}/fuse-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "f797055d9296b275e981f5f62d4e32e089614fc253d1ef2985851025b8a0ce87"

S = "${WORKDIR}/fuse-${PV}"

UPSTREAM_CHECK_URI = "https://github.com/libfuse/libfuse/releases"
UPSTREAM_CHECK_REGEX = "fuse\-(?P<pver>3(\.\d+)+).tar.xz"

CVE_PRODUCT = "fuse_project:fuse"

inherit meson pkgconfig ptest

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-looseversion \
    bash \
"
RRECOMMENDS:${PN}-ptest += " kernel-module-cuse"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    install -d ${D}${PTEST_PATH}/example
    install -d ${D}${PTEST_PATH}/util
    cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/

    example_excutables=`find ${B}/example -type f -executable`
    util_excutables=`find ${B}/util -type f -executable`
    test_excutables=`find ${B}/test -type f -executable`

    for e in $example_excutables
    do
        cp -rf $e  ${D}${PTEST_PATH}/example/
    done

    for e in $util_excutables
    do
        cp -rf $e  ${D}${PTEST_PATH}/util/
    done

    for e in $test_excutables
    do
        cp -rf $e  ${D}${PTEST_PATH}/test
    done
}

DEPENDS = "udev"

PACKAGES =+ "fuse3-utils"

RPROVIDES:${PN}-dbg += "fuse3-utils-dbg"

RRECOMMENDS:${PN}:class-target = "kernel-module-fuse fuse3-utils"

FILES:${PN} += "${libdir}/libfuse3.so.*"
FILES:${PN}-dev += "${libdir}/libfuse3*.la"

# Forbid auto-renaming to libfuse3-utils
FILES:fuse3-utils = "${bindir} ${base_sbindir}"
DEBIAN_NOAUTONAME:fuse3-utils = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"

do_install:append() {
    rm -rf ${D}${base_prefix}/dev
}
