#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit meson setuptools3-base python3targetconfig python_pep517

# meson_do_qa_configure does the wrong thing here because
# mesonpy runs "meson setup ..." in do_compile context.
# Make it a dummy function.
meson_do_qa_configure () {
    :
}

# This prevents the meson error:
# ERROR: Got argument buildtype as both -Dbuildtype and --buildtype. Pick one.
MESONOPTS:remove = "--buildtype ${MESON_BUILDTYPE}"

CONFIGURE_FILES = "pyproject.toml"

DEPENDS += "python3-wheel-native python3-meson-python-native"

def mesonpy_get_args(d):
    vars = ['MESONOPTS', 'MESON_CROSS_FILE', 'EXTRA_OEMESON']
    varlist = []
    for var in vars:
        value = d.getVar(var)
        vallist = value.split()
        for elem in vallist:
            varlist.append("-Csetup-args=" + elem)
    return ' '.join(varlist)

PEP517_BUILD_OPTS = "-Cbuilddir='${B}' ${@mesonpy_get_args(d)}"

python_mesonpy_do_configure () {
    python_pep517_do_configure
}

python_mesonpy_do_compile () {
    python_pep517_do_compile
}

python_mesonpy_do_install () {
    python_pep517_do_install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
