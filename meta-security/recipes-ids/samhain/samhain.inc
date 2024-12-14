DESCRIPTION = "Provides file integrity checking and log file monitoring/analysis"
HOMEPAGE    = "http://www.la-samhna.de/samhain/"
LICENSE     = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ca43cbc842c2336e835926c2166c28b"

PV = "4.4.10"

SRC_URI = "https://la-samhna.de/archive/samhain_signed-${PV}.tar.gz \
           file://${INITSCRIPT_NAME}.init \
           file://${INITSCRIPT_NAME}.default \
           file://samhain.service \
           file://samhain-mips64-aarch64-dnmalloc-hash-fix.patch \
           file://samhain-samhainrc.patch \
           file://samhain-samhainrc-fix-files-dirs-path.patch \
           file://samhain-pid-path.patch \
           file://samhain-sha256-big-endian.patch \
           file://samhain-configure-add-option-for-ps.patch \
           file://samhain-avoid-searching-host-for-postgresql.patch \
           file://samhain-add-LDFLAGS-variable-for-samhain_setpwd.patch \
           file://fix-build-with-new-version-attr.patch \
           file://samhain-fix-initializer-element-is-not-constant.patch \
           "

SRC_URI[sha256sum] = "ae6ee8eff3cb111b7fc14a57bcc258443dd0bcf1bfacfdf229935ed053c1ce3d"

UPSTREAM_CHECK_URI = "https://www.la-samhna.de/samhain/archive.html"
UPSTREAM_CHECK_REGEX = "samhain_signed-(?P<pver>(\d+(\.\d+)+))\.tar"

S = "${UNPACKDIR}/samhain-${PV}"

inherit autotools-brokensep update-rc.d pkgconfig systemd

SAMHAIN_PORT ??= "49777"
SAMHAIN_SERVER ??= "NULL"

INITSCRIPT_NAME = "${BPN}"
INITSCRIPT_PARAMS ?= "defaults"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${INITSCRIPT_NAME}.service"
SYSTEMD_AUTO_ENABLE = "disable"

# supports mysql|postgresql|oracle|odbc but postgresql is the only one available

PACKAGECONFIG ??= "postgresql ps \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux audit', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)} \
"

PACKAGECONFIG[postgresql]  = "--with-database=postgresql --enable-xml-log PGSQL_INC_DIR=${STAGING_INCDIR} PGSQL_LIB_DIR=${STAGING_LIBDIR}, , postgresql"
PACKAGECONFIG[suidcheck]  = "--enable-suidcheck, , "
PACKAGECONFIG[logwatch]  = "--enable-login-watch, , "
PACKAGECONFIG[mounts]  = "--enable-mounts-check, , "
PACKAGECONFIG[userfiles]  = "--enable-userfiles, , "
PACKAGECONFIG[ipv6]  = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[selinux] = "--enable-selinux, --disable-selinux, libselinux attr"
PACKAGECONFIG[acl] = " --enable-posix-acl , --disable-posix-acl, acl"
PACKAGECONFIG[audit] = "ac_cv_header_auparse_h=yes,ac_cv_header_auparse_h=no,audit"
PACKAGECONFIG[ps] = "--with-ps-path=${base_bindir}/ps,,,procps"

EXTRA_OECONF += "INSTALL='install -p'"
EXTRA_OEMAKE:append:aarch64 = " CPPFLAGS+=-DCONFIG_ARCH_AARCH64=1"
EXTRA_OEMAKE:append:mips64 = " CPPFLAGS+=-DCONFIG_ARCH_MIPS64=1"

do_unpack_samhain() {
    cd ${UNPACKDIR}
    tar -xzvf samhain-${PV}.tar.gz -C ${UNPACKDIR}
}

python do_unpack:append() {
    bb.build.exec_func('do_unpack_samhain', d)
}

do_configure:prepend:arm() {
    export sh_cv___va_copy=yes
}

do_configure:prepend:aarch64() {
    export sh_cv___va_copy=yes
}

# If we use oe_runconf in do_configure() it will by default
# use the prefix --oldincludedir=/usr/include which is not
# recognized by Samhain's configure script and would invariably
# throw back the error "unrecognized option: --oldincludedir=/usr/include"
do_configure:prepend () {
    cat << EOF > ${S}/config-site.${BP}
ssp_cv_lib=no
sh_cv_va_copy=yes
EOF
    export CONFIG_SITE=${S}/config-site.${BP}
    # remove the buildpath
    sed -i -e 's;mydefarg;mydefargholder;g' ${S}/scripts/samhain.ebuild.in
    sed -i -e 's;mydefarg;mydefargholder;g' ${S}/scripts/samhain.ebuild-light.in
}

do_configure () {
	autoconf -f
	./configure \
	--build=${BUILD_SYS} \
	--host=${HOST_SYS} \
	--target=${TARGET_SYS} \
	--prefix=${prefix} \
	--exec_prefix=${exec_prefix} \
	--bindir=${bindir} \
	--sbindir=${sbindir} \
	--libexecdir=${libexecdir} \
	--datadir=${datadir} \
	--sysconfdir=${sysconfdir} \
	--sharedstatedir=${sharedstatedir} \
	--localstatedir=${localstatedir} \
	--libdir=${libdir} \
	--includedir=${includedir} \
	--infodir=${infodir} \
	--mandir=${mandir} \
	--enable-network=${SAMHAIN_MODE} \
	--with-pid-file=${localstatedir}/run/samhain.pid \
	--with-data-file=${localstatedir}/lib/samhain/samhain_file \
	${EXTRA_OECONF}
}

do_compile:prepend:libc-musl () {
	sed -i 's/^#define HAVE_MALLOC_H.*//' ${B}/config.h
}

# Install the init script, it's default file, and the extraneous
# documentation.
do_install:append () {
	oe_runmake install DESTDIR='${D}' INSTALL=install-boot

	install -D -m 755 ${UNPACKDIR}/${INITSCRIPT_NAME}.init \
		${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}

	install -D -m 755 ${UNPACKDIR}/${INITSCRIPT_NAME}.default \
		${D}${sysconfdir}/default/${INITSCRIPT_NAME}

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		if [ "${SAMHAIN_MODE}" = "no" ]; then
		    install -D -m 0644 ${UNPACKDIR}/samhain.service ${D}/${systemd_system_unitdir}/samhain.service
		else
		    install -D -m 0644 ${UNPACKDIR}/samhain.service ${D}/${systemd_system_unitdir}/${BPN}.service
		fi
		install -D -m 0755 ${UNPACKDIR}/${BPN}.init ${D}/${libexecdir}/${BPN}
		sed -i -e 's,@LIBDIR@,${libexecdir},' \
		       -e 's,@SAMHAIN_HELPER@,${BPN},' \
		       -e 's,@MODE_NAME@,${MODE_NAME},' \
		       ${D}${systemd_system_unitdir}/samhain*.service
    fi

	install -d ${D}${docdir}/${BPN}
	cp -r docs/* ${D}${docdir}/${BPN}
	cp -r scripts ${D}${docdir}/${BPN}
	install -d -m 755 ${D}${localstatedir}/samhain

	# Prevent QA warnings about installed ${localstatedir}/run
	if [ -d ${D}${localstatedir}/run ]; then
		rmdir ${D}${localstatedir}/run
	fi

	rm -rf ${D}${localstatedir}/log
}

FILES:${PN} += "${systemd_system_unitdir}"
