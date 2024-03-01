#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def preferred_ml_updates(d):
    # If any of PREFERRED_PROVIDER, PREFERRED_RPROVIDER, REQUIRED_VERSION
    # or PREFERRED_VERSION are set, we need to mirror these variables in
    # the multilib case;
    multilibs = d.getVar('MULTILIBS') or ""
    if not multilibs:
        return

    prefixes = []
    for ext in multilibs.split():
        eext = ext.split(':')
        if len(eext) > 1 and eext[0] == 'multilib':
            prefixes.append(eext[1])

    required_versions = []
    preferred_versions = []
    providers = []
    rproviders = []
    for v in d.keys():
        if v.startswith("REQUIRED_VERSION_"):
            required_versions.append(v)
        if v.startswith("PREFERRED_VERSION_"):
            preferred_versions.append(v)
        if v.startswith("PREFERRED_PROVIDER_"):
            providers.append(v)
        if v.startswith("PREFERRED_RPROVIDER_"):
            rproviders.append(v)

    def sort_versions(versions, keyword):
        version_str = "_".join([keyword, "VERSION", ""])
        for v in versions:
            val = d.getVar(v, False)
            pkg = v.replace(version_str, "")
            if pkg.endswith("-native") or "-crosssdk-" in pkg or pkg.startswith(("nativesdk-", "virtual/nativesdk-")):
                continue
            if '-cross-' in pkg and '${' in pkg:
                for p in prefixes:
                    localdata = bb.data.createCopy(d)
                    override = ":virtclass-multilib-" + p
                    localdata.setVar("OVERRIDES", localdata.getVar("OVERRIDES", False) + override)
                    if "-canadian-" in pkg:
                        newtune = localdata.getVar("DEFAULTTUNE:" + "virtclass-multilib-" + p, False)
                        if newtune:
                            localdata.setVar("DEFAULTTUNE", newtune)
                        newname = localdata.expand(v)
                    else:
                        newname = localdata.expand(v).replace(version_str, version_str + p + '-')
                    if newname != v:
                        newval = localdata.expand(val)
                        d.setVar(newname, newval)
                # Avoid future variable key expansion
                vexp = d.expand(v)
                if v != vexp and d.getVar(v, False):
                    d.renameVar(v, vexp)
                continue
            for p in prefixes:
                newname = version_str + p + "-" + pkg
                if not d.getVar(newname, False):
                    d.setVar(newname, val)

    sort_versions(required_versions, "REQUIRED")
    sort_versions(preferred_versions, "PREFERRED")

    for prov in providers:
        val = d.getVar(prov, False)
        pkg = prov.replace("PREFERRED_PROVIDER_", "")
        if pkg.endswith("-native") or "-crosssdk-" in pkg or pkg.startswith(("nativesdk-", "virtual/nativesdk-")):
            continue
        if 'cross-canadian' in pkg:
            for p in prefixes:
                localdata = bb.data.createCopy(d)
                override = ":virtclass-multilib-" + p
                localdata.setVar("OVERRIDES", localdata.getVar("OVERRIDES", False) + override)
                newname = localdata.expand(prov)
                if newname != prov:
                    newval = localdata.expand(val)
                    d.setVar(newname, newval)
            # Avoid future variable key expansion
            provexp = d.expand(prov)
            if prov != provexp and d.getVar(prov, False):
                d.renameVar(prov, provexp)
            continue
        virt = ""
        if pkg.startswith("virtual/"):
            pkg = pkg.replace("virtual/", "")
            virt = "virtual/"
        for p in prefixes:
            newval = None
            if pkg != "kernel":
                newval = p + "-" + val

            # implement variable keys
            localdata = bb.data.createCopy(d)
            override = ":virtclass-multilib-" + p
            localdata.setVar("OVERRIDES", localdata.getVar("OVERRIDES", False) + override)
            newname = localdata.expand(prov)
            if newname != prov and not d.getVar(newname, False):
                d.setVar(newname, localdata.expand(newval))

            # implement alternative multilib name
            newname = localdata.expand("PREFERRED_PROVIDER_" + virt + p + "-" + pkg)
            if not d.getVar(newname, False) and newval != None:
                d.setVar(newname, localdata.expand(newval))
        # Avoid future variable key expansion
        provexp = d.expand(prov)
        if prov != provexp and d.getVar(prov, False):
            d.renameVar(prov, provexp)

    for prov in rproviders:
        val = d.getVar(prov, False)
        pkg = prov.replace("PREFERRED_RPROVIDER_", "")
        for p in prefixes:
            newval = p + "-" + val

            # implement variable keys
            localdata = bb.data.createCopy(d)
            override = ":virtclass-multilib-" + p
            localdata.setVar("OVERRIDES", localdata.getVar("OVERRIDES", False) + override)
            newname = localdata.expand(prov)
            if newname != prov and not d.getVar(newname, False):
                d.setVar(newname, localdata.expand(newval))

            # implement alternative multilib name
            newname = localdata.expand("PREFERRED_RPROVIDER_" + p + "-" + pkg)
            if not d.getVar(newname, False) and newval != None:
                d.setVar(newname, localdata.expand(newval))
        # Avoid future variable key expansion
        provexp = d.expand(prov)
        if prov != provexp and d.getVar(prov, False):
            d.renameVar(prov, provexp)

    def translate_provide(prefix, prov):
        # Really need to know if kernel modules class is inherited somehow
        if prov == "lttng-modules":
            return prov
        if not prov.startswith("virtual/"):
            return prefix + "-" + prov
        if prov == "virtual/kernel":
            return prov
        prov = prov.replace("virtual/", "")
        return "virtual/" + prefix + "-" + prov

    mp = (d.getVar("BB_MULTI_PROVIDER_ALLOWED") or "").split()
    extramp = []
    for p in mp:
        if p.endswith("-native") or "-crosssdk-" in p or p.startswith(("nativesdk-", "virtual/nativesdk-")) or 'cross-canadian' in p:
            continue
        for pref in prefixes:
            extramp.append(translate_provide(pref, p))
    d.setVar("BB_MULTI_PROVIDER_ALLOWED", " ".join(mp + extramp))

    abisafe = (d.getVar("SIGGEN_EXCLUDERECIPES_ABISAFE") or "").split()
    extras = []
    for p in prefixes:
        for a in abisafe:
            extras.append(p + "-" + a)
    d.appendVar("SIGGEN_EXCLUDERECIPES_ABISAFE", " " + " ".join(extras))

    siggen_exclude = (d.getVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS") or "").split()
    extras = []
    for p in prefixes:
        for a in siggen_exclude:
            a1, a2 = a.split("->")
            extras.append(translate_provide(p, a1) + "->" + translate_provide(p, a2))
    d.appendVar("SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS", " " + " ".join(extras))

python multilib_virtclass_handler_vendor () {
    if isinstance(e, bb.event.ConfigParsed):
        for v in e.data.getVar("MULTILIB_VARIANTS").split():
            if e.data.getVar("TARGET_VENDOR:virtclass-multilib-" + v, False) is None:
                e.data.setVar("TARGET_VENDOR:virtclass-multilib-" + v, e.data.getVar("TARGET_VENDOR", False) + "ml" + v)
        preferred_ml_updates(e.data)
}
addhandler multilib_virtclass_handler_vendor
multilib_virtclass_handler_vendor[eventmask] = "bb.event.ConfigParsed"

python multilib_virtclass_handler_global () {
    variant = e.data.getVar("BBEXTENDVARIANT")
    if variant:
        return

    non_ml_recipes = d.getVar('NON_MULTILIB_RECIPES').split()

    if bb.data.inherits_class('kernel', e.data) or \
            bb.data.inherits_class('module-base', e.data) or \
            d.getVar('BPN') in non_ml_recipes:

            # We need to avoid expanding KERNEL_VERSION which we can do by deleting it
            # from a copy of the datastore
            localdata = bb.data.createCopy(d)
            localdata.delVar("KERNEL_VERSION")
            localdata.delVar("KERNEL_VERSION_PKG_NAME")

            variants = (e.data.getVar("MULTILIB_VARIANTS") or "").split()

            import oe.classextend
            clsextends = []
            for variant in variants:
                clsextends.append(oe.classextend.ClassExtender(variant, localdata))

            # Process PROVIDES
            origprovs = provs = localdata.getVar("PROVIDES") or ""
            for clsextend in clsextends:
                provs = provs + " " + clsextend.map_variable("PROVIDES", setvar=False)
            e.data.setVar("PROVIDES", provs)

            # Process RPROVIDES
            origrprovs = rprovs = localdata.getVar("RPROVIDES") or ""
            for clsextend in clsextends:
                rprovs = rprovs + " " + clsextend.map_variable("RPROVIDES", setvar=False)
            if rprovs.strip():
                e.data.setVar("RPROVIDES", rprovs)

            # Process RPROVIDES:${PN}...
            for pkg in (e.data.getVar("PACKAGES") or "").split():
                origrprovs = rprovs = localdata.getVar("RPROVIDES:%s" % pkg) or ""
                for clsextend in clsextends:
                    rprovs = rprovs + " " + clsextend.map_variable("RPROVIDES:%s" % pkg, setvar=False)
                    rprovs = rprovs + " " + clsextend.extname + "-" + pkg
                e.data.setVar("RPROVIDES:%s" % pkg, rprovs)
}

addhandler multilib_virtclass_handler_global
multilib_virtclass_handler_global[eventmask] = "bb.event.RecipeTaskPreProcess"
