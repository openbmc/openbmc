# Allow checking of required and conflicting DISTRO_FEATURES
#
# ANY_OF_DISTRO_FEATURES:     ensure at least one item on this list is included
#                             in DISTRO_FEATURES.
# REQUIRED_DISTRO_FEATURES:   ensure every item on this list is included
#                             in DISTRO_FEATURES.
# CONFLICT_DISTRO_FEATURES:   ensure no item in this list is included in
#                             DISTRO_FEATURES.
# ANY_OF_MACHINE_FEATURES:    ensure at least one item on this list is included
#                             in MACHINE_FEATURES.
# REQUIRED_MACHINE_FEATURES:  ensure every item on this list is included
#                             in MACHINE_FEATURES.
# CONFLICT_MACHINE_FEATURES:  ensure no item in this list is included in
#                             MACHINE_FEATURES.
# ANY_OF_COMBINED_FEATURES:   ensure at least one item on this list is included
#                             in COMBINED_FEATURES.
# REQUIRED_COMBINED_FEATURES: ensure every item on this list is included
#                             in COMBINED_FEATURES.
# CONFLICT_COMBINED_FEATURES: ensure no item in this list is included in
#                             COMBINED_FEATURES.
#
# Copyright 2019 (C) Texas Instruments Inc.
# Copyright 2013 (C) O.S. Systems Software LTDA.

python () {
    if d.getVar('PARSE_ALL_RECIPES', False):
        return

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

    # Assume at least one var is set.
    machine_features = set((d.getVar('MACHINE_FEATURES') or '').split())

    any_of_machine_features = set((d.getVar('ANY_OF_MACHINE_FEATURES') or '').split())
    if any_of_machine_features:
        if set.isdisjoint(any_of_machine_features, machine_features):
            raise bb.parse.SkipRecipe("one of '%s' needs to be in MACHINE_FEATURES" % ' '.join(any_of_machine_features))

    required_machine_features = set((d.getVar('REQUIRED_MACHINE_FEATURES') or '').split())
    if required_machine_features:
        missing = set.difference(required_machine_features, machine_features)
        if missing:
            raise bb.parse.SkipRecipe("missing required machine feature%s '%s' (not in MACHINE_FEATURES)" % ('s' if len(missing) > 1 else '', ' '.join(missing)))

    conflict_machine_features = set((d.getVar('CONFLICT_MACHINE_FEATURES') or '').split())
    if conflict_machine_features:
        conflicts = set.intersection(conflict_machine_features, machine_features)
        if conflicts:
            raise bb.parse.SkipRecipe("conflicting machine feature%s '%s' (in MACHINE_FEATURES)" % ('s' if len(conflicts) > 1 else '', ' '.join(conflicts)))

    # Assume at least one var is set.
    combined_features = set((d.getVar('COMBINED_FEATURES') or '').split())

    any_of_combined_features = set((d.getVar('ANY_OF_COMBINED_FEATURES') or '').split())
    if any_of_combined_features:
        if set.isdisjoint(any_of_combined_features, combined_features):
            raise bb.parse.SkipRecipe("one of '%s' needs to be in COMBINED_FEATURES" % ' '.join(any_of_combined_features))

    required_combined_features = set((d.getVar('REQUIRED_COMBINED_FEATURES') or '').split())
    if required_combined_features:
        missing = set.difference(required_combined_features, combined_features)
        if missing:
            raise bb.parse.SkipRecipe("missing required machine feature%s '%s' (not in COMBINED_FEATURES)" % ('s' if len(missing) > 1 else '', ' '.join(missing)))

    conflict_combined_features = set((d.getVar('CONFLICT_COMBINED_FEATURES') or '').split())
    if conflict_combined_features:
        conflicts = set.intersection(conflict_combined_features, combined_features)
        if conflicts:
            raise bb.parse.SkipRecipe("conflicting machine feature%s '%s' (in COMBINED_FEATURES)" % ('s' if len(conflicts) > 1 else '', ' '.join(conflicts)))
}
