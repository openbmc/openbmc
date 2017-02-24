inherit module-base kernel-module-split

addtask make_scripts after do_patch before do_compile
do_make_scripts[lockfiles] = "${TMPDIR}/kernel-scripts.lock"
do_make_scripts[depends] += "virtual/kernel:do_shared_workdir"

EXTRA_OEMAKE += "KERNEL_SRC=${STAGING_KERNEL_DIR}"

MODULES_INSTALL_TARGET ?= "modules_install"

python __anonymous () {
    depends = d.getVar('DEPENDS', True)
    extra_symbols = []
    for dep in depends.split():
        if dep.startswith("kernel-module-"):
            extra_symbols.append("${STAGING_INCDIR}/" + dep + "/Module.symvers")
    d.setVar('KBUILD_EXTRA_SYMBOLS', " ".join(extra_symbols))
}

module_do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}   \
		   KERNEL_VERSION=${KERNEL_VERSION}    \
		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		   AR="${KERNEL_AR}" \
	           O=${STAGING_KERNEL_BUILDDIR} \
		   KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
		   ${MAKE_TARGETS}
}

module_do_install() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake DEPMOD=echo INSTALL_MOD_PATH="${D}" \
	           CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
	           O=${STAGING_KERNEL_BUILDDIR} \
	           ${MODULES_INSTALL_TARGET}

	install -d -m0755 ${D}${includedir}/${BPN}
	cp -a --no-preserve=ownership ${B}/Module.symvers ${D}${includedir}/${BPN}
	# it doesn't actually seem to matter which path is specified here
	sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
}

EXPORT_FUNCTIONS do_compile do_install

# add all splitted modules to PN RDEPENDS, PN can be empty now
KERNEL_MODULES_META_PACKAGE = "${PN}"
FILES_${PN} = ""
ALLOW_EMPTY_${PN} = "1"
