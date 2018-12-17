SUMMARY = "Poppler is a PDF rendering library based on the xpdf-3.0 code base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://poppler.freedesktop.org/${BP}.tar.xz \
           file://0001-Do-not-overwrite-all-our-build-flags.patch \
           file://0002-CairoOutputDev.cc-fix-build-error-when-using-fixedpo.patch \
           file://0001-glib-CMakeLists.txt-Add-libpoppler-to-link-along-wit.patch \
           file://basename-include.patch \
           "
SRC_URI[md5sum] = "fdb0699e9aa20e81acd1a50701374c8b"
SRC_URI[sha256sum] = "66972047d9ef8162cc8c389d7e7698291dfc9f2b3e4ea9a9f08ae604107451bd"

DEPENDS = "fontconfig zlib cairo lcms glib-2.0"

inherit cmake pkgconfig gobject-introspection

PACKAGECONFIG ??= "jpeg openjpeg png tiff nss ${@bb.utils.contains('BBFILE_COLLECTIONS', 'qt5-layer', 'qt5', '', d)}"
PACKAGECONFIG[jpeg] = "-DWITH_JPEG=ON -DENABLE_DCTDECODER=libjpeg,-DWITH_JPEG=OFF -DENABLE_DCTDECODER=none,jpeg"
PACKAGECONFIG[png] = "-DWITH_PNG=ON,-DWITH_PNG=OFF,libpng"
PACKAGECONFIG[tiff] = "-DWITH_TIFF=ON,-DWITH_TIFF=OFF,tiff"
PACKAGECONFIG[curl] = "-DENABLE_LIBCURL=ON,-DENABLE_LIBCURL=OFF,curl"
PACKAGECONFIG[openjpeg] = "-DENABLE_LIBOPENJPEG=openjpeg2,-DENABLE_LIBOPENJPEG=none,openjpeg"
PACKAGECONFIG[qt5] = "-DENABLE_QT5=ON,-DENABLE_QT5=OFF,qtbase qttools-native"
PACKAGECONFIG[nss] = "-DWITH_NSS3=ON,-DWITH_NSS3=OFF,nss"

# surprise - did not expect this to work :)
inherit ${@bb.utils.contains('PACKAGECONFIG', 'qt5', 'cmake_qt5', '', d)}

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

EXTRA_OECMAKE += " \
    -DENABLE_CMS=lcms2 \
    -DENABLE_XPDF_HEADERS=ON \
    -DBUILD_GTK_TESTS=OFF \
    -DENABLE_ZLIB=ON \
    -DCMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES:PATH='${STAGING_INCDIR}' \
"

do_configure_append() {
    # poppler macro uses pkg-config to check for g-ir runtimes. Something
    # makes them point to /usr/bin. Align them to sysroot - that's where the
    # git-wrappers are:
    sed -i 's: ${bindir}/g-ir: ${STAGING_BINDIR}/g-ir:' ${B}/build.ninja
}

# check for TARGET_FPU=soft and inform configure of the result so it can disable some floating points
def get_poppler_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU') in [ 'soft' ]:
        return "-DUSE_FIXEDPOINT=ON"
    return ""

EXTRA_OECMAKE += "${@get_poppler_fpu_setting(bb, d)}"

PACKAGES =+ "libpoppler libpoppler-glib"
FILES_libpoppler = "${libdir}/libpoppler.so.*"
FILES_libpoppler-glib = "${libdir}/libpoppler-glib.so.*"

RDEPENDS_libpoppler = "poppler-data"
