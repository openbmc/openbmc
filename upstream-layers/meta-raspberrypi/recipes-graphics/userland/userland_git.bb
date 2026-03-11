DESCRIPTION = "This repository contains the source code for the ARM side \
libraries used on Raspberry Pi. These typically are installed in /opt/vc/lib \
and includes source for the ARM side code to interface to: EGL, mmal, GLESv2,\
vcos, openmaxil, vchiq_arm, bcm_host, WFC, OpenVG."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENCE;md5=0448d6488ef8cc380632b1569ee6d196"

PROVIDES += "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "", "virtual/libgles2 virtual/egl", d)}"
PROVIDES += "virtual/libomxil"

RPROVIDES:${PN} += "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "", "libgles2 egl libegl libegl1 libglesv2-2", d)}"
COMPATIBLE_MACHINE = "^rpi$"

SRCBRANCH = "master"
SRCFORK = "raspberrypi"
SRCREV = "cc1ca18fb0689b01cc2ca2aa4b400dcee624a213"

# Use the date of the above commit as the package version. Update this when
# SRCREV is changed.
PV = "20230419"

SRC_URI = "\
    git://github.com/${SRCFORK}/userland.git;protocol=https;branch=${SRCBRANCH} \
    file://0001-mmal-Do-not-use-Werror.patch \
    file://0001-Allow-applications-to-set-next-resource-handle.patch \
    file://0002-wayland-Add-support-for-the-Wayland-winsys.patch \
    file://0003-wayland-Add-Wayland-example.patch \
    file://0004-wayland-egl-Add-bcm_host-to-dependencies.patch \
    file://0005-interface-remove-faulty-assert-to-make-weston-happy-.patch \
    file://0006-zero-out-wl-buffers-in-egl_surface_free.patch \
    file://0007-initialize-front-back-wayland-buffers.patch \
    file://0008-Remove-RPC_FLUSH.patch \
    file://0009-fix-cmake-dependency-race.patch \
    file://0010-Fix-for-framerate-with-nested-composition.patch \
    file://0011-build-shared-library-for-vchostif.patch \
    file://0012-implement-buffer-wrapping-interface-for-dispmanx.patch \
    file://0013-Implement-triple-buffering-for-wayland.patch \
    file://0014-GLES2-gl2ext.h-Define-GL_R8_EXT-and-GL_RG8_EXT.patch \
    file://0015-EGL-glplatform.h-define-EGL_CAST.patch \
    file://0016-Allow-multiple-wayland-compositor-state-data-per-pro.patch \
    file://0017-khronos-backport-typedef-for-EGL_EXT_image_dma_buf_i.patch \
    file://0018-Add-EGL_IMG_context_priority-related-defines.patch \
    file://0019-libfdt-Undefine-__wordsize-if-already-defined.patch \
    file://0020-openmaxil-add-pkg-config-file.patch \
    file://0021-cmake-Disable-format-overflow-warning-as-error.patch \
    file://0022-all-host_applications-remove-non-existent-projects.patch \
    file://0023-hello_pi-optionally-build-wayland-specific-app.patch \
    file://0024-userland-Sync-needed-defines-for-weston-build.patch \
    file://0025-CMakeLists.txt-.pc-respect-CMAKE_INSTALL_LIBDIR.patch \
"

SRC_URI:remove:toolchain-clang = "file://0021-cmake-Disable-format-overflow-warning-as-error.patch"

inherit cmake pkgconfig

ASNEEDED = ""

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DCMAKE_EXE_LINKER_FLAGS='-Wl,--no-as-needed' \
                 -DVMCS_INSTALL_PREFIX=${exec_prefix} \
"

EXTRA_OECMAKE:append:aarch64 = " -DARM64=ON "

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

PACKAGECONFIG[wayland] = "-DBUILD_WAYLAND=TRUE -DWAYLAND_SCANNER_EXECUTABLE:FILEPATH=${STAGING_BINDIR_NATIVE}/wayland-scanner,,wayland-native wayland"
PACKAGECONFIG[allapps] = "-DALL_APPS=true,,,"

CFLAGS:append = " -fPIC -Wno-unused-but-set-variable"

do_install:append () {
	for f in `find ${D}${includedir}/interface/vcos/ -name "*.h"`; do
		sed -i 's/include "vcos_platform.h"/include "pthreads\/vcos_platform.h"/g' ${f}
		sed -i 's/include "vcos_futex_mutex.h"/include "pthreads\/vcos_futex_mutex.h"/g' ${f}
		sed -i 's/include "vcos_platform_types.h"/include "pthreads\/vcos_platform_types.h"/g' ${f}
	done
        rm -rf ${D}${prefix}${sysconfdir}
	if [ "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "1", "0", d)}" = "1" ]; then
		rm -rf ${D}${libdir}/libEGL*
		rm -rf ${D}${libdir}/libGLES*
		rm -rf ${D}${libdir}/libwayland-*
		rm -rf ${D}${libdir}/pkgconfig/egl.pc ${D}${libdir}/pkgconfig/glesv2.pc \
			${D}${libdir}/pkgconfig/wayland-egl.pc
		rm -rf ${D}${includedir}/EGL ${D}${includedir}/GLES* ${D}${includedir}/KHR
        else
                ln -sf brcmglesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
                ln -sf brcmegl.pc ${D}${libdir}/pkgconfig/egl.pc
                ln -sf brcmvg.pc ${D}${libdir}/pkgconfig/vg.pc
	fi
	# Currently man files are installed in /usr/man instead of /usr/share/man, see comments in:
	# https://github.com/raspberrypi/userland/commit/45a0022ac64b4d0788def3c5230c972430f6fc23
	mkdir -pv ${D}${datadir}
	mv -v ${D}${prefix}/man ${D}${mandir}
}

# Shared libs from userland package  build aren't versioned, so we need
# to force the .so files into the runtime package (and keep them
# out of -dev package).
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

FILES:${PN} += " \
    ${libdir}/*.so \
    ${libdir}/plugins"
FILES:${PN}-dev += "${includedir} \
                   ${prefix}/src"
FILES:${PN}-doc += "${datadir}/install"
FILES:${PN}-dbg += "${libdir}/plugins/.debug"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "libegl-mesa", "", d)}"
