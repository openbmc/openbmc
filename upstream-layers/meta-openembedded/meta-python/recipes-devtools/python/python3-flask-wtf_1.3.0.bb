DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d98d089889e14b227732d45dac3aacc4"

SRC_URI[sha256sum] = "61d5dabc50c3df885c297dcbd80810443a5d632106c8a69cab8ce740f0cdd7cc"

PYPI_PACKAGE = "flask_wtf"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    python3-flask \
    python3-itsdangerous \
    python3-json \
    python3-wtforms \
"
