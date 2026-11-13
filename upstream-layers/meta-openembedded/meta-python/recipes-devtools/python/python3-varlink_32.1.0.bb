SUMMARY = "Python implementation of the Varlink protocol"
HOMEPAGE = "https://github.com/varlink/python"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e3fc50a88d0a364313df4b21ef20c29e"

DEPENDS += "python3-setuptools-scm-native python3-wheel-native"

# 32.1.0 is not published on PyPI
SRC_URI = "git://github.com/varlink/python.git;protocol=https;branch=master"
SRCREV = "8b3ba4e8206b098c524f538036a941b47797840d"

inherit python_setuptools_build_meta ptest-python-pytest

# Otherwise setuptools_scm falls back to 0.1.dev1
export SETUPTOOLS_SCM_PRETEND_VERSION = "${PV}"

RDEPENDS:${PN} = "\
    python3-core \
    python3-datetime \
    python3-io \
    python3-json \
    python3-netclient \
    python3-netserver \
    python3-shell \
"

# Keep the bundled test suite, and its python3-unittest dependency, separate
FILES:${PN}-ptest = "${PYTHON_SITEPACKAGES_DIR}/varlink/tests"
RDEPENDS:${PN}-ptest = "${PN} python3-unittest"

# Upstream keeps its tests inside the importable package rather than in a
# top-level tests/ directory.
PTEST_PYTEST_DIR = "varlink/tests"
