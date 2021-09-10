SUMMARY = "Live image init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "virtual/kernel"
RDEPENDS:${PN} = "busybox-mdev"
SRC_URI = "file://init-live.sh"

PR = "r12"

S = "${WORKDIR}"

do_install() {
        install -m 0755 ${WORKDIR}/init-live.sh ${D}/init
        install -d ${D}/dev
        mknod -m 622 ${D}/dev/console c 5 1
}

FILES:${PN} += " /init /dev "

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"
