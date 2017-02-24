SUMMARY = "Poppler is a PDF rendering library based on the xpdf-3.0 code base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = " \
    http://poppler.freedesktop.org/${BP}.tar.xz \
    file://0001-add-manadatory-options-to-find-qt4-qt5-moc.patch \
    file://0002-fix-gcc-6-math-ambiguous-errors.patch \
"
SRC_URI[md5sum] = "669b195ff24173d35cacf1d20b6fe4fa"
SRC_URI[sha256sum] = "b872e7228fc34a71ce4b47a5aea2a57ae67528818fa846e1e0eda089319bd242"

DEPENDS = "fontconfig zlib cairo lcms"

inherit autotools pkgconfig gtk-doc gobject-introspection

PACKAGECONFIG ??= "jpeg openjpeg png tiff nss ${@bb.utils.contains('BBFILE_COLLECTIONS', 'qt5-layer', 'qt5', '', d)}"
PACKAGECONFIG[jpeg] = "--enable-libjpeg,--disable-libjpeg,jpeg"
PACKAGECONFIG[png] = "--enable-libpng,--disable-libpng,libpng"
PACKAGECONFIG[tiff] = "--enable-libtiff,--disable-libtiff,tiff"
PACKAGECONFIG[curl] = "--enable-libcurl,--disable-libcurl,curl"
PACKAGECONFIG[openjpeg] = "--enable-libopenjpeg=openjpeg2,--disable-libopenjpeg,openjpeg"
PACKAGECONFIG[qt5] = "--enable-poppler-qt5 --with-moc-qt5=${STAGING_BINDIR_NATIVE}/qt5/moc,--disable-poppler-qt5,qtbase"
PACKAGECONFIG[qt4e] = "--enable-poppler-qt4 --with-moc-qt4=${STAGING_BINDIR_NATIVE}/moc4,--disable-poppler-qt4,qt4-embedded"
PACKAGECONFIG[nss] = "--enable-libnss,--disable-libnss,nss"

# Needed for qt5
CXXFLAGS += "--std=c++11"

EXTRA_OECONF = "\
    --enable-xpdf-headers \
    --disable-gtk-test \
    --enable-zlib \
"

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/poppler/.libs"
}

# Adjust library names when building for QT4e
QT4E_PATCHES = "${@bb.utils.contains('PACKAGECONFIG', 'qt4e', 'file://fix-qt4e-library-dependencies.patch', '', d)}"
SRC_URI_append = "${QT4E_PATCHES}"

# check for TARGET_FPU=soft and inform configure of the result so it can disable some floating points
def get_poppler_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', 1) in [ 'soft' ]:
        return "--enable-fixedpoint"
    return ""

EXTRA_OECONF += "${@get_poppler_fpu_setting(bb, d)}"

PACKAGES =+ "libpoppler libpoppler-glib"
FILES_libpoppler = "${libdir}/libpoppler.so.*"
FILES_libpoppler-glib = "${libdir}/libpoppler-glib.so.*"

RDEPENDS_libpoppler = "poppler-data"
