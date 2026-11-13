SUMMARY = "ACPICA tools for the development and debug of ACPI tables"
DESCRIPTION = "The ACPI Component Architecture (ACPICA) project provides an \
OS-independent reference implementation of the Advanced Configuration and \
Power Interface Specification (ACPI). ACPICA code contains those portions of \
ACPI meant to be directly integrated into the host OS as a kernel-resident \
subsystem, and a small set of tools to assist in developing and debugging \
ACPI tables."

HOMEPAGE = "https://www.intel.com/content/www/us/en/developer/topic-technology/open/acpica/overview.html"
SECTION = "console/tools"

LICENSE = "BSD-3-Clause OR GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.GPL-2.0-only;md5=570a9b3749dd0463a1778803b12a6dce \
		    file://LICENSE.BSD-3-Clause;md5=8896f63a9dcbf96114f60d32d38e9169 \
		    "

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

DEPENDS = "m4-native flex-native bison-native"

SRC_URI = "git://github.com/acpica/acpica;protocol=https;branch=master;tag=${PV}"
SRCREV = "e85aa3bebaae81ed1e6d163511ac7e1eafe732cf"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "acpixtract acpidump"

EXTRA_OEMAKE = "CC='${CC}' \
                OPT_CFLAGS=-Wall \
                DESTDIR=${D} \
                PREFIX=${prefix} \
                INSTALLDIR=${bindir} \
                INSTALLFLAGS= \
                YACC=bison \
                YFLAGS='-y --file-prefix-map=${WORKDIR}=${TARGET_DBGSRC_DIR}' \
                "

do_install() {
    oe_runmake install
}

# iasl*.bb is a subset of this recipe, so RREPLACE it
PROVIDES = "iasl"
RPROVIDES:${PN} += "iasl"
RREPLACES:${PN} += "iasl"
RCONFLICTS:${PN} += "iasl"

BBCLASSEXTEND = "native nativesdk"
