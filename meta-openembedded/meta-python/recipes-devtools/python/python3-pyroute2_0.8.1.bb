SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPL-2.0-or-later | Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dccbff78d7d79ae7e53953d43445c6e6 \
                    file://LICENSE.GPL-2.0-or-later;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache-2.0;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[sha256sum] = "b91f4a1f7abb9824637b1fe67e6e4a0a071d98d4a1a1b47ef792304ff3adad11"

inherit python_setuptools_build_meta pypi ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-io \
    python3-json \
    python3-fcntl \
    python3-logging \
    python3-multiprocessing \
    python3-pickle \
    python3-pkgutil \
    python3-pprint \
    python3-shell \
    python3-unixadmin \
"

RDEPENDS:${PN}-ptest += " \
    python3-fcntl \
    python3-html \
    python3-netclient \
    python3-sqlite3 \
"

do_install_ptest:append () {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    sed -e  "s|'test_unit|'tests/test_unit|g" -i  ${D}${PTEST_PATH}/tests/test_unit/test_nlmsg/test_marshal.py \
    ${D}${PTEST_PATH}/tests/test_unit/test_iproute_match/test_match.py
}
