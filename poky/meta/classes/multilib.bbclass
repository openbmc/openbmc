#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

python multilib_virtclass_handler () {
    cls = e.data.getVar("BBEXTENDCURR")
    variant = e.data.getVar("BBEXTENDVARIANT")
    if cls != "multilib" or not variant:
        return

    localdata = bb.data.createCopy(e.data)
    localdata.delVar('TMPDIR')
    e.data.setVar('STAGING_KERNEL_DIR', localdata.getVar('STAGING_KERNEL_DIR'))

    # There should only be one kernel in multilib configs
    # We also skip multilib setup for module packages.
    provides = (e.data.getVar("PROVIDES") or "").split()
    non_ml_recipes = d.getVar('NON_MULTILIB_RECIPES').split()
    bpn = e.data.getVar("BPN")
    if "virtual/kernel" in provides or \
            bb.data.inherits_class('module-base', e.data) or \
            bpn in non_ml_recipes:
        raise bb.parse.SkipRecipe("We shouldn't have multilib variants for %s" % bpn)

    save_var_name=e.data.getVar("MULTILIB_SAVE_VARNAME") or ""
    for name in save_var_name.split():
        val=e.data.getVar(name)
        if val:
            e.data.setVar(name + "_MULTILIB_ORIGINAL", val)

    # We nearly don't need this but dependencies on NON_MULTILIB_RECIPES don't work without it
    d.setVar("SSTATE_ARCHS_TUNEPKG", "${@all_multilib_tune_values(d, 'TUNE_PKGARCH')}")

    overrides = e.data.getVar("OVERRIDES", False)
    pn = e.data.getVar("PN", False)
    overrides = overrides.replace("pn-${PN}", "pn-${PN}:pn-" + pn)
    e.data.setVar("OVERRIDES", overrides)

    if bb.data.inherits_class('image', e.data):
        e.data.setVar("MLPREFIX", variant + "-")
        e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
        e.data.setVar('SDKTARGETSYSROOT', e.data.getVar('SDKTARGETSYSROOT'))
        override = ":virtclass-multilib-" + variant
        e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)
        target_vendor = e.data.getVar("TARGET_VENDOR:" + "virtclass-multilib-" + variant, False)
        if target_vendor:
            e.data.setVar("TARGET_VENDOR", target_vendor)
        return

    if bb.data.inherits_class('cross-canadian', e.data):
        # Multilib cross-candian should use the same nativesdk sysroot without MLPREFIX
        e.data.setVar("RECIPE_SYSROOT", "${WORKDIR}/recipe-sysroot")
        e.data.setVar("STAGING_DIR_TARGET", "${WORKDIR}/recipe-sysroot")
        e.data.setVar("STAGING_DIR_HOST", "${WORKDIR}/recipe-sysroot")
        e.data.setVar("RECIPE_SYSROOT_MANIFEST_SUBDIR", "nativesdk-" + variant)
        e.data.setVar("MLPREFIX", variant + "-")
        override = ":virtclass-multilib-" + variant
        e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)
        return

    if bb.data.inherits_class('native', e.data):
        raise bb.parse.SkipRecipe("We can't extend native recipes")

    if bb.data.inherits_class('nativesdk', e.data) or bb.data.inherits_class('crosssdk', e.data):
        raise bb.parse.SkipRecipe("We can't extend nativesdk recipes")

    if bb.data.inherits_class('allarch', e.data) and not d.getVar('MULTILIB_VARIANTS') \
        and not bb.data.inherits_class('packagegroup', e.data):
        raise bb.parse.SkipRecipe("Don't extend allarch recipes which are not packagegroups")

    # Expand this since this won't work correctly once we set a multilib into place
    e.data.setVar("ALL_MULTILIB_PACKAGE_ARCHS", e.data.getVar("ALL_MULTILIB_PACKAGE_ARCHS"))
 
    override = ":virtclass-multilib-" + variant

    skip_msg = e.data.getVarFlag('SKIP_RECIPE', e.data.getVar('PN'))
    if skip_msg:
        pn_new = variant + "-" + e.data.getVar('PN')
        if not e.data.getVarFlag('SKIP_RECIPE', pn_new):
            e.data.setVarFlag('SKIP_RECIPE', pn_new, skip_msg)

    e.data.setVar("MLPREFIX", variant + "-")
    e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)

    # Expand INCOMPATIBLE_LICENSE_EXCEPTIONS with multilib prefix
    pkgs = e.data.getVar("INCOMPATIBLE_LICENSE_EXCEPTIONS")
    if pkgs:
        for pkg in pkgs.split():
            pkgs += " " + variant + "-" + pkg
        e.data.setVar("INCOMPATIBLE_LICENSE_EXCEPTIONS", pkgs)

    # DEFAULTTUNE can change TARGET_ARCH override so expand this now before update_data
    newtune = e.data.getVar("DEFAULTTUNE:" + "virtclass-multilib-" + variant, False)
    if newtune:
        e.data.setVar("DEFAULTTUNE", newtune)
}

addhandler multilib_virtclass_handler
multilib_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

python __anonymous () {
    if bb.data.inherits_class('image', d):
        # set rpm preferred file color for 32-bit multilib image
        if d.getVar("SITEINFO_BITS") == "32":
            d.setVar("RPM_PREFER_ELF_ARCH", "1")

        variant = d.getVar("BBEXTENDVARIANT")
        import oe.classextend

        clsextend = oe.classextend.ClassExtender(variant, d)

        clsextend.map_depends_variable("PACKAGE_INSTALL")
        clsextend.map_depends_variable("LINGUAS_INSTALL")
        clsextend.map_depends_variable("RDEPENDS")
        pinstall = d.getVar("LINGUAS_INSTALL") + " " + d.getVar("PACKAGE_INSTALL")
        d.setVar("PACKAGE_INSTALL", pinstall)
        d.setVar("LINGUAS_INSTALL", "")
        # FIXME, we need to map this to something, not delete it!
        d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", "")
        bb.build.deltask('do_populate_sdk_ext', d)
        return
}

python multilib_virtclass_handler_postkeyexp () {
    cls = d.getVar("BBEXTENDCURR")
    variant = d.getVar("BBEXTENDVARIANT")
    if cls != "multilib" or not variant:
        return

    variant = d.getVar("BBEXTENDVARIANT")

    import oe.classextend

    clsextend = oe.classextend.ClassExtender(variant, d)

    if bb.data.inherits_class('image', d):
        return

    clsextend.map_depends_variable("DEPENDS")
    clsextend.map_depends_variable("PACKAGE_WRITE_DEPS")
    clsextend.map_variable("PROVIDES")

    if bb.data.inherits_class('cross-canadian', d):
        return

    clsextend.rename_packages()
    clsextend.rename_package_variables((d.getVar("PACKAGEVARS") or "").split())

    clsextend.map_packagevars()
    clsextend.map_regexp_variable("PACKAGES_DYNAMIC")
    clsextend.map_variable("INITSCRIPT_PACKAGES")
    clsextend.map_variable("USERADD_PACKAGES")
    clsextend.map_variable("SYSTEMD_PACKAGES")
    clsextend.map_variable("UPDATERCPN")

    reset_alternative_priority(d)
}

addhandler multilib_virtclass_handler_postkeyexp
multilib_virtclass_handler_postkeyexp[eventmask] = "bb.event.RecipePostKeyExpansion"

def reset_alternative_priority(d):
    if not bb.data.inherits_class('update-alternatives', d):
        return

    # There might be multiple multilibs at the same time, e.g., lib32 and
    # lib64, each of them should have a different priority.
    multilib_variants = d.getVar('MULTILIB_VARIANTS')
    bbextendvariant = d.getVar('BBEXTENDVARIANT')
    reset_gap = multilib_variants.split().index(bbextendvariant) + 1

    # ALTERNATIVE_PRIORITY = priority
    alt_priority_recipe = d.getVar('ALTERNATIVE_PRIORITY')
    # Reset ALTERNATIVE_PRIORITY when found
    if alt_priority_recipe:
        reset_priority = int(alt_priority_recipe) - reset_gap
        bb.debug(1, '%s: Setting ALTERNATIVE_PRIORITY to %s' % (d.getVar('PN'), reset_priority))
        d.setVar('ALTERNATIVE_PRIORITY', reset_priority)

    handled_pkgs = []
    for pkg in (d.getVar('PACKAGES') or "").split():
        # ALTERNATIVE_PRIORITY_pkg = priority
        alt_priority_pkg = d.getVar('ALTERNATIVE_PRIORITY_%s' % pkg)
        # Reset ALTERNATIVE_PRIORITY_pkg when found
        if alt_priority_pkg:
            reset_priority = int(alt_priority_pkg) - reset_gap
            if not pkg in handled_pkgs:
                handled_pkgs.append(pkg)
                bb.debug(1, '%s: Setting ALTERNATIVE_PRIORITY_%s to %s' % (pkg, pkg, reset_priority))
                d.setVar('ALTERNATIVE_PRIORITY_%s' % pkg, reset_priority)

        for alt_name in (d.getVar('ALTERNATIVE:%s' % pkg) or "").split():
            # ALTERNATIVE_PRIORITY_pkg[tool]  = priority
            alt_priority_pkg_name = d.getVarFlag('ALTERNATIVE_PRIORITY_%s' % pkg, alt_name)
            # ALTERNATIVE_PRIORITY[tool] = priority
            alt_priority_name = d.getVarFlag('ALTERNATIVE_PRIORITY', alt_name)

            if alt_priority_pkg_name:
                reset_priority = int(alt_priority_pkg_name) - reset_gap
                bb.debug(1, '%s: Setting ALTERNATIVE_PRIORITY_%s[%s] to %s' % (pkg, pkg, alt_name, reset_priority))
                d.setVarFlag('ALTERNATIVE_PRIORITY_%s' % pkg, alt_name, reset_priority)
            elif alt_priority_name:
                reset_priority = int(alt_priority_name) - reset_gap
                bb.debug(1, '%s: Setting ALTERNATIVE_PRIORITY[%s] to %s' % (pkg, alt_name, reset_priority))
                d.setVarFlag('ALTERNATIVE_PRIORITY', alt_name, reset_priority)

PACKAGEFUNCS:append = " do_package_qa_multilib"

python do_package_qa_multilib() {

    def check_mlprefix(pkg, var, mlprefix):
        values = bb.utils.explode_deps(d.getVar('%s:%s' % (var, pkg)) or d.getVar(var) or "")
        candidates = []
        for i in values:
            if i.startswith('virtual/'):
                i = i[len('virtual/'):]

            if (not (i.startswith(mlprefix) or i.startswith("kernel-") \
                    or ('cross-canadian' in i) or i.startswith("nativesdk-") \
                    or i.startswith("rtld") or i.startswith("/"))):
                candidates.append(i)

        if len(candidates) > 0:
            msg = "%s package %s - suspicious values '%s' in %s" \
                   % (d.getVar('PN'), pkg, ' '.join(candidates), var)
            oe.qa.handle_error("multilib", msg, d)

    ml = d.getVar('MLPREFIX')
    if not ml:
        return

    # exception for ${MLPREFIX}target-sdk-provides-dummy
    if 'target-sdk-provides-dummy' in d.getVar('PN'):
        return

    packages = d.getVar('PACKAGES')
    for pkg in packages.split():
        check_mlprefix(pkg, 'RDEPENDS', ml)
        check_mlprefix(pkg, 'RPROVIDES', ml)
        check_mlprefix(pkg, 'RRECOMMENDS', ml)
        check_mlprefix(pkg, 'RSUGGESTS', ml)
        check_mlprefix(pkg, 'RREPLACES', ml)
        check_mlprefix(pkg, 'RCONFLICTS', ml)
    oe.qa.exit_if_errors(d)
}
