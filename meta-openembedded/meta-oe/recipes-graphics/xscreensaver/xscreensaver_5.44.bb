SUMMARY = "X screen saver and locker"
HOMEPAGE = "https://www.jwz.org/xscreensaver/"
LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://driver/xscreensaver.h;endline=10;md5=2b97002f72fbfc9329b4336e798f2463"

SRC_URI = "https://www.jwz.org/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "9f764e561f9939f8684a66ec98b27cba"
SRC_URI[sha256sum] = "73d8089cfc7d7363b5dac99b5b01dffb3429d0a855e6af16ce9a4b7777017b95"

SRC_URI += " \
    file://xscreensaver.service \
    file://fix-buildscripts.patch \
    file://tweak-app-defaults.patch \
"

DEPENDS = "intltool-native libx11 libxext libxt libxft glib-2.0-native bc-native"
# These are only needed as part of the stopgap screensaver implementation:
RDEPENDS_${PN} = " \
    liberation-fonts \
    xuser-account \
"

inherit systemd perlnative pkgconfig gettext autotools-brokensep features_check

EXTRA_OECONF += "--with-x-app-defaults=${datadir}/X11/app-defaults"

REQUIRED_DISTRO_FEATURES = "x11"

do_install_append() {
    install -D ${WORKDIR}/xscreensaver.service ${D}${systemd_unitdir}/system/xscreensaver.service
}

FILES_${PN} += "${datadir}/X11/app-defaults/XScreenSaver"
SYSTEMD_SERVICE_${PN} = "xscreensaver.service"

CLEANBROKEN = "1"
