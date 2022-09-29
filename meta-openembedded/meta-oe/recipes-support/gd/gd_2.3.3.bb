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

DEPENDS = "freetype libpng jpeg zlib tiff"

SRC_URI = "git://github.com/libgd/libgd.git;nobranch=1;protocol=https \
           file://0001-Fix-deprecared-function-prototypes.patch \
           "

SRCREV = "b5319a41286107b53daa0e08e402aa1819764bdc"

S = "${WORKDIR}/git"

inherit autotools binconfig gettext pkgconfig

EXTRA_OECONF += " --disable-rpath \
                  --with-jpeg=${STAGING_LIBDIR}/.. \
                  --with-freetype=yes \
                  --without-fontconfig \
                  --without-webp \
                  --without-xpm \
                  --without-x \
                "

EXTRA_OEMAKE = 'LDFLAGS="${LDFLAGS}"'

DEBUG_OPTIMIZATION:append = " -Wno-error=maybe-uninitialized"

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
