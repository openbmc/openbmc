SUMMARY = "A renamed copy of the crypt module as it was present in Python 3.12 before it was removed."
HOMEPAGE = "https://github.com/fedora-python/crypt_r"
LICENSE = "Python-2.0.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2"

SRC_URI = "git://github.com/fedora-python/crypt_r.git;branch=main;protocol=https"
SRCREV = "b20e21d9f24d6fa17a6781bbc9f272ce38246eef"

S = "${UNPACKDIR}/git"

inherit python_setuptools_build_meta
