SUMMARY = "Parses image files' header and return image size."
HOMEPAGE = "https://github.com/shibukawa/imagesize_py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=0c128f0f7e8a02e1b83884c0b5a41cda"

SRC_URI[sha256sum] = "8e8358c4a05c304f1fccf7ff96f036e7243a189e9e42e90851993c558cfe9ee3"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-xml"
