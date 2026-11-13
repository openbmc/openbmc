SUMMARY = "Tool for applying a patch file"
DESCRIPTION = "patch takes a patch file containing a difference listing \
produced by the diff program and applies those differences to one or more \
original files, producing patched versions."
SECTION = "utils"
HOMEPAGE = "http://savannah.gnu.org/projects/patch/"

SRC_URI = "${GNU_MIRROR}/patch/patch-${PV}.tar.xz \
 	   file://run-ptest \
   	   "

S = "${UNPACKDIR}/patch-${PV}"

inherit autotools update-alternatives ptest

ALTERNATIVE:${PN} = "patch"
ALTERNATIVE_PRIORITY = "100"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0644 ${S}/tests/test-lib.sh ${D}${PTEST_PATH}/tests/
    for t in $(cd ${S}/tests && ls); do
        if [ -f ${S}/tests/$t ] && [ "$t" != "test-lib.sh" ] && \
           [ "$t" != "Makefile.am" ] && [ "$t" != "Makefile.in" ]; then
            install -m 0755 ${S}/tests/$t ${D}${PTEST_PATH}/tests/$t
        fi
    done
}

RDEPENDS:${PN}-ptest += "diffutils sed coreutils"
