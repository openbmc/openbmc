FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"
OEM_TIP = "Kmt_TipFwL0_Skmt_TipFwL1.bin"
SRC_URI:append:scm-npcm845 = " file://${OEM_TIP}"
do_deploy:scm-npcm845 () {
    install -D -m 644 ${WORKDIR}/${OEM_TIP} ${DEPLOYDIR}/Kmt_TipFwL0_Skmt_TipFwL1.bin
}
