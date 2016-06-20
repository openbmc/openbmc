SUMMARY = "Target Communication Framework for the Eclipse IDE"
HOMEPAGE = "http://wiki.eclipse.org/TCF"
BUGTRACKER = "https://bugs.eclipse.org/bugs/"

LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://edl-v10.html;md5=522a390a83dc186513f0500543ad3679"

SRCREV = "b9a735e9c7cf82f80d412b7ab15d08b89d5a4ccc"
PV = "1.3.0+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
SRC_URI = "git://git.eclipse.org/gitroot/tcf/org.eclipse.tcf.agent.git;branch=1.3_mars_bugfix \
           file://fix_ranlib.patch \
           file://ldflags.patch \
           file://0001-canonicalize_file_name-is-specific-to-glibc.patch;striplevel=2 \
           file://tcf-agent.init \
           file://tcf-agent.service \
          "

DEPENDS = "util-linux openssl"
RDEPENDS_${PN} = "bash"

S = "${WORKDIR}/git/agent"

inherit update-rc.d systemd

SYSTEMD_SERVICE_${PN} = "tcf-agent.service"

INITSCRIPT_NAME = "tcf-agent"
INITSCRIPT_PARAMS = "start 99 3 5 . stop 20 0 1 2 6 ."

# mangling needed for make
MAKE_ARCH = "`echo ${TARGET_ARCH} | sed s,i.86,i686,`"
MAKE_OS = "`echo ${TARGET_OS} | sed s,^linux.*,GNU/Linux,`"

EXTRA_OEMAKE = "MACHINE=${MAKE_ARCH} OPSYS=${MAKE_OS} 'CC=${CC}' 'AR=${AR}'"

# They don't build on ARM and we don't need them actually.
CFLAGS += "-DSERVICE_RunControl=0 -DSERVICE_Breakpoints=0 \
    -DSERVICE_Memory=0 -DSERVICE_Registers=0 -DSERVICE_MemoryMap=0 \
    -DSERVICE_StackTrace=0 -DSERVICE_Symbols=0 -DSERVICE_LineNumbers=0 \
    -DSERVICE_Expressions=0"

do_install() {
	oe_runmake install INSTALLROOT=${D}
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/tcf-agent.init ${D}${sysconfdir}/init.d/tcf-agent
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/tcf-agent.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/tcf-agent.service
}

