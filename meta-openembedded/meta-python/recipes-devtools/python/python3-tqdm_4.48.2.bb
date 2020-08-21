SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[md5sum] = "d29c836d74d2f2ec6a10d052937f7371"
SRC_URI[sha256sum] = "564d632ea2b9cb52979f7956e093e831c28d441c11751682f84c86fc46e4fd21"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
