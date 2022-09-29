#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Filter the license, the copyleft_should_include returns True for the
# COPYLEFT_LICENSE_INCLUDE recipe, and False for the
# COPYLEFT_LICENSE_EXCLUDE.
#
# By default, includes all GPL and LGPL, and excludes CLOSED and Proprietary.

COPYLEFT_LICENSE_INCLUDE ?= 'GPL* LGPL* AGPL*'
COPYLEFT_LICENSE_INCLUDE[type] = 'list'
COPYLEFT_LICENSE_INCLUDE[doc] = 'Space separated list of globs which include licenses'

COPYLEFT_LICENSE_EXCLUDE ?= 'CLOSED Proprietary'
COPYLEFT_LICENSE_EXCLUDE[type] = 'list'
COPYLEFT_LICENSE_EXCLUDE[doc] = 'Space separated list of globs which exclude licenses'

COPYLEFT_RECIPE_TYPE ?= '${@copyleft_recipe_type(d)}'
COPYLEFT_RECIPE_TYPE[doc] = 'The "type" of the current recipe (e.g. target, native, cross)'

COPYLEFT_RECIPE_TYPES ?= 'target'
COPYLEFT_RECIPE_TYPES[type] = 'list'
COPYLEFT_RECIPE_TYPES[doc] = 'Space separated list of recipe types to include'

COPYLEFT_AVAILABLE_RECIPE_TYPES = 'target native nativesdk cross crosssdk cross-canadian'
COPYLEFT_AVAILABLE_RECIPE_TYPES[type] = 'list'
COPYLEFT_AVAILABLE_RECIPE_TYPES[doc] = 'Space separated list of available recipe types'

COPYLEFT_PN_INCLUDE ?= ''
COPYLEFT_PN_INCLUDE[type] = 'list'
COPYLEFT_PN_INCLUDE[doc] = 'Space separated list of recipe names to include'

COPYLEFT_PN_EXCLUDE ?= ''
COPYLEFT_PN_EXCLUDE[type] = 'list'
COPYLEFT_PN_EXCLUDE[doc] = 'Space separated list of recipe names to exclude'

def copyleft_recipe_type(d):
    for recipe_type in oe.data.typed_value('COPYLEFT_AVAILABLE_RECIPE_TYPES', d):
        if oe.utils.inherits(d, recipe_type):
            return recipe_type
    return 'target'

def copyleft_should_include(d):
    """
    Determine if this recipe's sources should be deployed for compliance
    """
    import ast
    import oe.license
    from fnmatch import fnmatchcase as fnmatch

    recipe_type = d.getVar('COPYLEFT_RECIPE_TYPE')
    if recipe_type not in oe.data.typed_value('COPYLEFT_RECIPE_TYPES', d):
        included, motive = False, 'recipe type "%s" is excluded' % recipe_type
    else:
        included, motive = False, 'recipe did not match anything'

        include = oe.data.typed_value('COPYLEFT_LICENSE_INCLUDE', d)
        exclude = oe.data.typed_value('COPYLEFT_LICENSE_EXCLUDE', d)

        try:
            is_included, reason = oe.license.is_included(d.getVar('LICENSE'), include, exclude)
        except oe.license.LicenseError as exc:
            bb.fatal('%s: %s' % (d.getVar('PF'), exc))
        else:
            if is_included:
                if reason:
                    included, motive = True, 'recipe has included licenses: %s' % ', '.join(reason)
                else:
                    included, motive = False, 'recipe does not include a copyleft license'
            else:
                included, motive = False, 'recipe has excluded licenses: %s' % ', '.join(reason)

    if any(fnmatch(d.getVar('PN'), name) \
            for name in oe.data.typed_value('COPYLEFT_PN_INCLUDE', d)):
        included, motive =  True, 'recipe included by name'
    if any(fnmatch(d.getVar('PN'), name) \
            for name in oe.data.typed_value('COPYLEFT_PN_EXCLUDE', d)):
        included, motive = False, 'recipe excluded by name'

    return included, motive
