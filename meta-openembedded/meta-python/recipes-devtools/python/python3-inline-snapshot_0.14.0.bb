SUMMARY = "golden master/snapshot/approval testing library which puts the values right into your source code"
HOMEPAGE = "https://15r10nk.github.io/inline-snapshot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a35eb90dfdf03953dd2074d0fdba1d4"

DEPENDS = "python3-hatchling-native"
SRC_URI[sha256sum] = "54fdf7831055d06a2423054875d640102865a164cc8291a8086e44dd9b4fd316"

inherit pypi python_hatchling

PYPI_PACKAGE = "inline_snapshot"

RDEPENDS:${PN} = "python3-asttokens \
                  python3-black \
                  python3-click \
                  python3-executing \
                  python3-rich \
                  python3-tomli \
                  python3-typing-extensions \
                  "
