SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8394e266005f2d8c6f0bc6780001f7afa3ef81a7a2111fa35058ded6fce79e4d"

RDEPENDS:${PN} += " \
	python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
