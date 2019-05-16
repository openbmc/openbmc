SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=37bb53a08e6beaea0c90e7821d731284"

SRC_URI = "${SOURCEFORGE_MIRROR}/scons/scons-${PV}.tar.gz"
SRC_URI[md5sum] = "9f9c163e8bd48cf8cd92f03e85ca6395"
SRC_URI[sha256sum] = "df676f23dc6d4bfa384fc389d95dcd21ab907e6349d4c848958ba4befb73c73e"

S = "${WORKDIR}/scons-${PV}"

UPSTREAM_CHECK_URI = "http://scons.org/pages/download.html"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

inherit setuptools

RDEPENDS_${PN} = "\
  python-fcntl \
  python-io \
  python-json \
  python-subprocess \
  python-shell \
  python-pprint \
  "
