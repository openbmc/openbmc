SUMMARY = "UProbes kernel module for SystemTap"
HOMEPAGE = "https://sourceware.org/systemtap/"
require systemtap_git.inc

DEPENDS = "systemtap virtual/kernel"

# On systems without CONFIG_UTRACE, this package is empty.
ALLOW_EMPTY_${PN} = "1"

inherit module-base gettext

FILESEXTRAPATHS =. "${FILE_DIRNAME}/systemtap:"

FILES_${PN} += "${datadir}/systemtap/runtime/uprobes"

# Compile and install the uprobes kernel module on machines with utrace
# support.  Note that staprun expects it in the systemtap/runtime directory,
# not in /lib/modules.
do_compile() {
	if grep -q "CONFIG_UTRACE=y" ${STAGING_KERNEL_BUILDDIR}/.config
	then
		unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS CC LD CPP
		oe_runmake CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
			   AR="${KERNEL_AR}" \
			   -C ${STAGING_KERNEL_DIR} scripts
		oe_runmake KDIR=${STAGING_KERNEL_DIR}   \
			   M="${S}/runtime/uprobes/" \
			   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
			   AR="${KERNEL_AR}" \
			   -C "${S}/runtime/uprobes/"
	fi
}

do_install() {
	if [ -e "${S}/runtime/uprobes/uprobes.ko" ]
	then
		install -d ${D}${datadir}/systemtap/runtime/uprobes/
		install -m 0644 ${S}/runtime/uprobes/uprobes.ko ${D}${datadir}/systemtap/runtime/uprobes/
	fi
}
