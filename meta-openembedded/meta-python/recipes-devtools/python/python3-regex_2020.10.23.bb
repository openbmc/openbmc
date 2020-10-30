SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[md5sum] = "13fb62a1b276623e38931f7217f080d8"
SRC_URI[sha256sum] = "2278453c6a76280b38855a263198961938108ea2333ee145c5168c36b8e2b376"

BBCLASSEXTEND = "native nativesdk"
