SUMMARY = "Python template engine and code generation tool"
HOMEPAGE = "https://cheetahtemplate.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6c8d05debf9d3d283931051ce5232fe7"

PYPI_PACKAGE = "ct3"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-pickle python3-pprint"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "1c5f2000d52d591703c74f6f5f7ef427ed1b6501be28e3f1634f62c3a5d792e1"
