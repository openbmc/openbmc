SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "x11/graphics"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

DEPENDS = " \
    alsa-lib \
    atk \
    cairo \
    fontconfig \
    freetype \
    gdk-pixbuf-native \
    intltool-native \
    libxslt-native \
    gegl-native \
    dbus-glib \
    gtk+ \
    babl \
    gegl \
    libmypaint \
    mypaint-brushes-1.0 \
    gexiv2 \
    jpeg \
    libmng \
    libpng \
    libexif \
    tiff \
    lcms \
    poppler \
    poppler-data \
    jasper \
    bzip2 \
    libgudev \
    libmng \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxmu libxpm', '', d)} \
"
DEPENDS_append_libc-musl = " libexecinfo"

inherit features_check gnomebase gtk-icon-cache gtk-doc mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SHPV = "${@gnome_verdir("${PV}")}"

SRC_URI = "https://download.gimp.org/pub/${BPN}/v${SHPV}/${BP}.tar.bz2 \
           file://0001-configure.ac-fix-with-linux-input-handling-with-upco.patch \
          "
SRC_URI[sha256sum] = "2db84b57f3778d80b3466d7c21a21d22e315c7b062de2883cbaaeda9a0f618bb"

EXTRA_OECONF = "--disable-python \
                --without-webkit \
                --disable-check-update \
                --without-wmf"

EXTRA_OECONF_append_libc-musl_mipsarch = " --disable-vector-icons"
EXTRA_OECONF_append_arm = " --disable-vector-icons"

do_configure_append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

do_compile_prepend() {
    # Let native babl/gegl find their plugins
    export BABL_PATH=`find ${STAGING_LIBDIR_NATIVE} -maxdepth 1 -name 'babl-*'`
    export GEGL_PATH=`find ${STAGING_LIBDIR_NATIVE} -maxdepth 1 -name 'gegl-*'`
}

FILES_${PN}  += "${datadir}/metainfo"

RDEPENDS_${PN} += "mypaint-brushes-1.0"
