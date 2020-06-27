SUMMARY = "Xilinx's hardware device trees required for QEMU"
HOMEPAGE = "https://github.com/xilinx/qemu-devicetrees/"
LICENSE = "BSD"
DEPENDS += "dtc-native"

inherit deploy

LIC_FILES_CHKSUM = "file://Makefile;beginline=1;endline=27;md5=7348b6cbcae69912cb1dee68d6c68d99"

PV = "xilinx-${XILINX_RELEASE_VERSION}+git${SRCPV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " file://0001-Makefile-Use-python3-instead-of-python.patch"

BRANCH ?= ""
REPO ?= "git://github.com/Xilinx/qemu-devicetrees.git;protocol=https"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

S = "${WORKDIR}/git"

# Don't need to do anything
do_install() {
	:
}

do_deploy() {
	# single-arch dtbs
	for DTS_FILE in ${S}/LATEST/SINGLE_ARCH/*.dtb; do
		install -Dm 0644 $DTS_FILE ${DEPLOYDIR}/qemu-hw-devicetrees/$(basename $DTS_FILE .dtb).dtb
	done

	# multi-arch dtbs
	for DTS_FILE in ${S}/LATEST/MULTI_ARCH/*.dtb; do
		install -Dm 0644 $DTS_FILE ${DEPLOYDIR}/qemu-hw-devicetrees/multiarch/$(basename $DTS_FILE .dtb).dtb
	done
}

addtask deploy after do_install
