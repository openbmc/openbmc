SUMMARY = "A specification that python filesystems should adhere to."
HOMEPAGE = "https://github.com/fsspec/filesystem_spec"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b38a11bf4dcdfc66307f8515ce1fbaa6"

SRC_URI[sha256sum] = "1ee6a0e28677557f8c2f994e3eea77db6392b4de9cd1f5d7a9e87a0ae9d01b41"

inherit pypi python_hatchling ptest

PYPI_PACKAGE = "fsspec"

DEPENDS = "python3-hatch-vcs-native python3-hatchling-native"

RDEPENDS:${PN}-ptest += "\
	python3-pytest \
	python3-pytest-mock \
	python3-pytest-asyncio \
	python3-pytest-cov \
	python3-pytest-benchmark \
	python3-aiohttp \
	python3-numpy \
	python3-requests \
"

BBCLASSEXTEND = "native nativesdk"
