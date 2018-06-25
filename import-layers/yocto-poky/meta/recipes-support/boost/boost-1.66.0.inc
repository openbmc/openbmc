# The Boost web site provides free peer-reviewed portable
# C++ source libraries. The emphasis is on libraries which
# work well with the C++ Standard Library. The libraries are
# intended to be widely useful, and are in regular use by
# thousands of programmers across a broad spectrum of applications.
HOMEPAGE = "http://www.boost.org/"
LICENSE = "BSL-1.0 & MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

BOOST_VER = "${@"_".join(d.getVar("PV").split("."))}"
BOOST_MAJ = "${@"_".join(d.getVar("PV").split(".")[0:2])}"
BOOST_P = "boost_${BOOST_VER}"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/boost/boost/${PV}/${BOOST_P}.tar.bz2"
SRC_URI[md5sum] = "b2dfbd6c717be4a7bb2d88018eaccf75"
SRC_URI[sha256sum] = "5721818253e6a0989583192f96782c4a98eb6204965316df9f5ad75819225ca9"

UPSTREAM_CHECK_URI = "http://www.boost.org/users/download/"
UPSTREAM_CHECK_REGEX = "boostorg/release/(?P<pver>.*)/source/"

S = "${WORKDIR}/${BOOST_P}"
