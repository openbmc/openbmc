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
PV = "2.7.0+${SRCREV}"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}-git:"

SRCREV = "c298ef781c2d35d939fe163cbc2f41ea7b1cb8d1"
SRC_URI = "file://openvswitch-switch \
           file://openvswitch-switch-setup \
           file://openvswitch-testcontroller \
           file://openvswitch-testcontroller-setup \
           git://github.com/openvswitch/ovs.git;protocol=git;branch=branch-2.7 \
           file://openvswitch-add-ptest-${SRCREV}.patch \
           file://run-ptest \
           file://disable_m4_check.patch \
           file://kernel_module.patch \
           file://python-make-remaining-scripts-use-usr-bin-env.patch \
           file://0001-use-the-linux-if_packet.h-Interface-directly.patch \
           file://0002-Define-WAIT_ANY-if-not-provided-by-system.patch \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=17b2c9d4c70853a09c0e143137754b35"

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
