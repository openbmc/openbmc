SUMMARY = "Produce a broken pc file"
LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"

do_install() {
    install -d ${D}${libdir}/test/
    echo '${WORKDIR}' > ${D}${libdir}/test/test.pc
}

BBCLASSEXTEND += "native"
