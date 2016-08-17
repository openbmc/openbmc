SUMMARY = "Analyze Suspend"
DESCRIPTION = "analyze-suspend is a tool for system developers to visualize \
the activity between suspend and resume, allowing them to identify \
inefficiencies and bottlenecks."
HOMEPAGE = "https://01.org/suspendresume"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

# Apart from the listed RDEPENDS, analyze-suspend depends on some features
# provided by the kernel. These options are:
#   - CONFIG_PM_DEBUG=y
#   - CONFIG_PM_SLEEP_DEBUG=y
#   - CONFIG_FTRACE=y
#   - CONFIG_FUNCTION_TRACER=y
#   - CONFIG_FUNCTION_GRAPH_TRACER=y

RDEPENDS_${PN} += "python-core python-datetime python-stringold"

PV = "3.2+gitr${SRCPV}"
SRCREV = "bce3cbec43bc2ce7a8c79b210314dd9d9ac1010b"
SRC_URI = "git://github.com/01org/suspendresume.git;protocol=https"
S = "${WORKDIR}/git"

do_install() {
	install -Dm 0755 analyze_suspend.py ${D}${bindir}/analyze_suspend.py
	install -Dm 0644 README ${D}${docdir}/analyze-suspend/README
}

BBCLASSEXTEND = "native"
