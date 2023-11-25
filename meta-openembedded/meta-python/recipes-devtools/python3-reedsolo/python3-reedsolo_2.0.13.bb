SUMMARY = "Pure-Python Reed Solomon encoder/decoder"
HOMEPAGE = "https://github.com/tomerfiliba/reedsolomon"
LICENSE = "MIT-0 | Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ffde61aeb8917e70e0257e0a4b6d103c"

SRC_URI[sha256sum] = "71b4121c6860a55899435c552051a19d5f023c50358be4b1c0fa0c6e2f4ac717"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"
