SUMMARY = "Universal Addresses to RPC Program Number Mapper"
DESCRIPTION = "The rpcbind utility is a server that converts RPC \
               program numbers into universal addresses."
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/rpcbind/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=201237&atid=976751"
DEPENDS = "libtirpc quota"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b46486e4c4a416602693a711bb5bfa39 \
                    file://src/rpcinfo.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24"

SRC_URI = "${SOURCEFORGE_MIRROR}/rpcbind/rpcbind-${PV}.tar.bz2 \
           file://init.d \
           file://rpcbind.conf \
           file://rpcbind.tmpfiles \
           file://rpcbind.systemd \
           file://rpcbind_add_option_to_fix_port_number.patch \
           file://0001-systemd-use-EnvironmentFile.patch \
          "
SRC_URI[sha256sum] = "f6edf8cdf562aedd5d53b8bf93962d61623292bfc4d47eedd3f427d84d06f37e"

inherit autotools update-rc.d systemd pkgconfig update-alternatives sourceforge-releases

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"

INITSCRIPT_NAME = "rpcbind"
INITSCRIPT_PARAMS = "start 12 2 3 4 5 . stop 60 0 1 6 ."

SYSTEMD_SERVICE:${PN} = "rpcbind.service rpcbind.socket"

inherit useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir / \
                       --shell /bin/false --user-group rpc"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}/, \
                          --without-systemdsystemunitdir, \
                          systemd \
"

EXTRA_OECONF += " --enable-warmstarts --with-rpcuser=rpc --with-statedir=${runtimedir}/rpcbind"

do_install:append () {
	install -d ${D}${sysconfdir}/init.d
	sed -e 's,/etc/,${sysconfdir}/,g' \
		-e 's,/sbin/,${sbindir}/,g' \
		${UNPACKDIR}/init.d > ${D}${sysconfdir}/init.d/rpcbind
	chmod 0755 ${D}${sysconfdir}/init.d/rpcbind
	install -m 0644 ${UNPACKDIR}/rpcbind.conf ${D}${sysconfdir}/rpcbind.conf

	install -d ${D}${sysconfdir}/tmpfiles.d
	install -m 0644 ${UNPACKDIR}/rpcbind.tmpfiles ${D}${sysconfdir}/tmpfiles.d/rpcbind.conf

	install -d ${D}${systemd_system_unitdir}/rpcbind.service.d
	install -m 0644 ${UNPACKDIR}/rpcbind.systemd ${D}${systemd_system_unitdir}/rpcbind.service.d/rpcbind.conf
}

FILES:${PN} += "${systemd_system_unitdir}/rpcbind.service.d/rpcbind.conf"

ALTERNATIVE:${PN} = "rpcinfo"
ALTERNATIVE_LINK_NAME[rpcinfo] = "${bindir}/rpcinfo"
