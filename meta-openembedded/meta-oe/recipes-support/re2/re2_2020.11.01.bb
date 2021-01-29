DESCRIPTION = "A regular expression library"
HOMEPAGE = "https://github.com/google/re2/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b5c31eb512bdf3cb11ffd5713963760"

SRCREV = "166dbbeb3b0ab7e733b278e8f42a84f6882b8a25"

SRC_URI = "git://github.com/google/re2.git;branch=master"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += " \
	-DBUILD_SHARED_LIBS=ON \
	-DRE2_BUILD_TESTING=OFF \
"

# Don't include so files in dev package
FILES_${PN} = "${libdir}"
FILES_${PN}-dev = "${includedir} ${libdir}/cmake"

BBCLASSEXTEND = "native nativesdk"
