SUMMARY = "Library for serializing and deserializing Python Objects to and from JSON and XML."
HOMEPAGE = "https://github.com/madpah/serializable"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "9d5db56154a867a9b897c0163b33a793c804c80cee984116d02d49e4578fc103"

inherit pypi python_poetry_core

PYPI_PACKAGE = "py_serializable"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-defusedxml"
