SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode-TOU"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=239668a7c6066d9e0c5382e9c8c6c0e1"

SRC_URI[sha256sum] = "814f528e8dead7d329833b91c5faa87d60bf71824cd12a7530b5526063d02cb4"

inherit pypi python_flit_core

RDEPENDS:${PN}:class-target = "\
    ${PYTHON_PN}-codecs \
"

BBCLASSEXTEND = "native nativesdk"
