#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2015        Intel Corporation
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

from django.db.models import Q, Max, Min
from django.utils import dateparse, timezone
from datetime import timedelta

class TableFilter(object):
    """
    Stores a filter for a named field, and can retrieve the action
    requested from the set of actions for that filter;
    the order in which actions are added governs the order in which they
    are returned in the JSON for the filter
    """

    def __init__(self, name, title):
        self.name = name
        self.title = title
        self.__filter_action_map = {}

        # retains the ordering of actions
        self.__filter_action_keys = []

    def add_action(self, action):
        self.__filter_action_keys.append(action.name)
        self.__filter_action_map[action.name] = action

    def get_action(self, action_name):
        return self.__filter_action_map[action_name]

    def to_json(self, queryset):
        """
        Dump all filter actions as an object which can be JSON serialised;
        this is used to generate the JSON for processing in
        table.js / filterOpenClicked()
        """
        filter_actions = []

        # add the "all" pseudo-filter action, which just selects the whole
        # queryset
        filter_actions.append({
            'action_name' : 'all',
            'title' : 'All',
            'type': 'toggle',
            'count' : queryset.count()
        })

        # add other filter actions
        for action_name in self.__filter_action_keys:
            filter_action = self.__filter_action_map[action_name]
            obj = filter_action.to_json(queryset)
            obj['action_name'] = action_name
            filter_actions.append(obj)

        return {
            'name': self.name,
            'title': self.title,
            'filter_actions': filter_actions
        }

class TableFilterQueryHelper(object):
    def dateStringsToQ(self, field_name, date_from_str, date_to_str):
        """
        Convert the date strings from_date_str and to_date_str into a
        set of args in the form

          {'<field_name>__gte': <date from>, '<field_name>__lte': <date to>}

        where date_from and date_to are Django-timezone-aware dates; then
        convert that into a Django Q object

        Returns the Q object based on those criteria
        """

        # one of the values required for the filter is missing, so set
        # it to the one which was supplied
        if date_from_str == '':
            date_from_str = date_to_str
        elif date_to_str == '':
            date_to_str = date_from_str

        date_from_naive = dateparse.parse_datetime(date_from_str + ' 00:00:00')
        date_to_naive = dateparse.parse_datetime(date_to_str + ' 23:59:59')

        tz = timezone.get_default_timezone()
        date_from = timezone.make_aware(date_from_naive, tz)
        date_to = timezone.make_aware(date_to_naive, tz)

        args = {}
        args[field_name + '__gte'] = date_from
        args[field_name + '__lte'] = date_to

        return Q(**args)

class TableFilterAction(object):
    """
    A filter action which displays in the filter popup for a ToasterTable
    and uses an associated QuerysetFilter to filter the queryset for that
    ToasterTable
    """

    def __init__(self, name, title, criteria):
        self.name = name
        self.title = title
        self.criteria = criteria

        # set in subclasses
        self.type = None

    def set_filter_params(self, params):
        """
        params: (str) a string of extra parameters for the action;
        the structure of this string depends on the type of action;
        it's ignored for a toggle filter action, which is just on or off
        """
        pass

    def filter(self, queryset):
        if self.criteria:
            return queryset.filter(self.criteria)
        else:
            return queryset

    def to_json(self, queryset):
        """ Dump as a JSON object """
        return {
            'title': self.title,
            'type': self.type,
            'count': self.filter(queryset).count()
        }

class TableFilterActionToggle(TableFilterAction):
    """
    A single filter action which will populate one radio button of
    a ToasterTable filter popup; this filter can either be on or off and
    has no other parameters
    """

    def __init__(self, *args):
        super(TableFilterActionToggle, self).__init__(*args)
        self.type = 'toggle'

class TableFilterActionDay(TableFilterAction):
    """
    A filter action which filters according to the named datetime field and a
    string representing a day ("today" or "yesterday")
    """

    TODAY = 'today'
    YESTERDAY = 'yesterday'

    def __init__(self, name, title, field, day,
    query_helper = TableFilterQueryHelper()):
        """
        field: (string) the datetime field to filter by
        day: (string) "today" or "yesterday"
        """
        super(TableFilterActionDay, self).__init__(name, title, None)
        self.type = 'day'
        self.field = field
        self.day = day
        self.query_helper = query_helper

    def filter(self, queryset):
        """
        Apply the day filtering before returning the queryset;
        this is done here as the value of the filter criteria changes
        depending on when the filtering is applied
        """

        now = timezone.now()

        if self.day == self.YESTERDAY:
            increment = timedelta(days=1)
            wanted_date = now - increment
        else:
            wanted_date = now

        wanted_date_str = wanted_date.strftime('%Y-%m-%d')

        self.criteria = self.query_helper.dateStringsToQ(
            self.field,
            wanted_date_str,
            wanted_date_str
        )

        return queryset.filter(self.criteria)

class TableFilterActionDateRange(TableFilterAction):
    """
    A filter action which will filter the queryset by a date range.
    The date range can be set via set_params()
    """

    def __init__(self, name, title, field,
    query_helper = TableFilterQueryHelper()):
        """
        field: (string) the field to find the max/min range from in the queryset
        """
        super(TableFilterActionDateRange, self).__init__(
            name,
            title,
            None
        )

        self.type = 'daterange'
        self.field = field
        self.query_helper = query_helper

    def set_filter_params(self, params):
        """
        This filter depends on the user selecting some input, so it needs
        to have its parameters set before its queryset is filtered

        params: (str) a string of extra parameters for the filtering
        in the format "2015-12-09,2015-12-11" (from,to); this is passed in the
        querystring and used to set the criteria on the QuerysetFilter
        associated with this action
        """

        # if params are invalid, return immediately, resetting criteria
        # on the QuerysetFilter
        try:
            date_from_str, date_to_str = params.split(',')
        except ValueError:
            self.criteria = None
            return

        # one of the values required for the filter is missing, so set
        # it to the one which was supplied
        self.criteria = self.query_helper.dateStringsToQ(
            self.field,
            date_from_str,
            date_to_str
        )

    def to_json(self, queryset):
        """ Dump as a JSON object """
        data = super(TableFilterActionDateRange, self).to_json(queryset)

        # additional data about the date range covered by the queryset's
        # records, retrieved from its <field> column
        data['min'] = queryset.aggregate(Min(self.field))[self.field + '__min']
        data['max'] = queryset.aggregate(Max(self.field))[self.field + '__max']

        # a range filter has a count of None, as the number of records it
        # will select depends on the date range entered and we don't know
        # that ahead of time
        data['count'] = None

        return data

class TableFilterMap(object):
    """
    Map from field names to TableFilter objects for those fields
    """

    def __init__(self):
        self.__filters = {}

    def add_filter(self, filter_name, table_filter):
        """ table_filter is an instance of Filter """
        self.__filters[filter_name] = table_filter

    def get_filter(self, filter_name):
        return self.__filters[filter_name]

    def to_json(self, queryset):
        data = {}

        for filter_name, table_filter in self.__filters.iteritems():
            data[filter_name] = table_filter.to_json()

        return data
