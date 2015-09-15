#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

from datetime import datetime, timedelta
from os.path import relpath
import re
from django import template
from django.utils import timezone
from django.template.defaultfilters import filesizeformat
import json as JsonLib
from django.utils.safestring import mark_safe

register = template.Library()

@register.simple_tag
def time_difference(start_time, end_time):
    return end_time - start_time

@register.filter(name = 'sectohms')
def sectohms(time):
    try:
        tdsec = int(time)
    except ValueError:
        tdsec = 0
    hours = int(tdsec / 3600)
    return "%02d:%02d:%02d" % (hours, int((tdsec - (hours * 3600))/ 60), int(tdsec) % 60)


@register.filter(name = 'get_tasks')
def get_tasks(queryset):
    return list(target + ':' + task if task else target \
                    for target, task in queryset.values_list('target', 'task'))


@register.filter(name = "json")
def json(value, default = None):
    # JSON spec says that "\/" is functionally identical to "/" to allow for HTML-tag embedding in JSON strings
    # unfortunately, I can't find any option in the json module to turn on forward-slash escaping, so we do
    # it manually here
    return mark_safe(JsonLib.dumps(value, indent=2, default = default, ensure_ascii=False).replace('</', '<\\/'))

@register.assignment_tag
def query(qs, **kwargs):
    """ template tag which allows queryset filtering. Usage:
          {% query books author=author as mybooks %}
          {% for book in mybooks %}
            ...
          {% endfor %}
    """
    return qs.filter(**kwargs)


@register.filter("whitespace_slice")
def whitespace_space_filter(value, arg):
    try:
        bits = []
        for x in arg.split(":"):
            if len(x) == 0:
                bits.append(None)
            else:
                # convert numeric value to the first whitespace after
                first_whitespace = value.find(" ", int(x))
                if first_whitespace == -1:
                    bits.append(int(x))
                else:
                    bits.append(first_whitespace)
        return value[slice(*bits)]
    except (ValueError, TypeError):
        raise

@register.filter
def divide(value, arg):
    if int(arg) == 0:
        return -1
    return int(value) / int(arg)

@register.filter
def multiply(value, arg):
    return int(value) * int(arg)

@register.assignment_tag
def datecompute(delta, start = timezone.now()):
    return start + timedelta(delta)


@register.filter(name = 'sortcols')
def sortcols(tablecols):
    return sorted(tablecols, key = lambda t: t['name'])

@register.filter
def task_color(task_object, show_green=False):
    """ Return css class depending on Task execution status and execution outcome.
        By default, green is not returned for executed and successful tasks;
        show_green argument should be True to get green color.
    """
    if not task_object.task_executed:
        return 'class=muted'
    elif task_object.outcome == task_object.OUTCOME_FAILED:
        return 'class=error'
    elif task_object.outcome == task_object.OUTCOME_SUCCESS and show_green:
        return 'class=green'
    else:
        return ''

@register.filter
def filtered_icon(options, filter):
    """Returns btn-primary if the filter matches one of the filter options
    """
    for option in options:
        if filter == option[1]:
            return "btn-primary"
        if ('daterange' == option[1]) and filter.startswith(option[4]):
            return "btn-primary"
    return ""

@register.filter
def filtered_tooltip(options, filter):
    """Returns tooltip for the filter icon if the filter matches one of the filter options
    """
    for option in options:
        if filter == option[1]:
            return "Showing only %s"%option[0]
        if ('daterange' == option[1]) and filter.startswith(option[4]):
            return "Showing only %s"%option[0]
    return ""

@register.filter
def format_none_and_zero(value):
    """Return empty string if the value is None, zero or Not Applicable
    """
    return "" if (not value) or (value == 0) or (value == "0") or (value == 'Not Applicable') else value

@register.filter
def filtered_filesizeformat(value):
    """
    If the value is -1 return an empty string. Otherwise,
    change output from fileformatsize to suppress trailing '.0'
    and change 'bytes' to 'B'.
    """
    if value == -1:
        return ''

    return filesizeformat(value).replace("bytes", "B")

@register.filter
def filtered_packagespec(value):
    """Strip off empty version and revision"""
    return re.sub(r'(--$)', '', value)

@register.filter
def check_filter_status(options, filter):
    """Check if the active filter is among the available options, and return 'checked'
       if filter is not active.
       Used in FilterDialog to select the first radio button if the filter is not active.
    """
    for option in options:
        if filter == option[1]:
            return ""
    return "checked"

@register.filter
def variable_parent_name(value):
    """ filter extended variable names to the parent name
    """
    value=re.sub('_\$.*', '', value)
    return re.sub('_[a-z].*', '', value)

@register.filter
def filter_setin_files(file_list, matchstr):
    """Filter/search the 'set in' file lists."""
    result = []
    search, filter = matchstr.split(':')
    for pattern in (search, filter):
        if pattern:
            for fobj in file_list:
                fname = fobj.file_name
                if fname not in result and re.search(pattern, fname):
                    result.append(fname)

    # no filter, show last file (if any)
    last = list(file_list)[-1].file_name
    if not filter and last not in result:
        result.append(last)

    return result

@register.filter
def string_slice(strvar,slicevar):
    """ slice a string with |string_slice:'[first]:[last]'
    """
    first,last= slicevar.partition(':')[::2]
    if first=='':
        return strvar[:int(last)]
    elif last=='':
        return strvar[int(first):]
    else:
        return strvar[int(first):int(last)]

@register.filter
def string_remove_regex(value,ex):
    """ remove sub-string of string that matches regex
    """
    return re.sub(ex, '', value)

@register.filter
def filtered_installedsize(size, installed_size):
    """If package.installed_size not null and not empty return it,
       else return package.size
    """
    return size if (installed_size == 0) or (installed_size == "") or (installed_size == None) else installed_size

@register.filter
def filtered_packageversion(version, revision):
    """ Emit "version-revision" if version and revision are not null
        else "version" if version is not null
        else ""
    """
    return "" if (not version or version == "") else version if (not revision or revision == "") else version + "-" + revision

@register.filter
def filter_sizeovertotal(package_object, total_size):
    """ Return the % size of the package over the total size argument
        formatted nicely.
    """
    size = package_object.installed_size
    if size == None or size == '':
        size = package_object.size

    return '{:.1%}'.format(float(size)/float(total_size))

from django.utils.safestring import mark_safe
@register.filter
def format_vpackage_rowclass(size):
    if size == -1:
        return mark_safe('class="muted"')
    return ''

@register.filter
def format_vpackage_namehelp(name):
    r =  name + '&nbsp;'
    r += '<i class="icon-question-sign get-help hover-help"'
    r += ' title = "' + name + ' has not been built">'
    r += '</i>'
    return mark_safe(r)

@register.filter
def get_dict_value(dictionary, key):
    """ return the value of a dictionary key
    """
    try:
        return dictionary[key]
    except (KeyError, IndexError):
        return ''

@register.filter
def format_build_date(completed_on):
    now = timezone.now()
    delta = now - completed_on

    if delta.days >= 1:
        return True

@register.filter
def is_shaid(text):
    """ return True if text length is 40 characters and all hex-digits
    """
    try:
        int(text, 16)
        if len(text) == 40:
            return True
        return False
    except ValueError:
        return False

@register.filter
def cut_path_prefix(fullpath, prefixes):
    """Cut path prefix from fullpath."""
    for prefix in prefixes:
        if fullpath.startswith(prefix):
            return relpath(fullpath, prefix)
    return fullpath
