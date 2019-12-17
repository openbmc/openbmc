SUMMARY = "X screen saver and locker"

LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://driver/xscreensaver.h;endline=10;md5=e141ab5822fb2d43694e1e47b59fc0df"

SRC_URI = "https://www.jwz.org/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "a5da62b91271f4e8afcc73d44697364b"
SRC_URI[sha256sum] = "48ce1880f18b5321182be0c033aeceb5ec5628a1505b9d1ff69dbf06093c2426"

SRC_URI += " \
    file://xscreensaver.service \
    file://fix-buildscripts.patch \
    file://tweak-app-defaults.patch \
"

DEPENDS = "intltool-native libx11 libxext libxt libxft glib-2.0-native bc-native"
# These are only needed as part of the stopgap screensaver implementation:
RDEPENDS_${PN} += "liberation-fonts"

inherit systemd perlnative pkgconfig gettext autotools-brokensep features_check

EXTRA_OECONF += "--with-x-app-defaults=${datadir}/X11/app-defaults"

REQUIRED_DISTRO_FEATURES = "x11"

do_install_append() {
    install -D ${WORKDIR}/xscreensaver.service ${D}${systemd_unitdir}/system/xscreensaver.service
}

FILES_${PN} += "${datadir}/X11/app-defaults/XScreenSaver"
SYSTEMD_SERVICE_${PN} = "xscreensaver.service"

CLEANBROKEN = "1"
