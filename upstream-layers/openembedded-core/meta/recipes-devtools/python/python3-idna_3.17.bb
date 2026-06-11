SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=9a6c29079fc90c29d80332f44d2625f2"

SRC_URI[sha256sum] = "5eb0cb53bc467c12eadcf6de83163ad8527cec9416f44b9b61b19caedad2b87f"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output"

BBCLASSEXTEND = "native nativesdk"
