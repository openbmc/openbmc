SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

inherit pypi python_hatchling ptest-python-pytest python_setuptools_build_meta

SRC_URI[sha256sum] = "85a45d1da8fe9c9f7477fed8aef59ad2b939af3d6611507e1a9cbdacdcd3450a"

DEPENDS += "python3-setuptools-scm-native python3-hatch-vcs-native"

BBCLASSEXTEND = "native nativesdk"

PTEST_PYTEST_DIR = "pint"

RDEPENDS:${PN} += " \
	python3-setuptools \
	python3-packaging \
	python3-platformdirs \
"
# python3-misc for timeit.py 
RDEPENDS:${PN}-ptest += " \
	python3-appdirs \
	python3-attrs \
	python3-flexcache \
	python3-flexparser \
	python3-misc \
	python3-pytest-benchmark \
	python3-pytest-subtests \
	python3-statistics \
"

