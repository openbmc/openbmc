SUMMARY = "Xfce screensaver Application"
DESCRIPTION = "Xfce screensaver is a screen saver and locker that aims to have simple, sane, secure defaults and be well integrated with the desktop."
HOMEPAGE = "https://git.xfce.org/apps/xfce4-screensaver/about/"
SECTION = "x11/application"

LICENSE = "GPLv2+ & LGPLv2+ "
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    "

DEPENDS = "dbus-glib garcon gtk+3 libxklavier libxscrnsaver virtual/libx11 xfconf"

inherit xfce-app

SRC_URI_append = " \
    file://fix-cross-compile.patch \
    file://fix-pam-config.patch \
"
SRC_URI[md5sum] = "982f9a355456320933ec08313523a567"
SRC_URI[sha256sum] = "fdecf0fc432b59defba2dd706051696d47227ca0744c7971014021190126f2d5"

do_install_append() {
    install -D -m 0644 ${S}/data/xfce4-screensaver.common-auth ${D}${sysconfdir}/pam.d/xfce4-screensaver
}

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/desktop-directories"
