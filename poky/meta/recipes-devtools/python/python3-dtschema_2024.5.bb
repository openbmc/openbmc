SUMMARY = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "ea259a9ba4d1e1eff44ac8d362950361e46750bdeaf30a437a7ed33159c35fd8"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "\
        python3-dtc \
        python3-jsonschema \
        python3-rfc3987 \
        python3-ruamel-yaml \
        "

BBCLASSEXTEND = "native nativesdk"
