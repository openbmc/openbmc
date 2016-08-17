SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "babl gdk-pixbuf-native libart-lgpl gtk+ jpeg libpng libexif tiff lcms gegl poppler jasper bzip2 libgudev"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxmu libxpm', '', d)}"

inherit gnome gtk-doc

SRC_URI = " \
    http://ftp.gimp.org/pub/gimp/v2.8/gimp-${PV}.tar.bz2 \
    file://0001-configure-ac-do-not-check-for-freetype-config.patch \
    file://bump_Babl-GEGL_versions.patch \
"
SRC_URI[md5sum] = "233c948203383fa078434cc3f8f925cb"
SRC_URI[sha256sum] = "d82a958641c9c752d68e35f65840925c08e314cea90222ad845892a40e05b22d"

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
