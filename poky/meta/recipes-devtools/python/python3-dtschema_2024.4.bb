SUMMARY = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "18dd1d34b4a5e451291e5444e9ceb4a6febc605871cdaef22673b6f80aa4a131"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "\
        python3-dtc \
        python3-jsonschema \
        python3-rfc3987 \
        python3-ruamel-yaml \
        "

BBCLASSEXTEND = "native nativesdk"
