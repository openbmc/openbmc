SUMMARY = "VCD (Value Change Dump) file waveform viewer"
DESCRIPTION = "gtkwave is a viewer for VCD (Value Change Dump) files which are usually created by digital circuit simulators. (These files have no connection to video CDs!) "
HOMEPAGE = "http://gtkwave.sourceforge.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SRC_URI = "http://gtkwave.sourceforge.net/${BP}.tar.gz"

SRC_URI[md5sum] = "13da7d4235d4031fde52971830458850"
SRC_URI[sha256sum] = "80bb7cb92db45872209f4ca48fc95a0460e0d89b0fe0c310c836d9b04c77fec7"

inherit pkgconfig autotools gettext texinfo
DEPENDS = "tcl tk gperf-native bzip2 xz pango zlib gtk+ gdk-pixbuf glib-2.0"
RDEPENDS_${PN} = "tk-lib"

# depends on gtk+ which has this restriction
inherit features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

EXTRA_OECONF = "--with-tcl=${STAGING_BINDIR_CROSS} --with-tk=${STAGING_BINDIR_CROSS} --with-tirpc"

FILES_${PN} = "${bindir} ${datadir}"
