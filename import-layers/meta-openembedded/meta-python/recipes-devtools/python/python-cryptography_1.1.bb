SUMMARY = "Provides cryptographic recipes and primitives to Python developers"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"
DEPENDS += " python-cffi-native python-cffi python-enum34 python-six python-pyasn1"
SRCNAME = "cryptography"

SRC_URI = "file://run-ptest \
           file://build_fix_openssl_1.0.1g.patch \
           file://Remove_SSLv2_bindings.patch \
           file://Comment_lingering_SSLv2_symbol.patch"

SRC_URI[md5sum] = "dd06da41535184f48f2c8e8b74dd570f"
SRC_URI[sha256sum] = "059bc6428b1d0e2317f505698602642f1d8dda5b120ec573a59a430d8cb7a32d"

inherit pypi setuptools

RDEPENDS_${PN} = "\
                  python-pyasn1\
                  python-six\
                  python-cffi\
                  python-enum34\
                  python-setuptools\
                  python-pycparser\
                  python-subprocess\
                  python-threading\
                  python-numbers\
                  python-contextlib\
                  python-ipaddress\
                  python-pyasn1\
                  python-idna\
"

RDEPENDS_${PN}-ptest = "\
                       ${PN}\
                       python-pytest\
                       python-pretend\
                       python-iso8601\
                       python-cryptography-vectors\
"

inherit ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    install -d ${D}${PTEST_PATH}/tests/hazmat
    cp -rf ${S}/tests/hazmat/* ${D}${PTEST_PATH}/tests/hazmat/
}

FILES_${PN}-dbg += " \
    ${libdir}/python2.7/site-packages/${SRCNAME}/hazmat/bindings/.debug \
    "
