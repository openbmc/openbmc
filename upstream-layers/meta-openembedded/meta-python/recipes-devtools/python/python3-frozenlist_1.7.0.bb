SUMMARY = "A list-like structure which implements collections.abc.MutableSequence, and which can be made immutable."
HOMEPAGE = "https://github.com/aio-libs/frozenlist"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "2e310d81923c2437ea8670467121cc3e9b0f76d3043cc1d2331d56c7fb7a3a8f"

inherit pypi python_setuptools_build_meta cython

DEPENDS += " \
    python3-expandvars-native \
"

BBCLASSEXTEND = "native nativesdk"
