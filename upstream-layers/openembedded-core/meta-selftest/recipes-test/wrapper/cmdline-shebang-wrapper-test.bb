SUMMARY = "Check that create_cmdline_shebang works"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI += "file://test.awk"

EXCLUDE_FROM_WORLD = "1"
do_install() {
    install -d ${D}${bindir}
    # was not able to make ownership preservation check
    install -m 0400 ${UNPACKDIR}/test.awk ${D}${bindir}/test

    perm_old="$(stat --format='%a' ${D}${bindir}/test)"
    sed -i -e 's|@AWK_BIN@|${bindir}/awk|g' ${D}${bindir}/test
    create_cmdline_shebang_wrapper ${D}${bindir}/test
    if [ $(${D}${bindir}/test) != "Don't Panic!" ]; then
        bbfatal "Wrapper is broken"
    else
        bbnote "Wrapper is good"
    fi

    perm_new="$(stat --format='%a' ${D}${bindir}/test.real)"

    if [ "$perm_new" != "$perm_old" ]; then
        bbfatal "Wrapper permissions for ${D}${bindir}/test.real not preserved. Found $perm_new but expected $perm_old"
    fi
}

BBCLASSEXTEND = "native"
