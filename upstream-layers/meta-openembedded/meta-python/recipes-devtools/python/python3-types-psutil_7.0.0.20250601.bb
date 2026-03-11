SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec038232ab86edd7354b091c54e190e2"

PYPI_PACKAGE = "types_psutil"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "71fe9c4477a7e3d4f1233862f0877af87bff057ff398f04f4e5c0ca60aded197"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "\
	python3-psutil \
"
