# Allow checking of required and conflicting DISTRO_FEATURES
#
# ANY_OF_DISTRO_FEATURES:   ensure at least one item on this list is included
#                           in DISTRO_FEATURES.
# REQUIRED_DISTRO_FEATURES: ensure every item on this list is included
#                           in DISTRO_FEATURES.
# CONFLICT_DISTRO_FEATURES: ensure no item in this list is included in
#                           DISTRO_FEATURES.
#
# Copyright 2013 (C) O.S. Systems Software LTDA.

python () {
    # Assume at least one var is set.
    distro_features = (d.getVar('DISTRO_FEATURES') or "").split()

    any_of_distro_features = d.getVar('ANY_OF_DISTRO_FEATURES')
    if any_of_distro_features:
        any_of_distro_features = any_of_distro_features.split()
        if set.isdisjoint(set(any_of_distro_features),set(distro_features)):
            raise bb.parse.SkipRecipe("one of '%s' needs to be in DISTRO_FEATURES" % any_of_distro_features)

    required_distro_features = d.getVar('REQUIRED_DISTRO_FEATURES')
    if required_distro_features:
        required_distro_features = required_distro_features.split()
        for f in required_distro_features:
            if f in distro_features:
                continue
            else:
                raise bb.parse.SkipRecipe("missing required distro feature '%s' (not in DISTRO_FEATURES)" % f)

    conflict_distro_features = d.getVar('CONFLICT_DISTRO_FEATURES')
    if conflict_distro_features:
        conflict_distro_features = conflict_distro_features.split()
        for f in conflict_distro_features:
            if f in distro_features:
                raise bb.parse.SkipRecipe("conflicting distro feature '%s' (in DISTRO_FEATURES)" % f)
}
