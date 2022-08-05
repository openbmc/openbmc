SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7b5751ddd6b643203c31ff873051d069"

inherit pypi setuptools3

SRC_URI[sha256sum] = "fa8a4bc81b15f49c57ede3fd636786c6619179661acf2430fcc387d75bf28d33"

RDEPENDS:${PN} += " \
	python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
