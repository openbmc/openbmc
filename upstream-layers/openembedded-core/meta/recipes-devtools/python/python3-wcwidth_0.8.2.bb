SUMMARY = "Library for building powerful interactive command lines in Python"
DESCRIPTION = "Measures the displayed width of unicode strings in a terminal"
HOMEPAGE = "https://github.com/jquast/wcwidth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b15979c39a2543892fca8cd86b4b52cb"

SRC_URI[sha256sum] = "91fbef97204b96a3d4d421609b80340b760cf33e26da123ff243d76b1fda8dda"

inherit pypi python_hatchling ptest-python-pytest

do_install_ptest:aapend() {
      install -d ${D}${PTEST_PATH}/bin
      cp -rf ${S}/bin/* ${D}${PTEST_PATH}/bin/
}

BBCLASSEXTEND = "native nativesdk"
