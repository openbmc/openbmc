SUMMARY = "Test equality of unordered collections in pytest"
HOMEPAGE = "https://github.com/utapyngo/pytest-unordered"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcd3af2d38a4d4dfd5138c6f163dbe2e"

SRC_URI[sha256sum] = "f61b4f6e06a60a92db50968954efac93e2f584290a49f53ad135e3f32f57e02a"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pytest"

BBCLASSEXTEND = "native nativesdk"
