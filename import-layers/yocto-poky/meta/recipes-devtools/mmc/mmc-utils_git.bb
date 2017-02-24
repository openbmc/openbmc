SUMMARY = "Userspace tools for MMC/SD devices"
HOMEPAGE = "http://git.kernel.org/cgit/linux/kernel/git/cjb/mmc-utils.git/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://mmc.c;beginline=1;endline=20;md5=fae32792e20f4d27ade1c5a762d16b7d"

SRCBRANCH ?= "master"
SRCREV = "bb779acfc385d135b32a6998c1d1fceef0491400"

PV = "0.1"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/cjb/mmc-utils.git;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

CFLAGS_append_powerpc64 = " -D__SANE_USERSPACE_TYPES__"
CFLAGS_append_mips64 = " -D__SANE_USERSPACE_TYPES__"
CFLAGS_append_mips64n32 = " -D__SANE_USERSPACE_TYPES__"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 mmc ${D}${bindir}
}
