SUMMARY = "A list-like structure which implements collections.abc.MutableSequence, and which can be made immutable."
HOMEPAGE = "https://github.com/aio-libs/frozenlist"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "09163bdf0b2907454042edb19f887c6d33806adc71fbd54afc14908bfdc22251"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

