SUMMARY = "Ordered YAML loader and dumper for PyYAML."
HOMEPAGE = "https://github.com/Phynix/yamlloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6831ef36faa29329bce2420c5356f97e"

SRC_URI[sha256sum] = "8a297c7a197683ba02e5e2b882ffd6c6180d01bdefb534b69cd3962df020bfe6"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-pyyaml \
"
