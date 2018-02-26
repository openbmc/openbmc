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
PV = "0.6+git${SRCPV}"
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/kexecboot/kexecboot.git"
SRCREV = "4c4f127e79ac5b8d6b6e2fbb938ccbf12b04c531"
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
