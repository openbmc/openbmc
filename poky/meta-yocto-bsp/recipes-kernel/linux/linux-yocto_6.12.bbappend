COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_MACHINE:beaglebone-yocto = "beaglebone-yocto"
COMPATIBLE_MACHINE:genericx86 = "genericx86"
COMPATIBLE_MACHINE:genericx86-64 = "genericx86-64"

KMACHINE:beaglebone-yocto ?= "beaglebone"
KMACHINE:genericx86 ?= "common-pc"
KMACHINE:genericx86-64 ?= "common-pc-64"

FILESEXTRAPATHS:prepend:genericarm64 := "${THISDIR}/files:"
SRC_URI:append:genericarm64 = " file://0001-Revert-serial-8250_omap-Drop-pm_runtime_irq_safe.patch"
