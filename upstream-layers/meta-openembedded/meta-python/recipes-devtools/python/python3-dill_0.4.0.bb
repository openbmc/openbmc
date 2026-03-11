SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed5ea77287d7d542949d6dd0bc288ac0"

SRC_URI[sha256sum] = "0633f1d2df477324f53a895b02c901fb961bdbf65a17122586ea7019292cbcf0"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-multiprocessing \
    python3-logging \
    python3-profile \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
