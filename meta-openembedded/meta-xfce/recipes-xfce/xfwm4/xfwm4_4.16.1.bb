DESCRIPTION = "Xfce4 Window Manager"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "virtual/libx11 libxfce4ui libwnck3 libxinerama"

inherit xfce update-alternatives features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "b5b24ca04bd73c642db0a4b4df81d262381d758f01b51108257d48b391b8718c"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'epoxy', '', d)} \
    xpresent \
    startup-notification \
"

PACKAGECONFIG[epoxy] = "--enable-epoxy,--disable-epoxy,libepoxy"
PACKAGECONFIG[xpresent] = "--enable-xpresent,--disable-xpresent,libxpresent"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

python populate_packages_prepend () {
    themedir = d.expand('${datadir}/themes')
    do_split_packages(d, themedir, '^(.*)', 'xfwm4-theme-%s', 'XFWM4 theme %s', allow_dirs=True)
}

PACKAGES_DYNAMIC += "^xfwm4-theme-.*"

ALTERNATIVE_${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/xfwm4"
ALTERNATIVE_PRIORITY[x-window-manager] = "30"

RDEPENDS_${PN} = "xfwm4-theme-default"
FILES_${PN} += "${libdir}/xfce4/xfwm4/helper-dialog \
                ${datadir}/xfwm4/defaults \
"
