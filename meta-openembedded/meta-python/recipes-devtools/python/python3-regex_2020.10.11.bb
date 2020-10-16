SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[md5sum] = "642c259122a3b578cec66d8bba5665e4"
SRC_URI[sha256sum] = "463e770c48da76a8da82b8d4a48a541f314e0df91cbb6d873a341dbe578efafd"

BBCLASSEXTEND = "native nativesdk"
