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

SRC_URI_append = " file://fix-cross-compile.patch"
SRC_URI[md5sum] = "18a619849f85c24d784c7fa27279ca4b"
SRC_URI[sha256sum] = "ed04ae32034b8e13a78495ca2bd7789a20ec7f67891ab9e92826a944371eabef"

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/desktop-directories"
