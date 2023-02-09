SUMMARY = "kexecboot linux-as-bootloader"
DESCRIPTION = "kexecboot is a graphical linux-as-bootloader implementation based on kexec."
HOMEPAGE = "https://github.com/kexecboot/kexecboot/wiki"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
PV = "0.6+git${SRCPV}"
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/kexecboot/kexecboot.git;branch=master;protocol=https"
SRC_URI:append:libc-klibc = "\
    file://0001-kexecboot-Use-new-reboot-API-with-klibc.patch \
    file://0001-make-Add-compiler-includes-in-cflags.patch \
"
SRCREV = "5a5e04be206140059f42ac786d424da1afaa04b6"
inherit autotools

EXTRA_OECONF = "--enable-textui --enable-delay=2 --enable-evdev-rate=1000,250"

CFLAGS += "-fcommon"

do_install () {
    install -D -m 0755 ${B}/src/kexecboot ${D}${bindir}/kexecboot
    install -d ${D}/proc
    install -d ${D}/mnt
    install -d ${D}/dev
    install -d ${D}/sys
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} += " ${bindir}/kexecboot /init /proc /mnt /dev /sys"

pkg_postinst:${PN} () {
    ln -sf ${bindir}/kexecboot $D/init
}

BBCLASSEXTEND = "klibc"
