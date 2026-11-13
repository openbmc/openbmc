SUMMARY = "Provider of IANA time zone data"
HOMEPAGE = "https://github.com/python/tzdata"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=804220b73c90950be376e7ff7b2066bb \
                    file://licenses/LICENSE_APACHE;md5=86d3f3a95c324c9479bd8986968f4327 \
                    "

SRC_URI[sha256sum] = "85d2cd9c09f81f0bb29db27759fc83af4f1e620efb814f8c5bf8b4cc4feece6d"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN}-ptest += "\
    python3-attrs \
    python3-pytest-subtests \
"

do_install_ptest:append() {
	install ${S}/VERSION ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
