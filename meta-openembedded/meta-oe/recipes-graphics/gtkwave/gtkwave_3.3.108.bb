SUMMARY = "VCD (Value Change Dump) file waveform viewer"
DESCRIPTION = "gtkwave is a viewer for VCD (Value Change Dump) files which are usually created by digital circuit simulators. (These files have no connection to video CDs!) "
HOMEPAGE = "http://gtkwave.sourceforge.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SRC_URI = "http://gtkwave.sourceforge.net/${BP}.tar.gz"

SRC_URI[md5sum] = "dd40f09f44d7aed937e63c29b63cd3af"
SRC_URI[sha256sum] = "ece447340442e7ad029713789552e8392b75dd3808c882ac5193d42fce55eb3b"

inherit pkgconfig autotools gettext texinfo mime mime-xdg
DEPENDS += "tcl tk gperf-native bzip2 xz pango zlib gtk+ gdk-pixbuf glib-2.0"
RDEPENDS_${PN} += "tk-lib"

# depends on gtk+ which has this restriction
inherit features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OECONF = "--with-tcl=${STAGING_BINDIR_CROSS} --with-tk=${STAGING_BINDIR_CROSS} --with-tirpc --disable-mime-update"

FILES_${PN} = "${bindir} ${datadir}"
