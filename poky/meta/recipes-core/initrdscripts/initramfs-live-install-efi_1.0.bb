SUMMARY = "Live image install script for grub-efi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-install-efi.sh"

PR = "r1"

RDEPENDS_${PN} = "parted e2fsprogs-mke2fs dosfstools util-linux-blkid ${VIRTUAL-RUNTIME_base-utils}"
RRECOMMENDS_${PN} = "${VIRTUAL-RUNTIME_base-utils-syslog}"

S = "${WORKDIR}"

do_install() {
        install -m 0755 ${WORKDIR}/init-install-efi.sh ${D}/install-efi.sh
}

# While this package maybe an allarch due to it being a
# simple script, reality is that it is Host specific based
# on the COMPATIBLE_HOST below, which needs to take precedence
#inherit allarch
INHIBIT_DEFAULT_DEPS = "1"

FILES_${PN} = " /install-efi.sh "

COMPATIBLE_HOST = "(i.86.*|x86_64.*|aarch64.*)-linux"
