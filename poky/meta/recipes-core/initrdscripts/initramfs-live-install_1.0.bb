SUMMARY = "Live image install script for grub"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-install.sh"


S = "${WORKDIR}"

RDEPENDS:${PN} = "grub parted e2fsprogs-mke2fs util-linux-blkid ${VIRTUAL-RUNTIME_base-utils}"
RRECOMMENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils-syslog}"

do_install() {
        install -m 0755 ${WORKDIR}/init-install.sh ${D}/install.sh
}

# While this package maybe an allarch due to it being a 
# simple script, reality is that it is Host specific based
# on the COMPATIBLE_HOST below, which needs to take precedence
#inherit allarch
INHIBIT_DEFAULT_DEPS = "1"

FILES:${PN} = " /install.sh "

COMPATIBLE_HOST = "(i.86.*|x86_64.*|aarch64.*)-linux"
