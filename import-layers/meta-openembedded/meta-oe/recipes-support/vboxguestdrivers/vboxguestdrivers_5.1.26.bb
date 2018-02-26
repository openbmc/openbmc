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
"
SRC_URI[md5sum] = "d3aec8190c649d7e0d92ba374779dfe3"
SRC_URI[sha256sum] = "b5715035e681a11ef1475f83f9503d34a00f0276b89c572eebec363dda80c8a9"

S = "${WORKDIR}/vbox_module"

export BUILD_TARGET_ARCH="${ARCH}"
export BUILD_TARGET_ARCH_x86-64="amd64"
export KERN_DIR="${STAGING_KERNEL_DIR}"

addtask export_sources before do_patch after do_unpack

do_export_sources() {
    mkdir -p "${S}"
    ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/export_modules ${T}/vbox_modules.tar.gz
    tar -C "${S}" -xzf ${T}/vbox_modules.tar.gz

    # add a mount utility to use shared folder from VBox Addition Source Code
    mkdir -p "${S}/utils"
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/mount.vboxsf.c ${S}/utils
    install ${WORKDIR}/${VBOX_NAME}/src/VBox/Additions/linux/sharedfolders/vbsfmount.c ${S}/utils
    install ${S}/../Makefile.utils ${S}/utils/Makefile

}

# compile and install mount utility
do_compile_append() {
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
