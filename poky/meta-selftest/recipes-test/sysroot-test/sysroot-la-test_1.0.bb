SUMMARY = "Produce a broken la file"
LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

EXCLUDE_FROM_WORLD = "1"

# remove-libtool.bbclass is inherited by default and removes all
# .la files which for this test we specifically do not want.
REMOVE_LIBTOOL_LA = "0"

do_install() {
    install -d ${D}${libdir}/test/
    echo '${WORKDIR}' > ${D}${libdir}/test/la-test.la
}

BBCLASSEXTEND += "native"
