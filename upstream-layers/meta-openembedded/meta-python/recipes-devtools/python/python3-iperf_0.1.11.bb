SUMMARY = "Python wrapper around iperf3"
HOMEPAGE = "https://github.com/thiezn/iperf3-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7d0900b3d30647cdda002c9549ca40f"

PYPI_PACKAGE = "iperf3"

SRC_URI[sha256sum] = "d50eebbf2dcf445a173f98a82f9c433e0302d3dfb7987e1f21b86b35ef63ce26"

inherit setuptools3 pypi

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-json \
    python3-threading \
"
