SUMMARY = "Library for building powerful interactive command lines in Python"
DESCRIPTION = "Measures the displayed width of unicode strings in a terminal"
HOMEPAGE = "https://github.com/jquast/wcwidth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b15979c39a2543892fca8cd86b4b52cb"

SRC_URI[sha256sum] = "fd8d57d3cdfe570e3265c8fab1885c24e34ef40889c0759c0c304a19e9835954"

inherit pypi python_hatchling ptest-python-pytest

do_install_ptest:append() {
      cp ${S}/pyproject.toml ${S}/setup.py ${D}${PTEST_PATH}/
      cp -r ${S}/libwcwidth ${D}${PTEST_PATH}/libwcwidth

      # Remove pre-release tests, require authentication with github
      rm ${D}${PTEST_PATH}/tests/test_check_release.py
}

BBCLASSEXTEND = "native nativesdk"
