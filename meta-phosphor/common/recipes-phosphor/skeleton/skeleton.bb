SUMMARY = "Temp placeholder for skeleton function"
DESCRIPTION = "Temp placeholder for skeleton repository"
HOMEPAGE = "http://github.com/openbmc/skeleton"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit obmc-phosphor-chassis-mgmt
inherit obmc-phosphor-fan-mgmt
inherit obmc-phosphor-flash-mgmt
inherit obmc-phosphor-policy-mgmt
inherit obmc-phosphor-sensor-mgmt
inherit obmc-phosphor-system-mgmt
inherit pythonnative
inherit python-dir

VIRTUAL-RUNTIME_skeleton_workbook ?= ""

DEPENDS += "glib-2.0 systemd python"
RDEPENDS_${PN} += "python-argparse python-json python-subprocess python-compression libsystemd ${VIRTUAL-RUNTIME_skeleton_workbook}"
SKELETON_BRANCH = "openbmc-v1.0-stable"
SRC_URI += "git://github.com/openbmc/skeleton;branch=${SKELETON_BRANCH}"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/*"

# RDEPEND on pflash if the openpower-pflash machine feature is set.
PACKAGECONFIG ??= "${@bb.utils.contains('MACHINE_FEATURES', 'openpower-pflash', 'openpower-pflash', '', d)}"
PACKAGECONFIG[openpower-pflash] = ",,,pflash"

SRCREV = "6019000c3cc52d33d85906c93ffd1b8614b65c59"

S = "${WORKDIR}"

# needed to invoke setuptools
export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS
export PYTHON_SITEPACKAGES_DIR

do_compile() {
        oe_runmake -C git PYTHON=${PYTHON}
}

do_install() {
        oe_runmake -C git install \
                PYTHON=${PYTHON} \
                DESTDIR=${D} \
                PREFIX=/usr
}
