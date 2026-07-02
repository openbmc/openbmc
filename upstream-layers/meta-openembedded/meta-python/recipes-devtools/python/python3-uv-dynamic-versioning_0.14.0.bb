SUMMARY = "Dynamic versioning based on VCS tags for uv/hatch project"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=14d953809f6381e54162e13c2c846fbc"

SRC_URI[sha256sum] = "574fbc07e87ace45c01d55967ad3b864871257b98ff5b8ac87c261227ac8db5b"

PYPI_PACKAGE = "uv_dynamic_versioning"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
