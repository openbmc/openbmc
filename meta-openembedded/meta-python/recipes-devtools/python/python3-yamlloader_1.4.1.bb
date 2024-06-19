SUMMARY = "Ordered YAML loader and dumper for PyYAML."
HOMEPAGE = "https://github.com/Phynix/yamlloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6831ef36faa29329bce2420c5356f97e"

SRC_URI[sha256sum] = "b6fe40ecf5af596d840e920670ed3475f9813492bf6e55b24f2ad450c212bab5"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:class-target += "\
    python3-pyyaml \
"
