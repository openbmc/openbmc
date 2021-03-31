SUMMARY = "Generic library support script"
DESCRIPTION = "This is GNU libtool, a generic library support script. \
Libtool hides the complexity of generating special library types \
(such as shared libraries) behind a consistent interface."
HOMEPAGE = "http://www.gnu.org/software/libtool/libtool.html"
SECTION = "devel"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://libltdl/COPYING.LIB;md5=4fbd65380cdd255951079008b364516c "

SRC_URI = "${GNU_MIRROR}/libtool/libtool-${PV}.tar.gz \
           file://trailingslash.patch \
           file://rename-with-sysroot.patch \
           file://use-sysroot-in-libpath.patch \
           file://fix-final-rpath.patch \
           file://fix-rpath.patch \
           file://norm-rpath.patch \
           file://dont-depend-on-help2man.patch \
           file://fix-resolve-lt-sysroot.patch \
           file://nohardcodepaths.patch \
           file://unwind-opt-parsing.patch \
           file://0001-libtool-Fix-support-for-NIOS2-processor.patch \
           file://0001-libtool-Check-for-static-libs-for-internal-compiler-.patch \
           file://0001-Makefile.am-make-sure-autoheader-run-before-autoconf.patch \
          "

SRC_URI[md5sum] = "addf44b646ddb4e3919805aa88fa7c5e"
SRC_URI[sha256sum] = "e3bd4d5d3d025a36c21dd6af7ea818a2afcd4dfc1ea5a17b39d7854bcd0c06e3"

do_compile_prepend () {
	# Sometimes this file doesn't get rebuilt, force the issue
	rm -f ${S}/build-aux/ltmain.sh
	make build-aux/ltmain.sh
	./config.status
}

inherit autotools texinfo
EXTRA_AUTORECONF = "--exclude=libtoolize"

CACHED_CONFIGUREVARS += "ac_cv_path_GREP=grep"

DEPENDS = "libtool-native"

PACKAGES =+ "libltdl"
FILES_${PN} += "${datadir}/aclocal"

FILES_${PN}-dev_remove = "${datadir}/aclocal"
FILES_libltdl = "${libdir}/libltdl${SOLIBS}"

export CONFIG_SHELL="/bin/bash"
