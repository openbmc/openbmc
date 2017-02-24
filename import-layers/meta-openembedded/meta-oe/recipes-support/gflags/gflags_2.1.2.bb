DESCRIPTION = "The gflags package contains a C++ library that implements commandline flags processing. It includes built-in support for standard types such as string and the ability to define flags in the source file in which they are used"

HOMEPAGE = "https://github.com/gflags/gflags"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=c80d1a3b623f72bb85a4c75b556551df"

SRC_URI = "git://github.com/gflags/gflags.git;branch=release"
SRCREV = "1a02f2851ee3d48d32d2c8f4d8f390a0bc25565c"
S = "${WORKDIR}/git/"

FILES_${PN}-dev += "${libdir}/cmake"

inherit cmake

EXTRA_OECMAKE="-DBUILD_SHARED_LIBS=ON"

PACKAGES =+ "${PN}-bash-completion"
FILES_${PN}-bash-completion += "${bindir}/gflags_completions.sh"
RDEPENDS_${PN}-bash-completion = "bash-completion"
