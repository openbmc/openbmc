SUMMARY = "Target Communication Framework for the Eclipse IDE"
HOMEPAGE = "http://wiki.eclipse.org/TCF"
DESCRIPTION = "TCF is a vendor-neutral, lightweight, extensible network protocol mainly for communicating with embedded systems (targets)."
BUGTRACKER = "https://bugs.eclipse.org/bugs/"

LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://edl-v10.html;md5=522a390a83dc186513f0500543ad3679"

SRCREV = "1f11747e83ebf4f53e8d17f430136f92ec378709"
PV = "1.8.0+git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
SRC_URI = "git://git.eclipse.org/r/tcf/org.eclipse.tcf.agent.git;protocol=https;branch=master \
           file://ldflags.patch \
           file://tcf-agent.init \
           file://tcf-agent.service \
          "

DEPENDS = "util-linux openssl"
RDEPENDS:${PN} = "bash"

S = "${WORKDIR}/git/agent"

inherit update-rc.d systemd

SYSTEMD_SERVICE:${PN} = "tcf-agent.service"

INITSCRIPT_NAME = "tcf-agent"
INITSCRIPT_PARAMS = "start 99 3 5 . stop 20 0 1 2 6 ."

# mangling needed for make
MAKE_ARCH = "`echo ${TARGET_ARCH} | sed s,i.86,i686, | sed s,aarch64.*,a64, | sed s,armeb,arm,`"
MAKE_OS = "`echo ${TARGET_OS} | sed s,^linux.*,GNU/Linux,`"

EXTRA_OEMAKE = "MACHINE=${MAKE_ARCH} OPSYS=${MAKE_OS} 'CC=${CC}' 'AR=${AR}'"

LCL_STOP_SERVICES = "-DSERVICE_RunControl=0 -DSERVICE_Breakpoints=0 \
    -DSERVICE_Memory=0 -DSERVICE_Registers=0 -DSERVICE_MemoryMap=0 \
    -DSERVICE_StackTrace=0 -DSERVICE_Expressions=0"


# These features don't compile for several cases.
#
CFLAGS:append:arc = " ${LCL_STOP_SERVICES}"
CFLAGS:append:mips = " ${LCL_STOP_SERVICES}"
CFLAGS:append:mips64 = " ${LCL_STOP_SERVICES}"
CFLAGS:append:libc-musl = " ${LCL_STOP_SERVICES}"
CFLAGS:append:powerpc64 = " ${LCL_STOP_SERVICES}"
CFLAGS:append:powerpc64le = " ${LCL_STOP_SERVICES}"
CFLAGS:append:riscv64 = " ${LCL_STOP_SERVICES}"
CFLAGS:append:riscv32 = " ${LCL_STOP_SERVICES}"
CFLAGS:append:loongarch64 = " ${LCL_STOP_SERVICES}"

do_install() {
	oe_runmake install INSTALLROOT=${D}
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${UNPACKDIR}/tcf-agent.init ${D}${sysconfdir}/init.d/tcf-agent
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${UNPACKDIR}/tcf-agent.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_system_unitdir}/tcf-agent.service
}

