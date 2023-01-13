DESCRIPTION = "Xfce4 Window Manager"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "virtual/libx11 libxfce4ui libwnck3 libxinerama"

inherit xfce update-alternatives features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "92cd1b889bb25cb4bc06c1c6736c238d96e79c1e706b9f77fad0a89d6e5fc13f"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'epoxy', '', d)} \
    xpresent \
    startup-notification \
"

PACKAGECONFIG[epoxy] = "--enable-epoxy,--disable-epoxy,libepoxy"
PACKAGECONFIG[xpresent] = "--enable-xpresent,--disable-xpresent,libxpresent"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

python populate_packages:prepend () {
    themedir = d.expand('${datadir}/themes')
    do_split_packages(d, themedir, '^(.*)', 'xfwm4-theme-%s', 'XFWM4 theme %s', allow_dirs=True)
}

PACKAGES_DYNAMIC += "^xfwm4-theme-.*"

ALTERNATIVE:${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/xfwm4"
ALTERNATIVE_PRIORITY[x-window-manager] = "30"

RDEPENDS:${PN} = "xfwm4-theme-default"
FILES:${PN} += "${libdir}/xfce4/xfwm4/helper-dialog \
                ${datadir}/xfwm4/defaults \
"
