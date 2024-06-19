SUMMARY = "Generic library support script"
DESCRIPTION = "This is GNU libtool, a generic library support script. \
Libtool hides the complexity of generating special library types \
(such as shared libraries) behind a consistent interface."
HOMEPAGE = "http://www.gnu.org/software/libtool/libtool.html"
SECTION = "devel"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://libltdl/COPYING.LIB;md5=4fbd65380cdd255951079008b364516c "

SRC_URI = "https://alpha.gnu.org/gnu/libtool/libtool-${PV}.tar.gz \
           file://0001-ltmain.in-Handle-trailing-slashes-on-install-command.patch \
           file://0003-ltmain.in-Add-missing-sysroot-to-library-path.patch \
           file://0004-ltmain.sh-Fix-sysroot-paths-being-encoded-into-RPATH.patch \
           file://0005-ltmain.in-Don-t-encode-RATHS-which-match-default-lin.patch \
           file://dont-depend-on-help2man.patch \
           file://0006-libtool.m4-Handle-as-a-sysroot-correctly.patch \
           file://0008-libtool-Check-for-static-libs-for-internal-compiler-.patch \
           file://nohardcodepaths.patch \
           file://filefix.patch \
          "



SRC_URI[sha256sum] = "d438c317455d85554ce2f998be48866fd40cfcbf834d60b3026f4fc0ed583d23"

inherit autotools texinfo
EXTRA_AUTORECONF = "--exclude=libtoolize"

CACHED_CONFIGUREVARS += "ac_cv_path_GREP=grep"

DEPENDS = "libtool-native"

PACKAGES =+ "libltdl"
FILES:${PN} += "${datadir}/aclocal"

FILES:${PN}-dev:remove = "${datadir}/aclocal"
FILES:libltdl = "${libdir}/libltdl${SOLIBS}"

export CONFIG_SHELL="/bin/bash"
