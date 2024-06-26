FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://BootBlockAndHeader_GBS.xml"
SRC_URI:append = " file://UbootHeader_GBS.xml"

# Prepare the Bootblock XMLs.
do_gbs_prepare_xmls() {
    cp ${UNPACKDIR}/BootBlockAndHeader_GBS.xml ${S}/ImageGeneration/references/
    cp ${UNPACKDIR}/UbootHeader_GBS.xml ${S}/ImageGeneration/references/
}

addtask do_gbs_prepare_xmls after do_patch before do_install
