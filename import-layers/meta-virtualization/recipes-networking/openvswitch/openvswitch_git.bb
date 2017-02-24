require openvswitch.inc

DEPENDS += "virtual/kernel"

RDEPENDS_${PN}-ptest += "\
	python-logging python-syslog python-argparse python-io \
	python-fcntl python-shell python-lang python-xml python-math \
	python-datetime python-netclient python sed \
	ldd perl-module-socket perl-module-carp perl-module-exporter \
	perl-module-xsloader python-netserver python-threading \
	python-resource python-subprocess \
	"

S = "${WORKDIR}/git"
PV = "2.5.0+${SRCREV}"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}-git:"

SRCREV = "8c0b419a0b9ac0141d6973dcc80306dfc6a83d31"
SRC_URI += "\
	git://github.com/openvswitch/ovs.git;protocol=git;branch=branch-2.5 \
	file://openvswitch-add-more-target-python-substitutions.patch \
	file://openvswitch-add-ptest-${SRCREV}.patch \
	file://run-ptest \
	file://disable_m4_check.patch \
	file://kernel_module.patch \
	file://openvswitch-ptest-Fix-python-path.patch \
	"

LIC_FILES_CHKSUM = "file://COPYING;md5=247d8817aece26b21a8cd6791b3ea994"

PACKAGECONFIG ?= ""
PACKAGECONFIG[dpdk] = "--with-dpdk=${STAGING_DIR_TARGET}/opt/dpdk/${TARGET_ARCH}-native-linuxapp-gcc,,dpdk,"

# Don't compile kernel modules by default since it heavily depends on
# kernel version. Use the in-kernel module for now.
# distro layers can enable with EXTRA_OECONF_pn_openvswitch += ""
# EXTRA_OECONF += "--with-linux=${STAGING_KERNEL_BUILDDIR} --with-linux-source=${STAGING_KERNEL_DIR} KARCH=${TARGET_ARCH}"

# silence a warning
FILES_${PN} += "/lib/modules"

inherit ptest

EXTRA_OEMAKE += "TEST_DEST=${D}${PTEST_PATH} TEST_ROOT=${PTEST_PATH}"

do_install_ptest() {
	oe_runmake test-install
}

do_install_append() {
	oe_runmake modules_install INSTALL_MOD_PATH=${D}
	rm -r ${D}/${localstatedir}/run
}
