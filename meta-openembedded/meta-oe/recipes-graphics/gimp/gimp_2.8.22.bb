SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    gdk-pixbuf-native \
    intltool-native \
    libxslt-native \
    gtk+ \
    babl \
    gegl \
    jpeg \
    libpng \
    libexif \
    tiff \
    lcms \
    poppler \
    jasper \
    bzip2 \
    libgudev \
    libmng \
"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxmu libxpm', '', d)}"

inherit distro_features_check gnome gtk-doc

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://ftp.gimp.org/pub/gimp/v2.8/gimp-${PV}.tar.bz2 \
           file://0001-configure-ac-do-not-check-for-freetype-config.patch \
           file://bump_Babl-GEGL_versions.patch \
           file://0003-Fix-use-of-gegl-API.patch \
           "
SRC_URI[md5sum] = "7e4fd7a53b1d3c32dff642ab1a94b44d"
SRC_URI[sha256sum] = "9187a35cc52b110d78124d7b27b68a68ade14a794c2721314bac6134d2a5638a"

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
