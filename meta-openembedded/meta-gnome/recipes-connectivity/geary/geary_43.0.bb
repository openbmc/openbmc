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

inherit meson pkgconfig mime-xdg gtk-icon-cache gobject-introspection vala features_check

SRC_URI = " \
	git://github.com/GNOME/geary.git;nobranch=1;protocol=https \
"

S = "${WORKDIR}/git"
SRCREV = "94d6bec861daffb27efea85a296f347db7a5af6d"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data opengl"

GIR_MESON_OPTION = ""
EXTRA_OEMESON = "-Dprofile=release"

PACKAGECONFIG[libunwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"
PACKAGECONFIG[tnef] = "-Dtnef=enabled,-Dtnef=disabled,libytnef"
PACKAGECONFIG[valadoc] = "-Dvaladoc=enabled,-Dvaladoc=disabled"

PACKAGECONFIG ??= ""
# rfc822/rfc822-message.c:2097:12: error: incompatible pointer to integer conversion returning 'void *' from a function with result type 'gboolean' (aka 'int') [-Wint-conversion]
#|                                 return NULL;
#|                                        ^~~~
CFLAGS:append:toolchain-clang = " -Wno-error=int-conversion"

FILES:${PN} += "${datadir}"

