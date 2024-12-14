SUMMARY = "Geary is an email application built around conversations, for the GNOME 3 desktop."
SECTION = "network"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2a2244d5a13871ad950c55877546a6a2"

DEPENDS = " \
    appstream-glib \
    cairo \
    desktop-file-utils-native \
    enchant2 \
    evolution-data-server \
    folks \
    gcr3 \
    gmime \
    gnome-online-accounts \
    gsound \
    gspell \
    gtk+3 \
    icu \
    iso-codes \
    itstool-native \
    json-glib \
    libhandy \
    libical \
    libpeas \
    libsecret \
    libsoup \
    libstemmer \
    libxml2 \
    sqlite3 \
    webkitgtk3 \
"

RDEPENDS:${PN} = "gnome-keyring"

inherit meson pkgconfig mime-xdg gsettings gtk-icon-cache gobject-introspection vala features_check

SRC_URI = " \
	git://github.com/GNOME/geary.git;nobranch=1;protocol=https \
	file://0001-application-client.vala-hardcode-some-paths.patch \
	file://0001-meson-Do-not-check-for-iso-xml-files-during-build.patch \
"

S = "${WORKDIR}/git"
SRCREV = "46e93c0c0dafc381e8a308b1befb07e908121722"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data opengl"

GIR_MESON_OPTION = ""
EXTRA_OEMESON = "-Dprofile=release \
                 -Diso_639_xml=${datadir}/xml/iso-codes/iso_639.xml \
                 -Diso_3166_xml=${datadir}/xml/iso-codes/iso_3166.xml \
                 "

PACKAGECONFIG[libunwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"
PACKAGECONFIG[tnef] = "-Dtnef=enabled,-Dtnef=disabled,libytnef"
PACKAGECONFIG[valadoc] = "-Dvaladoc=enabled,-Dvaladoc=disabled"

PACKAGECONFIG ??= ""

FILES:${PN} += "${datadir}"

