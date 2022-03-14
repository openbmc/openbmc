SUMMARY = "Tools for managing Yocto Project style branched kernels"
DESCRIPTION = "Powerful set of tools or managing Yocto Linux kernel sources \
and configuration data. You can use these tools to make a single configuration \
change, apply multiple patches, or work with your own kernel sources."
HOMEPAGE = "https://www.yoctoproject.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://tools/kgit;beginline=5;endline=9;md5=9c30e971d435e249624278c3e343e501"

DEPENDS = "git-native"

SRCREV = "90598a5fae1172e3f7782a1b02f7b7518efd32c8"
PV = "0.3+git${SRCPV}"

inherit native

SRC_URI = "git://git.yoctoproject.org/yocto-kernel-tools.git;branch=master"
S = "${WORKDIR}/git"

do_configure() {
	:
}

do_compile() { 
	:
}

do_install() {
	oe_runmake DESTDIR=${D}${bindir} install
}
