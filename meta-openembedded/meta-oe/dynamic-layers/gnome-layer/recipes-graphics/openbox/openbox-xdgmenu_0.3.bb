SUMMARY = "Openbox configuration tool"
HOMEPAGE = "https://launchpad.net/openbox-xdgmenu/"
SECTION = "x11/wm"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"
DEPENDS = " \
    gnome-menus \
    glib-2.0 \
"
PV = "0.3"

SRC_URI = " \
    http://launchpad.net/openbox-xdgmenu/trunk/0.3/+download/openbox-xdgmenu-0.3.tar.gz \
    file://7_6.diff;striplevel=0 \
    file://port-gnome-menus3.patch \
    file://fix-menu-generation.patch \
"
SRC_URI[sha256sum] = "824e099928aab2fb628f0fa4870ef7bba10b95581e47c2d8fa216709a0f399b3"
UPSTREAM_CHECK_URI="https://launchpad.net/openbox-xdgmenu/"

inherit pkgconfig features_check
# depends on openbox, which is X11-only
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OEMAKE = " \
    CC='${CC}' \
    CFLAGS='${CPPFLAGS} ${CFLAGS} `pkg-config --cflags glib-2.0 libgnome-menu-3.0` -DGMENU_I_KNOW_THIS_IS_UNSTABLE' \
    LDFLAGS='${LDFLAGS} `pkg-config --libs glib-2.0 libgnome-menu-3.0`' \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 openbox-xdgmenu ${D}${bindir}
}

RDEPENDS:${PN} += "virtual-x-terminal-emulator"
