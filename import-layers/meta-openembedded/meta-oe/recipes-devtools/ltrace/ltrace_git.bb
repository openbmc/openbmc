SUMMARY = "ltrace intercepts and records dynamic library calls"

DESCRIPTION = "ltrace intercepts and records dynamic library calls \
which are called by an executed process and the signals received by that process. \
It can also intercept and print the system calls executed by the program.\
"
HOMEPAGE = "http://ltrace.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

PE = "1"
PV = "7.3+git${SRCPV}"
SRCREV = "37ecc41b58be3dbdd79592a76e331b5b371e4f81"

DEPENDS = "elfutils"
RDEPENDS_${PN} = "elfutils"
SRC_URI = "git://anonscm.debian.org/collab-maint/ltrace.git;branch=ltrace-0.7 \
           file://ltrace-0.7.2-unused-typedef.patch \
           file://configure-allow-to-disable-selinux-support.patch \
           file://0001-ltrace-fix-gcc-5-logical-not-parentheses.patch \
          "
S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG[unwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,libselinux"

do_configure_prepend () {
	( cd ${S}; ./autogen.sh )
}
