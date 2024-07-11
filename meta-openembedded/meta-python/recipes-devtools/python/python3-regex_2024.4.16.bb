SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7b5751ddd6b643203c31ff873051d069"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fa454d26f2e87ad661c4f0c5a5fe4cf6aab1e307d1b94f16ffdfcb089ba685c0"

RDEPENDS:${PN} += " \
	python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
