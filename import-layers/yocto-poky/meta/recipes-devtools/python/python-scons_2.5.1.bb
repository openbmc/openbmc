SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3a885dff6d14e4cd876d9008a09a42de"
SRCNAME = "scons"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "3eac81e5e8206304a9b4683c57665aa4"
SRC_URI[sha256sum] = "c8de85fc02ed1a687b1f2ac791eaa0c1707b4382a204f17d782b5b111b9fdf07"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/SCons/"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "\
  python-fcntl \
  python-io \
  python-json \
  python-subprocess \
  "
