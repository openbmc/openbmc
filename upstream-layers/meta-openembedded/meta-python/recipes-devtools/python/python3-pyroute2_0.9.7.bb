SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "Apache-2.0 OR GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dccbff78d7d79ae7e53953d43445c6e6 \
                    file://LICENSE.GPL-2.0-or-later;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache-2.0;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[sha256sum] = "a678b098478c46ec51d6bf9a03c3cd2ac0172dffa590e9e8c1eeab633d0c17c6"

inherit python_setuptools_build_meta pypi ptest-python-pytest

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-fcntl \
    python3-io \
    python3-json \
    python3-logging \
    python3-multiprocessing \
    python3-pickle \
    python3-pkgutil \
    python3-pprint \
    python3-resource \
    python3-shell \
    python3-sqlite3 \
    python3-unittest \
    python3-unixadmin \
"

RDEPENDS:${PN}-ptest += " \
    python3-fcntl \
    python3-html \
    python3-netclient \
"

do_install_ptest:append () {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    # Remove test_frozen.py because it is missing some components
    # and is not present in the project's upstream GitHub repository.
    rm ${D}${PTEST_PATH}/tests/test_unit/test_frozen.py
    # Fix paths
    sed -e  "s|'test_unit|'tests/test_unit|g" -i  ${D}${PTEST_PATH}/tests/test_unit/test_nlmsg/test_marshal.py \
    ${D}${PTEST_PATH}/tests/test_unit/test_iproute_match/test_match.py \
    ${D}${PTEST_PATH}/tests/test_unit/test_nlmsg/test_nl80211_station.py
}
