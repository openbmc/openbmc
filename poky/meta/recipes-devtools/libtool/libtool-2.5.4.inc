SUMMARY = "Generic library support script"
DESCRIPTION = "This is GNU libtool, a generic library support script. \
Libtool hides the complexity of generating special library types \
(such as shared libraries) behind a consistent interface."
HOMEPAGE = "http://www.gnu.org/software/libtool/libtool.html"
SECTION = "devel"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce \
    file://libltdl/COPYING.LIB;md5=4bf661c1e3793e55c8d1051bc5e0ae21 "

SRC_URI = "${GNU_MIRROR}/libtool/libtool-${PV}.tar.gz \
           file://0001-ltmain.sh-Fix-sysroot-paths-being-encoded-into-RPATH.patch \
           file://0002-ltmain.in-Don-t-encode-RATHS-which-match-default-lin.patch \
           file://0003-libtool-remove-help2man-dependency.patch \
           file://0004-libtool.m4-Change-libtool-to-handle-sysroots-by-defa.patch \
           file://0005-libtool-Check-for-static-libs-for-internal-compiler-.patch \
           file://0006-libtool-Fix-tools-path-issues.patch \
           file://0007-libtool-Fix-problem-with-libtoolize-in-multilib.patch \
          "

SRC_URI[sha256sum] = "da8ebb2ce4dcf46b90098daf962cffa68f4b4f62ea60f798d0ef12929ede6adf"

inherit autotools texinfo
EXTRA_AUTORECONF = "--exclude=libtoolize"

CACHED_CONFIGUREVARS += "ac_cv_path_GREP=grep"

DEPENDS = "libtool-native"

PACKAGES =+ "libltdl"
FILES:${PN} += "${datadir}/aclocal"

FILES:${PN}-dev:remove = "${datadir}/aclocal"
FILES:libltdl = "${libdir}/libltdl${SOLIBS}"

export CONFIG_SHELL="/bin/bash"
