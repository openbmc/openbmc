SUMMARY = "userspace utilities for kernel nfs"
DESCRIPTION = "The nfs-utils package provides a daemon for the kernel \
NFS server and related tools."
HOMEPAGE = "http://nfs.sourceforge.net/"
SECTION = "console/network"

LICENSE = "MIT & GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=95f3a93a5c3c7888de623b46ea085a84"

# util-linux for libblkid
DEPENDS = "libcap libnfsidmap libevent util-linux sqlite3 libtirpc"
RDEPENDS_${PN}-client = "rpcbind bash"
RDEPENDS_${PN} = "${PN}-client bash"
RRECOMMENDS_${PN} = "kernel-module-nfsd"

inherit useradd

USERADD_PACKAGES = "${PN}-client"
USERADD_PARAM_${PN}-client = "--system  --home-dir /var/lib/nfs \
			      --shell /bin/false --user-group rpcuser"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/nfs-utils/${PV}/nfs-utils-${PV}.tar.xz \
           file://0001-configure-Allow-to-explicitly-disable-nfsidmap.patch \
           file://nfs-utils-1.2.3-sm-notify-res_init.patch \
           file://nfsserver \
           file://nfscommon \
           file://nfs-utils.conf \
           file://nfs-server.service \
           file://nfs-mountd.service \
           file://nfs-statd.service \
           file://proc-fs-nfsd.mount \
           file://nfs-utils-Do-not-pass-CFLAGS-to-gcc-while-building.patch \
           file://nfs-utils-debianize-start-statd.patch \
           file://0001-nfs-utils-statd-fix-a-segfault-caused-by-improper-us.patch \
           file://bugfix-adjust-statd-service-name.patch \
"

SRC_URI[md5sum] = "cd6b568c2e9301cc3bfac09d87fbbc0b"
SRC_URI[sha256sum] = "700d689c5622c87953c34102e5befafc4d3c811e676852238f0dd79c9c0c084d"

# Only kernel-module-nfsd is required here (but can be built-in)  - the nfsd module will
# pull in the remainder of the dependencies.

INITSCRIPT_PACKAGES = "${PN} ${PN}-client"
INITSCRIPT_NAME = "nfsserver"
INITSCRIPT_PARAMS = "defaults"
INITSCRIPT_NAME_${PN}-client = "nfscommon"
INITSCRIPT_PARAMS_${PN}-client = "defaults 19 21"

inherit autotools-brokensep update-rc.d systemd pkgconfig

SYSTEMD_SERVICE_${PN} = "nfs-server.service nfs-mountd.service"
SYSTEMD_SERVICE_${PN}-client = "nfs-statd.service"
SYSTEMD_AUTO_ENABLE = "disable"

# --enable-uuid is need for cross-compiling
EXTRA_OECONF = "--with-statduser=rpcuser \
                --enable-mountconfig \
                --enable-libmount-mount \
                --disable-nfsv41 \
                --enable-uuid \
                --disable-gss \
                --disable-nfsdcltrack \
                --with-statdpath=/var/lib/nfs/statd \
               "

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG_remove_libc-musl = "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers,tcp-wrappers"
PACKAGECONFIG[nfsidmap] = "--enable-nfsidmap,--disable-nfsidmap,keyutils"

INHIBIT_AUTO_STAGE = "1"

PACKAGES =+ "${PN}-client ${PN}-stats"

CONFFILES_${PN}-client += "${localstatedir}/lib/nfs/etab \
			   ${localstatedir}/lib/nfs/rmtab \
			   ${localstatedir}/lib/nfs/xtab \
			   ${localstatedir}/lib/nfs/statd/state \
			   ${sysconfdir}/nfsmount.conf"

FILES_${PN}-client = "${base_sbindir}/*mount.nfs* ${sbindir}/*statd \
		      ${sbindir}/rpc.idmapd ${sbindir}/sm-notify \
		      ${sbindir}/showmount ${sbindir}/nfsstat \
		      ${localstatedir}/lib/nfs \
		      ${sysconfdir}/nfs-utils.conf \
		      ${sysconfdir}/nfsmount.conf \
		      ${sysconfdir}/init.d/nfscommon \
		      ${systemd_unitdir}/system/nfs-statd.service"
FILES_${PN}-stats = "${sbindir}/mountstats ${sbindir}/nfsiostat"
RDEPENDS_${PN}-stats = "python"

FILES_${PN} += "${systemd_unitdir}"

do_configure_prepend() {
        sed -i -e 's,sbindir = /sbin,sbindir = ${base_sbindir},g' \
            ${S}/utils/mount/Makefile.am

        sed -i -e 's,sbindir = /sbin,sbindir = ${base_sbindir},g' \
            ${S}/utils/osd_login/Makefile.am
}

# Make clean needed because the package comes with
# precompiled 64-bit objects that break the build
do_compile_prepend() {
	make clean
}

do_install_append () {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/nfsserver ${D}${sysconfdir}/init.d/nfsserver
	install -m 0755 ${WORKDIR}/nfscommon ${D}${sysconfdir}/init.d/nfscommon

	install -m 0755 ${WORKDIR}/nfs-utils.conf ${D}${sysconfdir}
	install -m 0755 ${S}/utils/mount/nfsmount.conf ${D}${sysconfdir}

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/nfs-server.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${WORKDIR}/nfs-mountd.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${WORKDIR}/nfs-statd.service ${D}${systemd_unitdir}/system/
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@SYSCONFDIR@,${sysconfdir},g' \
		${D}${systemd_unitdir}/system/*.service
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
	    install -d ${D}${sysconfdir}/modules-load.d
	    echo "nfsd" > ${D}${sysconfdir}/modules-load.d/nfsd.conf
	    install -m 0644 ${WORKDIR}/proc-fs-nfsd.mount ${D}${systemd_unitdir}/system/
	    install -d ${D}${systemd_unitdir}/system/sysinit.target.wants/
	    ln -sf ../proc-fs-nfsd.mount ${D}${systemd_unitdir}/system/sysinit.target.wants/proc-fs-nfsd.mount
	fi

	# kernel code as of 3.8 hard-codes this path as a default
	install -d ${D}/var/lib/nfs/v4recovery

	# chown the directories and files
	chown -R rpcuser:rpcuser ${D}${localstatedir}/lib/nfs/statd
	chmod 0644 ${D}${localstatedir}/lib/nfs/statd/state

	# the following are built by CC_FOR_BUILD
	rm -f ${D}${sbindir}/rpcdebug
	rm -f ${D}${sbindir}/rpcgen
	rm -f ${D}${sbindir}/locktest
}
