LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""


BBRANCH = "master"
SRC_URI = "git://github.com/HewlettPackard/gxp-bootblock.git;branch=${BBRANCH};protocol=https"
SRCREV = "bab416f8ca8c8465d308cfeb7f8d5abc21ba343b"
S = "${WORKDIR}/git"

inherit deploy

do_deploy () {
  install -d ${DEPLOYDIR}
  install -m 644 gxp-bootblock.bin ${DEPLOYDIR}/gxp-bootblock.bin

}

addtask deploy before do_build after do_compile

