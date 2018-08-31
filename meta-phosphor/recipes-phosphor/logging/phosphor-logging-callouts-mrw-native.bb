SUMMARY = "Generated callout information for phosphor-logging"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-logging
inherit mrw-xml

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-logging-callouts"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${callouts_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_callouts.pl \
            -m ${mrw_datadir}/${MRW_XML} \
            -o ${DEST}/callouts.yaml
}
