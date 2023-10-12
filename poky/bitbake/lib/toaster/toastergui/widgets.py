#
# BitBake Toaster Implementation
#
# Copyright (C) 2015        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.views.decorators.cache import cache_control
from django.shortcuts import HttpResponse
from django.core.cache import cache
from django.core.paginator import Paginator, EmptyPage
from django.db.models import Q
from orm.models import Project, Build
from django.template import Context, Template
from django.template import VariableDoesNotExist
from django.template import TemplateSyntaxError
from django.core.serializers.json import DjangoJSONEncoder
from django.core.exceptions import FieldError
from django.utils import timezone
from toastergui.templatetags.projecttags import sectohms, get_tasks
from toastergui.templatetags.projecttags import json as template_json
from django.http import JsonResponse
from django.urls import reverse

import types
import json
import collections
import re
import os

from toastergui.tablefilter import TableFilterMap
from toastermain.logs import log_view_mixin

try:
    from urllib import unquote_plus
except ImportError:
    from urllib.parse import unquote_plus

import logging
logger = logging.getLogger("toaster")


class NoFieldOrDataName(Exception):
    pass


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

    # prevent HTTP caching of table data
    @method_decorator(cache_control(must_revalidate=True,
                   max_age=0, no_store=True, no_cache=True))
    def dispatch(self, *args, **kwargs):
        return super(ToasterTable, self).dispatch(*args, **kwargs)

    def get_context_data(self, **kwargs):
        context = super(ToasterTable, self).get_context_data(**kwargs)
        context['title'] = self.title
        context['table_name'] = type(self).__name__.lower()
        context['empty_state'] = self.empty_state

        # global variables
        context['project_enable'] = ('1' == os.environ.get('TOASTER_BUILDSERVER'))
        try:
            context['project_specific'] = ('1' == os.environ.get('TOASTER_PROJECTSPECIFIC'))
        except:
            context['project_specific'] = ''

        return context

    @log_view_mixin
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
        """ function to implement in the subclass which sets up
        the columns """
        pass

    def setup_filters(self, *args, **kwargs):
        """ function to implement in the subclass which sets up the
        filters """
        pass

    def setup_queryset(self, *args, **kwargs):
        """ function to implement in the subclass which sets up the
        queryset"""
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

        self.columns.append({'title': title,
                             'help_text': help_text,
                             'orderable': orderable,
                             'hideable': hideable,
                             'hidden': hidden,
                             'field_name': field_name,
                             'filter_name': filter_name,
                             'static_data_name': static_data_name,
                             'static_data_template': static_data_template})

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
          'extra': self.static_context_extra,
          'data': row,
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
            action_params = unquote_plus(filter_value)
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

        search_queries = None
        for st in search_term.split(" "):
            queries = None
            for field in self.queryset.model.search_allowed_fields:
                query = Q(**{field + '__icontains': st})
                if queries:
                    queries |= query
                else:
                    queries = query

            if search_queries:
                search_queries &= queries
            else:
                search_queries = queries

        self.queryset = self.queryset.filter(search_queries)

    def get_data(self, request, **kwargs):
        """
        Returns the data for the page requested with the specified
        parameters applied

        filters: filter and action name, e.g. "outcome:build_succeeded"
        filter_value: value to pass to the named filter+action, e.g. "on"
        (for a toggle filter) or "2015-12-11,2015-12-12"
        (for a date range filter)
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

        for key, val in request.GET.items():
            if key == 'nocache':
                continue
            cache_name = cache_name + str(key) + str(val)

        for key, val in kwargs.items():
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

        self.apply_orderby('pk')
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
            'total': self.queryset.count(),
            'default_orderby': self.default_orderby,
            'columns': self.columns,
            'rows': [],
            'error': "ok",
        }

        try:
            for model_obj in page.object_list:
                # Use collection to maintain the order
                required_data = collections.OrderedDict()

                for col in self.columns:
                    field = col['field_name']
                    if not field:
                        field = col['static_data_name']
                    if not field:
                        raise NoFieldOrDataName("Must supply a field_name or"
                                                "static_data_name for column"
                                                "%s.%s" %
                                                (self.__class__.__name__, col)
                                                )

                    # Check if we need to process some static data
                    if "static_data_name" in col and col['static_data_name']:
                        # Overwrite the field_name with static_data_name
                        # so that this can be used as the html class name
                        col['field_name'] = col['static_data_name']

                        try:
                            # Render the template given
                            required_data[col['static_data_name']] = \
                                    self.render_static_data(
                                        col['static_data_template'], model_obj)
                        except (TemplateSyntaxError,
                                VariableDoesNotExist) as e:
                            logger.error("could not render template code"
                                         "%s %s %s",
                                         col['static_data_template'],
                                         e, self.__class__.__name__)
                            required_data[col['static_data_name']] =\
                                '<!--error-->'

                    else:
                        # Traverse to any foriegn key in the field
                        # e.g. recipe__layer_version__name
                        model_data = None

                        if "__" in field:
                            for subfield in field.split("__"):
                                if not model_data:
                                    # The first iteration is always going to
                                    # be on the actual model object instance.
                                    # Subsequent ones are on the result of
                                    # that. e.g. forieng key objects
                                    model_data = getattr(model_obj,
                                                         subfield)
                                else:
                                    model_data = getattr(model_data,
                                                         subfield)

                        else:
                            model_data = getattr(model_obj,
                                                 col['field_name'])

                        # We might have a model function as the field so
                        # call it to return the data needed
                        if isinstance(model_data, types.MethodType):
                            model_data = model_data()

                        required_data[col['field_name']] = model_data

                data['rows'].append(required_data)

        except FieldError:
            # pass  it to the user - programming-error here
            raise

        data = json.dumps(data, indent=2, cls=DjangoJSONEncoder)
        cache.set(cache_name, data, 60*30)

        return data


class ToasterTypeAhead(View):
    """ A typeahead mechanism to support the front end typeahead widgets """
    MAX_RESULTS = 6

    class MissingFieldsException(Exception):
        pass

    def __init__(self, *args, **kwargs):
        super(ToasterTypeAhead, self).__init__()

    @log_view_mixin
    def get(self, request, *args, **kwargs):
        def response(data):
            return HttpResponse(json.dumps(data,
                                           indent=2,
                                           cls=DjangoJSONEncoder),
                                content_type="application/json")

        error = "ok"

        search_term = request.GET.get("search", None)
        if search_term is None:
            # We got no search value so return empty reponse
            return response({'error': error, 'results': []})

        try:
            prj = Project.objects.get(pk=kwargs['pid'])
        except KeyError:
            prj = None

        results = self.apply_search(search_term,
                                    prj,
                                    request)[:ToasterTypeAhead.MAX_RESULTS]

        if len(results) > 0:
            try:
                self.validate_fields(results[0])
            except self.MissingFieldsException as e:
                error = e

        data = {'results': results,
                'error': error}

        return response(data)

    def validate_fields(self, result):
        if 'name' in result is False or 'detail' in result is False:
            raise self.MissingFieldsException(
                "name and detail are required fields")

    def apply_search(self, search_term, prj):
        """ Override this function to implement search. Return an array of
        dictionaries with a minium of a name and detail field"""
        pass


class MostRecentBuildsView(View):
    def _was_yesterday_or_earlier(self, completed_on):
        now = timezone.now()
        delta = now - completed_on

        if delta.days >= 1:
            return True

        return False

    @log_view_mixin
    def get(self, request, *args, **kwargs):
        """
        Returns a list of builds in JSON format.
        """
        project = None

        project_id = request.GET.get('project_id', None)
        if project_id:
            try:
                project = Project.objects.get(pk=project_id)
            except:
                # if project lookup fails, assume no project
                pass

        recent_build_objs = Build.get_recent(project)
        recent_builds = []

        for build_obj in recent_build_objs:
            dashboard_url = reverse('builddashboard', args=(build_obj.pk,))
            buildtime_url = reverse('buildtime', args=(build_obj.pk,))
            rebuild_url = \
                reverse('xhr_buildrequest', args=(build_obj.project.pk,))
            cancel_url = \
                reverse('xhr_buildrequest', args=(build_obj.project.pk,))

            build = {}
            build['id'] = build_obj.pk
            build['dashboard_url'] = dashboard_url

            buildrequest_id = None
            if hasattr(build_obj, 'buildrequest'):
                buildrequest_id = build_obj.buildrequest.pk
            build['buildrequest_id'] = buildrequest_id

            if build_obj.recipes_to_parse > 0:
                build['recipes_parsed_percentage'] = \
                    int((build_obj.recipes_parsed /
                         build_obj.recipes_to_parse) * 100)
            else:
                build['recipes_parsed_percentage'] = 0
            if build_obj.repos_to_clone > 0:
                build['repos_cloned_percentage'] = \
                    int((build_obj.repos_cloned /
                         build_obj.repos_to_clone) * 100)
            else:
                build['repos_cloned_percentage'] = 0

            build['progress_item'] = build_obj.progress_item

            tasks_complete_percentage = 0
            if build_obj.outcome in (Build.SUCCEEDED, Build.FAILED):
                tasks_complete_percentage = 100
            elif build_obj.outcome == Build.IN_PROGRESS:
                tasks_complete_percentage = build_obj.completeper()
            build['tasks_complete_percentage'] = tasks_complete_percentage

            build['state'] = build_obj.get_state()

            build['errors'] = build_obj.errors.count()
            build['dashboard_errors_url'] = dashboard_url + '#errors'

            build['warnings'] = build_obj.warnings.count()
            build['dashboard_warnings_url'] = dashboard_url + '#warnings'

            build['buildtime'] = sectohms(build_obj.timespent_seconds)
            build['buildtime_url'] = buildtime_url

            build['rebuild_url'] = rebuild_url
            build['cancel_url'] = cancel_url

            build['is_default_project_build'] = build_obj.project.is_default

            build['build_targets_json'] = \
                template_json(get_tasks(build_obj.target_set.all()))

            # convert completed_on time to user's timezone
            completed_on = timezone.localtime(build_obj.completed_on)

            completed_on_template = '%H:%M'
            if self._was_yesterday_or_earlier(completed_on):
                completed_on_template = '%d/%m/%Y ' + completed_on_template
            build['completed_on'] = completed_on.strftime(
                completed_on_template)

            targets = []
            target_objs = build_obj.get_sorted_target_list()
            for target_obj in target_objs:
                if target_obj.task:
                    targets.append(target_obj.target + ':' + target_obj.task)
                else:
                    targets.append(target_obj.target)
            build['targets'] = ' '.join(targets)

            # abbreviated form of the full target list
            abbreviated_targets = ''
            num_targets = len(targets)
            if num_targets > 0:
                abbreviated_targets = targets[0]
            if num_targets > 1:
                abbreviated_targets += (' +%s' % (num_targets - 1))
            build['targets_abbreviated'] = abbreviated_targets

            recent_builds.append(build)

        return JsonResponse(recent_builds, safe=False)
