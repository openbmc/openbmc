SUMMARY = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "48e8f4d9e7e5878571ecf6f2b4e57634f93cd474cc8cfbd2376f2d11b396e30d"

SRC_URI += "file://run-ptest"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += " \
        python3-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	python3-statistics \
        "

BBCLASSEXTEND = "native nativesdk"
