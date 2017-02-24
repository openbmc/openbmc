SUMMARY = "gd is a library used to create PNG, JPEG, or WBMP images"
DESCRIPTION = "The gd graphics library allows your code to quickly draw images \
complete with lines, arcs, text, multiple colors, cut and paste from other \
images, and flood fills, and to write out the result as a PNG or JPEG file. \
This is particularly useful in Web applications, where PNG and JPEG are two \
of the formats accepted for inline images by most browsers. Note that gd is not \
a paint program."
HOMEPAGE = "http://libgd.bitbucket.org/"

SECTION = "libs"
LICENSE = "GD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c97638cafd3581eb87abd37332137669"
DEPENDS = "freetype libpng jpeg zlib tiff"

SRC_URI = "git://github.com/libgd/libgd.git;branch=GD-2.2 \
           file://fix-gcc-unused-functions.patch"

SRCREV = "46ceef5970bf3a847ff61d1bdde7501d66c11d0c"

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

do_install_append() {
    # cleanup buildpaths from gdlib.pc
    sed -i -e 's#${STAGING_DIR_HOST}##g' ${D}${libdir}/pkgconfig/gdlib.pc
}

PACKAGES += "${PN}-tools"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"
FILES_${PN}-tools = "${bindir}/*"

PROVIDES += "${PN}-tools"
RPROVIDES_${PN}-tools = "${PN}-tools"
RDEPENDS_${PN}-tools = "perl perl-module-strict"
