SUMMARY = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=141643e11c48898150daa83802dbc65f"

SRC_URI[sha256sum] = "2884ed22e7d8961de1c9a05142eb69a247f120291bc0206a00a7642f09b5b715"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-toml-native"

RDEPENDS:${PN} += "python3-compression \
                   python3-math \
                   python3-more-itertools"

BBCLASSEXTEND = "native nativesdk"
