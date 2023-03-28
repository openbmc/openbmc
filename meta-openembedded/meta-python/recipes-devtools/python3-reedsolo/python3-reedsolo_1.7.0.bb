SUMMARY = "Pure-Python Reed Solomon encoder/decoder"
HOMEPAGE = "https://github.com/tomerfiliba/reedsolomon"
LICENSE = "MIT-0 | Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ffde61aeb8917e70e0257e0a4b6d103c"

SRC_URI[sha256sum] = "c1359f02742751afe0f1c0de9f0772cc113835aa2855d2db420ea24393c87732"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"
