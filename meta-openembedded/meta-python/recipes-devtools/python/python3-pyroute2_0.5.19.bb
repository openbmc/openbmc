SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPL-2.0-only & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=34281e312165f843a2b7d1f114fe65ce"

SRC_URI[sha256sum] = "45460d12ed2a5caf272a357a3360b36d1e346f17afe1425b66fc21d70f462b29"

inherit setuptools3 pypi ptest

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-distutils \
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

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-fcntl \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
