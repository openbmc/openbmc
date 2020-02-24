inherit obmc-phosphor-utils
inherit python3native

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
