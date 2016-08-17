###########################
# Configure options:
#
#  --enable-static-linking compile kexecboot as static executable [default=no]
#  --enable-fbui           support framebuffer menu [default=yes]
#  --enable-fbui-width     limit FB UI width to specified value [default=no]
#  --enable-fbui-height    limit FB UI height to specified value [default=no]
#  --enable-textui         support console text user interface [default=no]
#  --enable-cfgfiles       support config files [default=yes]
#  --enable-icons          support custom icons (depends on fbui) [default=yes]
#  --enable-zaurus         compile Sharp Zaurus specific code [default=no]
#  --enable-zimage         compile with zImage support [default=yes]
#  --enable-uimage         compile with uImage support [default=no]
#  --enable-machine-kernel look for machine-specific zImage kernel [default=no]
#  --enable-devices-recreating
#                          enable devices re-creating [default=yes]
#  --enable-debug          enable debug output [default=no]
#  --enable-host-debug     allow for non-destructive executing of kexecboot on
#                            host system [default=no]
#  --enable-numkeys        enable menu item selection by keys [0-9] [default=yes]
#  --enable-bg-buffer      enable buffer for pre-drawed FB GUI background
#
#  --enable-timeout        allow to boot 1st kernel after timeout in seconds
#                            [default=no]
#  --enable-delay          specify delay before device scanning, allowing
#                            initialization of old CF/SD cards [default=1]
#  --enable-bpp            enable support of specified bpp modes
#                            (all,32,24,18,16,4,2,1) [default=all]
#  --enable-evdev-rate     change evdev (keyboard/mouse) repeat rate
#                            in milliseconds e.g. "1000,250" [default=no]
#  --with-kexec-binary     look for kexec binary at path
#                            [default="/usr/sbin/kexec"]
##########################
SUMMARY = "kexecboot linux-as-bootloader"
DESCRIPTION = "kexecboot is a graphical linux-as-bootloader implementation based on kexec."
HOMEPAGE = "http://kexecboot.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

S = "${WORKDIR}/kexecboot-${PV}"
SRC_URI = "https://github.com/kexecboot/kexecboot/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "46b7c1a6f20531be56445ebb8669a2b8"
SRC_URI[sha256sum] = "6b360b8aa59bc5d68a96705349a0dd416f8ed704e931fa0ac7849298258f0f15"

SRC_URI += "\
            file://0001-kexecboot-fix-build-when-S-B.patch \
            file://0002-kexecboot-fix-configure-warnings.patch \
            file://0003-kexecboot-do-not-hardcode-MOUNTPOINT.patch \
            file://0004-kexecboot.c-workaround-for-absolute-kernel-and-initr.patch \
            \
            file://0005-rgb.h-fix-build-with-gcc5.patch \
            "

inherit autotools

EXTRA_OECONF = "--enable-textui --enable-delay=2 --enable-evdev-rate=1000,250"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 kexecboot ${D}${bindir}
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
