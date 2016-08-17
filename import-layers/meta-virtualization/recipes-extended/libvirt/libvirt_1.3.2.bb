DESCRIPTION = "A toolkit to interact with the virtualization capabilities of recent versions of Linux." 
HOMEPAGE = "http://libvirt.org"
LICENSE = "LGPLv2.1+"
LICENSE_${PN}-ptest = "GPLv2+ & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LESSER;md5=4b54a1fd55a448865a0b32d41598759d"
SECTION = "console/tools"
PR = "r1"

DEPENDS = "bridge-utils gnutls libxml2 lvm2 avahi parted curl libpcap util-linux e2fsprogs pm-utils \
	   iptables dnsmasq readline libtasn1 libxslt-native"

# libvirt-guests.sh needs gettext.sh
#
RDEPENDS_${PN} = "gettext-runtime"

RDEPENDS_${PN}-ptest += "make gawk"

RDEPENDS_libvirt-libvirtd += "bridge-utils iptables pm-utils dnsmasq netcat-openbsd"
RDEPENDS_libvirt-libvirtd_append_x86-64 = " dmidecode"
RDEPENDS_libvirt-libvirtd_append_x86 = " dmidecode"

#connman blocks the 53 port and libvirtd can't start its DNS service
RCONFLICTS_${PN}_libvirtd = "connman"

SRC_URI = "http://libvirt.org/sources/libvirt-${PV}.tar.gz;name=libvirt \
           file://tools-add-libvirt-net-rpc-to-virt-host-validate-when.patch \
           file://libvirtd.sh \
           file://libvirtd.conf \
           file://runptest.patch \
           file://run-ptest \
           file://tests-allow-separated-src-and-build-dirs.patch \
           file://libvirt-use-pkg-config-to-locate-libcap.patch \
           file://0001-to-fix-build-error.patch \
           file://Revert-build-add-prefix-to-SYSTEMD_UNIT_DIR.patch \
          "

SRC_URI[libvirt.md5sum] = "b48b06bbc7efbe9973ed0f3f223d6da2"
SRC_URI[libvirt.sha256sum] = "e3c6fc2683178660b371efb3ac7a1103a3f4b78efac7ffe560bc5917974ccf05"

inherit autotools gettext update-rc.d pkgconfig ptest systemd

CACHED_CONFIGUREVARS += "\
ac_cv_path_XMLLINT=/usr/bin/xmllint \
ac_cv_path_XMLCATLOG=/usr/bin/xmlcatalog \
ac_cv_path_AUGPARSE=/usr/bin/augparse \
ac_cv_path_DNSMASQ=/usr/bin/dnsmasq \
ac_cv_path_BRCTL=/usr/sbin/brctl \
ac_cv_path_TC=/sbin/tc \
ac_cv_path_UDEVADM=/sbin/udevadm \
ac_cv_path_MODPROBE=/sbin/modprobe \
ac_cv_path_IP_PATH=/bin/ip \
ac_cv_path_IPTABLES_PATH=/usr/sbin/iptables \
ac_cv_path_IP6TABLES_PATH=/usr/sbin/ip6tables \
ac_cv_path_MOUNT=/bin/mount \
ac_cv_path_UMOUNT=/bin/umount \
ac_cv_path_MKFS=/usr/sbin/mkfs \
ac_cv_path_SHOWMOUNT=/usr/sbin/showmount \
ac_cv_path_PVCREATE=/usr/sbin/pvcreate \
ac_cv_path_VGCREATE=/usr/sbin/vgcreate \
ac_cv_path_LVCREATE=/usr/sbin/lvcreate \
ac_cv_path_PVREMOVE=/usr/sbin/pvremove \
ac_cv_path_VGREMOVE=/usr/sbin/vgremove \
ac_cv_path_LVREMOVE=/usr/sbin/lvremove \
ac_cv_path_LVCHANGE=/usr/sbin/lvchange \
ac_cv_path_VGCHANGE=/usr/sbin/vgchange \
ac_cv_path_VGSCAN=/usr/sbin/vgscan \
ac_cv_path_PVS=/usr/sbin/pvs \
ac_cv_path_VGS=/usr/sbin/vgs \
ac_cv_path_LVS=/usr/sbin/lvs \
ac_cv_path_PARTED=/usr/sbin/parted \
ac_cv_path_DMSETUP=/usr/sbin/dmsetup"

# Ensure that libvirt uses polkit rather than policykit, whether the host has
# pkcheck installed or not, and ensure the path is correct per our config.
CACHED_CONFIGUREVARS += "ac_cv_path_PKCHECK_PATH=${bindir}/pkcheck"

# Some other possible paths we are not yet setting
#ac_cv_path_RPCGEN=
#ac_cv_path_XSLTPROC=
#ac_cv_path_RADVD=
#ac_cv_path_UDEVSETTLE=
#ac_cv_path_EBTABLES_PATH=
#ac_cv_path_PKG_CONFIG=
#ac_cv_path_ac_pt_PKG_CONFIG
#ac_cv_path_POLKIT_AUTH=
#ac_cv_path_DTRACE=
#ac_cv_path_ISCSIADM=
#ac_cv_path_MSGFMT=
#ac_cv_path_GMSGFMT=
#ac_cv_path_XGETTEXT=
#ac_cv_path_MSGMERGE=
#ac_cv_path_SCRUB=
#ac_cv_path_PYTHON=

ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "${PN}-libvirtd ${PN}-virsh"

ALLOW_EMPTY_${PN}-libvirtd = "1"

FILES_${PN}-libvirtd = " \
	${sysconfdir}/init.d \
	${sysconfdir}/sysctl.d \
	${sysconfdir}/logrotate.d \
	${sysconfdir}/libvirt/libvirtd.conf \
        /usr/lib/sysctl.d/60-libvirtd.conf \
	${sbindir}/libvirtd \
	${systemd_unitdir}/system/* \
	${@base_contains('DISTRO_FEATURES', 'sysvinit', '', '${libexecdir}/libvirt-guests.sh', d)} \
        "

FILES_${PN}-virsh = "${bindir}/virsh"
FILES_${PN} += "${libdir}/libvirt/connection-driver \
	    ${datadir}/augeas \
	    ${datadir}/polkit-1"

FILES_${PN}-dbg += "${libdir}/libvirt/connection-driver/.debug ${libdir}/libvirt/lock-driver/.debug"
FILES_${PN}-staticdev += "${libdir}/*.a ${libdir}/libvirt/connection-driver/*.a ${libdir}/libvirt/lock-driver/*.a"

CONFFILES_${PN} += "${sysconfdir}/libvirt/libvirt.conf \
                    ${sysconfdir}/libvirt/lxc.conf \
                    ${sysconfdir}/libvirt/qemu-lockd.conf \
                    ${sysconfdir}/libvirt/qemu.conf \
                    ${sysconfdir}/libvirt/virt-login-shell.conf \
                    ${sysconfdir}/libvirt/virtlockd.conf"

CONFFILES_${PN}-libvirtd = "${sysconfdir}/logrotate.d/libvirt ${sysconfdir}/logrotate.d/libvirt.lxc \
                            ${sysconfdir}/logrotate.d/libvirt.qemu ${sysconfdir}/logrotate.d/libvirt.uml \
                            ${sysconfdir}/libvirt/libvirtd.conf \
                            /usr/lib/sysctl.d/libvirtd.conf"

INITSCRIPT_PACKAGES = "${PN}-libvirtd"
INITSCRIPT_NAME_${PN}-libvirtd = "libvirtd"
INITSCRIPT_PARAMS_${PN}-libvirtd = "defaults 72"

SYSTEMD_PACKAGES = "${PN}-libvirtd"
SYSTEMD_SERVICE_${PN}-libvirtd = " \
    libvirtd.socket \
    libvirtd.service \
    virtlockd.service \
    libvirt-guests.service \
    virtlockd.socket \
    "


PRIVATE_LIBS_${PN}-ptest = " \
	libvirt-lxc.so.0 \
	libvirt.so.0 \
	libvirt-qemu.so.0 \
	lockd.so \
	libvirt_driver_secret.so \
	libvirt_driver_nodedev.so \
	libvirt_driver_vbox.so \
	libvirt_driver_interface.so \
	libvirt_driver_uml.so \
	libvirt_driver_network.so \
	libvirt_driver_nwfilter.so \
	libvirt_driver_qemu.so \
	libvirt_driver_storage.so \
	libvirt_driver_lxc.so \
    "

# xen-minimal config
#PACKAGECONFIG ??= "xen libxl xen-inotify test remote libvirtd"

# full config
PACKAGECONFIG ??= "qemu yajl uml openvz vmware vbox esx iproute2 lxc test \
                   remote macvtap libvirtd netcf udev python ebtables \
                   ${@base_contains('DISTRO_FEATURES', 'selinux', 'selinux audit libcap-ng', '', d)} \
                   ${@base_contains('DISTRO_FEATURES', 'xen', 'xen libxl xen-inotify', '', d)} \
                   ${@base_contains('DISTRO_FEATURES', 'x11', 'polkit', '', d)} \
                  "

# enable,disable,depends,rdepends
#
PACKAGECONFIG[qemu] = "--with-qemu,--without-qemu,qemu,"
PACKAGECONFIG[yajl] = "--with-yajl,--without-yajl,yajl,yajl"
PACKAGECONFIG[xen] = "--with-xen,--without-xen,xen,"
PACKAGECONFIG[xenapi] = "--with-xenapi,--without-xenapi,,"
PACKAGECONFIG[libxl] = "--with-libxl=${STAGING_DIR_TARGET}/lib,--without-libxl,libxl,"
PACKAGECONFIG[xen-inotify] = "--with-xen-inotify,--without-xen-inotify,xen,"
PACKAGECONFIG[uml] = "--with-uml, --without-uml,,"
PACKAGECONFIG[openvz] = "--with-openvz,--without-openvz,,"
PACKAGECONFIG[vmware] = "--with-vmware,--without-vmware,,"
PACKAGECONFIG[phyp] = "--with-phyp,--without-phyp,,"
PACKAGECONFIG[vbox] = "--with-vbox,--without-vbox,,"
PACKAGECONFIG[esx] = "--with-esx,--without-esx,,"
PACKAGECONFIG[hyperv] = "--with-hyperv,--without-hyperv,,"
PACKAGECONFIG[polkit] = "--with-polkit,--without-polkit,polkit,polkit"
PACKAGECONFIG[lxc] = "--with-lxc,--without-lxc, lxc,"
PACKAGECONFIG[test] = "--with-test=yes,--with-test=no,,"
PACKAGECONFIG[remote] = "--with-remote,--without-remote,,"
PACKAGECONFIG[macvtap] = "--with-macvtap=yes,--with-macvtap=no,libnl,libnl"
PACKAGECONFIG[libvirtd] = "--with-libvirtd,--without-libvirtd,,"
PACKAGECONFIG[netcf] = "--with-netcf,--without-netcf,netcf,netcf"
PACKAGECONFIG[dtrace] = "--with-dtrace,--without-dtrace,,"
PACKAGECONFIG[udev] = "--with-udev --with-pciaccess,--without-udev,udev libpciaccess,"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux,"
PACKAGECONFIG[ebtables] = "ac_cv_path_EBTABLES_PATH=/sbin/ebtables,ac_cv_path_EBTABLES_PATH=,ebtables,ebtables"
PACKAGECONFIG[python] = ",,python,"
PACKAGECONFIG[sasl] = "--with-sasl,--without-sasl,cyrus-sasl,cyrus-sasl"
PACKAGECONFIG[iproute2] = "ac_cv_path_IP_PATH=/sbin/ip,ac_cv_path_IP_PATH=,iproute2,iproute2"
PACKAGECONFIG[numactl] = "--with-numactl,--without-numactl,numactl,"
PACKAGECONFIG[fuse] = "--with-fuse,--without-fuse,fuse,"
PACKAGECONFIG[audit] = "--with-audit,--without-audit,audit,"
PACKAGECONFIG[libcap-ng] = "--with-capng,--without-capng,libcap-ng,"

# Enable the Python tool support
require libvirt-python.inc

do_install_append() {
	install -d ${D}/etc/init.d
	install -d ${D}/etc/libvirt

	install -m 0755 ${WORKDIR}/libvirtd.sh ${D}/etc/init.d/libvirtd
	install -m 0644 ${WORKDIR}/libvirtd.conf ${D}/etc/libvirt/libvirtd.conf

	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
	    # This will wind up in the libvirtd package, but will NOT be invoked by default.
	    #
	    mv ${D}/${libexecdir}/libvirt-guests.sh ${D}/${sysconfdir}/init.d
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
	    # This variable is used by libvirtd.service to start libvirtd in the right mode
	    sed -i '/#LIBVIRTD_ARGS="--listen"/a LIBVIRTD_ARGS="--listen --daemon"' ${D}/${sysconfdir}/sysconfig/libvirtd

	    # We can't use 'notify' when we don't support 'sd_notify' dbus capabilities.
	    sed -i -e 's/Type=notify/Type=forking/' \
	           -e '/Type=forking/a PIDFile=${localstatedir}/run/libvirtd.pid' \
		   ${D}/${systemd_unitdir}/system/libvirtd.service
	fi

	# The /var/run/libvirt directories created by the Makefile
	# are wiped out in volatile, we need to create these at boot.
	rm -rf ${D}${localstatedir}/run
	install -d ${D}${sysconfdir}/default/volatiles
	echo "d root root 0755 ${localstatedir}/run/libvirt none" \
	     > ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 ${localstatedir}/run/libvirt/lockd none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 ${localstatedir}/run/libvirt/lxc none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 ${localstatedir}/run/libvirt/network none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt
	echo "d root root 0755 ${localstatedir}/run/libvirt/qemu none" \
	     >> ${D}${sysconfdir}/default/volatiles/99_libvirt

	# Add hook support for libvirt
	mkdir -p ${D}/etc/libvirt/hooks 

	# remove .la references to our working diretory
	for i in `find ${D}${libdir} -type f -name *.la`; do
	    sed -i -e 's#-L${B}/src/.libs##g' $i
	done
}

EXTRA_OECONF += " \
    --with-init-script=systemd \
    "

EXTRA_OEMAKE = "BUILD_DIR=${B} DEST_DIR=${D}${PTEST_PATH} PTEST_DIR=${PTEST_PATH}"

do_compile_ptest() {
	oe_runmake -C tests buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C tests install-ptest

	# remove .la files for ptest, they aren't required and can trigger QA errors
	for i in `find ${D}${PTEST_PATH} -type f -name *.la`; do
                rm -f $i
	done
}

pkg_postinst_libvirt() {
        if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
                /etc/init.d/populate-volatile.sh update
        fi
}

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}
