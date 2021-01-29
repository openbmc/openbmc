SUMMARY = "VirtualBox Linux Guest Drivers"
SECTION = "core"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/${VBOX_NAME}/COPYING;md5=e197d5641bb35b29d46ca8c4bf7f2660"

DEPENDS = "virtual/kernel"

inherit module kernel-module-split

COMPATIBLE_MACHINE = "(qemux86|qemux86-64)"

VBOX_NAME = "VirtualBox-${PV}"

SRC_URI = "http://download.virtualbox.org/virtualbox/${PV}/${VBOX_NAME}.tar.bz2 \
    file://Makefile.utils \
    file://kernel-5.10.patch \
"
SRC_URI[md5sum] = "c61001386eb3822ab8f06d688a82e84b"
SRC_URI[sha256sum] = "108d42b9b391b7a332a33df1662cf7b0e9d9a80f3079d16288d8b9487f427d40"

S ?= "${WORKDIR}/vbox_module"
S_task-patch = "${WORKDIR}/${VBOX_NAME}"

export BUILD_TARGET_ARCH="${ARCH}"
export BUILD_TARGET_ARCH_x86-64="amd64"

EXTRA_OEMAKE += "KERN_DIR='${WORKDIR}/${KERNEL_VERSION}/build' KBUILD_VERBOSE=1"

# otherwise 5.2.22 builds just vboxguest
MAKE_TARGETS = "all"

addtask export_sources after do_patch before do_configure

do_export_sources() {
    mkdir -p "${S}"
    ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/export_modules.sh ${T}/vbox_modules.tar.gz
    tar -C "${S}" -xzf ${T}/vbox_modules.tar.gz

    # add a mount utility to use shared folder from VBox Addition Source Code
    mkdir -p "${S}/utils"
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/mount.vboxsf.c ${S}/utils
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/vbsfmount.c ${S}/utils
    install ${S}/../Makefile.utils ${S}/utils/Makefile

}

do_configure_prepend() {
    # vboxguestdrivers/5.2.6-r0/vbox_module/vboxguest/Makefile.include.header:99: *** The variable KERN_DIR must be a kernel build folder and end with /build without a trailing slash, or KERN_VER must be set.  Stop.
    # vboxguestdrivers/5.2.6-r0/vbox_module/vboxguest/Makefile.include.header:108: *** The kernel build folder path must end in <version>/build, or the variable KERN_VER must be set.  Stop.
    mkdir -p ${WORKDIR}/${KERNEL_VERSION}
    ln -snf ${STAGING_KERNEL_DIR} ${WORKDIR}/${KERNEL_VERSION}/build
}

# compile and install mount utility
do_compile() {
    oe_runmake all
    oe_runmake 'LD=${CC}' 'LDFLAGS=${LDFLAGS}' -C ${S}/utils
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

do_install_append() {
    install -d ${D}${base_sbindir}
    install -m 755 ${S}/utils/mount.vboxsf ${D}${base_sbindir}
}

PACKAGES += "kernel-module-vboxguest kernel-module-vboxsf kernel-module-vboxvideo"
RRECOMMENDS_${PN} += "kernel-module-vboxguest kernel-module-vboxsf kernel-module-vboxvideo"

FILES_${PN} = "${base_sbindir}"

# autoload if installed
KERNEL_MODULE_AUTOLOAD += "vboxguest vboxsf vboxvideo"
