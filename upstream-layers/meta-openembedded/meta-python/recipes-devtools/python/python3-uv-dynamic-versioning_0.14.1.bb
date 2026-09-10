SUMMARY = "Dynamic versioning based on VCS tags for uv/hatch project"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=14d953809f6381e54162e13c2c846fbc"

SRC_URI[sha256sum] = "8642db686ce5c50417035e7a257ac73b7e5c3a7a32c33e45bd7e36ba22eeb648"

PYPI_PACKAGE = "uv_dynamic_versioning"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
