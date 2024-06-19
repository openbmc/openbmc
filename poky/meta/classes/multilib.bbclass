#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

python multilib_virtclass_handler () {
    cls = d.getVar("BBEXTENDCURR")
    variant = d.getVar("BBEXTENDVARIANT")
    if cls != "multilib" or not variant:
        return

    localdata = bb.data.createCopy(d)
    localdata.delVar('TMPDIR')
    d.setVar('STAGING_KERNEL_DIR', localdata.getVar('STAGING_KERNEL_DIR'))

    # There should only be one kernel in multilib configs
    # We also skip multilib setup for module packages.
    provides = (d.getVar("PROVIDES") or "").split()
    non_ml_recipes = d.getVar('NON_MULTILIB_RECIPES').split()
    bpn = d.getVar("BPN")
    if ("virtual/kernel" in provides
            or bb.data.inherits_class('module-base', d)
            or bpn in non_ml_recipes):
        raise bb.parse.SkipRecipe("We shouldn't have multilib variants for %s" % bpn)

    save_var_name = d.getVar("MULTILIB_SAVE_VARNAME") or ""
    for name in save_var_name.split():
        val = d.getVar(name)
        if val:
            d.setVar(name + "_MULTILIB_ORIGINAL", val)

    # We nearly don't need this but dependencies on NON_MULTILIB_RECIPES don't work without it
    d.setVar("SSTATE_ARCHS_TUNEPKG", "${@all_multilib_tune_values(d, 'TUNE_PKGARCH')}")

    overrides = e.data.getVar("OVERRIDES", False)
    pn = e.data.getVar("PN", False)
    overrides = overrides.replace("pn-${PN}", "pn-${PN}:pn-" + pn)
    d.setVar("OVERRIDES", overrides)

    if bb.data.inherits_class('image', d):
        d.setVar("MLPREFIX", variant + "-")
        d.setVar("PN", variant + "-" + d.getVar("PN", False))
        d.setVar('SDKTARGETSYSROOT', d.getVar('SDKTARGETSYSROOT'))
        override = ":virtclass-multilib-" + variant
        d.setVar("OVERRIDES", d.getVar("OVERRIDES", False) + override)
        target_vendor = d.getVar("TARGET_VENDOR:" + "virtclass-multilib-" + variant, False)
        if target_vendor:
            d.setVar("TARGET_VENDOR", target_vendor)
        return

    if bb.data.inherits_class('cross-canadian', d):
        # Multilib cross-candian should use the same nativesdk sysroot without MLPREFIX
        d.setVar("RECIPE_SYSROOT", "${WORKDIR}/recipe-sysroot")
        d.setVar("STAGING_DIR_TARGET", "${WORKDIR}/recipe-sysroot")
        d.setVar("STAGING_DIR_HOST", "${WORKDIR}/recipe-sysroot")
        d.setVar("RECIPE_SYSROOT_MANIFEST_SUBDIR", "nativesdk-" + variant)
        d.setVar("MLPREFIX", variant + "-")
        override = ":virtclass-multilib-" + variant
        d.setVar("OVERRIDES", d.getVar("OVERRIDES", False) + override)
        return

    if bb.data.inherits_class('native', d):
        raise bb.parse.SkipRecipe("We can't extend native recipes")

    if bb.data.inherits_class('nativesdk', d) or bb.data.inherits_class('crosssdk', d):
        raise bb.parse.SkipRecipe("We can't extend nativesdk recipes")

    if (bb.data.inherits_class('allarch', d)
            and not d.getVar('MULTILIB_VARIANTS')
            and not bb.data.inherits_class('packagegroup', d)):
        raise bb.parse.SkipRecipe("Don't extend allarch recipes which are not packagegroups")

    # Expand this since this won't work correctly once we set a multilib into place
    d.setVar("ALL_MULTILIB_PACKAGE_ARCHS", d.getVar("ALL_MULTILIB_PACKAGE_ARCHS"))
 
    override = ":virtclass-multilib-" + variant

    skip_msg = d.getVarFlag('SKIP_RECIPE', d.getVar('PN'))
    if skip_msg:
        pn_new = variant + "-" + d.getVar('PN')
        if not d.getVarFlag('SKIP_RECIPE', pn_new):
            d.setVarFlag('SKIP_RECIPE', pn_new, skip_msg)

    d.setVar("MLPREFIX", variant + "-")
    d.setVar("PN", variant + "-" + d.getVar("PN", False))
    d.setVar("OVERRIDES", d.getVar("OVERRIDES", False) + override)

    # Expand INCOMPATIBLE_LICENSE_EXCEPTIONS with multilib prefix
    pkgs = d.getVar("INCOMPATIBLE_LICENSE_EXCEPTIONS")
    if pkgs:
        for pkg in pkgs.split():
            pkgs += " " + variant + "-" + pkg
        d.setVar("INCOMPATIBLE_LICENSE_EXCEPTIONS", pkgs)

    # DEFAULTTUNE can change TARGET_ARCH override so expand this now before update_data
    newtune = d.getVar("DEFAULTTUNE:" + "virtclass-multilib-" + variant, False)
    if newtune:
        d.setVar("DEFAULTTUNE", newtune)
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
