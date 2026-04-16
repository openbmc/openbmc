SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec038232ab86edd7354b091c54e190e2"

PYPI_PACKAGE = "types_psutil"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "e8053450685965b8cd52afb62569073d00ea9967ae78bb45dff5f606847f97f2"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "\
	python3-psutil \
"
