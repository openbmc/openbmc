SUMMARY = "Extremely basic live image init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-boot.sh"

PR = "r2"

S = "${WORKDIR}"

do_install() {
        install -m 0755 ${WORKDIR}/init-boot.sh ${D}/init
}

inherit allarch

FILES_${PN} += " /init "
