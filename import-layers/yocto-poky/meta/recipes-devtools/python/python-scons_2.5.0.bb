SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3a885dff6d14e4cd876d9008a09a42de"
SRCNAME = "scons"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "bda5530a70a41a7831d83c8b191c021e"
SRC_URI[sha256sum] = "01f1b3d6023516a8e1b5e77799e5a82a23b32953b1102d339059ffeca8600493"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/SCons/"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

RDEPENDS_${PN} = "\
  python-fcntl \
  python-io \
  python-json \
  python-subprocess \
  "
