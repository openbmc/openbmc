# Class for packagegroup (package group) recipes

# By default, only the packagegroup package itself is in PACKAGES.
# -dbg and -dev flavours are handled by the anonfunc below.
# This means that packagegroup recipes used to build multiple packagegroup
# packages have to modify PACKAGES after inheriting packagegroup.bbclass.
PACKAGES = "${PN}"

# By default, packagegroup packages do not depend on a certain architecture.
# Only if dependencies are modified by MACHINE_FEATURES, packages
# need to be set to MACHINE_ARCH after inheriting packagegroup.bbclass
PACKAGE_ARCH ?= "all"

# Fully expanded - so it applies the overrides as well
PACKAGE_ARCH_EXPANDED := "${PACKAGE_ARCH}"

inherit ${@oe.utils.ifelse(d.getVar('PACKAGE_ARCH_EXPANDED', True) == 'all', 'allarch', '')}

# This automatically adds -dbg and -dev flavours of all PACKAGES
# to the list. Their dependencies (RRECOMMENDS) are handled as usual
# by package_depchains in a following step.
# Also mark all packages as ALLOW_EMPTY
python () {
    packages = d.getVar('PACKAGES', True).split()
    if d.getVar('PACKAGEGROUP_DISABLE_COMPLEMENTARY', True) != '1':
        types = ['', '-dbg', '-dev']
        if bb.utils.contains('DISTRO_FEATURES', 'ptest', True, False, d):
            types.append('-ptest')
        packages = [pkg + suffix for pkg in packages
                    for suffix in types]
        d.setVar('PACKAGES', ' '.join(packages))
    for pkg in packages:
        d.setVar('ALLOW_EMPTY_%s' % pkg, '1')
}

# We don't want to look at shared library dependencies for the
# dbg packages
DEPCHAIN_DBGDEFAULTDEPS = "1"

# We only need the packaging tasks - disable the rest
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"

python () {
    initman = d.getVar("VIRTUAL-RUNTIME_init_manager", True)
    if initman and initman in ['sysvinit', 'systemd'] and not bb.utils.contains('DISTRO_FEATURES', initman, True, False, d):
        bb.fatal("Please ensure that your setting of VIRTUAL-RUNTIME_init_manager (%s) matches the entries enabled in DISTRO_FEATURES" % initman)
}

