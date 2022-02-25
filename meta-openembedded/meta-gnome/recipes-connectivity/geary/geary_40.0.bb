SUMMARY = "Geary is an email application built around conversations, for the GNOME 3 desktop."
SECTION = "network"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2a2244d5a13871ad950c55877546a6a2"

DEPENDS = " \
    appstream-glib \
    cairo \
    enchant2 \
    evolution-data-server \
    folks \
    gcr \
    gmime \
    gnome-online-accounts \
    gsound \
    gspell \
    gtk+3 \
    icu \
    iso-codes \
    json-glib \
    libhandy \
    libical \
    libpeas \
    libsecret \
    libstemmer \
    libxml2 \
    sqlite3 \
    webkitgtk \
"

RDEPENDS:${PN} = "gnome-keyring"

inherit meson pkgconfig mime-xdg gtk-icon-cache gobject-introspection vala

SRC_URI = " \
	git://github.com/GNOME/geary.git;nobranch=1;protocol=https \
"

S = "${WORKDIR}/git"
SRCREV = "e561775c1580a9f60a726355b2b897bfc9cb3382"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

GIR_MESON_OPTION = ""
EXTRA_OEMESON = "-Dprofile=release"

PACKAGECONFIG[libunwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"
PACKAGECONFIG[tnef] = "-Dtnef=enabled,-Dtnef=disabled,libytnef"
PACKAGECONFIG[valadoc] = "-Dvaladoc=enabled,-Dvaladoc=disabled"

PACKAGECONFIG ??= ""

FILES:${PN} += "${datadir}"

