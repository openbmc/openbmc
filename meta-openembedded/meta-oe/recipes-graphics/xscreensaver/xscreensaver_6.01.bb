SUMMARY = "X screen saver and locker"
HOMEPAGE = "https://www.jwz.org/xscreensaver/"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://driver/xscreensaver.h;endline=10;md5=c3ce41cdff745eb1dc9d4fcfbffb4d51"

SRC_URI = "https://www.jwz.org/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "085484665d91f60b4a1dedacd94bcf9b74b0fb096bcedc89ff1c245168e5473b"

SRC_URI += " \
    file://xscreensaver.service \
    file://0001-build-Do-not-build-po-files.patch \
    file://tweak-app-defaults.patch \
"

DEPENDS = "intltool-native libx11 libxext libxt libxft libxi glib-2.0-native bc-native libpam"
# These are only needed as part of the stopgap screensaver implementation:
RDEPENDS:${PN} = " \
    liberation-fonts \
    xuser-account \
"

inherit systemd perlnative pkgconfig gettext autotools-brokensep features_check


EXTRA_OECONF += "--with-app-defaults=${datadir}/X11/app-defaults --libdir=${STAGING_LIBDIR} \
                 --includedir=${STAGING_INCDIR} --with-pam --with-login-manager"

EXTRA_OEMAKE += "install_prefix=${D}"
REQUIRED_DISTRO_FEATURES = "x11 pam"

do_install:append() {
    install -D ${WORKDIR}/xscreensaver.service ${D}${systemd_unitdir}/system/xscreensaver.service
    for f in xscreensaver-getimage-file xscreensaver-getimage-video webcollage xscreensaver-text vidwhacker
    do
        sed -i -e "s|${STAGING_BINDIR_NATIVE}/perl-native/perl|/usr/bin/perl|g" ${D}/${libexecdir}/${PN}/$f
    done
}

PACKAGES =+ "${PN}-perl"

FILES:${PN}-perl = "\
    ${libexecdir}/${PN}/xscreensaver-getimage-file \
    ${libexecdir}/${PN}/xscreensaver-getimage-video \
    ${libexecdir}/${PN}/webcollage \
    ${libexecdir}/${PN}/xscreensaver-text \
    ${libexecdir}/${PN}/vidwhacker \
    "
FILES:${PN} += "${datadir}/X11/app-defaults/XScreenSaver ${datadir}/fonts"
SYSTEMD_SERVICE:${PN} = "xscreensaver.service"

RDEPENDS:${PN}-perl = "perl"

CLEANBROKEN = "1"
