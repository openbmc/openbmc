FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " \
        file://blacklist.json \
    "

do_install:append() {
     install -m 0444 ${UNPACKDIR}/blacklist.json -D -t ${D}${datadir}/entity-manager
}
