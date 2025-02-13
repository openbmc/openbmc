SUMMARY = "Provider of IANA time zone data"
HOMEPAGE = "https://github.com/python/tzdata"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fca9fd5c15a28eb874ba38577a585d48 \
                    file://licenses/LICENSE_APACHE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "24894909e88cdb28bd1636c6887801df64cb485bd593f2fd83ef29075a81d694"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += "\
    python3-attrs \
    python3-pytest-subtests \
"

do_install_ptest:append() {
	install ${S}/VERSION ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
