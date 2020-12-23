SUMMARY = "A library for property-based testing"
HOMEPAGE = "https://github.com/HypothesisWorks/hypothesis/tree/master/hypothesis-python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4ee62c16ebd0f4f99d906f36b7de8c3c"

PYPI_PACKAGE = "hypothesis"

inherit pypi setuptools3

SRC_URI[sha256sum] = "7ef22dd2ae4a906ef1e237dcd6806aa7f97e30c37f924a0e6d595f4639350b53"

RDEPENDS_${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
