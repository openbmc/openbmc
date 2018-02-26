SUMMARY = "Target Communication Framework for the Eclipse IDE"
HOMEPAGE = "http://wiki.eclipse.org/TCF"
BUGTRACKER = "https://bugs.eclipse.org/bugs/"

LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://edl-v10.html;md5=522a390a83dc186513f0500543ad3679"

SRCREV = "2dddd5f440a15d5406032fac20f206484513646f"
PV = "1.4.0+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
SRC_URI = "git://git.eclipse.org/gitroot/tcf/org.eclipse.tcf.agent;branch=1.4_neon_bugfix \
           file://fix_ranlib.patch \
           file://ldflags.patch \
           file://0001-canonicalize_file_name-is-specific-to-glibc.patch;striplevel=2 \
           file://tcf-agent.init \
           file://tcf-agent.service \
          "
UPSTREAM_VERSION_UNKNOWN = "1"

DEPENDS = "util-linux openssl"
RDEPENDS_${PN} = "bash"

S = "${WORKDIR}/git/agent"

inherit update-rc.d systemd

SYSTEMD_SERVICE_${PN} = "tcf-agent.service"

INITSCRIPT_NAME = "tcf-agent"
INITSCRIPT_PARAMS = "start 99 3 5 . stop 20 0 1 2 6 ."

# mangling needed for make
MAKE_ARCH = "`echo ${TARGET_ARCH} | sed s,i.86,i686, | sed s,aarch64,a64,`"
MAKE_OS = "`echo ${TARGET_OS} | sed s,^linux.*,GNU/Linux,`"

EXTRA_OEMAKE = "MACHINE=${MAKE_ARCH} OPSYS=${MAKE_OS} 'CC=${CC}' 'AR=${AR}'"

LCL_STOP_SERVICES = "-DSERVICE_RunControl=0 -DSERVICE_Breakpoints=0 \
    -DSERVICE_Memory=0 -DSERVICE_Registers=0 -DSERVICE_MemoryMap=0 \
    -DSERVICE_StackTrace=0 -DSERVICE_Expressions=0"


# These features don't compile for several cases.
#
CFLAGS_append_mips = " ${LCL_STOP_SERVICES}"
CFLAGS_append_mips64 = " ${LCL_STOP_SERVICES}"
CFLAGS_append_libc-musl = " ${LCL_STOP_SERVICES}"
CFLAGS_append_powerpc64 = " ${LCL_STOP_SERVICES}"

do_install() {
	oe_runmake install INSTALLROOT=${D}
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/tcf-agent.init ${D}${sysconfdir}/init.d/tcf-agent
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/tcf-agent.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/tcf-agent.service
}

