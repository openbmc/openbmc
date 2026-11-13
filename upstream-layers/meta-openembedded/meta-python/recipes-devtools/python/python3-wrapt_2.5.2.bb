SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "https://wrapt.readthedocs.org/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=63a78af2900bfcc5ce482f3b8d445898"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "551cb49e88b3722faad780303833bc4fa70685fb1773270fb98b40d39d9fddbf"

# python3-misc for 'this' module, python3-image for colorsys (test_deferred_patching)
RDEPENDS:${PN}-ptest += " \
	python3-image \
	python3-misc \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-stringold \
    python3-threading \
    python3-asyncio \
    python3-json \
"

BBCLASSEXTEND = "native"
