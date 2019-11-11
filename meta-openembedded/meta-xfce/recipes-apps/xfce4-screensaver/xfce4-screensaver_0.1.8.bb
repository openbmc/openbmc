SUMMARY = "Xfce screensaver Application"
DESCRIPTION = "Xfce screensaver is a screen saver and locker that aims to have simple, sane, secure defaults and be well integrated with the desktop."
HOMEPAGE = "https://git.xfce.org/apps/xfce4-screensaver/about/"
SECTION = "x11/application"

LICENSE = "GPLv2+ & LGPLv2+ "
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    "

DEPENDS = "dbus-glib garcon gtk+3 libxklavier libxscrnsaver virtual/libx11 xfconf libwnck3"

inherit xfce-app

SRC_URI_append = " \
    file://fix-cross-compile.patch \
    file://fix-pam-config.patch \
"
SRC_URI[md5sum] = "0fea7b676e6e533a3f305c6f642fe0cd"
SRC_URI[sha256sum] = "4056045ea5fd3eccfe328b86ae245ee4949b9e3044e42ca29c492c0f4ac860d7"

do_install_append() {
    install -D -m 0644 ${S}/data/xfce4-screensaver.common-auth ${D}${sysconfdir}/pam.d/xfce4-screensaver
}

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/desktop-directories"
RDEPENDS_${PN} += "python3-core"
