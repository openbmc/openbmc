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
    distro_features = set((d.getVar('DISTRO_FEATURES') or '').split())

    any_of_distro_features = set((d.getVar('ANY_OF_DISTRO_FEATURES') or '').split())
    if any_of_distro_features:
        if set.isdisjoint(any_of_distro_features, distro_features):
            raise bb.parse.SkipRecipe("one of '%s' needs to be in DISTRO_FEATURES" % ' '.join(any_of_distro_features))

    required_distro_features = set((d.getVar('REQUIRED_DISTRO_FEATURES') or '').split())
    if required_distro_features:
        missing = set.difference(required_distro_features, distro_features)
        if missing:
            raise bb.parse.SkipRecipe("missing required distro feature%s '%s' (not in DISTRO_FEATURES)" % ('s' if len(missing) > 1 else '', ' '.join(missing)))

    conflict_distro_features = set((d.getVar('CONFLICT_DISTRO_FEATURES') or '').split())
    if conflict_distro_features:
        conflicts = set.intersection(conflict_distro_features, distro_features)
        if conflicts:
            raise bb.parse.SkipRecipe("conflicting distro feature%s '%s' (in DISTRO_FEATURES)" % ('s' if len(conflicts) > 1 else '', ' '.join(conflicts)))
}
