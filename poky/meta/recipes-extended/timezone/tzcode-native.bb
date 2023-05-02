require timezone.inc

SUMMARY = "tzcode, timezone zoneinfo utils -- zic, zdump, tzselect"

SRC_URI += "file://0001-Fix-C23-related-conformance-bug.patch"

inherit native

EXTRA_OEMAKE += "cc='${CC}'"

do_install () {
        install -d ${D}${bindir}/
        install -m 755 zic ${D}${bindir}/
        install -m 755 zdump ${D}${bindir}/
        install -m 755 tzselect ${D}${bindir}/
}
