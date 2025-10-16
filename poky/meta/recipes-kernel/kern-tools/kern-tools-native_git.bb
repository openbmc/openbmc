SUMMARY = "Tools for managing Yocto Project style branched kernels"
DESCRIPTION = "Powerful set of tools or managing Yocto Linux kernel sources \
and configuration data. You can use these tools to make a single configuration \
change, apply multiple patches, or work with your own kernel sources."
HOMEPAGE = "https://www.yoctoproject.org/"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "\
	file://tools/kgit;beginline=5;endline=9;md5=9c30e971d435e249624278c3e343e501 \
	file://Kconfiglib/LICENSE.txt;md5=712177a72a3937909543eda3ad1bfb7c \
"

DEPENDS += "git-replacement-native"

SRCREV = "f589e1df23251d8319063da0a61c1016b2a0bf85"
PV = "0.3+git"

inherit native

SRC_URI = "git://git.yoctoproject.org/yocto-kernel-tools.git;branch=master;protocol=https"
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
