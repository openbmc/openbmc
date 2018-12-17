SUMMARY = "An open source MPEG-4 and MPEG-2 AAC decoding library"
HOMEPAGE = "http://www.audiocoding.com/faad2.html"
SECTION = "libs"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=381c8cbe277a7bc1ee2ae6083a04c958"

LICENSE_FLAGS = "commercial"

PR = "r1"

inherit autotools

SRC_URI = "${SOURCEFORGE_MIRROR}/faac/faad2-src/faad2-${PV}/${BP}.tar.bz2;name=faad2 \
"

SRC_URI[faad2.md5sum] = "4c332fa23febc0e4648064685a3d4332"
SRC_URI[faad2.sha256sum] = "14561b5d6bc457e825bfd3921ae50a6648f377a9396eaf16d4b057b39a3f63b5"

PACKAGES =+ "libfaad libfaad-dev libmp4ff libmp4ff-dev"

FILES_${PN} = "${bindir}/faad"

FILES_libfaad = "${libdir}/libfaad.so.*"
FILES_libfaad-dev = "${libdir}/libfaad.so ${libdir}/libfaad.la ${includedir}/faad.h"
FILES_libfaad-staticdev = "${libdir}/libfaad.a"

FILES_libmp4ff = "${libdir}/libmp4ff.so.*"
FILES_libmp4ff-dev = "${libdir}/libmp4ff.so ${libdir}/libmp4ff.la ${includedir}/mp4ff.h"
FILES_libmp4ff-staticdev = "${libdir}/libmp4ff.a"

