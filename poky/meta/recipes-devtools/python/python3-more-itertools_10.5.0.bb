SUMMARY = "More routines for operating on iterables, beyond itertools"
HOMEPAGE = "https://github.com/erikrose/more-itertools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "5482bfef7849c25dc3c6dd53a6173ae4795da2a41a80faea6700d9f5846c5da6"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += " \
        python3-asyncio \
        "

RDEPENDS:${PN}-ptest += " \
	python3-statistics \
        "

BBCLASSEXTEND = "native nativesdk"
