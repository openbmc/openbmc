DESCRIPTION = "The gflags package contains a C++ library that implements commandline flags processing. It includes built-in support for standard types such as string and the ability to define flags in the source file in which they are used"
HOMEPAGE = "https://github.com/gflags/gflags"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=c80d1a3b623f72bb85a4c75b556551df"

SRC_URI = "git://github.com/gflags/gflags.git"
SRCREV = "e171aa2d15ed9eb17054558e0b3a6a413bb01067"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE="-DBUILD_SHARED_LIBS=ON -DREGISTER_INSTALL_PREFIX=OFF -DLIB_INSTALL_DIR=${baselib}"

PACKAGES =+ "${PN}-bash-completion"
FILES_${PN}-bash-completion += "${bindir}/gflags_completions.sh"

RDEPENDS_${PN}-bash-completion = "bash bash-completion"

BBCLASSEXTEND = "native nativesdk"
