SUMMARY = "xorg.conf keyboard layout callout"
DESCRIPTION = "system-setup-keyboard is a daemon to monitor the keyboard layout configured in \
/etc/sysconfig/keyboard and transfer this into the matching xorg.conf.d snippet."

HOMEPAGE = "https://git.fedorahosted.org/git/system-setup-keyboard.git"
SECTION = "Applications/System"

SRC_URI = "https://git.fedorahosted.org/cgit/${PN}.git/snapshot/${BP}.tar.gz"
SRC_URI[md5sum] = "399003968ccc739cddd9cc370af377a0"
SRC_URI[sha256sum] = "1ef6ef79c3588e85d7f42e99eb80a2e459f966284cf029c2d6fc1b645abcb860"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bf57969a59612c5aca007b340c49d3a2"

inherit pythonnative

DEPENDS = "glib-2.0 system-config-keyboard-native"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile_prepend() {
    ${PYTHON} -v get_layouts.py > keyboards.h
}

do_install() {
    oe_runmake install DESTDIR=${D}
    install -d ${D}/etc/X11/xorg.conf.d
    touch ${D}/etc/X11/xorg.conf.d/00-system-setup-keyboard.conf
}

FILES_${PN} += "${systemd_unitdir}/system/${BPN}.service"
