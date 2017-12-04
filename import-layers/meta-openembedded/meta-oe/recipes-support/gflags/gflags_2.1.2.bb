DESCRIPTION = "The gflags package contains a C++ library that implements commandline flags processing. It includes built-in support for standard types such as string and the ability to define flags in the source file in which they are used"

HOMEPAGE = "https://github.com/gflags/gflags"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=c80d1a3b623f72bb85a4c75b556551df"

SRC_URI = "https://github.com/gflags/gflags/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "ac432de923f9de1e9780b5254884599f"
SRC_URI[sha256sum] = "d8331bd0f7367c8afd5fcb5f5e85e96868a00fd24b7276fa5fcee1e5575c2662"
S = "${WORKDIR}/${PN}-${PV}/"

FILES_${PN}-dev += "${libdir}/cmake"

inherit cmake

EXTRA_OECMAKE="-DBUILD_SHARED_LIBS=ON"

PACKAGES =+ "${PN}-bash-completion"
FILES_${PN}-bash-completion += "${bindir}/gflags_completions.sh"
RDEPENDS_${PN}-bash-completion = "bash-completion"
