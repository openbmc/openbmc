SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=204c0612e40a4dd46012a78d02c80fb1"

SRC_URI[sha256sum] = "12f65c9b470abda6dc35cf8e63cc574b1c52b11df2c86030af0ac09b01b13ea9"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output"

BBCLASSEXTEND = "native nativesdk"
