SUMMARY = "A VBA p-code disassembler"
HOMEPAGE = "https://github.com/bontchev/pcodedmp"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI[sha256sum] = "025f8c809a126f45a082ffa820893e6a8d990d9d7ddb68694b5a9f0a6dbcd955"

inherit pypi setuptools3

PYPI_PACKAGE = "pcodedmp"

BBCLASSEXTEND = "native nativesdk"
