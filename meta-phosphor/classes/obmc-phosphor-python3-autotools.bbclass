inherit obmc-phosphor-utils
inherit python3native

OBMC_PYTHON_EXE="python3"
OBMC_PYTHON_EXE_class-native="nativepython3"

DEPENDS += "python3"

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

PYTHON_AUTOTOOLS_PACKAGE ?= "${PN}"

python() {
    for pkg in listvar_to_list(d, 'PYTHON_AUTOTOOLS_PACKAGE'):
        set_append(d, 'FILES_%s' % pkg,
                   d.getVar('PYTHON_SITEPACKAGES_DIR', True))
}

# python-setuptools does some mangling of the #! in any scripts it installs,
# which has been reported for years at pypa/setuptools#494.  OE has
# workarounds in distutils3.bbclass, but we cannot inherit that here because
# it conflicts with autotools.bbclass.  Port the un-mangling code here.
#
# This finds any ${PYTHON} executable path that got put into the scripts
# and reverts it back to "/usr/bin/env python3".  It also reverts any full
# ${STAGING_BINDIR_NATIVE} path back to "/usr/bin".
#
do_install_append() {
    for i in ${D}${bindir}/* ${D}${sbindir}/*; do
        if [ -f "$i" ]; then
            sed -i -e s:${PYTHON}:${USRBINPATH}/env\ ${OBMC_PYTHON_EXE}:g $i
            sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
        fi
    done
}
