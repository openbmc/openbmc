SUMMARY = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=141643e11c48898150daa83802dbc65f"

SRC_URI[sha256sum] = "bf1dcf6450f873a13e952a29504887c89e6de7506209e5b1bcc3460135d4de19"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-toml-native"

RDEPENDS:${PN} += "python3-compression \
                   python3-math \
                   python3-more-itertools"

BBCLASSEXTEND = "native nativesdk"
