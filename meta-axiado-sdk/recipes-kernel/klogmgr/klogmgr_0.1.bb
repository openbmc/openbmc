SUMMARY = "AX-LOGGER Kernel Module"
DESCRIPTION = "A53 AX Logger kernel module"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
PV = "0.1"
LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://ax-logger.ko"

inherit module

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
        install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
        install -m 0644 ${UNPACKDIR}/ax-logger.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
}

FILES_${PN} = "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/ax-logger.ko"
