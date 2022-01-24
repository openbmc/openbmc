SUMMARY = "A list of registered asynchronous callbacks"
HOMEPAGE = "https://github.com/aio-libs/aiosignal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf056e8e7a0a5477451af18b7b5aa98c"

SRC_URI[sha256sum] = "78ed67db6c7b7ced4f98e495e572106d5c432a93e1ddd1bf475e1dc05f5b7df2"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "\
	${PYTHON_PN}-frozenlist \
"
