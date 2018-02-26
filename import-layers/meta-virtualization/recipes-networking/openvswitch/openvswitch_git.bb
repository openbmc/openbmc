require openvswitch.inc

DEPENDS += "virtual/kernel"

RDEPENDS_${PN}-ptest += "\
	python3-logging python3-syslog python3-argparse python3-io \
	python3-fcntl python3-shell python3-lang python3-xml python3-math \
	python3-datetime python3-netclient python3 sed \
	ldd perl-module-socket perl-module-carp perl-module-exporter \
	perl-module-xsloader python3-netserver python3-threading \
	python3-resource python3-subprocess findutils which \
	"

S = "${WORKDIR}/git"
PV = "2.7.1+${SRCREV}"

FILESEXTRAPATHS_append := "${THISDIR}/${PN}-git:"

SRCREV = "b29cb89e9e9fe3119b2e5dd5d4fb79141635b7cc"
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
           file://CVE-2017-9263.patch \
           file://python-switch-remaining-scripts-to-use-python3.patch \
           "

# Temporarily backport patches to better support py3. These have been
# merged upstream but are not part of v2.7.1.
SRC_URI += " \
           file://0001-Python3-compatibility-Convert-print-statements.patch \
           file://0002-Python3-compatibility-exception-cleanup.patch \
           file://0003-Python3-compatibility-execfile-to-exec.patch \
           file://0004-Python3-compatibility-iteritems-to-items.patch \
           file://0005-Python3-compatibility-fix-integer-problems.patch \
           file://0006-Python3-compatibility-math-error-compatibility.patch \
           file://0007-Python3-compatibility-unicode-to-str.patch \
           file://0008-AUTHORS-Add-Jason-Wessel.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=17b2c9d4c70853a09c0e143137754b35"

DPDK_INSTALL_DIR ?= "/opt/dpdk"

PACKAGECONFIG ?= "libcap-ng"
PACKAGECONFIG[dpdk] = "--with-dpdk=${STAGING_DIR_TARGET}${DPDK_INSTALL_DIR}/share/${TARGET_ARCH}-native-linuxapp-gcc,,dpdk,dpdk"
PACKAGECONFIG[libcap-ng] = "--enable-libcapng,--disable-libcapng,libcap-ng,"

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
