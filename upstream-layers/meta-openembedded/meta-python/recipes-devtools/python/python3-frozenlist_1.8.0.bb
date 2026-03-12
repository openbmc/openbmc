SUMMARY = "A list-like structure which implements collections.abc.MutableSequence, and which can be made immutable."
HOMEPAGE = "https://github.com/aio-libs/frozenlist"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "3ede829ed8d842f6cd48fc7081d7a41001a56f1f38603f9d49bf3020d59a31ad"

inherit pypi python_setuptools_build_meta cython

SRC_URI += " \
    file://0001-build-wheel-in-place.patch \
"
DEPENDS += " \
    python3-expandvars-native \
"

BBCLASSEXTEND = "native nativesdk"
