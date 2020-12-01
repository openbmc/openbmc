SUMMARY = "Displays the full path of shell commands"
DESCRIPTION = "Which is a utility that prints out the full path of the \
executables that bash(1) would execute when the passed \
program names would have been entered on the shell prompt. \
It does this by using the exact same algorithm as bash."
SECTION = "libs"
HOMEPAGE = "https://carlowood.github.io/which/"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
                    file://which.c;beginline=1;endline=17;md5=a9963693af2272e7a8df6f231164e7a2"
DEPENDS     = "cwautomacros-native"

inherit autotools texinfo update-alternatives

PR = "r3"

EXTRA_OECONF = "--disable-iberty"

SRC_URI = "${GNU_MIRROR}/which/which-${PV}.tar.gz \
           file://automake.patch \
           "

SRC_URI[md5sum] = "097ff1a324ae02e0a3b0369f07a7544a"
SRC_URI[sha256sum] = "f4a245b94124b377d8b49646bf421f9155d36aa7614b6ebf83705d3ffc76eaad"

do_configure_prepend() {
	sed -i -e 's%@ACLOCAL_CWFLAGS@%-I ${STAGING_DIR_NATIVE}/usr/share/cwautomacros/m4%g' ${S}/Makefile.am ${S}/tilde/Makefile.am
}

ALTERNATIVE_${PN} = "which"
ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-doc = "which.1"
ALTERNATIVE_LINK_NAME[which.1] = "${mandir}/man1/which.1"
