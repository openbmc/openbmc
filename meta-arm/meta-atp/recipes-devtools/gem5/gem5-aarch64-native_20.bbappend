require recipes-devtools/atp/atp-source_3.1.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "${ATP_SRC};destsuffix=git/atp;name=atp \
            file://start-gem5-atp.sh"
SRCREV_FORMAT = "gem5_atp"
SRCREV_atp = "${ATP_REV}"
LICENSE += "& ${ATP_LIC}"
LIC_FILES_CHKSUM += "file://atp/LICENSE;md5=${ATP_LIC_MD5}"

EXTRA_OESCONS += "EXTRAS=${S}/atp"

do_install:append() {
    # baremetal_atp.py machine configuration and sample stream.atp file
    install -m 644 ${B}/atp/gem5/baremetal_atp.py \
                   ${B}/atp/configs/stream.atp \
                   ${D}${datadir}/gem5/configs
}

do_deploy:append() {
    # start-gem5-atp.sh launch script
    install -m 755 ${WORKDIR}/start-gem5-atp.sh ${DEPLOYDIR}
}
