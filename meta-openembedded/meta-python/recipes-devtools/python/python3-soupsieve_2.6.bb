SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=1bdfa4c8e8b6830bcf8e78cba1669e4d"

SRC_URI[sha256sum] = "e2e68417777af359ec65daac1057404a3c8a5455bb8abc36f1a9866ab1a51abb"

inherit pypi python_hatchling python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-core \
    python3-datetime \
"
RDEPENDS:${PN}:append:class-target = " \
    python3-beautifulsoup4 \
"

RDEPENDS:${PN}-ptest += " \
    python3-beautifulsoup4 \
    python3-typing-extensions \
"

BBCLASSEXTEND = "native nativesdk"
