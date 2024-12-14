SUMMARY = "Pure-Python Reed Solomon encoder/decoder"
HOMEPAGE = "https://github.com/tomerfiliba/reedsolomon"
LICENSE = "MIT-0 | Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ffde61aeb8917e70e0257e0a4b6d103c"

SRC_URI[sha256sum] = "e11528d63e75461d45b86c23f9db8260303d6ab6cae075fa9eddb44527ad8ece"

inherit pypi python_setuptools_build_meta cython

RDEPENDS:${PN} += "python3-core"
