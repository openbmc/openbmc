SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "x11/graphics"
LICENSE = "GPL-3.0-only"
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
DEPENDS:append:libc-musl = " libexecinfo"

GNOMEBASEBUILDCLASS = "autotools"
inherit features_check gnomebase gtk-icon-cache gtk-doc mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SHPV = "${@gnome_verdir("${PV}")}"

SRC_URI = "https://download.gimp.org/pub/${BPN}/v${SHPV}/${BP}.tar.bz2 \
           file://0001-configure-Keep-first-line-of-compiler-version-string.patch \
           file://0001-libtool-Do-not-add-build-time-library-paths-to-LD_LI.patch"
SRC_URI[sha256sum] = "3d3bc3c69a4bdb3aea9ba2d5385ed98ea03953f3857aafd1d6976011ed7cdbb2"

EXTRA_OECONF = "--disable-python \
                --without-webkit \
                --disable-check-update \
                --without-wmf"

EXTRA_OECONF += "${@oe.utils.conditional('SITEINFO_BITS', '32', ' --disable-vector-icons', '', d)}"

do_configure:append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

do_compile:prepend() {
    # Let native babl/gegl find their plugins
    export BABL_PATH=`find ${STAGING_LIBDIR_NATIVE} -maxdepth 1 -name 'babl-*'`
    export GEGL_PATH=`find ${STAGING_LIBDIR_NATIVE} -maxdepth 1 -name 'gegl-*'`
}

FILES:${PN}  += "${datadir}/metainfo"

RDEPENDS:${PN} += "mypaint-brushes-1.0"
