require atp-source_3.1.inc
inherit native

SUMMARY = "AMBA ATP Engine gem5 models"

S = "${WORKDIR}/git"
SRC_URI = "${ATP_SRC} file://start-gem5-atp.sh"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/gem5/configs ${D}${datadir}/atp ${D}${bindir}

    # baremetal_atp.py machine configuration and sample stream.atp file
    install ${S}/gem5/baremetal_atp.py ${S}/configs/stream.atp ${D}${datadir}/gem5/configs
    # ATP Engine sources for gem5 to use
    install ${S}/SConscript ${S}/*.hh ${S}/*.cc ${D}${datadir}/atp
    cp -RL ${S}/gem5 ${S}/proto ${D}${datadir}/atp

    install ${WORKDIR}/start-gem5-atp.sh ${D}${bindir}
}

addtask addto_recipe_sysroot after do_populate_sysroot before do_build
