SUMMARY = "Add a copy button to code blocks in Sphinx"
HOMEPAGE = "https://sphinx-copybutton.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c60e920848b6d2ecec51ea44a1a33bf0"

SRC_URI[sha256sum] = "4cf17c82fb9646d1bc9ca92ac280813a3b605d8c421225fd9913154103ee1fbd"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
