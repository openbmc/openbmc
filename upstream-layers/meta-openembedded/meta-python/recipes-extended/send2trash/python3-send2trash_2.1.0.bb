SUMMARY = "Send file to trash natively under Mac OS X, Windows and Linux"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a02659c2d5f4cc626e4dcf6504b865eb"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI += "file://run-ptest"
SRC_URI[sha256sum] = "1c72b39f09457db3c05ce1d19158c2cbef4c32b8bedd02c155e49282b7ea7459"

PYPI_PACKAGE = "send2trash"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += "\
    python3-io \
    python3-datetime \
"
