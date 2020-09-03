LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""


BBRANCH = "master"
SRC_URI = "git://github.com/HewlettPackard/gxp-bootblock.git;branch=${BBRANCH}"
SRCREV = "1d4b424934ab3a2f22cf1b9a459a38e45971509f"
S = "${WORKDIR}/git"

inherit deploy

do_deploy () {
  install -d ${DEPLOYDIR}

  install -m 644 gxp-bootblock-dl360poc.bin ${DEPLOYDIR}/gxp-bootblock.bin

}

addtask deploy before do_build after do_compile

