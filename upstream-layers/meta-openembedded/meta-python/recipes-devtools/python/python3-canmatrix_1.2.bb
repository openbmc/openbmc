SUMMARY = "Converting Can (Controller Area Network) Database Formats"
HOMEPAGE = "https://github.com/ebroecker/canmatrix"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb3bdbe015537f08812c87d93670ea1b"

SRC_URI[sha256sum] = "19d5ba3fd69974bd41985b90198503bfde6ae3639dfc8ce6a2f7a0338787dbf7"

inherit setuptools3 pypi

RDEPENDS:${PN} += "\
  python3-attrs \
  python3-click \
"
