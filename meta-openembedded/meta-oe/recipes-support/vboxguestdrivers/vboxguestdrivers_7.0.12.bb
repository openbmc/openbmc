SUMMARY = "VirtualBox Linux Guest Drivers"
SECTION = "core"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${WORKDIR}/${VBOX_NAME}/COPYING;md5=fff5fe1c81dd6dc3d522e7862e44881e"

DEPENDS = "virtual/kernel"

inherit module kernel-module-split

COMPATIBLE_MACHINE = "(qemux86|qemux86-64)"

VBOX_NAME = "VirtualBox-${PV}"

SRC_URI = "http://download.virtualbox.org/virtualbox/${PV}/${VBOX_NAME}.tar.bz2 \
    file://Makefile.utils \
"

SRC_URI[sha256sum] = "d76634c6ccf62503726a5aeae6c78a3462474c51a0ebe4942591ccc2d939890a"

S ?= "${WORKDIR}/vbox_module"
S:task-patch = "${WORKDIR}/${VBOX_NAME}"

export VBOX_KBUILD_TARGET_ARCH="${ARCH}"
export VBOX_KBUILD_TARGET_ARCH:x86-64="amd64"

EXTRA_OEMAKE += "KERN_DIR='${WORKDIR}/${KERNEL_VERSION}/build' KBUILD_VERBOSE=1 CC='${CC} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} -fdebug-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH}'"

# otherwise 5.2.22 builds just vboxguest
MAKE_TARGETS = "all"

addtask export_sources after do_patch before do_configure
do_export_sources[depends] += "virtual/kernel:do_shared_workdir"

do_export_sources() {
    mkdir -p "${S}"
    ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/export_modules.sh ${T}/vbox_modules.tar.gz
    tar -C "${S}" -xzf ${T}/vbox_modules.tar.gz

    # add a mount utility to use shared folder from VBox Addition Source Code
    mkdir -p "${S}/utils"
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/mount.vboxsf.c ${S}/utils
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/vbsfmount.c ${S}/utils
    install ${S}/../Makefile.utils ${S}/utils/Makefile

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
    oe_runmake all
    oe_runmake 'LD=${CC}' 'EXTRA_CFLAGS=-I${STAGING_KERNEL_BUILDDIR}/include/' 'LDFLAGS=${LDFLAGS}' -C ${S}/utils
    if ! [ -e vboxguest.ko -a -e vboxsf.ko -a -e vboxvideo.ko ] ; then
        echo "ERROR: One of vbox*.ko modules wasn't built"
        exit 1
    fi
}

module_do_install() {
    MODULE_DIR=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/misc
    install -d $MODULE_DIR
    install -m 644 vboxguest.ko $MODULE_DIR
    install -m 644 vboxsf.ko $MODULE_DIR
    install -m 644 vboxvideo.ko $MODULE_DIR
}

do_install:append() {
    install -d ${D}${base_sbindir}
    install -m 755 ${S}/utils/mount.vboxsf ${D}${base_sbindir}
}

PACKAGES += "kernel-module-vboxguest kernel-module-vboxsf kernel-module-vboxvideo"
RRECOMMENDS:${PN} += "kernel-module-vboxguest kernel-module-vboxsf kernel-module-vboxvideo"

FILES:${PN} = "${base_sbindir}"

# autoload if installed
KERNEL_MODULE_AUTOLOAD += "vboxguest vboxsf vboxvideo"
