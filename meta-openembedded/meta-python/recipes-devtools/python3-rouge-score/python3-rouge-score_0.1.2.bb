SUMMARY = "Pure python implementation of ROUGE-1.5.5."
DESCRIPTION = "This is a native python implementation of ROUGE, designed to \
               replicate results from the original perl package."
HOMEPAGE = "https://github.com/google-research/google-research/tree/master/rouge"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=93;md5=e8937c2bcd7cf57f2d1bd5f1bf7efd23"

RDEPENDS:${PN} = "\
    python3-absl \
    python3-nltk \
    python3-numpy \
    python3-six (>=1.14) \
"

inherit setuptools3 pypi

PYPI_PACKAGE = "rouge_score"

SRC_URI[sha256sum] = "c7d4da2683e68c9abf0135ef915d63a46643666f848e558a1b9f7ead17ff0f04"
