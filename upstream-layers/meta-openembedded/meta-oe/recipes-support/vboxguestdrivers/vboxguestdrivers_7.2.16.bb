SUMMARY = "VirtualBox Linux Guest Drivers"
HOMEPAGE = "https://www.virtualbox.org/"
SECTION = "core"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/${VBOX_NAME}/COPYING;md5=217590d3a513571b94632edf5fa1169a"

DEPENDS = "virtual/kernel"
# Besides the kernel modules, this recipe also builds a userspace utility
# (utils/mount.vboxsf). module.bbclass sets INHIBIT_DEFAULT_DEPS=1, which drops
# BASE_DEFAULT_DEPS ("virtual/cross-cc virtual/compilerlibs virtual/libc") and
# only re-adds virtual/cross-cc + virtual/cross-binutils. As a result the target
# libc headers/crt objects are never staged into recipe-sysroot, so the userspace
# compile fails with "errno.h: No such file or directory". Restore the libc and
# compiler-runtime deps needed to compile and link the userspace utility.
DEPENDS += "virtual/compilerlibs virtual/libc"

inherit module kernel-module-split

COMPATIBLE_MACHINE = "(qemux86|qemux86-64|qemuarm64)"

VBOX_NAME = "VirtualBox-${PV}"

SRC_URI = "http://download.virtualbox.org/virtualbox/${PV}/${VBOX_NAME}.tar.bz2 \
    file://Makefile.utils \
    file://0001-vboxvideo-let-the-build-decide-if-drm_fb_helper_alloc.patch \
"
SRC_URI[sha256sum] = "50356ccdaefe8f03537600ec31898b506e3a85ce79b94f26fb6cc1920c9e18eb"

S ?= "${UNPACKDIR}/vbox_module"
S:task-unpack = "${UNPACKDIR}/${VBOX_NAME}"
S:task-patch = "${UNPACKDIR}/${BP}"

export VBOX_KBUILD_TARGET_ARCH = "${ARCH}"
export VBOX_KBUILD_TARGET_ARCH:x86-64 = "amd64"

# The Makefile uses KERN_MAJ to decide whether vboxvideo is built. It defaults to
# the *host* kernel version (uname -r), so pass the target one instead.
KERN_MAJ = "${@(oe.kernel.get_version_file(d.getVar('STAGING_KERNEL_BUILDDIR')) or '').split('.')[0]}"

# VirtualBox only ships the out-of-tree vboxvideo DRM module for kernels older
# than 7.x - from 7.x on the in-tree drivers/gpu/drm/vboxvideo driver is used
# instead. Mirror that decision so the compile check, the install step and the
# packaging stay in sync with what "make all" actually produced.
VBOX_VIDEO_MODULE = "${@'vboxvideo' if (d.getVar('KERN_MAJ') or '').isdigit() and int(d.getVar('KERN_MAJ')) < 7 else ''}"
VBOX_MODULES = "vboxguest vboxsf ${VBOX_VIDEO_MODULE}"

EXTRA_OEMAKE += "KERN_DIR='${WORKDIR}/${KERNEL_VERSION}/build' KERN_MAJ='${KERN_MAJ}' KBUILD_VERBOSE=1 CC='${CC} ${DEBUG_PREFIX_MAP} -ffile-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} -ffile-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH}'"

# otherwise 5.2.22 builds just vboxguest
MAKE_TARGETS = "all"

addtask export_sources before do_patch after do_unpack
do_export_sources[depends] += "virtual/kernel:do_shared_workdir"

do_export_sources() {
    mkdir -p "${S}"
    ${UNPACKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/export_modules.sh ${T}/vbox_modules.tar.gz
    tar -C "${S}" -xzf ${T}/vbox_modules.tar.gz

    # add a mount utility to use shared folder from VBox Addition Source Code
    mkdir -p "${S}/utils"
    install ${UNPACKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/mount.vboxsf.c ${S}/utils
    install ${UNPACKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/vbsfmount.c ${S}/utils
    install ${UNPACKDIR}/Makefile.utils ${S}/utils/Makefile

    # some kernel versions have issues with stdarg.h and compatibility with
    # the sysroot and libc-headers/uapi. If we include the file directly from
    # the kernel source (STAGING_KERNEL_DIR) we get conflicting types on many
    # structures, due to kernel .h files being found before libc .h files.
    # if we grab just this one file from the source, and put it into our
    # file structure, everything holds together
    mkdir -p ${S}/vboxsf/include/linux
    install ${STAGING_KERNEL_DIR}/include/linux/stdarg.h  ${S}/vboxsf/include/linux
}

do_configure:prepend() {
    # vboxguestdrivers/5.2.6-r0/vbox_module/vboxguest/Makefile.include.header:99: *** The variable KERN_DIR must be a kernel build folder and end with /build without a trailing slash, or KERN_VER must be set.  Stop.
    # vboxguestdrivers/5.2.6-r0/vbox_module/vboxguest/Makefile.include.header:108: *** The kernel build folder path must end in <version>/build, or the variable KERN_VER must be set.  Stop.
    mkdir -p ${WORKDIR}/${KERNEL_VERSION}
    ln -snf ${STAGING_KERNEL_DIR} ${WORKDIR}/${KERNEL_VERSION}/build
}

# compile and install mount utility
do_compile() {
    vbox_kcflags=""
    if [ -e "${STAGING_KERNEL_DIR}/include/drm/drm_fb_helper.h" ] &&
       ! grep -q drm_fb_helper_alloc_info "${STAGING_KERNEL_DIR}/include/drm/drm_fb_helper.h"; then
        vbox_kcflags="-DVBOX_NO_DRM_FB_HELPER_ALLOC_INFO"
    fi

    oe_runmake all KCFLAGS="$vbox_kcflags"
    oe_runmake 'LD=${CC}' 'EXTRA_CFLAGS=-I${STAGING_KERNEL_BUILDDIR}/include/' 'LDFLAGS=${LDFLAGS}' -C ${S}/utils
    for m in ${VBOX_MODULES} ; do
        if ! [ -e $m.ko ] ; then
            echo "ERROR: kernel module $m.ko wasn't built"
            exit 1
        fi
    done
}

module_do_install() {
    MODULE_DIR=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/misc
    install -d $MODULE_DIR
    for m in ${VBOX_MODULES} ; do
        install -m 644 $m.ko $MODULE_DIR
    done
}

do_install:append() {
    install -d ${D}${base_sbindir}
    install -m 755 ${S}/utils/mount.vboxsf ${D}${base_sbindir}
}

VBOX_MODULE_PACKAGES = "${@' '.join('kernel-module-' + m for m in d.getVar('VBOX_MODULES').split())}"
PACKAGES += "${VBOX_MODULE_PACKAGES}"
RRECOMMENDS:${PN} += "${VBOX_MODULE_PACKAGES}"

FILES:${PN} = "${base_sbindir}"

# autoload if installed
KERNEL_MODULE_AUTOLOAD += "${VBOX_MODULES}"
