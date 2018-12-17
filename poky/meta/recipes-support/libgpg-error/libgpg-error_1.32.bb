SUMMARY = "Small library that defines common error values for all GnuPG components"
HOMEPAGE = "http://www.gnupg.org/related_software/libgpg-error/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/gpg-error.h.in;beginline=2;endline=18;md5=524d4e810c4dcdc38e4fa28e70a13bf8 \
                    file://src/init.c;beginline=2;endline=17;md5=f01cdfcf747af5380590cfd9bbfeaaf7"


SECTION = "libs"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgpg-error/libgpg-error-${PV}.tar.bz2 \
           file://pkgconfig.patch \
           file://0001-syscfg-Support-ARC-CPUs-and-simplify-aliasing-table.patch \
           file://0002-syscfg-Add-support-for-arc-unknown-linux-gnu.patch \
	  "
SRC_URI[md5sum] = "ef3d928a5a453fa701ecc3bb22be1c64"
SRC_URI[sha256sum] = "c345c5e73cc2332f8d50db84a2280abfb1d8f6d4f1858b9daa30404db44540ca"

BINCONFIG = "${bindir}/gpg-error-config"

inherit autotools binconfig-disabled pkgconfig gettext multilib_header multilib_script

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/gpgrt-config"

CPPFLAGS += "-P"
do_compile_prepend() {
	TARGET_FILE=linux-gnu
	if [ ${TARGET_OS} = "mingw32" ]; then
		# There are no arch specific syscfg files for mingw32
		TARGET_FILE=
	elif [ ${TARGET_ARCH} = "arc" ]; then
		# ARC syscfg file is automatically aliased to i686-pc-linux-gnu
		TARGET_FILE=
	elif [ ${TARGET_OS} != "linux" ]; then
		TARGET_FILE=${TARGET_OS}
	fi

	case ${TARGET_ARCH} in
	  aarch64_be) TUPLE=aarch64-unknown-linux-gnu ;;
	  arm)	      TUPLE=arm-unknown-linux-gnueabi ;;
	  armeb)      TUPLE=arm-unknown-linux-gnueabi ;;
	  i586|i686)  TUPLE=i686-unknown-linux-gnu;;
	  mips64*)    TUPLE=mips64el-unknown-linux-gnuabi64 ;;
	  mips*el)    TUPLE=mipsel-unknown-linux-gnu ;;
	  mips*)      TUPLE=mips-unknown-linux-gnu ;;
	  x86_64)     TUPLE=x86_64-unknown-linux-gnu ;;
	  ppc64)      TUPLE=powerpc64-unknown-linux-gnu ;;
	  ppc64le)    TUPLE=powerpc64le-unknown-linux-gnu ;;
	  *)          TUPLE=${TARGET_ARCH}-unknown-linux-gnu ;;
	esac

	if [ -n "$TARGET_FILE" ]; then
		cp ${S}/src/syscfg/lock-obj-pub.$TUPLE.h \
			${S}/src/syscfg/lock-obj-pub.$TARGET_FILE.h
	fi
}

do_install_append() {
	# we don't have common lisp in OE
	rm -rf "${D}${datadir}/common-lisp/"
	oe_multilib_header gpg-error.h gpgrt.h
}

FILES_${PN}-dev += "${bindir}/gpg-error"
FILES_${PN}-doc += "${datadir}/libgpg-error/errorref.txt"

BBCLASSEXTEND = "native nativesdk"
