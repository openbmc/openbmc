SUMMARY = "A library to handle Apple Property List format whereas it's binary or XML"
HOMEPAGE = "https://github.com/libimobiledevice/libplist"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "libxml2 glib-2.0 swig python3"

inherit autotools pkgconfig python3native python3targetconfig ptest

SRCREV = "cf5897a71ea412ea2aeb1e2f6b5ea74d4fabfd8c"
SRC_URI = "git://github.com/libimobiledevice/libplist;protocol=https;branch=master;tag=${PV} \
           file://run-ptest \
           file://0001-test-fix-operator-error.patch \
           file://0001-ostep-invalid-types.test-Fix-ostep-invalid-types-tes.patch \
"

CVE_STATUS_GROUPS += "CVE_STATUS_LIBLIST"
CVE_STATUS_LIBLIST[status] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."
CVE_STATUS_LIBLIST = " \
    CVE-2017-5834 \
    CVE-2017-5835 \
    CVE-2017-5836 \
"

do_configure:prepend() {
    rm -f ${S}/m4/ax_python_devel.m4
}

do_install:append () {
    if [ -e ${D}${PYTHON_SITEPACKAGES_DIR}/plist/_plist.so ]; then
        chrpath -d ${D}${PYTHON_SITEPACKAGES_DIR}/plist/_plist.so
    fi
}

do_install_ptest(){
    # tests expect a particular directory structure for input and output
    install -d ${D}${PTEST_PATH}/input/test/data
    install -d ${D}${PTEST_PATH}/test/tools
    install -d ${D}${PTEST_PATH}/test/tools/.libs
    install -d ${D}${PTEST_PATH}/test/test/.libs
    install -d ${D}${PTEST_PATH}/test/test/data
    install -m 0755 ${B}/tools/.libs/plistutil ${D}${PTEST_PATH}/test/tools/.libs/
    install ${S}/test/data/* ${D}${PTEST_PATH}/input/test/data/
    install ${S}/test/*.test ${D}${PTEST_PATH}/test/
    install -m 0755 ${B}/test/.libs/plist* ${D}${PTEST_PATH}/test/test/.libs/
    install -m 0755 ${B}/test/.libs/integer_set_test ${D}${PTEST_PATH}/test/test/
    for t in $(find ${B}/test -type f -name 'plist*' \! -name '*.o'); do
        install -m 0755 $t ${D}${PTEST_PATH}/test/test/
    done
    for t in $(find ${B}/tools -type f -name 'plist*' \! -name '*.o'); do
        install -m 0755 $t ${D}${PTEST_PATH}/test/tools/
    done
    for f in test/plist_cmp test/plist_test test/plist_test++ test/plist_jtest \
        test/plist_btest test/plist_otest tools/plistutil; do
        sed -i 's@LD_LIBRARY_PATH="[^"]*:@LD_LIBRARY_PATH="@g' \
            ${D}${PTEST_PATH}/test/$f
    done
    sed -i '/notinst_deplibs=/d' ${D}${PTEST_PATH}/test/test/plist_test++
}

PACKAGES =+ "${PN}-utils \
             ${PN}++ \
             ${PN}-python"

FILES:${PN} = "${libdir}/libplist-2.0${SOLIBS}"
FILES:${PN}++ = "${libdir}/libplist++-2.0${SOLIBS}"
FILES:${PN}-utils = "${bindir}/*"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*"

RDEPENDS:${PN}-ptest += "bash diffutils"
