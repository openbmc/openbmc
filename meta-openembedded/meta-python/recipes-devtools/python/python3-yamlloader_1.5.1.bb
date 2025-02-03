SUMMARY = "Ordered YAML loader and dumper for PyYAML."
HOMEPAGE = "https://github.com/Phynix/yamlloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6831ef36faa29329bce2420c5356f97e"

SRC_URI[sha256sum] = "8dece19b050acb1c6a8ca14aa30793388f9be154f734b826541f9a1828d41cec"

inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN}:class-target += "\
    python3-pyyaml \
"
