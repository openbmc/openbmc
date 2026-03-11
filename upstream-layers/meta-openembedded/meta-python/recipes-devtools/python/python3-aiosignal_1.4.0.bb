SUMMARY = "A list of registered asynchronous callbacks"
HOMEPAGE = "https://github.com/aio-libs/aiosignal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "f47eecd9468083c2029cc99945502cb7708b082c232f9aca65da147157b251c7"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
	python3-frozenlist \
"
