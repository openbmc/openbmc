def preferred_ml_updates(d):
    # If any PREFERRED_PROVIDER or PREFERRED_VERSION are set,
    # we need to mirror these variables in the multilib case;
    multilibs = d.getVar('MULTILIBS') or ""
    if not multilibs:
        return

    prefixes = []
    for ext in multilibs.split():
        eext = ext.split(':')
        if len(eext) > 1 and eext[0] == 'multilib':
            prefixes.append(eext[1])

    versions = []
    providers = []
    for v in d.keys():
        if v.startswith("PREFERRED_VERSION_"):
            versions.append(v)
        if v.startswith("PREFERRED_PROVIDER_"):
            providers.append(v)

    for v in versions:
        val = d.getVar(v, False)
        pkg = v.replace("PREFERRED_VERSION_", "")
        if pkg.endswith("-native") or "-crosssdk-" in pkg or pkg.startswith(("nativesdk-", "virtual/nativesdk-")):
            continue
        if '-cross-' in pkg and '${' in pkg:
            for p in prefixes:
                localdata = bb.data.createCopy(d)
                override = ":virtclass-multilib-" + p
                localdata.setVar("OVERRIDES", localdata.getVar("OVERRIDES", False) + override)
                if "-canadian-" in pkg:
                    newname = localdata.expand(v)
                else:
                    newname = localdata.expand(v).replace("PREFERRED_VERSION_", "PREFERRED_VERSION_" + p + '-')
                if newname != v:
                    newval = localdata.expand(val)
                    d.setVar(newname, newval)
            # Avoid future variable key expansion
            vexp = d.expand(v)
            if v != vexp and d.getVar(v, False):
                d.renameVar(v, vexp)
            continue
        for p in prefixes:
            newname = "PREFERRED_VERSION_" + p + "-" + pkg
            if not d.getVar(newname, False):
                d.setVar(newname, val)

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

    def translate_provide(prefix, prov):
        if not prov.startswith("virtual/"):
            return prefix + "-" + prov
        if prov == "virtual/kernel":
            return prov
        prov = prov.replace("virtual/", "")
        return "virtual/" + prefix + "-" + prov

    mp = (d.getVar("MULTI_PROVIDER_WHITELIST") or "").split()
    extramp = []
    for p in mp:
        if p.endswith("-native") or "-crosssdk-" in p or p.startswith(("nativesdk-", "virtual/nativesdk-")) or 'cross-canadian' in p:
            continue
        for pref in prefixes:
            extramp.append(translate_provide(pref, p))
    d.setVar("MULTI_PROVIDER_WHITELIST", " ".join(mp + extramp))

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
            if e.data.getVar("TARGET_VENDOR_virtclass-multilib-" + v, False) is None:
                e.data.setVar("TARGET_VENDOR_virtclass-multilib-" + v, e.data.getVar("TARGET_VENDOR", False) + "ml" + v)
        preferred_ml_updates(e.data)
}
addhandler multilib_virtclass_handler_vendor
multilib_virtclass_handler_vendor[eventmask] = "bb.event.ConfigParsed"

python multilib_virtclass_handler_global () {
    variant = e.data.getVar("BBEXTENDVARIANT")
    if variant:
        return

    if bb.data.inherits_class('kernel', e.data) or \
            bb.data.inherits_class('module-base', e.data) or \
            (bb.data.inherits_class('allarch', e.data) and\
             not bb.data.inherits_class('packagegroup', e.data)):
            variants = (e.data.getVar("MULTILIB_VARIANTS") or "").split()

            import oe.classextend
            clsextends = []
            for variant in variants:
                clsextends.append(oe.classextend.ClassExtender(variant, e.data))

            # Process PROVIDES
            origprovs = provs = e.data.getVar("PROVIDES") or ""
            for clsextend in clsextends:
                provs = provs + " " + clsextend.map_variable("PROVIDES", setvar=False)
            e.data.setVar("PROVIDES", provs)

            # Process RPROVIDES
            origrprovs = rprovs = e.data.getVar("RPROVIDES") or ""
            for clsextend in clsextends:
                rprovs = rprovs + " " + clsextend.map_variable("RPROVIDES", setvar=False)
            if rprovs.strip():
                e.data.setVar("RPROVIDES", rprovs)

	    # Process RPROVIDES_${PN}...
            for pkg in (e.data.getVar("PACKAGES") or "").split():
                origrprovs = rprovs = e.data.getVar("RPROVIDES_%s" % pkg) or ""
                for clsextend in clsextends:
                    rprovs = rprovs + " " + clsextend.map_variable("RPROVIDES_%s" % pkg, setvar=False)
                    rprovs = rprovs + " " + clsextend.extname + "-" + pkg
                e.data.setVar("RPROVIDES_%s" % pkg, rprovs)
}

addhandler multilib_virtclass_handler_global
multilib_virtclass_handler_global[eventmask] = "bb.event.RecipeParsed"

