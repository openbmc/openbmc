SUMMARY = "module for creating simple ASCII tables"
HOMEPAGE = "https://github.com/foutaise/texttable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a97cdac2d9679ffdcfef3dc036d24f6"

inherit pypi setuptools3

SRC_URI[sha256sum] = "290348fb67f7746931bcdfd55ac7584ecd4e5b0846ab164333f0794b121760f2"

BBCLASSEXTEND = "native nativesdk"
