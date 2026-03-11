#
# SPDX-License-Identifier: GPL-2.0-only
#

from django import template

register = template.Library()

def field_values(iterable, field):
    """
    Convert an iterable of models into a list of strings, one for each model,
    where the string for each model is the value of the field "field".
    """
    objects = []

    if field:
        for item in iterable:
            objects.append(getattr(item, field))

    return objects

register.filter('field_values', field_values)