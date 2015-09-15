SUMMARY = "Clutter based UI widget library"
LICENSE = "LGPLv2.1"

inherit clutter autotools-brokensep

DEPENDS = "clutter-1.0 dbus-glib gdk-pixbuf"

SRC_URI = "http://source.clutter-project.org/sources/mx/${@get_verdir("${PV}")}/mx-${PV}.tar.xz"

EXTRA_OECONF = "--disable-introspection		\
	        --disable-gtk-doc		\
	        --disable-gtk-widgets		\
		--with-dbus			\
		--with-winsys=none		\
		--without-clutter-imcontext	\
		--without-clutter-gesture	\
		--without-startup-notification	\
		--without-glade			\
	       "

FILES_${PN} += "${datadir}"
