SUMMARY = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "bb7dd36ff9406ec89607b893cfc5e1ffa7ead76fd5c48df77eafc9b4d4196047"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "\
        python3-dtc \
        python3-jsonschema \
        python3-rfc3987 \
        python3-ruamel-yaml \
        "

BBCLASSEXTEND = "native nativesdk"
