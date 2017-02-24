SUMMARY = "OpenvSwitch"
DESCRIPTION = "\
	Open vSwitch is a production quality, multilayer virtual switch \
	licensed under the open source Apache 2.0 license. It is designed \
	to enable massive network automation through programmatic extension, \
	while still supporting standard management interfaces and protocols \
	(e.g. NetFlow, sFlow, SPAN, RSPAN, CLI, LACP, 802.1ag) \
	"

HOMEPAGE = "http://openvswitch.org/"
SECTION = "networking"
LICENSE = "Apache-2"

DEPENDS += "bridge-utils openssl python perl"

RDEPENDS_${PN} += "util-linux-uuidgen util-linux-libuuid coreutils \
	python perl perl-module-strict ${PN}-switch \
        bash python-twisted"
RDEPENDS_${PN}-testcontroller = "${PN} lsb ${PN}-pki"
RDEPENDS_${PN}-switch = "${PN} openssl procps util-linux-uuidgen"
RDEPENDS_${PN}-pki = "${PN}"
RDEPENDS_${PN}-brcompat = "${PN} ${PN}-switch"

# Some installers will fail because of an install order based on
# rdeps.  E.g. ovs-pki calls sed in the postinstall.  sed may be
# queued for install later.
RDEPENDS_${PN} += "sed gawk grep"

SRC_URI = "\
	file://openvswitch-switch \
	file://openvswitch-switch-setup \
	file://openvswitch-testcontroller \
	file://openvswitch-testcontroller-setup \
	file://openvswitch-add-target-python-handling.patch \
	file://openvswitch-add-target-perl-handling.patch \
	"

EXTRA_OECONF += "\
	TARGET_PYTHON=${bindir}/python \
	TARGET_PERL=${bindir}/perl \
	"
CONFIGUREOPT_DEPTRACK = ""

# Don't compile kernel modules by default since it heavily depends on
# kernel version. Use the in-kernel module for now.
# distro layers can enable with EXTRA_OECONF_pn_openvswitch += ""
# EXTRA_OECONF = "--with-linux=${STAGING_KERNEL_DIR} KARCH=${TARGET_ARCH}"

ALLOW_EMPTY_${PN}-pki = "1"
PACKAGES =+ "${PN}-testcontroller ${PN}-switch ${PN}-brcompat ${PN}-pki"

FILES_${PN}-testcontroller = "\
	${sysconfdir}/init.d/openvswitch-testcontroller \
	${sysconfdir}/default/openvswitch-testcontroller \
	${sysconfdir}/openvswitch-testcontroller \
	${bindir}/ovs-testcontroller \
	"

FILES_${PN}-brcompat = "${sbindir}/ovs-brcompatd"

FILES_${PN}-switch = "\
	${sysconfdir}/init.d/openvswitch-switch \
	${sysconfdir}/default/openvswitch-switch \
	${systemd_unitdir}/system/openvswitch.service \
	${systemd_unitdir}/system/openvswitch-nonetwork.service \
	"

# silence a warning
FILES_${PN} += "${datadir}/ovsdbmonitor"
FILES_${PN} += "/run"

FILES_${PN} += "${libdir}/python${PYTHON_BASEVERSION}/"
inherit autotools update-rc.d systemd python-dir

SYSTEMD_PACKAGES = "${PN}-switch"
SYSTEMD_SERVICE_${PN}-switch = " \
    openvswitch-nonetwork.service \
    openvswitch-switch.service \
"

INITSCRIPT_PACKAGES = "${PN}-switch ${PN}-testcontroller"
INITSCRIPT_NAME_${PN}-switch = "openvswitch-switch"
INITSCRIPT_PARAMS_${PN}-switch = "defaults 71"

INITSCRIPT_NAME_${PN}-testcontroller = "openvswitch-testcontroller"
INITSCRIPT_PARAMS_${PN}-testcontroller = "defaults 72"

do_install_append() {
	install -d ${D}/${sysconfdir}/default/
	install -m 660 ${WORKDIR}/openvswitch-switch-setup ${D}/${sysconfdir}/default/openvswitch-switch
	install -d ${D}/${sysconfdir}/openvswitch-testcontroller
	install -m 660 ${WORKDIR}/openvswitch-testcontroller-setup ${D}/${sysconfdir}/default/openvswitch-testcontroller

	install -d ${D}/${sysconfdir}/init.d/
	install -m 755 ${WORKDIR}/openvswitch-testcontroller ${D}/${sysconfdir}/init.d/openvswitch-testcontroller
	install -m 755 ${WORKDIR}/openvswitch-switch ${D}/${sysconfdir}/init.d/openvswitch-switch
	true || rm -fr ${D}/${datadir}/${PN}/pki

	install -d ${D}/${systemd_unitdir}/system/
	install -m 644 ${S}/rhel/usr_lib_systemd_system_openvswitch.service \
		${D}/${systemd_unitdir}/system/openvswitch-switch.service
	install -m 644 ${S}/rhel/usr_lib_systemd_system_openvswitch-nonetwork.service \
		${D}/${systemd_unitdir}/system/openvswitch-nonetwork.service

	oe_runmake modules_install INSTALL_MOD_PATH=${D}
	install -d ${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages
	cp -r ${S}/python/ovstest/ ${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages/
}

pkg_postinst_${PN}-pki () {
	# can't do this offline
	if [ "x$D" != "x" ]; then
		exit 1
	fi
	if test ! -d $D/${datadir}/${PN}/pki; then
		ovs-pki init --dir=$D/${datadir}/${PN}/pki
	fi
}

pkg_postinst_${PN}-testcontroller () {
	# can't do this offline
	if [ "x$D" != "x" ]; then
		exit 1
	fi

	if test ! -d $D/${datadir}/${PN}/pki; then
		ovs-pki init --dir=$D/${datadir}/${PN}/pki
	fi

	cd $D/${sysconfdir}/openvswitch-testcontroller
	if ! test -e cacert.pem; then
		ln -s $D/${datadir}/${PN}/pki/switchca/cacert.pem cacert.pem
	fi
	if ! test -e privkey.pem || ! test -e cert.pem; then
		oldumask=$(umask)
		umask 077
		ovs-pki req+sign --dir=$D/${datadir}/${PN}/pki tmp controller >/dev/null
		mv tmp-privkey.pem privkey.pem
		mv tmp-cert.pem cert.pem
		mv tmp-req.pem req.pem
		chmod go+r cert.pem req.pem
		umask $oldumask
	fi
}
