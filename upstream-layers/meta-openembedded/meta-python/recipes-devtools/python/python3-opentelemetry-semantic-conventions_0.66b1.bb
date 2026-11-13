SUMMARY = "OpenTelemetry Semantic Conventions"
DESCRIPTION = "Semantic conventions for OpenTelemetry, an open source \
observability framework for cloud native software."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "497ca63bf383723411e8eaf60c8779e9877633c936bb641080adab59d0eb6ec8"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
    python3-opentelemetry-api \
    python3-typing-extensions \
"
