SUMMARY = "Clutter based UI widget library"
DESCRIPTION = "Mx is a widget toolkit using Clutter that provides a set of standard interface \
elements, including buttons, progress bars, scroll bars and others. It also \
implements some standard  managers. One other interesting feature is the \
possibility setting style properties from a CSS format file."
HOMEPAGE = "https://github.com/clutter-project/mx"
BUGTRACKER = "https://github.com/clutter-project/mx/issues"
LICENSE = "LGPLv2.1"

inherit clutter autotools features_check gobject-introspection gtk-doc
# depends on clutter-1.0 which depends on cogl-1.0
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = "intltool-native clutter-1.0 dbus-glib gdk-pixbuf"

SRC_URI = "http://source.clutter-project.org/sources/mx/${@get_verdir("${PV}")}/mx-${PV}.tar.xz"

EXTRA_OECONF = "--disable-gtk-widgets		\
		--with-dbus			\
		--with-winsys=none		\
		--without-clutter-imcontext	\
		--without-clutter-gesture	\
		--without-startup-notification	\
		--without-glade			\
	       "

FILES_${PN} += "${datadir}"
