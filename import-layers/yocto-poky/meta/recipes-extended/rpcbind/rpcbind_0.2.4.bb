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
           file://rpcbind.conf \
           file://rpcbind.socket \
           file://rpcbind.service \
           file://0001-rpcbind-pair-all-svc_getargs-calls-with-svc_freeargs.patch \
          "
SRC_URI[md5sum] = "cf10cd41ed8228fc54c316191c1f07fe"
SRC_URI[sha256sum] = "074a9a530dc7c11e0d905aa59bcb0847c009313f02e98d3d798aa9568f414c66"

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

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
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
