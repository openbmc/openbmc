SUMMARY = "JTAG utility to interface w/ a S3C2410 device"
SECTION = "devel"
AUTHOR = "Harald Welte <laforge@openmoko.org>"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://parport.c;endline=19;md5=b5681091b0fd8c5f7068835c441bf0c8"
SRCREV = "4268"
PV = "0.1+svnr${SRCPV}"
PR = "r1"

SRC_URI = "svn://svn.openmoko.org/trunk/src/host/;module=sjf2410-linux;protocol=http"
S = "${WORKDIR}/sjf2410-linux"

inherit native deploy
do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_TOOLS}"

CFLAGS += "-DLINUX_PPDEV"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 sjf2410 ${D}/${bindir}
}

do_deploy() {
    install -d ${DEPLOY_DIR_TOOLS}
    install -m 0755 sjf2410 ${DEPLOY_DIR_TOOLS}/sjf2410-${PV}
}

addtask deploy before do_build after do_install
