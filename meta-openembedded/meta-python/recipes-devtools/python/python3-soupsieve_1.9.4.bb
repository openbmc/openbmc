SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=5a6fd3b0c24fc5a041a3d1bbb22c81b5"

SRC_URI[md5sum] = "43d8ea20c58494446aa65ba5cc6320fe"
SRC_URI[sha256sum] = "605f89ad5fdbfefe30cdc293303665eff2d188865d4dbe4eb510bba1edfbfce3"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
