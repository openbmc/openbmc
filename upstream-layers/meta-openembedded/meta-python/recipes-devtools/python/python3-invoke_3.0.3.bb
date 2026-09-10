SUMMARY = "Pythonic task execution"
HOMEPAGE = "https://www.pyinvoke.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8815068973f31b78c328dc067e297ab"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "437b6a622223824380bfb4e64f612711a6b648c795f565efc8625af66fb57f0c"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-fcntl \
    python3-json \
    python3-logging \
    python3-pprint \
    python3-terminal \
    python3-unittest \
    python3-unixadmin \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-pytest-benchmark \
    python3-core \
    python3-io \
    python3-pytest-relaxed \
    python3-icecream \
    python3-unittest \
    python3-unittest-automake-output \
"
