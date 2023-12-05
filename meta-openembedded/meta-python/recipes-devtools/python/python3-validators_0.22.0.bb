SUMMARY = "Python Data Validation for Humans"
HOMEPAGE = "https://python-validators.github.io/validators"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=fcf28bd09a60e145c3171c531b9e677d"
SRC_URI[sha256sum] = "77b2689b172eeeb600d9605ab86194641670cdb73b60afd577142a9397873370"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
