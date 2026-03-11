SUMMARY = "MusicBrainz client library"
DESCRIPTION = "The MusicBrainz client is a library which can be built into other programs.  The library allows you to access the data held on the MusicBrainz server."
HOMEPAGE = "http://musicbrainz.org"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=fbc093901857fcd118f065f900982c24"
DEPENDS = "expat libxml2 libxml2-native neon neon-native libmusicbrainz-native"

PV = "5.1.0+git"

SRCREV = "4efbed3afae11ef68281816088d7cf3d0f704dfe"
SRC_URI = "git://github.com/metabrainz/libmusicbrainz.git;branch=master;protocol=https \
           file://0001-http-fetch-Pass-a-non-null-buffer-to-ne_set_request_.patch \
           "


inherit cmake pkgconfig

EXTRA_OECMAKE:append:class-target = " -DIMPORT_EXECUTABLES=${STAGING_LIBDIR_NATIVE}/cmake/${BPN}/ImportExecutables.cmake"

do_install:append:class-native() {
    install -Dm 0755 ${B}/src/make-c-interface ${D}${bindir}/make-c-interface
    install -Dm 0644 ${B}/ImportExecutables.cmake ${D}${libdir}/cmake/${BPN}/ImportExecutables.cmake
	sed -i '/^# Create imported target make-c-interface$/a\
get_filename_component(_IMPORT_PREFIX "${CMAKE_CURRENT_LIST_DIR}/../../../" ABSOLUTE)' ${D}${libdir}/cmake/${BPN}/ImportExecutables.cmake
    sed -i -e 's:${B}/src/:${_IMPORT_PREFIX}/bin/:g' ${D}${libdir}/cmake/${BPN}/ImportExecutables.cmake
}

BBCLASSEXTEND = "native"
