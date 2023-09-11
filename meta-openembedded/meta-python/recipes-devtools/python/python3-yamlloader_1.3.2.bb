SUMMARY = "Ordered YAML loader and dumper for PyYAML."
HOMEPAGE = "https://github.com/Phynix/yamlloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6831ef36faa29329bce2420c5356f97e"

SRC_URI[sha256sum] = "7dbd98421d8090c521655f1b06ca030067f29df5253a8878126bce3a90f56817"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-pyyaml \
"
