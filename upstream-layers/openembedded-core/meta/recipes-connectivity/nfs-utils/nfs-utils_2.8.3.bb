SUMMARY = "userspace utilities for kernel nfs"
DESCRIPTION = "The nfs-utils package provides a daemon for the kernel \
NFS server and related tools."
HOMEPAGE = "http://nfs.sourceforge.net/"
SECTION = "console/network"

LICENSE = "MIT & GPL-2.0-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=95f3a93a5c3c7888de623b46ea085a84"

# util-linux for libblkid
DEPENDS = "libcap libevent util-linux sqlite3 libtirpc libxml2"
RDEPENDS:${PN} = "${PN}-client"
RRECOMMENDS:${PN} = "kernel-module-nfsd"

inherit useradd

USERADD_PACKAGES = "${PN}-client"
USERADD_PARAM:${PN}-client = "--system  --home-dir /var/lib/nfs \
			      --shell /bin/false --user-group rpcuser"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/nfs-utils/${PV}/nfs-utils-${PV}.tar.xz \
           file://nfsserver \
           file://nfscommon \
           file://0001-locktest-Makefile.am-Do-not-use-build-flags.patch \
           file://0004-Use-nogroup-for-nobody-group.patch \
           file://0005-find-OE-provided-Kerberos.patch \
           "

SRC_URI[sha256sum] = "11e7c5847a8423a72931c865bd9296e7fd56ff270a795a849183900961711725"

# Only kernel-module-nfsd is required here (but can be built-in)  - the nfsd module will
# pull in the remainder of the dependencies.

INITSCRIPT_PACKAGES = "${PN} ${PN}-client"
INITSCRIPT_NAME = "nfsserver"
INITSCRIPT_PARAMS = "defaults"
INITSCRIPT_NAME:${PN}-client = "nfscommon"
INITSCRIPT_PARAMS:${PN}-client = "defaults 19 21"

inherit autotools-brokensep update-rc.d systemd pkgconfig

SYSTEMD_PACKAGES = "${PN} ${PN}-client"
SYSTEMD_SERVICE:${PN} = "nfs-server.service"
SYSTEMD_SERVICE:${PN}-client = "nfs-client.target"

# --enable-uuid is need for cross-compiling
EXTRA_OECONF = "--with-statduser=rpcuser \
                --enable-mountconfig \
                --enable-libmount-mount \
                --enable-uuid \
                --with-statdpath=/var/lib/nfs/statd \
                --with-pluginpath=${libdir}/libnfsidmap \
                --with-rpcgen=${HOSTTOOLS_DIR}/rpcgen \
               "

LDFLAGS += "-lsqlite3 -levent"

PACKAGECONFIG ??= "tcp-wrappers \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)} \
"

PACKAGECONFIG:remove:libc-musl = "tcp-wrappers"
#krb5 is available in meta-oe
PACKAGECONFIG[gssapi] = "--with-krb5=${STAGING_EXECPREFIXDIR} --enable-gss --enable-svcgss,--disable-gss --disable-svcgss,krb5"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers,tcp-wrappers"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
# libdevmapper is available in meta-oe
PACKAGECONFIG[nfsv41] = "--enable-nfsv41,--disable-nfsv41,libdevmapper,libdevmapper"
# keyutils is available in meta-oe
PACKAGECONFIG[nfsv4] = "--enable-nfsv4 --enable-nfsdcltrack,--disable-nfsv4 --disable-nfsdcltrack,keyutils,python3-core"
PACKAGECONFIG[nfsdctl] = "--enable-nfsdctl,--disable-nfsdctl,libnl readline,"
PACKAGECONFIG[systemd] = "--with-systemd=${systemd_unitdir}/system,--without-systemd"

PACKAGES =+ "${PN}-client ${PN}-mount ${PN}-stats ${PN}-rpcctl"

CONFFILES:${PN}-client += "${localstatedir}/lib/nfs/etab \
			   ${localstatedir}/lib/nfs/rmtab \
			   ${localstatedir}/lib/nfs/xtab \
			   ${localstatedir}/lib/nfs/statd/state \
			   ${sysconfdir}/idmapd.conf \
			   ${sysconfdir}/nfs.conf \
			   ${sysconfdir}/nfsmount.conf"

FILES:${PN}-client = "${sbindir}/*statd \
		      ${sbindir}/rpc.idmapd ${sbindir}/sm-notify \
		      ${sbindir}/showmount ${sbindir}/nfsstat \
		      ${sbindir}/rpc.gssd \
		      ${sbindir}/nfsconf \
		      ${libdir}/libnfsidmap.so.* \
		      ${libdir}/libnfsidmap/*.so \
		      ${libexecdir}/nfsrahead \
		      ${localstatedir}/lib/nfs \
		      ${sysconfdir}/idmapd.conf \
		      ${sysconfdir}/init.d/nfscommon \
		      ${sysconfdir}/nfs.conf \
		      ${sysconfdir}/nfsmount.conf \
		      ${systemd_system_unitdir}/auth-rpcgss-module.service \
		      ${systemd_system_unitdir}/nfs-client.target \
		      ${systemd_system_unitdir}/nfs-idmapd.service \
		      ${systemd_system_unitdir}/nfs-statd.service \
		      ${systemd_system_unitdir}/nfscommon.service \
		      ${systemd_system_unitdir}/rpc-gssd.service \
		      ${systemd_system_unitdir}/rpc-statd-notify.service \
		      ${systemd_system_unitdir}/rpc-statd.service \
		      ${systemd_system_unitdir}/rpc_pipefs.target \
		      ${systemd_system_unitdir}/var-lib-nfs-rpc_pipefs.mount \
		      ${nonarch_libdir}/udev/rules.d/*"
RDEPENDS:${PN}-client = "${PN}-mount rpcbind"

FILES:${PN}-mount = "${base_sbindir}/*mount.nfs*"

FILES:${PN}-stats = "${sbindir}/mountstats ${sbindir}/nfsiostat ${sbindir}/nfsdclnts"
RDEPENDS:${PN}-stats = "python3-core"

FILES:${PN}-rpcctl = "${sbindir}/rpcctl"
RDEPENDS:${PN}-rpcctl = "python3-core"

FILES:${PN}-staticdev += "${libdir}/libnfsidmap/*.a"

FILES:${PN} += "${systemd_unitdir} ${libdir}/libnfsidmap/ ${nonarch_libdir}/modprobe.d"

do_configure:prepend() {
	sed -i -e 's,sbindir = /sbin,sbindir = ${base_sbindir},g' \
		-e 's,udev_rulesdir = /usr/lib/udev/rules.d/,udev_rulesdir = ${nonarch_base_libdir}/udev/rules.d/,g' \
		${S}/utils/mount/Makefile.am ${S}/utils/nfsdcltrack/Makefile.am \
		${S}/systemd/Makefile.am ${S}/tools/nfsrahead/Makefile.am
}

# Make clean needed because the package comes with
# precompiled 64-bit objects that break the build
do_compile:prepend() {
	make clean
}

# Works on systemd only
HIGH_RLIMIT_NOFILE ??= "4096"

do_install:append () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${UNPACKDIR}/nfsserver ${D}${sysconfdir}/init.d/nfsserver
	install -m 0755 ${UNPACKDIR}/nfscommon ${D}${sysconfdir}/init.d/nfscommon

	install -m 0644 ${S}/support/nfsidmap/idmapd.conf ${D}${sysconfdir}
	install -m 0644 ${S}/nfs.conf ${D}${sysconfdir}

	install -d ${D}${systemd_system_unitdir}
	# Retain historical service name so old scripts keep working
	ln -s rpc-statd.service ${D}${systemd_system_unitdir}/nfs-statd.service
	# Add compatibility symlinks for the sysvinit scripts
	ln -s nfs-server.service ${D}${systemd_system_unitdir}/nfsserver.service
	ln -s /dev/null ${D}${systemd_system_unitdir}/nfscommon.service

	# kernel code as of 3.8 hard-codes this path as a default
	install -d ${D}/var/lib/nfs/v4recovery

	# chown the directories and files
	chown -R rpcuser:rpcuser ${D}${localstatedir}/lib/nfs/statd
	chmod 0644 ${D}${localstatedir}/lib/nfs/statd/state
}
