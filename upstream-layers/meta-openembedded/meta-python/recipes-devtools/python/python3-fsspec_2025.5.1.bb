SUMMARY = "A specification that python filesystems should adhere to."
HOMEPAGE = "https://github.com/fsspec/filesystem_spec"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b38a11bf4dcdfc66307f8515ce1fbaa6"

SRC_URI[sha256sum] = "2e55e47a540b91843b755e83ded97c6e897fa0942b11490113f09e9c443c2475"

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
