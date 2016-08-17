SUMMARY = "Gnome session manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PR = "r3"

SECTION = "x11/gnome"
DEPENDS = "libxtst gtk+ glib-2.0 upower dbus-glib gconf pango gdk-pixbuf-native startup-notification"

inherit gnome

SRC_URI += "file://use_G_GINT64_FORMAT.patch"

SRC_URI[archive.md5sum] = "222bad6b446cb19a6b9028ea24538002"
SRC_URI[archive.sha256sum] = "22d93ce433fcf9c7ce6b5f36dd81f64e692ea0e41faaa0f61159ddac28c3686a"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = " --with-gtk=2.0 ac_cv_path_GCONF_SANITY_CHECK=set --disable-docbook-docs"

do_configure_append() {
    for i in $(find ${S} -name "Makefile") ; do
        sed -i -e s:"GCONFTOOL = .*/usr/bin/gconftool-2":"GCONFTOOL = /usr/bin/gconftool-2":g $i
        sed -i -e s:"GCONF_SANITY_CHECK = set":"GCONF_SANITY_CHECK = /usr/libexec/gconf-sanity-check-2":g $i
    done    
}

RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam-plugin-ck-connector', '', d)}"
FILES_${PN} += "${datadir}/xsessions ${datadir}/icons ${datadir}/gnome ${libdir}/gnome-session/helpers"
FILES_${PN}-dbg += "${libexecdir}/gnome-session/helpers/.debug"
