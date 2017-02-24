SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    gdk-pixbuf-native \
    intltool-native \
    gtk+ \
    babl \
    gegl \
    libart-lgpl \
    jpeg \
    libpng \
    libexif \
    tiff \
    lcms \
    poppler \
    jasper \
    bzip2 \
    libgudev \
"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxmu libxpm', '', d)}"

inherit gnome gtk-doc

SRC_URI = " \
    http://ftp.gimp.org/pub/gimp/v2.8/gimp-${PV}.tar.bz2 \
    file://0001-configure-ac-do-not-check-for-freetype-config.patch \
    file://bump_Babl-GEGL_versions.patch \
"
SRC_URI[md5sum] = "5adaa11a68bc8a42bb2c778fee4d389c"
SRC_URI[sha256sum] = "39dd2247c678deaf5cc664397d3c6bd4fb910d3472290fd54b52b441b5815441"

EXTRA_OECONF = "--disable-python \
                --without-webkit \
                --without-wmf"

do_configure_append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

CFLAGS += "-fPIC"

FILES_${PN}-dbg += "${libdir}/gimp/2.0/*/.debug"
FILES_${PN}  += "${datadir}/appdata"
