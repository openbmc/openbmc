SUMMARY = "Tools for managing Yocto Project style branched kernels"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://git/tools/kgit;beginline=5;endline=9;md5=9c30e971d435e249624278c3e343e501"

DEPENDS = "git-native"

SRCREV = "bb6df0ef2365689cd3df6f76a8838cddae0d9343"
PR = "r12"
PV = "0.2+git${SRCPV}"

inherit native

SRC_URI = "git://git.yoctoproject.org/yocto-kernel-tools.git"
S = "${WORKDIR}"
UPSTREAM_CHECK_COMMITS = "1"

do_compile() { 
	:
}

do_install() {
	cd ${S}/git
	make DESTDIR=${D}${bindir} install
}
