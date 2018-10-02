DESCRIPTION = "libGLES for ZynqMP with Mali 400"

LICENSE = "Proprietary"
LICENSE_FLAGS = "xilinx"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

inherit distro_features_check
inherit xilinx-fetch-restricted

ANY_OF_DISTRO_FEATURES = "fbdev x11"

PROVIDES += "virtual/libgles1 virtual/libgles2 virtual/egl"

FILESEXTRAPATHS_append := " \
                ${THISDIR}/files: \
                ${THISDIR}/r8p0-00rel0: "


# Fetch the MALI 400 binaries from here
# https://www.xilinx.com/member/forms/download/mali-driver-license.html?filename=mali-400-userspace.tar

PV = "r8p0-01rel0"
SRC_URI = " \
    https://www.xilinx.com/member/forms/download/mali-driver-license.html?filename=mali-400-userspace.tar;downloadfilename=mali-400-userspace.tar \
    file://egl.pc \
    file://glesv1_cm.pc \
    file://glesv1.pc \
    file://glesv2.pc \
    "

SRC_URI[md5sum] = "e75b147c8b4ee96616e24572cdc9c21f"
SRC_URI[sha256sum] = "7b179ec2df54ee05a886cca1535c0bdc6cba77a646e22742adedc79bfc2b3017"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmpeg = "zynqmpeg"
COMPATIBLE_MACHINE_zynqmpev = "zynqmpev"

PACKAGE_ARCH = "${SOC_FAMILY}${SOC_VARIANT}"


S = "${WORKDIR}/mali-400"

X11RDEPENDS = "libxdamage libxext libx11 libdrm libxfixes"
X11DEPENDS = "libxdamage libxext virtual/libx11 libdrm libxfixes"

RDEPENDS_${PN} = " \
	kernel-module-mali \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11RDEPENDS}', '', d)} \
	"

DEPENDS = "\
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)} \
	"

EGL_TYPE = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11',  \
               bb.utils.contains('DISTRO_FEATURES', 'fbdev',  'fbdev', '', d), d)}"

do_compile() {
	# Extract the MALI binaries into workdir
	tar -xf ${WORKDIR}/mali/rel-v2018.1/r8p0-01rel0.tar -C ${S}
}

do_install() {
    #Identify the ARCH type
    ${TARGET_PREFIX}gcc --version > ARCH_PLATFORM
    if grep -q aarch64 "ARCH_PLATFORM"; then
	ARCH_PLATFORM_DIR=aarch64-linux-gnu
    else
	ARCH_PLATFORM_DIR=arm-linux-gnueabihf
    fi

    # install headers
    install -d -m 0655 ${D}${includedir}/EGL
    install -m 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/${EGL_TYPE}/usr/include/EGL/*.h ${D}${includedir}/EGL/
    install -d -m 0655 ${D}${includedir}/GLES
    install -m 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/${EGL_TYPE}/usr/include/GLES/*.h ${D}${includedir}/GLES/
    install -d -m 0655 ${D}${includedir}/GLES2
    install -m 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/${EGL_TYPE}/usr/include/GLES2/*.h ${D}${includedir}/GLES2/
    install -d -m 0655 ${D}${includedir}/KHR
    install -m 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/${EGL_TYPE}/usr/include/KHR/*.h ${D}${includedir}/KHR/

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/egl.pc ${D}${libdir}/pkgconfig/egl.pc
    install -m 0644 ${WORKDIR}/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
    install -m 0644 ${WORKDIR}/glesv1.pc ${D}${libdir}/pkgconfig/glesv1.pc
    install -m 0644 ${WORKDIR}/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc

    install -d ${D}${libdir}
    cp -a --no-preserve=ownership ${S}/${PV}/${ARCH_PLATFORM_DIR}/${EGL_TYPE}/usr/lib/*.so* ${D}${libdir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'false', 'true', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if (1)/' ${D}${includedir}/EGL/eglplatform.h
    fi
}


# Inhibit warnings about files being stripped
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

RREPLACES_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2"
RPROVIDES_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2"
RCONFLICTS_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2"

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.
EXCLUDE_FROM_WORLD = "1"
