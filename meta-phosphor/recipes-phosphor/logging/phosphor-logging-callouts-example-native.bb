SUMMARY = "Generated callout information for phosphor-logging"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-logging

SRC_URI += "file://callouts.yaml"

PROVIDES += "virtual/phosphor-logging-callouts"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${callouts_datadir}
        install -d ${DEST}
        install callouts.yaml ${DEST}
}
