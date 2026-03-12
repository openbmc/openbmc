SUMMARY = "An implementation of figlet written in Python"
HOMEPAGE = "https://github.com/pwaller/pyfiglet"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f37a72c457e560fc4853ae67f3f9cc0e"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "db9c9940ed1bf3048deff534ed52ff2dafbbc2cd7610b17bb5eca1df6d4278ef"

BBCLASSEXTEND = "native nativesdk"
