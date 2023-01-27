SUMMARY = "Pure-Python Reed Solomon encoder/decoder"
HOMEPAGE = "https://github.com/tomerfiliba/reedsolomon"
LICENSE = "MIT-0 | Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ffde61aeb8917e70e0257e0a4b6d103c"

SRC_URI[sha256sum] = "a7ce6b0efad8df491a70b87b6ab3543e751f7700dc4ac0b12081a456ef634f5d"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"
