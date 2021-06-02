SUMMARY = "A library for property-based testing"
HOMEPAGE = "https://github.com/HypothesisWorks/hypothesis/tree/master/hypothesis-python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4ee62c16ebd0f4f99d906f36b7de8c3c"

PYPI_PACKAGE = "hypothesis"

inherit pypi setuptools3

SRC_URI[sha256sum] = "262bb8cee0293ad06c453e78cf89bddcb613b91f82ea5587f3787611ee62861b"

RDEPENDS_${PN} += "python3-attrs python3-core python3-sortedcontainers"

BBCLASSEXTEND = "native nativesdk"
