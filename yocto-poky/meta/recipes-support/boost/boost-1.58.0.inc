# The Boost web site provides free peer-reviewed portable
# C++ source libraries. The emphasis is on libraries which
# work well with the C++ Standard Library. The libraries are
# intended to be widely useful, and are in regular use by
# thousands of programmers across a broad spectrum of applications.
HOMEPAGE = "http://www.boost.org/"
LICENSE = "BSL-1.0 & MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

BOOST_VER = "${@"_".join(d.getVar("PV",1).split("."))}"
BOOST_MAJ = "${@"_".join(d.getVar("PV",1).split(".")[0:2])}"
BOOST_P = "boost_${BOOST_VER}"

SRC_URI = "${SOURCEFORGE_MIRROR}/boost/${BOOST_P}.tar.bz2 \
           file://0001-mips1-fix-added-gcc.jam-was-passing-the-options-m32-.patch \
           file://0002-Don-t-pass-m32-m64-where-it-s-not-supported.patch \
"

SRC_URI[md5sum] = "b8839650e61e9c1c0a89f371dd475546"
SRC_URI[sha256sum] = "fdfc204fc33ec79c99b9a74944c3e54bd78be4f7f15e260c0e2700a36dc7d3e5"

S = "${WORKDIR}/${BOOST_P}"
