SUMMARY = "Smack test scripts"
DESCRIPTION = "Smack scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
           file://notroot.py \
           file://smack_test_file_access.sh \
           file://test_privileged_change_self_label.sh \
           file://test_smack_onlycap.sh \
" 

S = "${WORKDIR}"

inherit features_check

REQUIRED_DISTRO_FEATURES = "smack"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 notroot.py ${D}${sbindir}
    install -m 0755 *.sh ${D}${sbindir}
}

RDEPENDS_${PN} = "smack python mmap-smack-test tcp-smack-test udp-smack-test"
