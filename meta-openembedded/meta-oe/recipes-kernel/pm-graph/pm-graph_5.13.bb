SUMMARY  = "Timing analysis tools for suspend/resume/boot"
DESCRIPTION = "This tool suite is designed to assist kernel and OS developers \
in optimizing their linux stack's suspend/resume & boot time."
HOMEPAGE = "https://01.org/pm-graph"
LICENSE  = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRCREV = "0987732b013936ad771334f51cba31c453c8a9c9"
SRC_URI = "git://github.com/intel/pm-graph.git;branch=master;protocol=https \
           file://0001-Makefile-fix-multilib-build-failure.patch \
"
S = "${WORKDIR}/git"

# Apart from the listed RDEPENDS, analyze-suspend depends on some features
# provided by the kernel. These options are:
#   - CONFIG_PM_DEBUG=y
#   - CONFIG_PM_SLEEP_DEBUG=y
#   - CONFIG_FTRACE=y
#   - CONFIG_FUNCTION_TRACER=y
#   - CONFIG_FUNCTION_GRAPH_TRACER=y

COMPATIBLE_HOST='(i.86|x86_64).*'
EXTRA_OEMAKE = "PREFIX=${prefix} DESTDIR=${D} LIBDIR=${libdir} BASELIB=${baselib}"

do_install() {
    oe_runmake install
    install -Dm 0755 ${S}/analyze_suspend.py ${D}${bindir}/analyze_suspend.py

    rm -rf ${D}${libdir}/pm-graph/__pycache__
}

RDEPENDS:${PN} += "python3-core python3-threading python3-datetime python3-compression"
RPROVIDES:${PN} = "analyze-suspend"
BBCLASSEXTEND = "native nativesdk"
