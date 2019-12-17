SUMMARY = "multi-protocol instant messaging client"
SECTION = "x11/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "python3 virtual/libintl intltool-native libxml2 gconf glib-2.0-native"

inherit autotools gettext pkgconfig gconf perlnative python3native

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/pidgin/pidgin-${PV}.tar.bz2 \
    file://sanitize-configure.ac.patch \
    file://purple-OE-branding-25.patch \
    file://pidgin-cross-python-265.patch \
    file://use_py3.patch \
"

SRC_URI[md5sum] = "423403494fe1951e47cc75231f743bb0"
SRC_URI[sha256sum] = "2747150c6f711146bddd333c496870bfd55058bab22ffb7e4eb784018ec46d8f"

PACKAGECONFIG ??= "gnutls consoleui avahi dbus idn nss \
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
PACKAGECONFIG[gnutls] = "--enable-gnutls --with-gnutls-includes=${STAGING_INCDIR} --with-gnutls-libs=${STAGING_LIBDIR},--disable-gnutls,gnutls,libpurple-plugin-ssl-gnutls"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus dbus-glib"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[nss] = "--enable-nss,--disable-nss,nss nspr,libpurple-plugin-ssl-nss"

EXTRA_OECONF = " \
    --disable-perl \
    --disable-tcl \
    --disable-gevolution \
    --disable-schemas-install \
    --disable-gtkspell \
    --disable-meanwhile \
    --disable-nm \
    --disable-screensaver \
"

do_configure_prepend() {
    touch ${S}/po/Makefile
    sed -i "s#PY_VERSION=`$PYTHON -c 'import sys ; print sys.version[0:3]'`#PY_VERSION=${PYTHON_BASEVERSION}#g" ${S}/configure.ac
}

OE_LT_RPATH_ALLOW=":${libdir}/purple-2:"
OE_LT_RPATH_ALLOW[export]="1"

PACKAGES =+ "libpurple-dev libpurple libgnt libgnt-dev finch finch-dev ${PN}-data"

RPROVIDES_${PN}-dbg += "libpurple-dbg libgnt-dbg finch-dbg"

LEAD_SONAME = "libpurple.so.0"
FILES_libpurple     = "${libdir}/libpurple*.so.* ${libdir}/purple-2 ${bindir}/purple-* ${sysconfdir}/gconf/schemas/purple* ${datadir}/purple/ca-certs"
FILES_libpurple-dev = "${libdir}/libpurple*.la \
                       ${libdir}/libpurple*.so \
                       ${libdir}/purple-2/*.la \
                       ${libdir}/purple-2/libjabber.so \
                       ${libdir}/purple-2/liboscar.so \
                       ${libdir}/purple-2/libymsg.so \
                       ${datadir}/aclocal"
FILES_libgnt         = "${libdir}/libgnt.so.* ${libdir}/gnt/*.so"
FILES_libgnt-dev     = "${libdir}/gnt/*.la"
FILES_finch          = "${bindir}/finch"
FILES_finch-dev      = "${libdir}/finch/*.la"

FILES_${PN} = "${bindir} ${datadir}/${PN} ${libdir}/${PN}/*.so \
           ${datadir}/applications"
RRECOMMENDS_${PN} = "${PN}-data libpurple-protocol-irc libpurple-protocol-xmpp"

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
