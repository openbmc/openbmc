SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[sha256sum] = "83d6b356e116ca119db8e7c6fc2983289d87b27b3fac238cfe5dca529d884562"

BBCLASSEXTEND = "native nativesdk"
