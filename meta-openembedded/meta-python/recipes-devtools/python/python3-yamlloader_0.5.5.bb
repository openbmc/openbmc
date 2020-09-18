SUMMARY = "Ordered YAML loader and dumper for PyYAML."
HOMEPAGE = "https://github.com/Phynix/yamlloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6831ef36faa29329bce2420c5356f97e"

SRC_URI[md5sum] = "2e0750ace81235f750c072833d79c4c3"
SRC_URI[sha256sum] = "dcab5f16b39bb03d10dda4cd4f30c943675ec4c7771807fc67e7f1bb319bf4c8"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-pyyaml \
"
