SUMMARY = "A list of registered asynchronous callbacks"
HOMEPAGE = "https://github.com/aio-libs/aiosignal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "a8c255c66fafb1e499c9351d0bf32ff2d8a0321595ebac3b93713656d2436f54"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
	python3-frozenlist \
"
