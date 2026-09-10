SUMMARY = "Relaxed test discovery for pytest"
HOMEPAGE = "https://github.com/bitprophet/pytest-relaxed"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8815068973f31b78c328dc067e297ab"

SRC_URI += "file://0001-pytest_relaxed-plugin.py-Handle-terminalreporter.patch"

SRC_URI[sha256sum] = "956ea028ec30dbbfb680dd8e7b4a7fb8f80a239595e88bace018bf2c0d718248"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core \
    python3-pytest \
    python3-decorator \
"
