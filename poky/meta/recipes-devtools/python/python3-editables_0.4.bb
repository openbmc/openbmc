SUMMARY = "A Python library for creating editable wheels"
HOMEPAGE = "https://github.com/pfmoore/editables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=41bc1be47b7bb8240db3ef928c7cb0bf"

SRC_URI[sha256sum] = "dc322c42e7ccaf19600874035a4573898d88aadd07e177c239298135b75da772"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
        python3-io \
"

BBCLASSEXTEND = "native nativesdk"
