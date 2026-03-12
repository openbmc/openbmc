DESCRIPTION = "pytest plugin for generating html reports from test results"
DEPENDS += "python3-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "b6a88cba507500d8709959201e2e757d3941e859fd17cfd4ed87b16fc0c67912"

PYPI_PACKAGE = "pytest_html"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-jinja2 \
    python3-pytest \
    python3-pytest-metadata \
"

BBCLASSEXTEND = "native nativesdk"
