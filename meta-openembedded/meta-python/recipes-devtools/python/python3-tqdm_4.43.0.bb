SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7ea57584e3f8bbde2ae3e1537551de25"

SRC_URI[md5sum] = "81454c26572e4e47911596ea065eb1b7"
SRC_URI[sha256sum] = "f35fb121bafa030bd94e74fcfd44f3c2830039a2ddef7fc87ef1c2d205237b24"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
