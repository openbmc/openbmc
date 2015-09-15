require recipes-extended/chkconfig/chkconfig_1.3.58.bb

SUMMARY = "${SUMMARY_chkconfig-alternatives}"
DESCRIPTION = "${DESCRIPTION_chkconfig-alternatives}"
DEPENDS = ""
PROVIDES += "virtual/update-alternatives-native"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# The sysroot branch is 1.3.59 + some git commits from master + --sysroot
# support for alternatives.
SRC_URI = "git://github.com/kergoth/chkconfig;branch=sysroot"
S = "${WORKDIR}/git"

SRCREV = "cd437ecbd8986c894442f8fce1e0061e20f04dee"
PV = "1.3.59+${SRCPV}"

inherit native

# We want our native recipes to build using the target paths rather than paths
# into the sysroot, as we may use them to construct the rootfs. As such, we
# only adjust the paths to match the metadata for the target, not native.
obey_variables () {
	sed -i 's,ALTERNATIVES_ROOT,OPKG_OFFLINE_ROOT,' alternatives.c
}

do_compile () {
	oe_runmake alternatives
}

do_install () {
	install -d ${D}${sysconfdir}/alternatives \
	           ${D}${localstatedir}/lib/alternatives

	install -D -m 0755 alternatives ${D}${bindir}/alternatives
	install -D -m 0644 alternatives.8 ${D}${mandir}/man8/alternatives.8

	ln -s alternatives ${D}${bindir}/update-alternatives
	ln -s alternatives.8 ${D}${mandir}/man8/update-alternatives.8
}

do_install_append_linuxstdbase() {
	rm -rf ${D}${libdir}/lsb
}
