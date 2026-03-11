#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit module-base kernel-module-split pkgconfig

EXTRA_OEMAKE += "KERNEL_SRC=${STAGING_KERNEL_DIR}"

MODULES_INSTALL_TARGET ?= "modules_install"
MODULES_MODULE_SYMVERS_LOCATION ?= ""

python __anonymous () {
    depends = d.getVar('DEPENDS')
    extra_symbols = []
    for dep in depends.split():
        if dep.startswith("kernel-module-"):
            extra_symbols.append("${STAGING_INCDIR}/" + dep + "/Module.symvers")
    d.setVar('KBUILD_EXTRA_SYMBOLS', " ".join(extra_symbols))
}

python do_package:prepend () {
    os.environ['STRIP'] = d.getVar('KERNEL_STRIP')
}

python do_devshell:prepend () {
    os.environ['CFLAGS'] = ''
    os.environ['CPPFLAGS'] = ''
    os.environ['CXXFLAGS'] = ''
    os.environ['LDFLAGS'] = ''

    os.environ['KERNEL_PATH'] = d.getVar('STAGING_KERNEL_DIR')
    os.environ['KERNEL_SRC'] = d.getVar('STAGING_KERNEL_DIR')
    os.environ['KERNEL_VERSION'] = d.getVar('KERNEL_VERSION')
    os.environ['CC'] = d.getVar('KERNEL_CC')
    os.environ['LD'] = d.getVar('KERNEL_LD')
    os.environ['AR'] = d.getVar('KERNEL_AR')
    os.environ['OBJCOPY'] = d.getVar('KERNEL_OBJCOPY')
    os.environ['STRIP'] = d.getVar('KERNEL_STRIP')
    os.environ['O'] = d.getVar('STAGING_KERNEL_BUILDDIR')
    kbuild_extra_symbols = d.getVar('KBUILD_EXTRA_SYMBOLS')
    if kbuild_extra_symbols:
        os.environ['KBUILD_EXTRA_SYMBOLS'] = kbuild_extra_symbols
    else:
        os.environ['KBUILD_EXTRA_SYMBOLS'] = ''
}

module_do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}   \
		   KERNEL_VERSION=${KERNEL_VERSION}    \
		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		   AR="${KERNEL_AR}" OBJCOPY="${KERNEL_OBJCOPY}" \
		   STRIP="${KERNEL_STRIP}" \
	           O=${STAGING_KERNEL_BUILDDIR} \
		   KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
		   ${MAKE_TARGETS}
}

module_do_install() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake DEPMOD=echo MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" \
	           INSTALL_FW_PATH="${D}${nonarch_base_libdir}/firmware" \
	           CC="${KERNEL_CC}" LD="${KERNEL_LD}" OBJCOPY="${KERNEL_OBJCOPY}" \
	           STRIP="${KERNEL_STRIP}" \
	           O=${STAGING_KERNEL_BUILDDIR} \
		   KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
	           ${MODULES_INSTALL_TARGET}

	if [ ! -e "${B}/${MODULES_MODULE_SYMVERS_LOCATION}/Module.symvers" ] ; then
		bbwarn "Module.symvers not found in ${B}/${MODULES_MODULE_SYMVERS_LOCATION}"
		bbwarn "Please consider setting MODULES_MODULE_SYMVERS_LOCATION to a"
		bbwarn "directory below B to get correct inter-module dependencies"
	else
		install -Dm0644 "${B}/${MODULES_MODULE_SYMVERS_LOCATION}"/Module.symvers ${D}${includedir}/${BPN}/Module.symvers
		# Module.symvers contains absolute path to the build directory.
		# While it doesn't actually seem to matter which path is specified,
		# clear them out to avoid confusion
		sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
	fi
}

EXPORT_FUNCTIONS do_compile do_install

# add all splitted modules to PN RDEPENDS, PN can be empty now
KERNEL_MODULES_META_PACKAGE = "${PN}"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
