SUMMARY = "Universal Addresses to RPC Program Number Mapper"
DESCRIPTION = "The rpcbind utility is a server that converts RPC \
               program numbers into universal addresses."
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/rpcbind/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=201237&atid=976751"
DEPENDS = "libtirpc quota"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b46486e4c4a416602693a711bb5bfa39 \
                    file://src/rpcinfo.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

SRC_URI = "${SOURCEFORGE_MIRROR}/rpcbind/rpcbind-${PV}.tar.bz2 \
           file://init.d \
           file://0001-Avoid-use-of-glibc-sys-cdefs.h-header.patch \
           file://remove-sys-queue.patch \
           file://0001-handle_reply-Don-t-use-the-xp_auth-pointer-directly.patch \
           ${UCLIBCPATCHES} \
           ${MUSLPATCHES} \
           file://rpcbind.conf \
           file://rpcbind.socket \
           file://rpcbind.service \
           file://cve-2015-7236.patch \
          "
MUSLPATCHES_libc-musl = "file://musl-sunrpc.patch"

UCLIBCPATCHES ?= ""
MUSLPATCHES ?= ""

SRC_URI[md5sum] = "c8875246b2688a1adfbd6ad43480278d"
SRC_URI[sha256sum] = "9897823a9d820ea011d9ea02054d5ab99469b9ca5346265fee380713c8fed27b"

inherit autotools update-rc.d systemd pkgconfig

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"

INITSCRIPT_NAME = "rpcbind"
INITSCRIPT_PARAMS = "start 12 2 3 4 5 . stop 60 0 1 6 ."

SYSTEMD_SERVICE_${PN} = "rpcbind.service"

inherit useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --home-dir / \
                       --shell /bin/false --user-group rpc"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/, \
                          --without-systemdsystemunitdir, \
                          systemd \
"

EXTRA_OECONF += " --enable-warmstarts --with-rpcuser=rpc"

do_install_append () {
	mv ${D}${bindir} ${D}${sbindir}

	install -d ${D}${sysconfdir}/init.d
	sed -e 's,/etc/,${sysconfdir}/,g' \
		-e 's,/sbin/,${sbindir}/,g' \
		${WORKDIR}/init.d > ${D}${sysconfdir}/init.d/rpcbind
	chmod 0755 ${D}${sysconfdir}/init.d/rpcbind

	install -m 0755 ${WORKDIR}/rpcbind.conf ${D}${sysconfdir}
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/rpcbind.socket ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/rpcbind.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@SYSCONFDIR@,${sysconfdir},g' \
		${D}${systemd_unitdir}/system/rpcbind.service
}
