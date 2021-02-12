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
LIC_FILES_CHKSUM = "file://COPYING;md5=8e5bc8627b9494741c905d65238c66b7"

DEPENDS = "freetype libpng jpeg zlib tiff"

SRC_URI = "git://github.com/libgd/libgd.git;branch=master \
          "

SRCREV = "75ef79c9b013fb5eafd92710b3867827682fd52d"

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

DEBUG_OPTIMIZATION_append = " -Wno-error=maybe-uninitialized"

do_install_append_class-target() {
    # cleanup buildpaths from gdlib.pc
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/gdlib.pc
}

PACKAGES += "${PN}-tools"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"
FILES_${PN}-tools = "${bindir}/*"

PROVIDES += "${PN}-tools"
RPROVIDES_${PN}-tools = "${PN}-tools"
RDEPENDS_${PN}-tools = "perl perl-module-strict"

CVE_PRODUCT = "libgd"

BBCLASSEXTEND = "native nativesdk"
