SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[md5sum] = "8150da7f676e73751b4e3c7b1ba79a34"
SRC_URI[sha256sum] = "dd3e6547ecf842a29cf25123fbf8d2461c53c8d37aa20d87ecee130c89b7079b"

BBCLASSEXTEND = "native nativesdk"
