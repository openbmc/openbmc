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
           file://0001-ltmain.in-Handle-trailing-slashes-on-install-command.patch \
           file://0002-libtool.m4-Rename-the-with-sysroot-option-to-avoid-c.patch \
           file://0003-ltmain.in-Add-missing-sysroot-to-library-path.patch \
           file://0004-ltmain.sh-Fix-sysroot-paths-being-encoded-into-RPATH.patch \
           file://0005-ltmain.in-Don-t-encode-RATHS-which-match-default-lin.patch \
           file://dont-depend-on-help2man.patch \
           file://0006-libtool.m4-Handle-as-a-sysroot-correctly.patch \
           file://nohardcodepaths.patch \
           file://unwind-opt-parsing.patch \
           file://0007-libtool-Fix-support-for-NIOS2-processor.patch \
           file://0008-libtool-Check-for-static-libs-for-internal-compiler-.patch \
           file://0009-Makefile.am-make-sure-autoheader-run-before-autoconf.patch \
           file://0010-Makefile.am-make-sure-autoheader-run-before-automake.patch \
           file://0011-ltmain.in-Handle-prefix-map-compiler-options-correct.patch \
           file://0012-libtool.m4-For-reproducibility-stop-encoding-hostnam.patch \
           file://libool.m4-add-ARFLAGS-variable.patch \
           file://ARFLAGS-use-cr-instead-of-cru-by-default.patch \
          "

SRC_URI[md5sum] = "addf44b646ddb4e3919805aa88fa7c5e"
SRC_URI[sha256sum] = "e3bd4d5d3d025a36c21dd6af7ea818a2afcd4dfc1ea5a17b39d7854bcd0c06e3"

do_compile:prepend () {
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
FILES:${PN} += "${datadir}/aclocal"

FILES:${PN}-dev:remove = "${datadir}/aclocal"
FILES:libltdl = "${libdir}/libltdl${SOLIBS}"

export CONFIG_SHELL="/bin/bash"
