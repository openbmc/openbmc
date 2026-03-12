SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a60f86720d45856324c45945cfed6b3"

SRC_URI[sha256sum] = "423092df4182177d4d8ba8290c8a5b640c66ab35ec7da59ccfa00f6fa3eea5fa"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-multiprocessing \
    python3-logging \
    python3-profile \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
