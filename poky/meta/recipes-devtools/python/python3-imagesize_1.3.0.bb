DESCRIPTION = "Parses image files’ header and return image size."
HOMEPAGE = "https://github.com/shibukawa/imagesize_py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=0c128f0f7e8a02e1b83884c0b5a41cda"

SRC_URI[sha256sum] = "cd1750d452385ca327479d45b64d9c7729ecf0b3969a58148298c77092261f9d"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-xml"
