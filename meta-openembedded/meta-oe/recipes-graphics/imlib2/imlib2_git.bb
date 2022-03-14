SUMMARY = "A graphic library for file loading, saving, rendering, and manipulation"

HOMEPAGE = "https://sourceforge.net/projects/enlightenment/"
SECTION = "libs"
LICENSE = "Imlib2"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35"

DEPENDS = "freetype "
PROVIDES = "virtual/imlib2"
PV = "1.7.1"
SRCREV = "01424487e360383a039dc123bc2a345fe7ea2535"

inherit autotools pkgconfig lib_package

AUTO_LIBNAME_PKGS = ""

SRC_URI = "git://git.enlightenment.org/legacy/${BPN}.git;protocol=https;branch=master"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= "jpeg png zlib ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

X11_DEPS = "virtual/libx11 libxext libice"
PACKAGECONFIG[x11] = "--with-x,--without-x,${X11_DEPS}"

PACKAGECONFIG[gif] = "--with-gif,--without-gif,giflib"
PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[tiff] = "--with-tiff,--without-tiff,tiff"
PACKAGECONFIG[webp] = "--with-webp,--without-webp,libwebp"

PACKAGECONFIG[bzip2] = "--with-bzip2,--without-bzip2,bzip2"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

PACKAGECONFIG[id3] = "--with-id3,--without-id3,libid3tag"

PACKAGES =+ "${BPN}-loaders ${BPN}-filters ${BPN}-data"
FILES:${PN}-data = "${datadir}"
FILES:imlib2-loaders = "${libdir}/imlib2/loaders/*.so"
FILES:imlib2-filters = "${libdir}/imlib2/filters/*.so"
