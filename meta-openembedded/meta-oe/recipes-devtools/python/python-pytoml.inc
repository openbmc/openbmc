SUMMARY = "A TOML-0.4.0 parser/writer for Python"
HOMEPAGE = "https://pypi.python.org/pypi/pytoml/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfff423699bdaef24331933ac4f56078"

SRC_URI[md5sum] = "a11d8a4859b53f6ebe2f782314428911"
SRC_URI[sha256sum] = "ca2d0cb127c938b8b76a9a0d0f855cf930c1d50cc3a0af6d3595b566519a1013"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
