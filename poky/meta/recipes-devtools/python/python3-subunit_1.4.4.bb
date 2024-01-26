SUMMARY = "Python implementation of subunit test streaming protocol"
HOMEPAGE = "https://pypi.org/project/python-subunit/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;beginline=1;endline=20;md5=b1121e68d06c8d9ea544fcd895df0d39"

PYPI_PACKAGE = "python-subunit"

SRC_URI[sha256sum] = "1079363131aa1d3f45259237265bc2e61a77e35f20edfb6e3d1d2558a2cdea34"

inherit pypi setuptools3

RDEPENDS:${PN} = " python3-testtools python3-iso8601"

BBCLASSEXTEND = "nativesdk"
