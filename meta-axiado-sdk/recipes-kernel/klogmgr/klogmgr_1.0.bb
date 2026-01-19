SUMMARY = "AX-LOGGER Kernel Module"
DESCRIPTION = "A53 AX Logger kernel module"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf540fc7d35b5777e36051280b3a911c"

inherit module systemd

SRCBRANCH ??= "${LATEST_RELEASE_VERSION}"
SRCREV = "7ac56c9806a398837539bd135e53fa3c8f1f3940"
SRC_URI = "git://git@sourcevault.axiadord:7999/linux/klogmgr.git;protocol=ssh;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
        install -m 0644 ${S}/ax-logger.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
}

FILES_${PN} = "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/ax-logger.ko"

