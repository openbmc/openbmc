SUMMARY = "Implementation of a fully functional filesystem in a userspace program"
DESCRIPTION = "FUSE (Filesystem in Userspace) is a simple interface for userspace \
               programs to export a virtual filesystem to the Linux kernel. FUSE \
               also aims to provide a secure method for non privileged users to \
               create and mount their own filesystem implementations. \
              "
HOMEPAGE = "https://github.com/libfuse/libfuse"
SECTION = "libs"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://GPL2.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LGPL2.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE;md5=a55c12a2d7d742ecb41ca9ae0a6ddc66"

SRC_URI = "https://github.com/libfuse/libfuse/releases/download/fuse-${PV}/fuse-${PV}.tar.xz \
"
SRC_URI[sha256sum] = "b2e283485d47404ac896dd0bb7f7ba81e1470838e677e45f659804c3a3b69666"

S = "${WORKDIR}/fuse-${PV}"

UPSTREAM_CHECK_URI = "https://github.com/libfuse/libfuse/releases"
UPSTREAM_CHECK_REGEX = "fuse\-(?P<pver>3(\.\d+)+).tar.xz"

CVE_PRODUCT = "fuse_project:fuse"

inherit meson pkgconfig ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
	bash \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}

DEPENDS = "udev"

PACKAGES =+ "fuse3-utils"

RPROVIDES:${PN}-dbg += "fuse3-utils-dbg"

RRECOMMENDS:${PN}:class-target = "kernel-module-fuse fuse3-utils"

FILES:${PN} += "${libdir}/libfuse3.so.*"
FILES:${PN}-dev += "${libdir}/libfuse3*.la"

EXTRA_OEMESON += " \
     -Dexamples=false \
"

# Forbid auto-renaming to libfuse3-utils
FILES:fuse3-utils = "${bindir} ${base_sbindir}"
DEBIAN_NOAUTONAME:fuse3-utils = "1"
DEBIAN_NOAUTONAME:${PN}-dbg = "1"

do_install:append() {
    rm -rf ${D}${base_prefix}/dev
}
