SUMMARY = "multi-protocol instant messaging client"

SECTION = "x11/network"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "python3 virtual/libintl intltool-native libxml2 gconf glib-2.0-native"

inherit autotools gettext pkgconfig gconf perlnative python3native

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/pidgin/pidgin-${PV}.tar.bz2 \
    file://sanitize-configure.ac.patch \
    file://purple-OE-branding-25.patch \
"

SRC_URI[sha256sum] = "19654ad276b149646371fbdac21bc7620742f2975f7399fed0ffc1a18fbaf603"

CVE_STATUS[CVE-2010-1624] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."
CVE_STATUS[CVE-2011-3594] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

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
PACKAGECONFIG[consoleui] = "--enable-consoleui --with-ncurses-headers=${STAGING_INCDIR},--disable-consoleui,libgnt"
PACKAGECONFIG[gnutls] = "--enable-gnutls --with-gnutls-includes=${STAGING_INCDIR} --with-gnutls-libs=${STAGING_LIBDIR},--disable-gnutls,gnutls,libpurple-plugin-ssl-gnutls"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus dbus-glib"
PACKAGECONFIG[avahi] = "--enable-avahi,--disable-avahi,avahi"
PACKAGECONFIG[nss] = "--enable-nss,--disable-nss,nss nspr,libpurple-plugin-ssl-nss"
PACKAGECONFIG[cyrus-sasl] = "--enable-cyrus-sasl,--disable-cyrus-sasl,cyrus-sasl"

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

OE_LT_RPATH_ALLOW=":${libdir}/purple-2:"
OE_LT_RPATH_ALLOW[export]="1"

PACKAGES =+ "libpurple-dev libpurple finch finch-dev ${PN}-data"

RPROVIDES:${PN}-dbg += "libpurple-dbg finch-dbg"

LEAD_SONAME = "libpurple.so.0"
FILES:libpurple     = "${libdir}/libpurple*.so.* ${libdir}/purple-2 ${bindir}/purple-* ${sysconfdir}/gconf/schemas/purple* ${datadir}/purple/ca-certs"
FILES:libpurple-dev = "${libdir}/libpurple*.la \
                       ${libdir}/libpurple*.so \
                       ${libdir}/purple-2/*.la \
                       ${libdir}/purple-2/libjabber.so \
                       ${libdir}/purple-2/liboscar.so \
                       ${libdir}/purple-2/libymsg.so \
                       ${datadir}/aclocal"
FILES:finch          = "${bindir}/finch"
FILES:finch-dev      = "${libdir}/finch/*.la"

FILES:${PN} = "${bindir} ${datadir}/${PN} ${libdir}/${PN}/*.so \
           ${datadir}/applications"
RRECOMMENDS:${PN} = "${PN}-data libpurple-protocol-irc libpurple-protocol-xmpp"

FILES:${PN}-data = "${datadir}/pixmaps ${datadir}/sounds ${datadir}/icons ${datadir}/appdata"
FILES:${PN}-dev += "${libdir}/${PN}/*.la"

PACKAGES_DYNAMIC += "^libpurple-protocol-.* ^libpurple-plugin-.* ^pidgin-plugin-.* ^finch-plugin-.*"

python populate_packages:prepend () {
    pidgroot = d.expand('${libdir}/pidgin')
    purple   = d.expand('${libdir}/purple-2')
    finch    = d.expand('${libdir}/finch')

    do_split_packages(d, pidgroot, r'^([^l][^i][^b].*)\.so$',
        output_pattern='pidgin-plugin-%s',
        description='Pidgin plugin %s',
        prepend=True, extra_depends='')

    do_split_packages(d, purple, r'^lib(.*)\.so$',
        output_pattern='libpurple-protocol-%s',
        description='Libpurple protocol plugin for %s',
        prepend=True, extra_depends='')

    do_split_packages(d, purple, r'^(ssl-.*)\.so$',
        output_pattern='libpurple-plugin-%s',
        description='libpurple plugin %s',
        prepend=True, extra_depends='libpurple-plugin-ssl')

    do_split_packages(d, purple, r'^([^l][^i][^b].*)\.so$',
        output_pattern='libpurple-plugin-%s',
        description='libpurple plugin %s',
        prepend=True, extra_depends='')

    do_split_packages(d, finch, r'^([^l][^i][^b].*)\.so$',
        output_pattern='finch-plugin-%s',
        description='Finch plugin %s',
        prepend=True, extra_depends='')
}
