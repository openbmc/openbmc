SUMMARY = "VCD (Value Change Dump) file waveform viewer"
DESCRIPTION = "gtkwave is a viewer for VCD (Value Change Dump) files which are usually created by digital circuit simulators. (These files have no connection to video CDs!) "
HOMEPAGE = "http://gtkwave.sourceforge.net/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SRC_URI = "http://gtkwave.sourceforge.net/gtkwave-gtk3-${PV}.tar.gz"
SRC_URI[sha256sum] = "5da94863fb2e5d88cc019e2bfdc676022398113894b585d864e11ca8341b24d8"
S = "${WORKDIR}/${BPN}-gtk3-${PV}"

DEPENDS = " \
    gperf-native \
    gtk+3 \
    gdk-pixbuf \
    tcl \
    tk \
    bzip2 \
    xz \
    pango \
    zlib \
"

inherit pkgconfig autotools gettext texinfo mime mime-xdg

inherit features_check
# depends on gtk+3 which has this restriction
# ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
# but https://github.com/gtkwave/gtkwave/blob/f9d82a82aa3ddc30ca47984278371f62c9a3bd81/gtkwave3-gtk3/src/gtk23compat.h#L10
# explicitly includes gdk/gdkwayland.h for gtk-3.22.26 and newer (oe-core currently has 3.24.29)
# and it needs x11 as well for tk dependency (so it happends to be both GTK3DISTROFEATURES instead of either of them)
REQUIRED_DISTRO_FEATURES = "wayland x11"

EXTRA_OECONF = " \
    --enable-gtk3 \
    --with-tcl=${STAGING_BINDIR_CROSS} \
    --with-tk=${STAGING_BINDIR_CROSS} \
    --with-tirpc \
    --disable-mime-update \
"

FILES:${PN} = "${bindir} ${datadir}"

RDEPENDS:${PN} += "tk-lib"
