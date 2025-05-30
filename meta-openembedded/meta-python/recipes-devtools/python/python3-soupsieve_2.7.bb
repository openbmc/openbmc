SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a23cdb0cf58a8b6d3d25202783bd6553"

SRC_URI[sha256sum] = "ad282f9b6926286d2ead4750552c8a6142bc4c783fd66b0293547c8fe6ae126a"

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
