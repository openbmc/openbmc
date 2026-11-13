SUMMARY = "OpenTelemetry Python API"
DESCRIPTION = "Python API for OpenTelemetry, an open source \
observability framework for cloud native software."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "d7b0008e21e5fd61cbe7a7f34ca3b6905a78ab677d9bc395e299359c75cda6b4"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-typing-extensions"
