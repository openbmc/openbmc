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

SRC_URI = "https://archives.boost.io/release/${PV}/source/${BOOST_P}.tar.bz2"
SRC_URI[sha256sum] = "af57be25cb4c4f4b413ed692fe378affb4352ea50fbe294a11ef548f4d527d89"

UPSTREAM_CHECK_URI = "http://www.boost.org/users/download/"
UPSTREAM_CHECK_REGEX = "release/(?P<pver>.*)/source/"

S = "${WORKDIR}/${BOOST_P}"
