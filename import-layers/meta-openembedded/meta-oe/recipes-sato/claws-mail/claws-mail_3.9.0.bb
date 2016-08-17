SECTION = "x11/network"
SUMMARY = "Mail user agent"
DEPENDS = "gtk+ libetpan openssl aspell curl libgcrypt"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=e059bde2972c1790af786f3e86bac22e"

PNBLACKLIST[claws-mail] ?= "depends on blacklisted libetpan"

inherit autotools pkgconfig gettext

# translation patch: http://www.thewildbeast.co.uk/claws-mail/bugzilla/show_bug.cgi?id=1774
SRC_URI = "\
        ${SOURCEFORGE_MIRROR}/project/claws-mail/Claws%20Mail/${PV}/claws-mail-${PV}.tar.bz2;name=archive "
SRC_URI[archive.md5sum] = "4c5ac7b21f0ed17d0f6404124c2229a4"
SRC_URI[archive.sha256sum] = "ed70975a5056b3ffc4fe6e977f0d9606febc1499763c090241b029a73ff24e65"

do_configure_append() {
    cd ${S}/po ; for PO in *.po ; do MO=`echo $PO | sed s/\\.po//`.gmo ; if ! test -f $MO ; then msgfmt $PO -o $MO ; fi ; done; cd ${B}
}

PACKAGECONFIG ??= "startup-notification dbus"
PACKAGECONFIG[enchant] = "--enable-enchant,--disable-enchant,enchant"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus dbus-glib"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap"

# FIXME: maemo builds may want --enable-maemo
# FIXME: some platforms may want --enable-generic-umpc
EXTRA_OECONF = " \
    --disable-manual \
    --disable-crash-dialog \
    --disable-jpilot \
    --disable-trayicon-plugin \
    --disable-spamassassin-plugin \
    --disable-bogofilter-plugin \
    --disable-pgpcore-plugin \
    --disable-pgpmime-plugin \
    --disable-pgpinline-plugin \
    --disable-dillo-viewer-plugin \
    --disable-valgrind \
"

# Remove enchant references:
do_install_prepend() {
    sed -i -e 's:${STAGING_INCDIR}:${includedir}:g;s:${STAGING_LIBDIR}:${libdir}:g' claws-mail.pc
}

# Work-around broken GPE icon lookup:
do_install_append() {
    rm -r ${D}${datadir}/icons
    install -d ${D}${datadir}/pixmaps
    install -m 0644 ${S}/claws-mail.png ${D}${datadir}/pixmaps/
    sed -i 's/Icon=[^.]*$/&.png/' ${D}${datadir}/applications/claws-mail.desktop
}

RSUGGESTS_${PN} = "claws-plugin-gtkhtml2-viewer claws-plugin-mailmbox claws-plugin-rssyl"
