SUMMARY = "Converting Can (Controller Area Network) Database Formats"
HOMEPAGE = "https://github.com/ebroecker/canmatrix"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb3bdbe015537f08812c87d93670ea1b"

SRC_URI[sha256sum] = "9af8f0a655277402c1fe2a5e2a8e8d5021e5c19e86f647f47b0c2aa209597ebb"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
  python3-attrs \
  python3-click \
"
