SUMMARY = "Python implementation of subunit test streaming protocol"
HOMEPAGE = "https://pypi.org/project/python-subunit/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 OR BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;beginline=1;endline=20;md5=b1121e68d06c8d9ea544fcd895df0d39"

PYPI_PACKAGE = "python-subunit"
PYPI_PACKAGE_SDIST = "python_subunit"

SRC_URI[sha256sum] = "31fe6a7b35550b0d72da98caaa6ba64c547d9346c2846b845cb5c781849e2762"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-iso8601-native python3-testtools-native"
RDEPENDS:${PN} = " python3-testtools python3-iso8601"

BBCLASSEXTEND = "nativesdk"
