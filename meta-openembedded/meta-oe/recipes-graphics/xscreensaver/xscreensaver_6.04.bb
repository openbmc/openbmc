SUMMARY = "X screen saver and locker"
HOMEPAGE = "https://www.jwz.org/xscreensaver/"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://driver/xscreensaver.h;endline=10;md5=c3ce41cdff745eb1dc9d4fcfbffb4d51"

SRC_URI = "https://www.jwz.org/${BPN}/${BP}.tar.gz \
    file://xscreensaver.service \
    file://0001-Tweak-app-defaults.patch \
    file://0002-build-Do-not-build-po-files.patch \
    file://0001-configure-Ignore-CONF_STATUS-for-gtk-and-openGL-need.patch \
"
MIRRORS += "https://www.jwz.org/${BPN} https://ftp.osuosl.org/pub/blfs/conglomeration/${BPN}"

SRC_URI[sha256sum] = "787014b29f0c5793ecc2d93e1109a049ff48ab0c29b851dab34f683ceef6b152"

DEPENDS = "intltool-native libx11 libxext libxt libxft libxi glib-2.0-native bc-native libpam jpeg"
# These are only needed as part of the stopgap screensaver implementation:
RDEPENDS:${PN} = " \
    liberation-fonts \
    xuser-account \
"

inherit systemd perlnative pkgconfig gettext autotools-brokensep features_check


EXTRA_OECONF += "--with-app-defaults=${datadir}/X11/app-defaults --libdir=${STAGING_LIBDIR} \
                 --includedir=${STAGING_INCDIR} --with-pam --with-login-manager"

EXTRA_OEMAKE += "install_prefix=${D} GTK_DATADIR=${datadir}"
REQUIRED_DISTRO_FEATURES = "x11 pam"

PACKAGECONFIG = "png ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"

PACKAGECONFIG[systemd] = "--with-systemd=yes,--with-systemd=no,systemd"
PACKAGECONFIG[png] = "--with-png=yes,--with-png=no,libpng"

CONFIGUREOPTS:remove = "--disable-silent-rules --disable-dependency-tracking"
EXTRA_OECONF:remove = "--disable-static"

do_install:append() {
    install -D ${UNPACKDIR}/xscreensaver.service ${D}${systemd_unitdir}/system/xscreensaver.service
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
