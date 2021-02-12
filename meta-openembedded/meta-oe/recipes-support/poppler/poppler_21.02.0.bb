SUMMARY = "Poppler is a PDF rendering library based on the xpdf-3.0 code base"
HOMEPAGE = "https://poppler.freedesktop.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://poppler.freedesktop.org/${BP}.tar.xz \
           file://0001-Do-not-overwrite-all-our-build-flags.patch \
           file://basename-include.patch \
           "
SRC_URI[sha256sum] = "5c14759c99891e6e472aced6d5f0ff1dacf85d80cd9026d365c55c653edf792c"

DEPENDS = "fontconfig zlib cairo lcms glib-2.0"

inherit cmake pkgconfig gobject-introspection

PACKAGECONFIG ??= "jpeg openjpeg png tiff nss splash"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG=ON -DENABLE_DCTDECODER=libjpeg,-DWITH_JPEG=OFF -DENABLE_DCTDECODER=none,jpeg"
PACKAGECONFIG[png] = "-DWITH_PNG=ON,-DWITH_PNG=OFF,libpng"
PACKAGECONFIG[tiff] = "-DWITH_TIFF=ON,-DWITH_TIFF=OFF,tiff"
PACKAGECONFIG[curl] = "-DENABLE_LIBCURL=ON,-DENABLE_LIBCURL=OFF,curl"
PACKAGECONFIG[openjpeg] = "-DENABLE_LIBOPENJPEG=openjpeg2,-DENABLE_LIBOPENJPEG=none,openjpeg"
PACKAGECONFIG[qt5] = "-DENABLE_QT5=ON,-DENABLE_QT5=OFF,qtbase qttools-native"
PACKAGECONFIG[nss] = "-DWITH_NSS3=ON,-DWITH_NSS3=OFF,nss"
PACKAGECONFIG[splash] = "-DENABLE_SPLASH=ON,-DENABLE_SPLASH=OFF,boost"

# surprise - did not expect this to work :)
inherit ${@bb.utils.contains('PACKAGECONFIG', 'qt5', 'cmake_qt5', '', d)}

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

EXTRA_OECMAKE += " \
    -DENABLE_CMS=lcms2 \
    -DENABLE_UNSTABLE_API_ABI_HEADERS=ON \
    -DBUILD_GTK_TESTS=OFF \
    -DENABLE_ZLIB=ON \
    -DCMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES:PATH='${STAGING_INCDIR}' \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_GOBJECT_INTROSPECTION=ON', '-DENABLE_GOBJECT_INTROSPECTION=OFF', d)} \
"

do_configure_append() {
    # poppler macro uses pkg-config to check for g-ir runtimes. Something
    # makes them point to /usr/bin. Align them to sysroot - that's where the
    # gir-wrappers are:
    sed -i 's: ${bindir}/g-ir: ${STAGING_BINDIR}/g-ir:' ${B}/build.ninja
}

PACKAGES =+ "libpoppler libpoppler-glib"
FILES_libpoppler = "${libdir}/libpoppler.so.*"
FILES_libpoppler-glib = "${libdir}/libpoppler-glib.so.*"

RDEPENDS_libpoppler = "poppler-data"
