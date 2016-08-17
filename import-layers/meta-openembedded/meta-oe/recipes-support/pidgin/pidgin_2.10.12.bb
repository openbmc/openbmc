DESCRIPTION = "multi-protocol instant messaging client"
SECTION = "x11/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "python virtual/libintl intltool-native libxml2 gconf"

inherit autotools gettext pkgconfig gconf perlnative

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/pidgin/pidgin-${PV}.tar.bz2 \
    file://sanitize-configure.ac.patch \
    file://purple-OE-branding-25.patch \
    file://pidgin-cross-python-265.patch \
"

SRC_URI[md5sum] = "14e0f5cfb2ed065e4dc80391a806ac76"
SRC_URI[sha256sum] = "2c7523f0fefe89749c03b2b738ab9f7bd186da435be4762f1487eee31e77ffdd"

PACKAGECONFIG ??= "gnutls consoleui avahi dbus idn \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 gtk startup-notification', '', d)} \
"
PACKAGECONFIG[farsight2] = "--enable-farstream,--disable-farstream,farsight2"
#  --disable-gstreamer     compile without GStreamer audio support
#  --disable-gstreamer-video
#                          compile without GStreamer 1.0 Video Overlay support
#  --disable-gstreamer-interfaces
#                          compile without GStreamer 0.10 interface support
#  --with-gstreamer=<version>
#                          compile with GStreamer 0.10 or 1.0 interface
PACKAGECONFIG[gstreamer] = "--enable-gstreamer,--disable-gstreamer,gstreamer"
PACKAGECONFIG[vv] = "--enable-vv,--disable-vv,gstreamer"
PACKAGECONFIG[idn] = "--enable-idn,--disable-idn,libidn"
PACKAGECONFIG[gtk] = "--enable-gtkui,--disable-gtkui,gtk+"
PACKAGECONFIG[x11] = "--with-x=yes --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR},--with-x=no,virtual/libx11"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"
PACKAGECONFIG[consoleui] = "--enable-consoleui --with-ncurses-headers=${STAGING_INCDIR},--disable-consoleui,ncurses"
PACKAGECONFIG[gnutls] = "--enable-gnutls --with-gnutls-includes=${STAGING_INCDIR} --with-gnutls-libs=${STAGING_LIBDIR},--disable-gnutls,gnutls"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus dbus-glib"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"

EXTRA_OECONF = " \
    --disable-perl \
    --disable-tcl \
    --disable-gevolution \
    --disable-schemas-install \
    --disable-gtkspell \
    --disable-meanwhile \
    --disable-nm \
    --disable-screensaver \
    --enable-nss=no \
"

do_configure_prepend() {
    touch ${S}/po/Makefile
    sed -i "s#PY_VERSION=`$PYTHON -c 'import sys ; print sys.version[0:3]'`#PY_VERSION=${PYTHON_BASEVERSION}#g" ${S}/configure.ac
}

OE_LT_RPATH_ALLOW=":${libdir}/purple-2:"
OE_LT_RPATH_ALLOW[export]="1"

PACKAGES =+ "libpurple-dbg libpurple-dev libpurple libgnt-dbg libgnt libgnt-dev finch-dbg finch finch-dev ${PN}-data"

LEAD_SONAME = "libpurple.so.0"
FILES_libpurple     = "${libdir}/libpurple*.so.* ${libdir}/purple-2 ${bindir}/purple-* ${sysconfdir}/gconf/schemas/purple* ${datadir}/purple/ca-certs"
FILES_libpurple-dev = "${libdir}/libpurple*.la \
                       ${libdir}/libpurple*.so \
                       ${libdir}/purple-2/*.la \
                       ${libdir}/purple-2/libjabber.so \
                       ${libdir}/purple-2/liboscar.so \
                       ${libdir}/purple-2/libymsg.so \
                       ${datadir}/aclocal"
FILES_libpurple-dbg += "${libdir}/.debug/libpurple* \
                        ${libdir}/purple-2/.debug"
FILES_libgnt         = "${libdir}/libgnt.so.* ${libdir}/gnt/*.so" 
FILES_libgnt-dev     = "${libdir}/gnt/*.la" 
FILES_libgnt-dbg     = "${libdir}/gnt/.debug"
FILES_finch          = "${bindir}/finch"
FILES_finch-dev      = "${libdir}/finch/*.la"
FILES_finch-dbg      = "${bindir}/.debug/finch \
                        ${libdir}/finch/.debug"

FILES_${PN} = "${bindir} ${datadir}/${PN} ${libdir}/${PN}/*.so \
           ${datadir}/applications"
RRECOMMENDS_${PN} = "${PN}-data libpurple-plugin-ssl-gnutls libpurple-protocol-irc libpurple-protocol-xmpp"

FILES_${PN}-data = "${datadir}/pixmaps ${datadir}/sounds ${datadir}/icons ${datadir}/appdata"
FILES_${PN}-dev += "${libdir}/${PN}/*.la"

PACKAGES_DYNAMIC += "^libpurple-protocol-.* ^libpurple-plugin-.* ^pidgin-plugin-.* ^finch-plugin-.*"

python populate_packages_prepend () {
    pidgroot = d.expand('${libdir}/pidgin')
    purple   = d.expand('${libdir}/purple-2')
    finch    = d.expand('${libdir}/finch')

    do_split_packages(d, pidgroot, '^([^l][^i][^b].*)\.so$',
        output_pattern='pidgin-plugin-%s',
        description='Pidgin plugin %s',
        prepend=True, extra_depends='')

    do_split_packages(d, purple, '^lib(.*)\.so$',
        output_pattern='libpurple-protocol-%s',
        description='Libpurple protocol plugin for %s',
        prepend=True, extra_depends='')

    do_split_packages(d, purple, '^(ssl-.*)\.so$',
        output_pattern='libpurple-plugin-%s',
        description='libpurple plugin %s',
        prepend=True, extra_depends='libpurple-plugin-ssl')

    do_split_packages(d, purple, '^([^l][^i][^b].*)\.so$',
        output_pattern='libpurple-plugin-%s',
        description='libpurple plugin %s',
        prepend=True, extra_depends='')

    do_split_packages(d, finch, '^([^l][^i][^b].*)\.so$',
        output_pattern='finch-plugin-%s',
        description='Finch plugin %s',
        prepend=True, extra_depends='')
}
