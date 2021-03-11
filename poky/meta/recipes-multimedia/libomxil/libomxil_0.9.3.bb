SUMMARY = "Bellagio OpenMAX Integration Layer (IL)"
DESCRIPTION = "Bellagio is an opensource implementation of the Khronos OpenMAX \
               Integration Layer API to access multimedia components."
HOMEPAGE = "http://omxil.sourceforge.net/"

LICENSE = "LGPLv2.1+"
LICENSE_FLAGS = "${@bb.utils.contains('PACKAGECONFIG', 'amr', 'commercial', '', d)}"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae6f0f4dbc7ac193b50f323a6ae191cb \
                    file://src/omxcore.h;beginline=1;endline=27;md5=806b1e5566c06486fe8e42b461e03a90"

SRC_URI = "${SOURCEFORGE_MIRROR}/omxil/libomxil-bellagio-${PV}.tar.gz \
           file://configure-fix.patch \
           file://parallel-make.patch \
           file://makefile-docdir-fix.patch \
           file://dynamicloader-linking.patch \
           file://disable-so-versioning.patch"

SRC_URI[md5sum] = "a1de827fdb75c02c84e55f740ca27cb8"
SRC_URI[sha256sum] = "593c0729c8ef8c1467b3bfefcf355ec19a46dd92e31bfc280e17d96b0934d74c"

S = "${WORKDIR}/${BPN}-bellagio-${PV}"

inherit autotools

EXTRA_OECONF += "--disable-doc --disable-Werror"

PROVIDES += "virtual/libomxil"

PACKAGECONFIG ??= ""

PACKAGECONFIG[amr] = "--enable-amr,,"

#
# The .so files under ${libdir}/bellagio are not intended to be versioned and symlinked.
# Make sure they get packaged in the main package.
#
FILES_${PN} += "${libdir}/bellagio/*.so \
                ${libdir}/omxloaders/*${SOLIBS}"
FILES_${PN}-staticdev += "${libdir}/bellagio/*.a \
                          ${libdir}/omxloaders/*.a"
FILES_${PN}-dev += "${libdir}/bellagio/*.la \
                    ${libdir}/omxloaders/*.la \
                    ${libdir}/omxloaders/*${SOLIBSDEV}"
