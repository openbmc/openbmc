SUMMARY = "Parses image files’ header and return image size."
HOMEPAGE = "https://github.com/shibukawa/imagesize_py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=0c128f0f7e8a02e1b83884c0b5a41cda"

SRC_URI[sha256sum] = "69150444affb9cb0d5cc5a92b3676f0b2fb7cd9ae39e947a5e11a36b4497cd4a"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-xml"
