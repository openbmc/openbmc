DESCRIPTION = "A python library for manipulating kickstart files"
HOMEPAGE = "https://fedoraproject.org/wiki/pykickstart"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=81bcece21748c91ba9992349a91ec11d"

inherit python_setuptools_build_meta ptest

RDEPENDS:${PN} = "python3 \
                  python3-requests \
                  python3-six \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests
    for file in `grep -Rl unittest.main ${D}${PTEST_PATH}/tests/`; do
        dirname=`dirname $file`
        basename=`basename $file`
        [ $basename != "__init__.py" ] && mv $file ${dirname}/test_${basename}
    done
}

SRC_URI = "git://github.com/rhinstaller/pykickstart.git;protocol=https;branch=master;tag=r${PV} \
           file://0001-support-authentication-for-kickstart.patch \
           file://0002-pykickstart-parser.py-add-lock-for-readKickstart-and.patch \
           file://0003-comment-out-sections-shutdown-and-environment-in-gen.patch \
           file://0004-load.py-retry-to-invoke-request-with-timeout.patch \
           file://run-ptest \
           "
SRCREV = "6e0d1238cb4696a9040072a5a28a706e5775c552"

UPSTREAM_CHECK_GITTAGREGEX = "r(?P<pver>\d+(\.\d+)+(-\d+)*)"

