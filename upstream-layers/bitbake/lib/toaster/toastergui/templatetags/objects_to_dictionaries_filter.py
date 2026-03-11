#
# SPDX-License-Identifier: GPL-2.0-only
#

from django import template

register = template.Library()

def objects_to_dictionaries(iterable, fields):
    """
    Convert an iterable into a list of dictionaries; fields should be set
    to a comma-separated string of properties for each item included in the
    resulting list; e.g. for a queryset:

        {{ queryset | objects_to_dictionaries:"id,name" }}

    will return a list like

        [{'id': 1, 'name': 'foo'}, ...]

    providing queryset has id and name fields

    This is mostly to support serialising querysets or lists of model objects
    to JSON
    """
    objects = []

    if fields:
        fields_list = [field.strip() for field in fields.split(',')]
        for item in iterable:
            out = {}
            for field in fields_list:
                out[field] = getattr(item, field)
            objects.append(out)

    return objects

register.filter('objects_to_dictionaries', objects_to_dictionaries)
