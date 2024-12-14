SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a41509b57cc475ed93f8cb1dbbfaeec1"

SRC_URI[sha256sum] = "81aa267dddf68cbfe8029c42ca9ec6a4ab3b22371d1c450abc54422577b4512c"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-multiprocessing \
    python3-logging \
    python3-profile \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
