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

from django.views.generic import View, TemplateView
from django.views.decorators.cache import cache_control
from django.shortcuts import HttpResponse
from django.http import HttpResponseBadRequest
from django.core import serializers
from django.core.cache import cache
from django.core.paginator import Paginator, EmptyPage
from django.db.models import Q
from orm.models import Project, ProjectLayer, Layer_Version
from django.template import Context, Template
from django.core.serializers.json import DjangoJSONEncoder
from django.core.exceptions import FieldError
from django.conf.urls import url, patterns

import types
import json
import collections
import operator
import re
import urllib

import logging
logger = logging.getLogger("toaster")

from toastergui.views import objtojson
from toastergui.tablefilter import TableFilterMap

class ToasterTable(TemplateView):
    def __init__(self, *args, **kwargs):
        super(ToasterTable, self).__init__()
        if 'template_name' in kwargs:
            self.template_name = kwargs['template_name']
        self.title = "Table"
        self.queryset = None
        self.columns = []

        # map from field names to Filter instances
        self.filter_map = TableFilterMap()

        self.total_count = 0
        self.static_context_extra = {}
        self.empty_state = "Sorry - no data found"
        self.default_orderby = ""

        # add the "id" column, undisplayable, by default
        self.add_column(title="Id",
                        displayable=False,
                        orderable=True,
                        field_name="id")

    # prevent HTTP caching of table data
    @cache_control(must_revalidate=True, max_age=0, no_store=True, no_cache=True)
    def dispatch(self, *args, **kwargs):
        return super(ToasterTable, self).dispatch(*args, **kwargs)

    def get_context_data(self, **kwargs):
        context = super(ToasterTable, self).get_context_data(**kwargs)
        context['title'] = self.title
        context['table_name'] =  type(self).__name__.lower()

        return context


    def get(self, request, *args, **kwargs):
        if request.GET.get('format', None) == 'json':

            self.setup_queryset(*args, **kwargs)
            # Put the project id into the context for the static_data_template
            if 'pid' in kwargs:
                self.static_context_extra['pid'] = kwargs['pid']

            cmd = request.GET.get('cmd', None)
            if cmd and 'filterinfo' in cmd:
                data = self.get_filter_info(request, **kwargs)
            else:
                # If no cmd is specified we give you the table data
                data = self.get_data(request, **kwargs)

            return HttpResponse(data, content_type="application/json")

        return super(ToasterTable, self).get(request, *args, **kwargs)

    def get_filter_info(self, request, **kwargs):
        data = None

        self.setup_filters(**kwargs)

        search = request.GET.get("search", None)
        if search:
            self.apply_search(search)

        name = request.GET.get("name", None)
        table_filter = self.filter_map.get_filter(name)
        return json.dumps(table_filter.to_json(self.queryset),
                          indent=2,
                          cls=DjangoJSONEncoder)

    def setup_columns(self, *args, **kwargs):
        """ function to implement in the subclass which sets up the columns """
        pass
    def setup_filters(self, *args, **kwargs):
        """ function to implement in the subclass which sets up the filters """
        pass
    def setup_queryset(self, *args, **kwargs):
        """ function to implement in the subclass which sets up the queryset"""
        pass

    def add_filter(self, table_filter):
        """Add a filter to the table.

        Args:
            table_filter: Filter instance
        """
        self.filter_map.add_filter(table_filter.name, table_filter)

    def add_column(self, title="", help_text="",
                   orderable=False, hideable=True, hidden=False,
                   field_name="", filter_name=None, static_data_name=None,
                   displayable=True, computation=None,
                   static_data_template=None):
        """Add a column to the table.

        Args:
            title (str): Title for the table header
            help_text (str): Optional help text to describe the column
            orderable (bool): Whether the column can be ordered.
                We order on the field_name.
            hideable (bool): Whether the user can hide the column
            hidden (bool): Whether the column is default hidden
            field_name (str or list): field(s) required for this column's data
            static_data_name (str, optional): The column's main identifier
                which will replace the field_name.
            static_data_template(str, optional): The template to be rendered
                as data
        """

        self.columns.append({'title' : title,
                             'help_text' : help_text,
                             'orderable' : orderable,
                             'hideable' : hideable,
                             'hidden' : hidden,
                             'field_name' : field_name,
                             'filter_name' : filter_name,
                             'static_data_name': static_data_name,
                             'static_data_template': static_data_template,
                             'displayable': displayable,
                             'computation': computation,
                            })

    def set_column_hidden(self, title, hidden):
        """
        Set the hidden state of the column to the value of hidden
        """
        for col in self.columns:
            if col['title'] == title:
                col['hidden'] = hidden
                break

    def set_column_hideable(self, title, hideable):
        """
        Set the hideable state of the column to the value of hideable
        """
        for col in self.columns:
            if col['title'] == title:
                col['hideable'] = hideable
                break

    def render_static_data(self, template, row):
        """Utility function to render the static data template"""

        context = {
          'extra' : self.static_context_extra,
          'data' : row,
        }

        context = Context(context)
        template = Template(template)

        return template.render(context)

    def apply_filter(self, filters, filter_value, **kwargs):
        """
        Apply a filter submitted in the querystring to the ToasterTable

        filters: (str) in the format:
          '<filter name>:<action name>'
        filter_value: (str) parameters to pass to the named filter

        <filter name> and <action name> are used to look up the correct filter
        in the ToasterTable's filter map; the <action params> are set on
        TableFilterAction* before its filter is applied and may modify the
        queryset returned by the filter
        """
        self.setup_filters(**kwargs)

        try:
            filter_name, action_name = filters.split(':')
            action_params = urllib.unquote_plus(filter_value)
        except ValueError:
            return

        if "all" in action_name:
            return

        try:
            table_filter = self.filter_map.get_filter(filter_name)
            action = table_filter.get_action(action_name)
            action.set_filter_params(action_params)
            self.queryset = action.filter(self.queryset)
        except KeyError:
            # pass it to the user - programming error here
            raise

    def apply_orderby(self, orderby):
        # Note that django will execute this when we try to retrieve the data
        self.queryset = self.queryset.order_by(orderby)

    def apply_search(self, search_term):
        """Creates a query based on the model's search_allowed_fields"""

        if not hasattr(self.queryset.model, 'search_allowed_fields'):
            raise Exception("Search fields aren't defined in the model %s"
                           % self.queryset.model)

        search_queries = []
        for st in search_term.split(" "):
            q_map = [Q(**{field + '__icontains': st})
                     for field in self.queryset.model.search_allowed_fields]

            search_queries.append(reduce(operator.or_, q_map))

        search_queries = reduce(operator.and_, search_queries)

        self.queryset = self.queryset.filter(search_queries)


    def get_data(self, request, **kwargs):
        """
        Returns the data for the page requested with the specified
        parameters applied

        filters: filter and action name, e.g. "outcome:build_succeeded"
        filter_value: value to pass to the named filter+action, e.g. "on"
        (for a toggle filter) or "2015-12-11,2015-12-12" (for a date range filter)
        """

        page_num = request.GET.get("page", 1)
        limit = request.GET.get("limit", 10)
        search = request.GET.get("search", None)
        filters = request.GET.get("filter", None)
        filter_value = request.GET.get("filter_value", "on")
        orderby = request.GET.get("orderby", None)
        nocache = request.GET.get("nocache", None)

        # Make a unique cache name
        cache_name = self.__class__.__name__

        for key, val in request.GET.iteritems():
            if key == 'nocache':
                continue
            cache_name = cache_name + str(key) + str(val)

        for key, val in kwargs.iteritems():
            cache_name = cache_name + str(key) + str(val)

        # No special chars allowed in the cache name apart from dash
        cache_name = re.sub(r'[^A-Za-z0-9-]', "", cache_name)

        if nocache:
            cache.delete(cache_name)

        data = cache.get(cache_name)

        if data:
            logger.debug("Got cache data for table '%s'" % self.title)
            return data

        self.setup_columns(**kwargs)

        if search:
            self.apply_search(search)
        if filters:
            self.apply_filter(filters, filter_value, **kwargs)
        if orderby:
            self.apply_orderby(orderby)

        paginator = Paginator(self.queryset, limit)

        try:
            page = paginator.page(page_num)
        except EmptyPage:
            page = paginator.page(1)

        data = {
            'total' : self.queryset.count(),
            'default_orderby' : self.default_orderby,
            'columns' : self.columns,
            'rows' : [],
            'error' : "ok",
        }

        try:
            for row in page.object_list:
                #Use collection to maintain the order
                required_data = collections.OrderedDict()

                for col in self.columns:
                    field = col['field_name']
                    if not field:
                        field = col['static_data_name']
                    if not field:
                        raise Exception("Must supply a field_name or static_data_name for column %s.%s" % (self.__class__.__name__,col))
                    # Check if we need to process some static data
                    if "static_data_name" in col and col['static_data_name']:
                        required_data["static:%s" % col['static_data_name']] = self.render_static_data(col['static_data_template'], row)

                        # Overwrite the field_name with static_data_name
                        # so that this can be used as the html class name

                        col['field_name'] = col['static_data_name']

                    # compute the computation on the raw data if needed
                    model_data = row
                    if col['computation']:
                        model_data = col['computation'](row)
                    else:
                        # Traverse to any foriegn key in the object hierachy
                        for subfield in field.split("__"):
                            if hasattr(model_data, subfield):
                                model_data = getattr(model_data, subfield)
                        # The field could be a function on the model so check
                        # If it is then call it
                        if isinstance(model_data, types.MethodType):
                          model_data = model_data()

                    required_data[col['field_name']] = model_data

                data['rows'].append(required_data)

        except FieldError:
            # pass  it to the user - programming-error here
            raise
        data = json.dumps(data, indent=2, default=objtojson)
        cache.set(cache_name, data, 60*30)

        return data



class ToasterTypeAhead(View):
    """ A typeahead mechanism to support the front end typeahead widgets """
    MAX_RESULTS = 6

    class MissingFieldsException(Exception):
        pass

    def __init__(self, *args, **kwargs):
        super(ToasterTypeAhead, self).__init__()

    def get(self, request, *args, **kwargs):
        def response(data):
            return HttpResponse(json.dumps(data,
                                           indent=2,
                                           cls=DjangoJSONEncoder),
                                content_type="application/json")

        error = "ok"

        search_term = request.GET.get("search", None)
        if search_term == None:
            # We got no search value so return empty reponse
            return response({'error' : error , 'results': []})

        try:
            prj = Project.objects.get(pk=kwargs['pid'])
        except KeyError:
            prj = None

        results = self.apply_search(search_term, prj, request)[:ToasterTypeAhead.MAX_RESULTS]

        if len(results) > 0:
            try:
                self.validate_fields(results[0])
            except MissingFieldsException as e:
                error = e

        data = { 'results' : results,
                'error' : error,
               }

        return response(data)

    def validate_fields(self, result):
        if 'name' in result == False or 'detail' in result == False:
            raise MissingFieldsException("name and detail are required fields")

    def apply_search(self, search_term, prj):
        """ Override this function to implement search. Return an array of
        dictionaries with a minium of a name and detail field"""
        pass
