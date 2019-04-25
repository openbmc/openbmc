SUMMARY = "kexecboot linux-as-bootloader"
DESCRIPTION = "kexecboot is a graphical linux-as-bootloader implementation based on kexec."
HOMEPAGE = "http://kexecboot.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
PV = "0.6+git${SRCPV}"
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/kexecboot/kexecboot.git"
SRC_URI_append_libc-klibc = " file://0001-kexecboot-Use-new-reboot-API-with-klibc.patch "

SRCREV = "7409a1e0aaea61af87c4eca0149cec18a9f58ab6"
inherit autotools

EXTRA_OECONF = "--enable-textui --enable-delay=2 --enable-evdev-rate=1000,250"

do_install () {
    install -D -m 0755 ${B}/src/kexecboot ${D}${bindir}/kexecboot
    install -d ${D}/proc
    install -d ${D}/mnt
    install -d ${D}/dev
    install -d ${D}/sys
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} += " ${bindir}/kexecboot /init /proc /mnt /dev /sys"

pkg_postinst_${PN} () {
    ln -sf ${bindir}/kexecboot $D/init
}

BBCLASSEXTEND = "klibc"
