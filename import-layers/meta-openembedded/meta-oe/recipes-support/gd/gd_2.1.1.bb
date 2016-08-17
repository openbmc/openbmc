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

SRC_URI = "https://bitbucket.org/libgd/gd-libgd/downloads/libgd-${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "d3f1a992ac9c550ebc6da89c147f89af"
SRC_URI[sha256sum] = "895ea9c6fcab187b0a908ae3e9e6b06423756f8a643d362349b0caab4014bd0d"

S = "${WORKDIR}/libgd-${PV}"

inherit autotools binconfig gettext pkgconfig

EXTRA_OECONF += " --disable-rpath \
                  --with-jpeg=${STAGING_LIBDIR}/.. \
                  --with-freetype=yes \
                  --without-fontconfig \
                  --without-xpm \
                  --without-x \
                  --without-vpx \
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
