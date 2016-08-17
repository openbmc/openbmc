SUMMARY = "Helper script for OE's llvm support"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420 \
"

SRC_URI = "file://llvm-config"

ALLOW_EMPTY_${PN} = "1"
SYSROOT_PREPROCESS_FUNCS_append_class-target = " llvm_common_sysroot_preprocess"

llvm_common_sysroot_preprocess() {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 0755 ${WORKDIR}/llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
}

do_install_class-native() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/llvm-config ${D}${bindir}
}

BBCLASSEXTEND = "native"
