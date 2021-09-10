SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=f0a3e4a2554ebb89c046c93d45d8e4bc"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e9700c52749cb3e90c98efd72b730c97b7e4962992fca5fbcaf1363be8e3b849"

RDEPENDS:${PN} += " \
	python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
