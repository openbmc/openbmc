FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OECONF += "--disable-mib-loading --disable-agent --disable-applications \
--disable-manuals --disable-scripts --disable-mibs  --disable-embedded-perl \
--disable-perl-cc-checks --disable-libtool-lock --disable-debugging \
--disable-snmpv1 --disable-des --disable-privacy --disable-md5 \
--disable-snmptrapd-subagent --disable-set-support --disable-deprecated"

do_install_append() {
    rm -f ${D}${libdir}/libnetsnmpmibs.so.*
    rm -f ${D}${libdir}/libnetsnmphelpers.so.*
    rm -f ${D}${libdir}/libnetsnmpagent.so.*
}
