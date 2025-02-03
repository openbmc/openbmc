# Allow checking of required and conflicting features
#
# xxx = [DISTRO,MACHINE,COMBINED,IMAGE]
#
# ANY_OF_xxx_FEATURES:        ensure at least one item on this list is included
#                             in xxx_FEATURES.
# REQUIRED_xxx_FEATURES:      ensure every item on this list is included
#                             in xxx_FEATURES.
# CONFLICT_xxx_FEATURES:      ensure no item in this list is included in
#                             xxx_FEATURES.
#
# Copyright 2019 (C) Texas Instruments Inc.
# Copyright 2013 (C) O.S. Systems Software LTDA.
#
# SPDX-License-Identifier: MIT


python () {
    if bb.utils.to_boolean(d.getVar('PARSE_ALL_RECIPES', False)):
        return

    unused = True

    for kind in ['DISTRO', 'MACHINE', 'COMBINED', 'IMAGE']:
        if d.getVar('ANY_OF_' + kind + '_FEATURES') is None and not d.hasOverrides('ANY_OF_' + kind + '_FEATURES') and \
           d.getVar('REQUIRED_' + kind + '_FEATURES') is None and not d.hasOverrides('REQUIRED_' + kind + '_FEATURES') and \
           d.getVar('CONFLICT_' + kind + '_FEATURES') is None and not d.hasOverrides('CONFLICT_' + kind + '_FEATURES'):
            continue

        unused = False

        # Assume at least one var is set.
        features = set((d.getVar(kind + '_FEATURES') or '').split())

        any_of_features = set((d.getVar('ANY_OF_' + kind + '_FEATURES') or '').split())
        if any_of_features:
            if set.isdisjoint(any_of_features, features):
                raise bb.parse.SkipRecipe("one of '%s' needs to be in %s_FEATURES"
                    % (' '.join(any_of_features), kind))

        required_features = set((d.getVar('REQUIRED_' + kind + '_FEATURES') or '').split())
        if required_features:
            missing = set.difference(required_features, features)
            if missing:
                raise bb.parse.SkipRecipe("missing required %s feature%s '%s' (not in %s_FEATURES)"
                    % (kind.lower(), 's' if len(missing) > 1 else '', ' '.join(missing), kind))

        conflict_features = set((d.getVar('CONFLICT_' + kind + '_FEATURES') or '').split())
        if conflict_features:
            conflicts = set.intersection(conflict_features, features)
            if conflicts:
                raise bb.parse.SkipRecipe("conflicting %s feature%s '%s' (in %s_FEATURES)"
                    % (kind.lower(), 's' if len(conflicts) > 1 else '', ' '.join(conflicts), kind))

    if unused:
        bb.warn("Recipe inherits features_check but doesn't use it")
}
