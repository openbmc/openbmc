SUMMARY = "VCD (Value Change Dump) file waveform viewer"
DESCRIPTION = "gtkwave is a viewer for VCD (Value Change Dump) files which are usually created by digital circuit simulators. (These files have no connection to video CDs!) "
HOMEPAGE = "http://gtkwave.sourceforge.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SRC_URI = "http://gtkwave.sourceforge.net/${BP}.tar.gz"

SRC_URI[md5sum] = "eac3073ef381e0c09da33590296ca37f"
SRC_URI[sha256sum] = "efa6bbbeb3bd54104425a69a2aa0d079bb5c3ecc1c420ba57dcaa1c97c5a22f6"

inherit pkgconfig autotools gettext texinfo
DEPENDS = "tcl tk gperf-native bzip2 xz pango zlib gtk+ gdk-pixbuf glib-2.0"
RDEPENDS_${PN} = "tk-lib"

# depends on gtk+ which has this restriction
inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OECONF = "--with-tcl=${STAGING_BINDIR_CROSS} --with-tk=${STAGING_BINDIR_CROSS}"

FILES_${PN} = "${bindir} ${datadir}"
