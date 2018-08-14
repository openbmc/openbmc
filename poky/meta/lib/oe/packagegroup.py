import itertools

def is_optional(feature, d):
    packages = d.getVar("FEATURE_PACKAGES_%s" % feature)
    if packages:
        return bool(d.getVarFlag("FEATURE_PACKAGES_%s" % feature, "optional"))
    else:
        return bool(d.getVarFlag("PACKAGE_GROUP_%s" % feature, "optional"))

def packages(features, d):
    for feature in features:
        packages = d.getVar("FEATURE_PACKAGES_%s" % feature)
        if not packages:
            packages = d.getVar("PACKAGE_GROUP_%s" % feature)
        for pkg in (packages or "").split():
            yield pkg

def required_packages(features, d):
    req = [feature for feature in features if not is_optional(feature, d)]
    return packages(req, d)

def optional_packages(features, d):
    opt = [feature for feature in features if is_optional(feature, d)]
    return packages(opt, d)

def active_packages(features, d):
    return itertools.chain(required_packages(features, d),
                           optional_packages(features, d))

def active_recipes(features, d):
    import oe.packagedata

    for pkg in active_packages(features, d):
        recipe = oe.packagedata.recipename(pkg, d)
        if recipe:
            yield recipe
