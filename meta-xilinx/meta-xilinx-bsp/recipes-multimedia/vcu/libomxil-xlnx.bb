SUMMARY = "OpenMAX Integration layer for VCU"
DESCRIPTION = "OMX IL Libraries,test applications and headers for VCU"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=03a7aef7e6f6a76a59fd9b8ba450b493"

XILINX_VCU_VERSION = "1.0.0"
XILINX_RELEASE_VERSION = "2018.3"
PV = "${XILINX_VCU_VERSION}-xilinx-${XILINX_RELEASE_VERSION}+git${SRCPV}"

BRANCH ?= "master"
REPO   ?= "git://github.com/xilinx/vcu-omx-il.git;protocol=https"
SRCREV ?= "cf4b031ac88c7889e4f29ac7fc8ca7592bf12144"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

S  = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmp = "zynqmp"

PACKAGE_ARCH = "${SOC_FAMILY}"

DEPENDS = "libvcu-xlnx"
RDEPENDS_${PN} = "kernel-module-vcu libvcu-xlnx"

EXTERNAL_INCLUDE="${STAGING_INCDIR}/vcu-ctrl-sw/include"

EXTRA_OEMAKE = " \
    CC='${CC}' CXX='${CXX} ${CXXFLAGS}' \
    EXTERNAL_INCLUDE='${EXTERNAL_INCLUDE}' \
    "

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/vcu-omx-il

    install -m 0644 ${S}/omx_header/*.h ${D}${includedir}/vcu-omx-il

    install -Dm 0755 ${S}/bin/omx_decoder ${D}/${bindir}/omx_decoder
    install -Dm 0755 ${S}/bin/omx_encoder ${D}/${bindir}/omx_encoder

    oe_libinstall -C ${S}/bin/ -so libOMX.allegro.core ${D}/${libdir}/
    oe_libinstall -C ${S}/bin/ -so libOMX.allegro.video_decoder ${D}/${libdir}/
    oe_libinstall -C ${S}/bin/ -so libOMX.allegro.video_encoder ${D}/${libdir}/
}

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.

EXCLUDE_FROM_WORLD = "1"
