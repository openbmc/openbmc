SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "https://wrapt.readthedocs.org/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=63a78af2900bfcc5ce482f3b8d445898"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "7082fc1f94b020ac275870c4af71b09cff22876fe6e9c4c0ad01ea21d217b288"

# python3-misc for 'this' module, python3-image for colorsys (test_deferred_patching)
RDEPENDS:${PN}-ptest += " \
	python3-image \
	python3-misc \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-stringold \
    python3-threading \
"

BBCLASSEXTEND = "native"
