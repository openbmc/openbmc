SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=18a4795c19833413a7e2f1cb3cd3b143"

SRC_URI[sha256sum] = "795dafcc9c04ed0c1fb032c2aa73654d8e8c5023a7df64a53f39190ada629902"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output"

BBCLASSEXTEND = "native nativesdk"
