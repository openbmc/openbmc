SUMMARY = "Parses image files' header and return image size."
HOMEPAGE = "https://github.com/shibukawa/imagesize_py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=0c128f0f7e8a02e1b83884c0b5a41cda"

SRC_URI[sha256sum] = "b2ba6a4dea487a7ebcd53248d3476aca449d30db12a2dde5e0c5ca9624fd77e5"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-xml"
