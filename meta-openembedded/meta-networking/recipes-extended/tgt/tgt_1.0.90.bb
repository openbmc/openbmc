DESCRIPTION = "Linux SCSI target framework (tgt)"
HOMEPAGE = "http://stgt.sourceforge.net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://scripts/tgtd.spec;beginline=7;endline=7;md5=21c19ea7dad04648b9c2f791b6e29b4c"
DEPENDS = "sg3-utils libaio"

SRCREV = "df991fa788e22cd8b8e3013cfae306988c19c3d0"

SRC_URI = "git://github.com/fujita/tgt.git;branch=master;protocol=https \
	      file://0001-Correct-the-path-of-header-files-check-in-Yocto-buil.patch \
        file://0001-usr-Makefile-WARNING-fix.patch \
        file://usr-Makefile-apply-LDFLAGS-to-all-executables.patch \
        file://musl-__wordsize.patch \
"
SRC_URI += "file://tgtd.init \
            file://tgtd.service \
            file://tgtd \
"

S = "${WORKDIR}/git"

CONFFILES:${PN} += "${sysconfdir}/tgt/targets.conf"

inherit update-rc.d systemd

SYSTEMD_SERVICE:${PN} = "tgtd.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

CFLAGS += ' -I. -DUSE_SIGNALFD -DUSE_TIMERFD -D_GNU_SOURCE -DTGT_VERSION=\\"1.0.63\\" -DBSDIR=\\"${libdir}/backing-store\\"'

CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"
#do_compile() {
#    oe_runmake SYSROOT="${STAGING_DIR_TARGET}" -e programs conf scripts
#}
EXTRA_OEMAKE = "-e programs conf scripts"

do_install() {
    oe_runmake -e DESTDIR="${D}" install-programs install-conf install-scripts

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/tgtd.init ${D}${sysconfdir}/init.d/tgtd
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/tgtd.service ${D}${systemd_unitdir}/system/tgtd.service
        install -d ${D}${sysconfdir}/sysconfig
        install -m 0644 ${WORKDIR}/tgtd ${D}${sysconfdir}/sysconfig/tgtd
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/tgtd.service
        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' ${D}${systemd_unitdir}/system/tgtd.service
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/tgtd.service
    fi
}

FILES:${PN} += "${systemd_unitdir}/system/tgtd.service \
                ${sysconfdir}/sysconfig/tgtd \
"

RDEPENDS:${PN} = " \
    bash \
    libaio \
    libconfig-general-perl \
    perl \
    perl-module-english \
    perl-module-tie-hash-namedcapture \
    perl-module-xsloader \
    perl-module-carp \
    perl-module-exporter \
    perl-module-errno \
    perl-module-exporter-heavy \
    perl-module-symbol \
    perl-module-selectsaver \
    perl-module-dynaloader \
    perl-module-carp-heavy \
    perl-module-filehandle \
    perl-module-feature \
    perl-module-overload \
    perl-module-fcntl \
    perl-module-io \
    perl-module-io-file \
    perl-module-io-handle \
    perl-module-io-seekable \
    perl-module-file-glob \
    perl-module-base \
    perl-module-encoding-warnings \
    perl-module-file-spec-unix \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-getopt-long \
    perl-module-constant \
    "
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "tgtd"

