SUMMARY = "Physical quantities module"
DESCRIPTION = "Physical quantities Python module"
HOMEPAGE = "https://github.com/hgrecco/pint"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bccf824202692270a1e0829a62e3f47b"

inherit pypi python_hatchling ptest-python-pytest

SRC_URI[sha256sum] = "391e5d2fc01fdb121c113b8eb47386688575542c1f5144c465ff318bf5133573"

DEPENDS += "python3-hatch-vcs-native"

BBCLASSEXTEND = "native nativesdk"

PTEST_PYTEST_DIR = "pint"

RDEPENDS:${PN} += " \
	python3-flexcache \
	python3-flexparser \
	python3-packaging \
	python3-platformdirs \
	python3-typing-extensions \
"
# python3-misc for timeit.py
RDEPENDS:${PN}-ptest += " \
	python3-appdirs \
	python3-attrs \
	python3-misc \
	python3-pytest-benchmark \
	python3-pytest-subtests \
	python3-statistics \
"
