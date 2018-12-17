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
LIC_FILES_CHKSUM = "file://COPYING;md5=07384b3aa2e0d39afca0d6c40286f545"

DEPENDS = "freetype libpng jpeg zlib tiff"

SRC_URI = "git://github.com/libgd/libgd.git;branch=GD-2.2 \
           file://0001-annotate.c-gdft.c-Replace-strncpy-with-memccpy-to-fi.patch \
           file://CVE-2018-1000222.patch \
          "

SRCREV = "8255231b68889597d04d451a72438ab92a405aba"

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
