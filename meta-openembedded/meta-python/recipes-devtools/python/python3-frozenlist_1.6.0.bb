SUMMARY = "A list-like structure which implements collections.abc.MutableSequence, and which can be made immutable."
HOMEPAGE = "https://github.com/aio-libs/frozenlist"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "b99655c32c1c8e06d111e7f41c06c29a5318cb1835df23a45518e02a47c63b68"

inherit pypi python_setuptools_build_meta cython

DEPENDS += " \
    python3-expandvars-native \
"

BBCLASSEXTEND = "native nativesdk"
