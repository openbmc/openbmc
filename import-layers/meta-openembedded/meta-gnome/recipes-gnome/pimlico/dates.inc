DESCRIPTION = "Dates is a calendar application."
HOMEPAGE = "http://pimlico-project.org/dates.html"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2 & GPLv2+ & LGPLv2+"

SECTION = "x11"
DEPENDS = "glib-2.0 gtk+ libglade evolution-data-server intltool-native"
RDEPENDS_${PN} = "libedata-cal"

inherit autotools gettext pkgconfig gtk-icon-cache
