SUMMARY = "MusicBrainz client library"
DESCRIPTION = "The MusicBrainz client is a library which can be built into other programs.  The library allows you to access the data held on the MusicBrainz server."
HOMEPAGE = "http://musicbrainz.org"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=fbc093901857fcd118f065f900982c24"
DEPENDS = "expat libxml2 libxml2-native neon neon-native libmusicbrainz-native"

PV = "5.1.0+git"

SRCREV = "8be45b12a86bc0e46f2f836c8ac88e1e98d82aee"
SRC_URI = "git://github.com/metabrainz/libmusicbrainz.git;branch=master;protocol=https \
           file://0001-http-fetch-Pass-a-non-null-buffer-to-ne_set_request_.patch \
           "

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE:append:class-target = " -DIMPORT_EXECUTABLES=${STAGING_LIBDIR_NATIVE}/cmake/${BPN}/ImportExecutables.cmake"

do_install:append:class-native() {
    install -Dm 0755 ${B}/src/make-c-interface ${D}${bindir}/make-c-interface
    install -Dm 0644 ${B}/ImportExecutables.cmake ${D}${libdir}/cmake/${BPN}/ImportExecutables.cmake
    sed -i -e s:'${B}'/src/::g ${D}${libdir}/cmake/${BPN}/ImportExecutables.cmake
}

BBCLASSEXTEND = "native"
