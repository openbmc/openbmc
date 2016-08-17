require gtk+.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gtk/gtk.h;endline=27;md5=c59e0b4490dd135a5726ebf851f9b17f \
                    file://gdk/gdk.h;endline=27;md5=07db285ec208fb3e0bf7d861b0614202 \
                    file://tests/testgtk.c;endline=27;md5=262db5db5f776f9863e56df31423e24c"
SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk+/2.24/gtk+-${PV}.tar.xz \
           file://xsettings.patch \
           file://hardcoded_libtool.patch \
           file://toggle-font.diff;striplevel=0 \
           file://doc-fixes.patch \
           file://strict-prototypes.patch \
	  "

SRC_URI[md5sum] = "1b7a3689f65617387b5b54520f4439e8"
SRC_URI[sha256sum] = "0741c59600d3d810a223866453dc2bbb18ce4723828681ba24aa6519c37631b8"

EXTRA_OECONF = "--enable-xkb --disable-glibtest --disable-cups --disable-xinerama"

LIBV = "2.10.0"

PACKAGES_DYNAMIC += "^gtk-immodule-.* ^gtk-printbackend-.*"

python populate_packages_prepend () {
    gtk_libdir = d.expand('${libdir}/gtk-2.0/${LIBV}')
    immodules_root = os.path.join(gtk_libdir, 'immodules')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    d.setVar('GTKIMMODULES_PACKAGES', ' '.join(do_split_packages(d, immodules_root, '^im-(.*)\.so$', 'gtk-immodule-%s', 'GTK input module for %s')))
    do_split_packages(d, printmodules_root, '^libprintbackend-(.*)\.so$', 'gtk-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES', True)):
        d.setVar(d.expand('PKG_${PN}'), '${MLPREFIX}libgtk-2.0')
}
