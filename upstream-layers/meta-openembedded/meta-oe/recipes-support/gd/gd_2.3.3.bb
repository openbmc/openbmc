SUMMARY = "gd is a library used to create PNG, JPEG, or WBMP images"
DESCRIPTION = "The gd graphics library allows your code to quickly draw images \
complete with lines, arcs, text, multiple colors, cut and paste from other \
images, and flood fills, and to write out the result as a PNG or JPEG file. \
This is particularly useful in Web applications, where PNG and JPEG are two \
of the formats accepted for inline images by most browsers. Note that gd is not \
a paint program."
HOMEPAGE = "http://libgd.github.io/"

SECTION = "libs"
LICENSE = "GD"
LIC_FILES_CHKSUM = "file://COPYING;md5=ace63adfdac78400fc30fa22ee9c1bb1"

SRC_URI = "git://github.com/libgd/libgd.git;nobranch=1;protocol=https \
           file://0001-Fix-deprecared-function-prototypes.patch \
           file://Fix-ftype-missing-const.patch \
           "

SRCREV = "b5319a41286107b53daa0e08e402aa1819764bdc"


inherit autotools binconfig gettext pkgconfig

PACKAGECONFIG ?= "jpeg png zlib tiff freetype"

PACKAGECONFIG[avif] = "--with-avif,--without-avif,libavif"
PACKAGECONFIG[fontconfig] = "--with-fontconfig,--without-fontconfig,fontconfig"
PACKAGECONFIG[freetype] = "--with-freetype,--without-freetype,freetype"
PACKAGECONFIG[heif] = "--with-heif,--without-heif,libheif"
PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[liq] = "--with-liq,--without-liq,libimagequant"
PACKAGECONFIG[png] = "--with-png,--without-png,libpng"
PACKAGECONFIG[raqm] = "--with-raqm,--without-raqm,libraqm"
PACKAGECONFIG[tiff] = "--with-tiff,--without-tiff,tiff"
PACKAGECONFIG[webp] = "--with-webp,--without-webp,libwebp"
PACKAGECONFIG[x] = "--with-x,--without-x,virtual/libx11"
PACKAGECONFIG[xpm] = "--with-xpm,--without-xpm,libxpm"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

EXTRA_OECONF += "--disable-rpath"

EXTRA_OEMAKE = 'LDFLAGS="${LDFLAGS}"'

# -Wmaybe-uninitialized is a GCC-only option; clang does not recognise it and
# errors out on -Wno-error=maybe-uninitialized (-Werror,-Wunknown-warning-option).
CFLAGS += "${@bb.utils.contains('TOOLCHAIN', 'gcc', '-Wno-error=maybe-uninitialized', '', d)}"

DEBUG_OPTIMIZATION:append = " ${@bb.utils.contains('TOOLCHAIN', 'gcc', '-Wno-error=maybe-uninitialized', '', d)}"

do_install:append:class-target() {
    # cleanup buildpaths from gdlib.pc
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/gdlib.pc
}

PACKAGES += "${PN}-tools"

FILES:${PN} = "${libdir}/lib*${SOLIBS}"
FILES:${PN}-tools = "${bindir}/*"

PROVIDES += "${PN}-tools"
RPROVIDES:${PN}-tools = "${PN}-tools"
RDEPENDS:${PN}-tools = "perl perl-module-strict"

CVE_PRODUCT = "libgd"

BBCLASSEXTEND = "native nativesdk"
