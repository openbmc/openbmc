DESCRIPTION = "Closed source binary files to help boot all raspberry pi devices."
LICENSE = "Broadcom-RPi"

LIC_FILES_CHKSUM = "file://LICENCE.broadcom;md5=c403841ff2837657b2ed8e5bb474ac8d"

inherit deploy nopackages

include recipes-bsp/common/raspberrypi-firmware.inc

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "rpi-config"

COMPATIBLE_MACHINE = "^rpi$"

S = "${RPIFW_S}/boot"

PR = "r3"

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}

    for i in ${S}/*.elf ; do
        cp $i ${DEPLOYDIR}/${PN}
    done
    for i in ${S}/*.dat ; do
        cp $i ${DEPLOYDIR}/${PN}
    done
    for i in ${S}/*.bin ; do
        cp $i ${DEPLOYDIR}/${PN}
    done

    # Add stamp in deploy directory
    touch ${DEPLOYDIR}/${PN}/${PN}-${PV}.stamp
}

do_deploy[depends] += "rpi-config:do_deploy"

addtask deploy before do_build after do_install
do_deploy[dirs] += "${DEPLOYDIR}/${PN}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

