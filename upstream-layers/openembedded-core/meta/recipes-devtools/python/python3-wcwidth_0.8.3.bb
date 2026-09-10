SUMMARY = "Library for building powerful interactive command lines in Python"
DESCRIPTION = "Measures the displayed width of unicode strings in a terminal"
HOMEPAGE = "https://github.com/jquast/wcwidth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b15979c39a2543892fca8cd86b4b52cb"

SRC_URI[sha256sum] = "d128512515fbf4612e0ff21fd6380399210318b7b54a9af59dff8454cf9730eb"

inherit pypi python_hatchling ptest-python-pytest

do_install_ptest:aapend() {
      install -d ${D}${PTEST_PATH}/bin
      cp -rf ${S}/bin/* ${D}${PTEST_PATH}/bin/
}

BBCLASSEXTEND = "native nativesdk"
