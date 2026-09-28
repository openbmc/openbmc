SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=18a4795c19833413a7e2f1cb3cd3b143"

SRC_URI += "file://CVE-2026-45409_p1.patch \
           file://CVE-2026-45409_p2.patch \
           file://CVE-2026-45409_p3.patch \
           "
SRC_URI[sha256sum] = "795dafcc9c04ed0c1fb032c2aa73654d8e8c5023a7df64a53f39190ada629902"

inherit pypi python_flit_core ptest-python-pytest

RDEPENDS:${PN} += "python3-codecs"
RDEPENDS:${PN}-ptest += "python3-unittest-automake-output"

CVE_PRODUCT = "kjd:idna kjd:internationalized_domain_names_in_applications"

BBCLASSEXTEND = "native nativesdk"
