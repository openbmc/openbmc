SUMMARY = "A Python library for creating editable wheels"
HOMEPAGE = "https://github.com/pfmoore/editables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=41bc1be47b7bb8240db3ef928c7cb0bf"

SRC_URI[sha256sum] = "309627d9b5c4adc0e668d8c6fa7bac1ba7c8c5d415c2d27f60f081f8e80d1de2"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
        python3-io \
"

BBCLASSEXTEND = "native nativesdk"
