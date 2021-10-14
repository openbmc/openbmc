SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[sha256sum] = "f585cbbeecb35f35609edccb95efd95a3e35824cd7752b586503f7e6087303f1"

RDEPENDS:${PN} += " \
	python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
