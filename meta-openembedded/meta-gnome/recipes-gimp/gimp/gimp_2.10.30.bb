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
DEPENDS:append:libc-musl = " libexecinfo"

inherit features_check gnomebase gtk-icon-cache gtk-doc mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SHPV = "${@gnome_verdir("${PV}")}"

SRC_URI = "https://download.gimp.org/pub/${BPN}/v${SHPV}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "88815daa76ed7d4277eeb353358bafa116cd2fcd2c861d95b95135c1d52b67dc"

EXTRA_OECONF = "--disable-python \
                --without-webkit \
                --disable-check-update \
                --without-wmf"

EXTRA_OECONF:append:toolchain-clang:riscv32 = " --disable-vector-icons"
EXTRA_OECONF:append:toolchain-clang:mips = " --disable-vector-icons"

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
