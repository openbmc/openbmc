python multilib_virtclass_handler () {
    cls = e.data.getVar("BBEXTENDCURR", True)
    variant = e.data.getVar("BBEXTENDVARIANT", True)
    if cls != "multilib" or not variant:
        return

    e.data.setVar('STAGING_KERNEL_DIR', e.data.getVar('STAGING_KERNEL_DIR', True))

    # There should only be one kernel in multilib configs
    # We also skip multilib setup for module packages.
    provides = (e.data.getVar("PROVIDES", True) or "").split()
    if "virtual/kernel" in provides or bb.data.inherits_class('module-base', e.data):
        raise bb.parse.SkipPackage("We shouldn't have multilib variants for the kernel")

    save_var_name=e.data.getVar("MULTILIB_SAVE_VARNAME", True) or ""
    for name in save_var_name.split():
        val=e.data.getVar(name, True)
        if val:
            e.data.setVar(name + "_MULTILIB_ORIGINAL", val)

    overrides = e.data.getVar("OVERRIDES", False)
    pn = e.data.getVar("PN", False)
    overrides = overrides.replace("pn-${PN}", "pn-${PN}:pn-" + pn)
    e.data.setVar("OVERRIDES", overrides)

    if bb.data.inherits_class('image', e.data):
        e.data.setVar("MLPREFIX", variant + "-")
        e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
        e.data.setVar('SDKTARGETSYSROOT', e.data.getVar('SDKTARGETSYSROOT', True))
        target_vendor = e.data.getVar("TARGET_VENDOR_" + "virtclass-multilib-" + variant, False)
        if target_vendor:
            e.data.setVar("TARGET_VENDOR", target_vendor)
        return

    if bb.data.inherits_class('cross-canadian', e.data):
        e.data.setVar("MLPREFIX", variant + "-")
        override = ":virtclass-multilib-" + variant
        e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)
        bb.data.update_data(e.data)
        return

    if bb.data.inherits_class('native', e.data):
        raise bb.parse.SkipPackage("We can't extend native recipes")

    if bb.data.inherits_class('nativesdk', e.data) or bb.data.inherits_class('crosssdk', e.data):
        raise bb.parse.SkipPackage("We can't extend nativesdk recipes")

    if bb.data.inherits_class('allarch', e.data) and not bb.data.inherits_class('packagegroup', e.data):
        raise bb.parse.SkipPackage("Don't extend allarch recipes which are not packagegroups")


    # Expand this since this won't work correctly once we set a multilib into place
    e.data.setVar("ALL_MULTILIB_PACKAGE_ARCHS", e.data.getVar("ALL_MULTILIB_PACKAGE_ARCHS", True))
 
    override = ":virtclass-multilib-" + variant

    e.data.setVar("MLPREFIX", variant + "-")
    e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)

    # Expand the WHITELISTs with multilib prefix
    for whitelist in ["WHITELIST_GPL-3.0", "LGPLv2_WHITELIST_GPL-3.0"]:
        pkgs = e.data.getVar(whitelist, True)
        for pkg in pkgs.split():
            pkgs += " " + variant + "-" + pkg
        e.data.setVar(whitelist, pkgs)

    # DEFAULTTUNE can change TARGET_ARCH override so expand this now before update_data
    newtune = e.data.getVar("DEFAULTTUNE_" + "virtclass-multilib-" + variant, False)
    if newtune:
        e.data.setVar("DEFAULTTUNE", newtune)
        e.data.setVar('DEFAULTTUNE_ML_%s' % variant, newtune)
}

addhandler multilib_virtclass_handler
multilib_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

STAGINGCC_prepend = "${BBEXTENDVARIANT}-"

python __anonymous () {
    variant = d.getVar("BBEXTENDVARIANT", True)

    import oe.classextend

    clsextend = oe.classextend.ClassExtender(variant, d)

    if bb.data.inherits_class('image', d):
        clsextend.map_depends_variable("PACKAGE_INSTALL")
        clsextend.map_depends_variable("LINGUAS_INSTALL")
        clsextend.map_depends_variable("RDEPENDS")
        pinstall = d.getVar("LINGUAS_INSTALL", True) + " " + d.getVar("PACKAGE_INSTALL", True)
        d.setVar("PACKAGE_INSTALL", pinstall)
        d.setVar("LINGUAS_INSTALL", "")
        # FIXME, we need to map this to something, not delete it!
        d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", "")

    if bb.data.inherits_class('image', d):
        return

    clsextend.map_depends_variable("DEPENDS")
    clsextend.map_variable("PROVIDES")

    if bb.data.inherits_class('cross-canadian', d):
        return

    clsextend.rename_packages()
    clsextend.rename_package_variables((d.getVar("PACKAGEVARS", True) or "").split())

    clsextend.map_packagevars()
    clsextend.map_regexp_variable("PACKAGES_DYNAMIC")
    clsextend.map_variable("PACKAGE_INSTALL")
    clsextend.map_variable("INITSCRIPT_PACKAGES")
    clsextend.map_variable("USERADD_PACKAGES")
    clsextend.map_variable("SYSTEMD_PACKAGES")
}

PACKAGEFUNCS_append = " do_package_qa_multilib"

python do_package_qa_multilib() {

    def check_mlprefix(pkg, var, mlprefix):
        values = bb.utils.explode_deps(d.getVar('%s_%s' % (var, pkg), True) or d.getVar(var, True) or "")
        candidates = []
        for i in values:
            if i.startswith('virtual/'):
                i = i[len('virtual/'):]
            if (not i.startswith('kernel-module')) and (not i.startswith(mlprefix)) and \
                (not 'cross-canadian' in i) and (not i.startswith("nativesdk-")) and \
                (not i.startswith("rtld")) and (not i.startswith('kernel-vmlinux')):
                candidates.append(i)
        if len(candidates) > 0:
            msg = "%s package %s - suspicious values '%s' in %s" \
                   % (d.getVar('PN', True), pkg, ' '.join(candidates), var)
            package_qa_handle_error("multilib", msg, d)

    ml = d.getVar('MLPREFIX', True)
    if not ml:
        return

    packages = d.getVar('PACKAGES', True)
    for pkg in packages.split():
        check_mlprefix(pkg, 'RDEPENDS', ml)
        check_mlprefix(pkg, 'RPROVIDES', ml)
        check_mlprefix(pkg, 'RRECOMMENDS', ml)
        check_mlprefix(pkg, 'RSUGGESTS', ml)
        check_mlprefix(pkg, 'RREPLACES', ml)
        check_mlprefix(pkg, 'RCONFLICTS', ml)
}
