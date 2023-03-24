FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " \
        file://blacklist.json \
    "

do_install:append() {
     install -m 0444 ${WORKDIR}/blacklist.json -D -t ${D}${datadir}/entity-manager
}
