SUMMARY = "OpenTelemetry Python SDK"
DESCRIPTION = "Python SDK for OpenTelemetry, an open source \
observability framework for cloud native software."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "7eec4de13dd21ff3a1670dcb0e02079e692736f94d733dfdce5af461d884248d"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
    python3-opentelemetry-api \
    python3-opentelemetry-semantic-conventions \
    python3-typing-extensions \
"
