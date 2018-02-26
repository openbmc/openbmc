SUMMARY = "Small library that defines common error values for all GnuPG components"
HOMEPAGE = "http://www.gnupg.org/related_software/libgpg-error/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/gpg-error.h.in;endline=23;md5=beae1e44d8d5c265d194760276033a7c \
                    file://src/init.c;endline=20;md5=872b2389fe9bae7ffb80d2b91225afbc"


SECTION = "libs"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgpg-error/libgpg-error-${PV}.tar.bz2 \
           file://pkgconfig.patch \
	  "

SRC_URI[md5sum] = "5217ef3e76a7275a2a3b569a12ddc989"
SRC_URI[sha256sum] = "4f93aac6fecb7da2b92871bb9ee33032be6a87b174f54abf8ddf0911a22d29d2"

BINCONFIG = "${bindir}/gpg-error-config"

inherit autotools binconfig-disabled pkgconfig gettext
CPPFLAGS += "-P"
do_compile_prepend() {
	TARGET_FILE=linux-gnu
	if [ ${TARGET_OS} = "mingw32" ]; then
		# There are no arch specific syscfg files for mingw32
		TARGET_FILE=
	elif [ ${TARGET_OS} != "linux" ]; then
		TARGET_FILE=${TARGET_OS}
	fi

	case ${TARGET_ARCH} in
	  aarch64_be) TUPLE=aarch64-unknown-linux-gnu ;;
	  arm)	      TUPLE=arm-unknown-linux-gnueabi ;;
	  armeb)      TUPLE=arm-unknown-linux-gnueabi ;;
	  i586|i686)  TUPLE=i686-pc-linux-gnu ;;
	  mips64*)    TUPLE=mips64el-unknown-linux-gnuabi64 ;;
	  mips*el)    TUPLE=mipsel-unknown-linux-gnu ;;
	  mips*)      TUPLE=mips-unknown-linux-gnu ;;
	  x86_64)     TUPLE=x86_64-pc-linux-gnu ;;
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
}

FILES_${PN}-dev += "${bindir}/gpg-error"
FILES_${PN}-doc += "${datadir}/libgpg-error/errorref.txt"

BBCLASSEXTEND = "native nativesdk"
