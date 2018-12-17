DESCRIPTION = "libGLES for ZynqMP with Mali 400"

LICENSE = "Proprietary"
LICENSE_FLAGS = "xilinx"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28"

inherit distro_features_check
inherit xilinx-fetch-restricted

ANY_OF_DISTRO_FEATURES = "fbdev x11"

PROVIDES += "virtual/libgles1 virtual/libgles2 virtual/egl virtual/libgbm"

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
    file://wayland-egl.pc \
    file://gbm.pc \
    "

SRC_URI[md5sum] = "4fd3456564ef8c818e21432221c9e1b7"
SRC_URI[sha256sum] = "26d473ae77c36104a215710beca55a22a712850dc26547dde950c7398210602c"

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
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland libdrm', '', d)} \
	"

USE_X11 = "${@bb.utils.contains("DISTRO_FEATURES", "x11", "yes", "no", d)}"
USE_FB = "${@bb.utils.contains("DISTRO_FEATURES", "fbdev", "yes", "no", d)}"
USE_WL = "${@bb.utils.contains("DISTRO_FEATURES", "wayland", "yes", "no", d)}"

do_compile() {
	# Extract the MALI binaries into workdir
	tar -xf ${WORKDIR}/mali/rel-v2018.3/r8p0-01rel0.tar -C ${S}
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
    install -m 0644 ${S}/${PV}/glesHeaders/EGL/*.h ${D}${includedir}/EGL/
    install -d -m 0655 ${D}${includedir}/GLES
    install -m 0644 ${S}/${PV}/glesHeaders/GLES/*.h ${D}${includedir}/GLES/
    install -d -m 0655 ${D}${includedir}/GLES2
    install -m 0644 ${S}/${PV}/glesHeaders/GLES2/*.h ${D}${includedir}/GLES2/
    install -d -m 0655 ${D}${includedir}/KHR
    install -m 0644 ${S}/${PV}/glesHeaders/KHR/*.h ${D}${includedir}/KHR/

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/egl.pc ${D}${libdir}/pkgconfig/egl.pc
    install -m 0644 ${WORKDIR}/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
    install -m 0644 ${WORKDIR}/glesv1.pc ${D}${libdir}/pkgconfig/glesv1.pc
    install -m 0644 ${WORKDIR}/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc

    install -d ${D}${libdir}
    install -d ${D}${includedir}

    cp -a --no-preserve=ownership ${S}/${PV}/${ARCH_PLATFORM_DIR}/common/*.so* ${D}${libdir}

    if [ "${USE_WL}" = "yes" ]; then
	install -m 0644 ${S}/${PV}/glesHeaders/GBM/gbm.h ${D}${includedir}/
	install -m 0644 ${WORKDIR}/gbm.pc ${D}${libdir}/pkgconfig/gbm.pc
	install -m 0644 ${WORKDIR}/wayland-egl.pc ${D}${libdir}/pkgconfig/wayland-egl.pc
	install -Dm 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/wayland/libMali.so.8.0 ${D}${libdir}/wayland/libMali.so.8.0
	ln -snf wayland/libMali.so.8.0 ${D}${libdir}/libMali.so.8.0
    elif [ "${USE_X11}" = "yes" ]; then
	install -Dm 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/x11/libMali.so.8.0 ${D}${libdir}/x11/libMali.so.8.0
	ln -snf x11/libMali.so.8.0 ${D}${libdir}/libMali.so.8.0
    elif [ "${USE_FB}" = "yes" ]; then
	install -Dm 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/fbdev/libMali.so.8.0 ${D}${libdir}/fbdev/libMali.so.8.0
	ln -snf fbdev/libMali.so.8.0 ${D}${libdir}/libMali.so.8.0
    else
	install -Dm 0644 ${S}/${PV}/${ARCH_PLATFORM_DIR}/headless/libMali.so.8.0 ${D}${libdir}/headless/libMali.so.8.0
	ln -snf headless/libMali.so.8.0 ${D}${libdir}/libMali.so.8.0
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'false', 'true', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if (1)/' ${D}${includedir}/EGL/eglplatform.h
    fi
}


# Inhibit warnings about files being stripped
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

RREPLACES_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RPROVIDES_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"
RCONFLICTS_${PN} = "libegl libgles1 libglesv1-cm1 libgles2 libglesv2-2 libgbm"

# These libraries shouldn't get installed in world builds unless something
# explicitly depends upon them.
EXCLUDE_FROM_WORLD = "1"
FILES_${PN} += "${libdir}/*"
