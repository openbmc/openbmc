SUMMARY = "Library for building powerful interactive command lines in Python"
DESCRIPTION = "Measures the displayed width of unicode strings in a terminal"
HOMEPAGE = "https://github.com/jquast/wcwidth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b15979c39a2543892fca8cd86b4b52cb"

SRC_URI[sha256sum] = "faf5b4a5366a72dc49cad48cdf21f52bdf63bdda995178e483ba247ff79089b9"

inherit pypi python_hatchling ptest-python-pytest

do_install_ptest:aapend() {
      install -d ${D}${PTEST_PATH}/bin
      cp -rf ${S}/bin/* ${D}${PTEST_PATH}/bin/
}

BBCLASSEXTEND = "native nativesdk"
