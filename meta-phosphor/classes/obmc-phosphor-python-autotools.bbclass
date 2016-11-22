inherit obmc-phosphor-utils
inherit pythonnative

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

OBMC_PHOSPHOR_PYTHON_AUTOTOOLS_PACKAGE ?= "${PN}"

python() {
    for pkg in listvar_to_list(d, 'OBMC_PHOSPHOR_PYTHON_AUTOTOOLS_PACKAGE'):
        set_append(d, 'FILES_%s' % pkg, 
                   d.getVar('PYTHON_SITEPACKAGES_DIR', True))
}
