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


import re

from django.db.models import F, Q, Sum
from django.db import IntegrityError
from django.shortcuts import render, redirect, get_object_or_404
from orm.models import Build, Target, Task, Layer, Layer_Version, Recipe
from orm.models import LogMessage, Variable, Package_Dependency, Package
from orm.models import Task_Dependency, Package_File
from orm.models import Target_Installed_Package, Target_File
from orm.models import TargetKernelFile, TargetSDKFile, Target_Image_File
from orm.models import BitbakeVersion, CustomImageRecipe

from django.core.urlresolvers import reverse, resolve
from django.core.exceptions import MultipleObjectsReturned, ObjectDoesNotExist
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.http import HttpResponseNotFound
from django.utils import timezone
from datetime import timedelta, datetime
from toastergui.templatetags.projecttags import json as jsonfilter
from decimal import Decimal
import json
import os
from os.path import dirname
import mimetypes

import logging

logger = logging.getLogger("toaster")


class MimeTypeFinder(object):
    # setting this to False enables additional non-standard mimetypes
    # to be included in the guess
    _strict = False

    # returns the mimetype for a file path as a string,
    # or 'application/octet-stream' if the type couldn't be guessed
    @classmethod
    def get_mimetype(self, path):
        guess = mimetypes.guess_type(path, self._strict)
        guessed_type = guess[0]
        if guessed_type == None:
            guessed_type = 'application/octet-stream'
        return guessed_type

# all new sessions should come through the landing page;
# determine in which mode we are running in, and redirect appropriately
def landing(request):
    # in build mode, we redirect to the command-line builds page
    # if there are any builds for the default (cli builds) project
    default_project = Project.objects.get_or_create_default_project()
    default_project_builds = Build.objects.filter(project = default_project)

    # we only redirect to projects page if there is a user-generated project
    num_builds = Build.objects.all().count()
    user_projects = Project.objects.filter(is_default = False)
    has_user_project = user_projects.count() > 0

    if num_builds == 0 and has_user_project:
        return redirect(reverse('all-projects'), permanent = False)

    if num_builds > 0:
        return redirect(reverse('all-builds'), permanent = False)

    context = {'lvs_nos' : Layer_Version.objects.all().count()}

    return render(request, 'landing.html', context)

def objtojson(obj):
    from django.db.models.query import QuerySet
    from django.db.models import Model

    if isinstance(obj, datetime):
        return obj.isoformat()
    elif isinstance(obj, timedelta):
        return obj.total_seconds()
    elif isinstance(obj, QuerySet) or isinstance(obj, set):
        return list(obj)
    elif isinstance(obj, Decimal):
        return str(obj)
    elif type(obj).__name__ == "RelatedManager":
        return [x.pk for x in obj.all()]
    elif hasattr( obj, '__dict__') and isinstance(obj, Model):
        d = obj.__dict__
        nd = dict(d)
        for di in d.keys():
            if di.startswith("_"):
                del nd[di]
            elif isinstance(d[di], Model):
                nd[di] = d[di].pk
            elif isinstance(d[di], int) and hasattr(obj, "get_%s_display" % di):
                nd[di] = getattr(obj, "get_%s_display" % di)()
        return nd
    elif isinstance( obj, type(lambda x:x)):
        import inspect
        return inspect.getsourcelines(obj)[0]
    else:
        raise TypeError("Unserializable object %s (%s) of type %s" % ( obj, dir(obj), type(obj)))


def _template_renderer(template):
    def func_wrapper(view):
        def returned_wrapper(request, *args, **kwargs):
            try:
                context = view(request, *args, **kwargs)
            except RedirectException as e:
                return e.get_redirect_response()

            if request.GET.get('format', None) == 'json':
                # objects is a special keyword - it's a Page, but we need the actual objects here
                # in XHR, the objects come in the "rows" property
                if "objects" in context:
                    context["rows"] = context["objects"].object_list
                    del context["objects"]

                # we're about to return; to keep up with the XHR API, we set the error to OK
                context["error"] = "ok"

                return HttpResponse(jsonfilter(context, default=objtojson ),
                            content_type = "application/json; charset=utf-8")
            else:
                return render(request, template, context)
        return returned_wrapper
    return func_wrapper


def _lv_to_dict(prj, x = None):
    if x is None:
        def wrapper(x):
            return _lv_to_dict(prj, x)
        return wrapper

    return {"id": x.pk,
            "name": x.layer.name,
            "tooltip": "%s | %s" % (x.layer.vcs_url,x.get_vcs_reference()),
            "detail": "(%s" % x.layer.vcs_url + (")" if x.release == None else " | "+x.get_vcs_reference()+")"),
            "giturl": x.layer.vcs_url,
            "layerdetailurl" : reverse('layerdetails', args=(prj.id,x.pk)),
            "revision" : x.get_vcs_reference(),
           }


def _build_page_range(paginator, index = 1):
    try:
        page = paginator.page(index)
    except PageNotAnInteger:
        page = paginator.page(1)
    except  EmptyPage:
        page = paginator.page(paginator.num_pages)


    page.page_range = [page.number]
    crt_range = 0
    for i in range(1,5):
        if (page.number + i) <= paginator.num_pages:
            page.page_range = page.page_range + [ page.number + i]
            crt_range +=1
        if (page.number - i) > 0:
            page.page_range =  [page.number -i] + page.page_range
            crt_range +=1
        if crt_range == 4:
            break
    return page


def _verify_parameters(g, mandatory_parameters):
    miss = []
    for mp in mandatory_parameters:
        if not mp in g:
            miss.append(mp)
    if len(miss):
        return miss
    return None

def _redirect_parameters(view, g, mandatory_parameters, *args, **kwargs):
    try:
        from urllib import unquote, urlencode
    except ImportError:
        from urllib.parse import unquote, urlencode
    url = reverse(view, kwargs=kwargs)
    params = {}
    for i in g:
        params[i] = g[i]
    for i in mandatory_parameters:
        if not i in params:
            params[i] = unquote(str(mandatory_parameters[i]))

    return redirect(url + "?%s" % urlencode(params), permanent = False, **kwargs)

class RedirectException(Exception):
    def __init__(self, view, g, mandatory_parameters, *args, **kwargs):
        super(RedirectException, self).__init__()
        self.view = view
        self.g = g
        self.mandatory_parameters = mandatory_parameters
        self.oargs  = args
        self.okwargs = kwargs

    def get_redirect_response(self):
        return _redirect_parameters(self.view, self.g, self.mandatory_parameters, self.oargs, **self.okwargs)

FIELD_SEPARATOR = ":"
AND_VALUE_SEPARATOR = "!"
OR_VALUE_SEPARATOR = "|"
DESCENDING = "-"

def __get_q_for_val(name, value):
    if "OR" in value or "AND" in value:
        result = None
        for x in value.split("OR"):
             x = __get_q_for_val(name, x)
             result = result | x if result else x
        return result
    if "AND" in value:
        result = None
        for x in value.split("AND"):
            x = __get_q_for_val(name, x)
            result = result & x if result else x
        return result
    if value.startswith("NOT"):
        value = value[3:]
        if value == 'None':
            value = None
        kwargs = { name : value }
        return ~Q(**kwargs)
    else:
        if value == 'None':
            value = None
        kwargs = { name : value }
        return Q(**kwargs)

def _get_filtering_query(filter_string):

    search_terms = filter_string.split(FIELD_SEPARATOR)
    and_keys = search_terms[0].split(AND_VALUE_SEPARATOR)
    and_values = search_terms[1].split(AND_VALUE_SEPARATOR)

    and_query = None
    for kv in zip(and_keys, and_values):
        or_keys = kv[0].split(OR_VALUE_SEPARATOR)
        or_values = kv[1].split(OR_VALUE_SEPARATOR)
        query = None
        for key, val in zip(or_keys, or_values):
            x = __get_q_for_val(key, val)
            query = query | x if query else x

        and_query = and_query & query if and_query else query

    return and_query

def _get_toggle_order(request, orderkey, toggle_reverse = False):
    if toggle_reverse:
        return "%s:+" % orderkey if request.GET.get('orderby', "") == "%s:-" % orderkey else "%s:-" % orderkey
    else:
        return "%s:-" % orderkey if request.GET.get('orderby', "") == "%s:+" % orderkey else "%s:+" % orderkey

def _get_toggle_order_icon(request, orderkey):
    if request.GET.get('orderby', "") == "%s:+"%orderkey:
        return "down"
    elif request.GET.get('orderby', "") == "%s:-"%orderkey:
        return "up"
    else:
        return None

# we check that the input comes in a valid form that we can recognize
def _validate_input(field_input, model):

    invalid = None

    if field_input:
        field_input_list = field_input.split(FIELD_SEPARATOR)

        # Check we have only one colon
        if len(field_input_list) != 2:
            invalid = "We have an invalid number of separators: " + field_input + " -> " + str(field_input_list)
            return None, invalid

        # Check we have an equal number of terms both sides of the colon
        if len(field_input_list[0].split(AND_VALUE_SEPARATOR)) != len(field_input_list[1].split(AND_VALUE_SEPARATOR)):
            invalid = "Not all arg names got values"
            return None, invalid + str(field_input_list)

        # Check we are looking for a valid field
        valid_fields = model._meta.get_all_field_names()
        for field in field_input_list[0].split(AND_VALUE_SEPARATOR):
            if True in [field.startswith(x) for x in valid_fields]:
                break
        else:
           return None, (field, valid_fields)

    return field_input, invalid

# uses search_allowed_fields in orm/models.py to create a search query
# for these fields with the supplied input text
def _get_search_results(search_term, queryset, model):
    search_object = None
    for st in search_term.split(" "):
        queries = None
        for field in model.search_allowed_fields:
            query = Q(**{field + '__icontains': st})
            queries = queries | query if queries else query

        search_object = search_object & queries if search_object else queries
    queryset = queryset.filter(search_object)

    return queryset


# function to extract the search/filter/ordering parameters from the request
# it uses the request and the model to validate input for the filter and orderby values
def _search_tuple(request, model):
    ordering_string, invalid = _validate_input(request.GET.get('orderby', ''), model)
    if invalid:
        raise BaseException("Invalid ordering model:" + str(model) + str(invalid))

    filter_string, invalid = _validate_input(request.GET.get('filter', ''), model)
    if invalid:
        raise BaseException("Invalid filter " + str(invalid))

    search_term = request.GET.get('search', '')
    return (filter_string, search_term, ordering_string)


# returns a lazy-evaluated queryset for a filter/search/order combination
def _get_queryset(model, queryset, filter_string, search_term, ordering_string, ordering_secondary=''):
    if filter_string:
        filter_query = _get_filtering_query(filter_string)
        queryset = queryset.filter(filter_query)
    else:
        queryset = queryset.all()

    if search_term:
        queryset = _get_search_results(search_term, queryset, model)

    if ordering_string:
        column, order = ordering_string.split(':')
        if column == re.sub('-','',ordering_secondary):
            ordering_secondary=''
        if order.lower() == DESCENDING:
            column = '-' + column
        if ordering_secondary:
            queryset = queryset.order_by(column, ordering_secondary)
        else:
            queryset = queryset.order_by(column)

    # insure only distinct records (e.g. from multiple search hits) are returned
    return queryset.distinct()

# returns the value of entries per page and the name of the applied sorting field.
# if the value is given explicitly as a GET parameter it will be the first selected,
# otherwise the cookie value will be used.
def _get_parameters_values(request, default_count, default_order):
    current_url = resolve(request.path_info).url_name
    pagesize = request.GET.get('count', request.session.get('%s_count' % current_url, default_count))
    orderby = request.GET.get('orderby', request.session.get('%s_orderby' % current_url, default_order))
    return (pagesize, orderby)


# set cookies for parameters. this is usefull in case parameters are set
# manually from the GET values of the link
def _set_parameters_values(pagesize, orderby, request):
    from django.core.urlresolvers import resolve
    current_url = resolve(request.path_info).url_name
    request.session['%s_count' % current_url] = pagesize
    request.session['%s_orderby' % current_url] =orderby

# date range: normalize GUI's dd/mm/yyyy to date object
def _normalize_input_date(date_str,default):
    date_str=re.sub('/', '-', date_str)
    # accept dd/mm/yyyy to d/m/yy
    try:
        date_in = datetime.strptime(date_str, "%d-%m-%Y")
    except ValueError:
        # courtesy try with two digit year
        try:
            date_in = datetime.strptime(date_str, "%d-%m-%y")
        except ValueError:
            return default
    date_in = date_in.replace(tzinfo=default.tzinfo)
    return date_in

# convert and normalize any received date range filter, for example:
# "completed_on__gte!completed_on__lt:01/03/2015!02/03/2015_daterange" to
# "completed_on__gte!completed_on__lt:2015-03-01!2015-03-02"
def _modify_date_range_filter(filter_string):
    # was the date range radio button selected?
    if 0 >  filter_string.find('_daterange'):
        return filter_string,''
    # normalize GUI dates to database format
    filter_string = filter_string.replace('_daterange','').replace(':','!');
    filter_list = filter_string.split('!');
    if 4 != len(filter_list):
        return filter_string
    today = timezone.localtime(timezone.now())
    date_id = filter_list[1]
    date_from = _normalize_input_date(filter_list[2],today)
    date_to = _normalize_input_date(filter_list[3],today)
    # swap dates if manually set dates are out of order
    if  date_to < date_from:
        date_to,date_from = date_from,date_to
    # convert to strings, make 'date_to' inclusive by moving to begining of next day
    date_from_str = date_from.strftime("%Y-%m-%d")
    date_to_str = (date_to+timedelta(days=1)).strftime("%Y-%m-%d")
    filter_string=filter_list[0]+'!'+filter_list[1]+':'+date_from_str+'!'+date_to_str
    daterange_selected = re.sub('__.*','', date_id)
    return filter_string,daterange_selected

def _add_daterange_context(queryset_all, request, daterange_list):
    # calculate the exact begining of local today and yesterday
    today_begin = timezone.localtime(timezone.now())
    yesterday_begin = today_begin - timedelta(days=1)
    # add daterange persistent
    context_date = {}
    context_date['last_date_from'] = request.GET.get('last_date_from',timezone.localtime(timezone.now()).strftime("%d/%m/%Y"))
    context_date['last_date_to'  ] = request.GET.get('last_date_to'  ,context_date['last_date_from'])
    # calculate the date ranges, avoid second sort for 'created'
    # fetch the respective max range from the database
    context_date['daterange_filter']=''
    for key in daterange_list:
        queryset_key = queryset_all.order_by(key)
        try:
            context_date['dateMin_'+key]=timezone.localtime(getattr(queryset_key.first(),key)).strftime("%d/%m/%Y")
        except AttributeError:
            context_date['dateMin_'+key]=timezone.localtime(timezone.now())
        try:
            context_date['dateMax_'+key]=timezone.localtime(getattr(queryset_key.last(),key)).strftime("%d/%m/%Y")
        except AttributeError:
            context_date['dateMax_'+key]=timezone.localtime(timezone.now())
    return context_date,today_begin,yesterday_begin


##
# build dashboard for a single build, coming in as argument
# Each build may contain multiple targets and each target
# may generate multiple image files. display them all.
#
def builddashboard( request, build_id ):
    template = "builddashboard.html"
    if Build.objects.filter( pk=build_id ).count( ) == 0 :
        return redirect( builds )
    build = Build.objects.get( pk = build_id );
    layerVersionId = Layer_Version.objects.filter( build = build_id );
    recipeCount = Recipe.objects.filter( layer_version__id__in = layerVersionId ).count( );
    tgts = Target.objects.filter( build_id = build_id ).order_by( 'target' );

    # set up custom target list with computed package and image data
    targets = []
    ntargets = 0

    # True if at least one target for this build has an SDK artifact
    # or image file
    has_artifacts = False

    for t in tgts:
        elem = {}
        elem['target'] = t

        target_has_images = False
        image_files = []

        npkg = 0
        pkgsz = 0
        package = None
        for package in Package.objects.filter(id__in = [x.package_id for x in t.target_installed_package_set.all()]):
            pkgsz = pkgsz + package.size
            if package.installed_name:
                npkg = npkg + 1
        elem['npkg'] = npkg
        elem['pkgsz'] = pkgsz
        ti = Target_Image_File.objects.filter(target_id = t.id)
        for i in ti:
            ndx = i.file_name.rfind('/')
            if ndx < 0:
                ndx = 0;
            f = i.file_name[ndx + 1:]
            image_files.append({
                'id': i.id,
                'path': f,
                'size': i.file_size,
                'suffix': i.suffix
            })
        if len(image_files) > 0:
            target_has_images = True
        elem['targetHasImages'] = target_has_images

        elem['imageFiles'] = image_files
        elem['target_kernel_artifacts'] = t.targetkernelfile_set.all()

        target_sdk_files = t.targetsdkfile_set.all()
        target_sdk_artifacts_count = target_sdk_files.count()
        elem['target_sdk_artifacts_count'] = target_sdk_artifacts_count
        elem['target_sdk_artifacts'] = target_sdk_files

        if target_has_images or target_sdk_artifacts_count > 0:
            has_artifacts = True

        targets.append(elem)

    ##
    # how many packages in this build - ignore anonymous ones
    #

    packageCount = 0
    packages = Package.objects.filter( build_id = build_id )
    for p in packages:
        if ( p.installed_name ):
            packageCount = packageCount + 1

    logmessages = list(LogMessage.objects.filter( build = build_id ))

    context = {
            'build'           : build,
            'project'         : build.project,
            'hasArtifacts'    : has_artifacts,
            'ntargets'        : ntargets,
            'targets'         : targets,
            'recipecount'     : recipeCount,
            'packagecount'    : packageCount,
            'logmessages'     : logmessages,
    }
    return render( request, template, context )



def generateCoveredList2( revlist = None ):
    if not revlist:
        revlist = []
    covered_list =  [ x for x in revlist if x.outcome == Task.OUTCOME_COVERED ]
    while len(covered_list):
        revlist =  [ x for x in revlist if x.outcome != Task.OUTCOME_COVERED ]
        if len(revlist) > 0:
            return revlist

        newlist = _find_task_revdep_list(covered_list)

        revlist = list(set(revlist + newlist))
        covered_list =  [ x for x in revlist if x.outcome == Task.OUTCOME_COVERED ]
    return revlist

def task( request, build_id, task_id ):
    template = "task.html"
    tasks_list = Task.objects.filter( pk=task_id )
    if tasks_list.count( ) == 0:
        return redirect( builds )
    task_object = tasks_list[ 0 ];
    dependencies = sorted(
        _find_task_dep( task_object ),
        key=lambda t:'%s_%s %s'%(t.recipe.name, t.recipe.version, t.task_name))
    reverse_dependencies = sorted(
        _find_task_revdep( task_object ),
        key=lambda t:'%s_%s %s'%( t.recipe.name, t.recipe.version, t.task_name ))
    coveredBy = '';
    if ( task_object.outcome == Task.OUTCOME_COVERED ):
#        _list = generateCoveredList( task )
        coveredBy = sorted(generateCoveredList2( _find_task_revdep( task_object ) ), key = lambda x: x.recipe.name)
    log_head = ''
    log_body = ''
    if task_object.outcome == task_object.OUTCOME_FAILED:
        pass

    uri_list= [ ]
    variables = Variable.objects.filter(build=build_id)
    v=variables.filter(variable_name='SSTATE_DIR')
    if v.count() > 0:
        uri_list.append(v[0].variable_value)
    v=variables.filter(variable_name='SSTATE_MIRRORS')
    if (v.count() > 0):
        for mirror in v[0].variable_value.split('\\n'):
            s=re.sub('.* ','',mirror.strip(' \t\n\r'))
            if len(s):
                uri_list.append(s)

    context = {
            'build'           : Build.objects.filter( pk = build_id )[ 0 ],
            'object'          : task_object,
            'task'            : task_object,
            'covered_by'      : coveredBy,
            'deps'            : dependencies,
            'rdeps'           : reverse_dependencies,
            'log_head'        : log_head,
            'log_body'        : log_body,
            'showing_matches' : False,
            'uri_list'        : uri_list,
            'task_in_tasks_table_pg':  int(task_object.order / 25) + 1
    }
    if request.GET.get( 'show_matches', "" ):
        context[ 'showing_matches' ] = True
        context[ 'matching_tasks' ] = Task.objects.filter(
            sstate_checksum=task_object.sstate_checksum ).filter(
            build__completed_on__lt=task_object.build.completed_on).exclude(
            order__isnull=True).exclude(outcome=Task.OUTCOME_NA).order_by('-build__completed_on')

    return render( request, template, context )

def recipe(request, build_id, recipe_id, active_tab="1"):
    template = "recipe.html"
    if Recipe.objects.filter(pk=recipe_id).count() == 0 :
        return redirect(builds)

    recipe_object = Recipe.objects.get(pk=recipe_id)
    layer_version = Layer_Version.objects.get(pk=recipe_object.layer_version_id)
    layer  = Layer.objects.get(pk=layer_version.layer_id)
    tasks_list  = Task.objects.filter(recipe_id = recipe_id, build_id = build_id).exclude(order__isnull=True).exclude(task_name__endswith='_setscene').exclude(outcome=Task.OUTCOME_NA)
    package_count = Package.objects.filter(recipe_id = recipe_id).filter(build_id = build_id).filter(size__gte=0).count()

    if active_tab != '1' and active_tab != '3' and active_tab != '4' :
        active_tab = '1'
    tab_states = {'1': '', '3': '', '4': ''}
    tab_states[active_tab] = 'active'

    context = {
            'build'   : Build.objects.get(pk=build_id),
            'object'  : recipe_object,
            'layer_version' : layer_version,
            'layer'   : layer,
            'tasks'   : tasks_list,
            'package_count' : package_count,
            'tab_states' : tab_states,
    }
    return render(request, template, context)

def recipe_packages(request, build_id, recipe_id):
    template = "recipe_packages.html"
    if Recipe.objects.filter(pk=recipe_id).count() == 0 :
        return redirect(builds)

    (pagesize, orderby) = _get_parameters_values(request, 10, 'name:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby': orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'recipe_packages', request.GET, mandatory_parameters, build_id = build_id, recipe_id = recipe_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Package)

    recipe_object = Recipe.objects.get(pk=recipe_id)
    queryset = Package.objects.filter(recipe_id = recipe_id).filter(build_id = build_id).filter(size__gte=0)
    package_count = queryset.count()
    queryset = _get_queryset(Package, queryset, filter_string, search_term, ordering_string, 'name')

    packages = _build_page_range(Paginator(queryset, pagesize),request.GET.get('page', 1))

    context = {
            'build'   : Build.objects.get(pk=build_id),
            'recipe'  : recipe_object,
            'objects'  : packages,
            'object_count' : package_count,
            'tablecols':[
                {
                    'name':'Package',
                    'orderfield': _get_toggle_order(request,"name"),
                    'ordericon': _get_toggle_order_icon(request,"name"),
                    'orderkey': "name",
                },
                {
                    'name':'Version',
                },
                {
                    'name':'Size',
                    'orderfield': _get_toggle_order(request,"size", True),
                    'ordericon': _get_toggle_order_icon(request,"size"),
                    'orderkey': 'size',
                    'dclass': 'sizecol span2',
                },
           ]
       }
    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

from django.core.serializers.json import DjangoJSONEncoder
from django.http import HttpResponse
def xhr_dirinfo(request, build_id, target_id):
    top = request.GET.get('start', '/')
    return HttpResponse(_get_dir_entries(build_id, target_id, top), content_type = "application/json")

from django.utils.functional import Promise
from django.utils.encoding import force_text
class LazyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, Promise):
            return force_text(obj)
        return super(LazyEncoder, self).default(obj)

from toastergui.templatetags.projecttags import filtered_filesizeformat
import os
def _get_dir_entries(build_id, target_id, start):
    node_str = {
        Target_File.ITYPE_REGULAR   : '-',
        Target_File.ITYPE_DIRECTORY : 'd',
        Target_File.ITYPE_SYMLINK   : 'l',
        Target_File.ITYPE_SOCKET    : 's',
        Target_File.ITYPE_FIFO      : 'p',
        Target_File.ITYPE_CHARACTER : 'c',
        Target_File.ITYPE_BLOCK     : 'b',
    }
    response = []
    objects  = Target_File.objects.filter(target__exact=target_id, directory__path=start)
    target_packages = Target_Installed_Package.objects.filter(target__exact=target_id).values_list('package_id', flat=True)
    for o in objects:
        # exclude root inode '/'
        if o.path == '/':
            continue
        try:
            entry = {}
            entry['parent'] = start
            entry['name'] = os.path.basename(o.path)
            entry['fullpath'] = o.path

            # set defaults, not all dentries have packages
            entry['installed_package'] = None
            entry['package_id'] = None
            entry['package'] = None
            entry['link_to'] = None
            if o.inodetype == Target_File.ITYPE_DIRECTORY:
                entry['isdir'] = 1
                # is there content in directory
                entry['childcount'] = Target_File.objects.filter(target__exact=target_id, directory__path=o.path).all().count()
            else:
                entry['isdir'] = 0

                # resolve the file to get the package from the resolved file
                resolved_id = o.sym_target_id
                resolved_path = o.path
                if target_packages.count():
                    while resolved_id != "" and resolved_id != None:
                        tf = Target_File.objects.get(pk=resolved_id)
                        resolved_path = tf.path
                        resolved_id = tf.sym_target_id

                    thisfile=Package_File.objects.all().filter(path__exact=resolved_path, package_id__in=target_packages)
                    if thisfile.count():
                        p = Package.objects.get(pk=thisfile[0].package_id)
                        entry['installed_package'] = p.installed_name
                        entry['package_id'] = str(p.id)
                        entry['package'] = p.name
                # don't use resolved path from above, show immediate link-to
                if o.sym_target_id != "" and o.sym_target_id != None:
                    entry['link_to'] = Target_File.objects.get(pk=o.sym_target_id).path
            entry['size'] = filtered_filesizeformat(o.size)
            if entry['link_to'] != None:
                entry['permission'] = node_str[o.inodetype] + o.permission
            else:
                entry['permission'] = node_str[o.inodetype] + o.permission
            entry['owner'] = o.owner
            entry['group'] = o.group
            response.append(entry)

        except Exception as e:
            print("Exception ", e)
            traceback.print_exc()

    # sort by directories first, then by name
    rsorted = sorted(response, key=lambda entry :  entry['name'])
    rsorted = sorted(rsorted, key=lambda entry :  entry['isdir'], reverse=True)
    return json.dumps(rsorted, cls=LazyEncoder).replace('</', '<\\/')

def dirinfo(request, build_id, target_id, file_path=None):
    template = "dirinfo.html"
    objects = _get_dir_entries(build_id, target_id, '/')
    packages_sum = Package.objects.filter(id__in=Target_Installed_Package.objects.filter(target_id=target_id).values('package_id')).aggregate(Sum('installed_size'))
    dir_list = None
    if file_path != None:
        """
        Link from the included package detail file list page and is
        requesting opening the dir info to a specific file path.
        Provide the list of directories to expand and the full path to
        highlight in the page.
        """
        # Aassume target's path separator matches host's, that is, os.sep
        sep = os.sep
        dir_list = []
        head = file_path
        while head != sep:
            (head, tail) = os.path.split(head)
            if head != sep:
                dir_list.insert(0, head)

    build = Build.objects.get(pk=build_id)

    context = { 'build': build,
                'project': build.project,
                'target': Target.objects.get(pk=target_id),
                'packages_sum': packages_sum['installed_size__sum'],
                'objects': objects,
                'dir_list': dir_list,
                'file_path': file_path,
              }
    return render(request, template, context)

def _find_task_dep(task_object):
    tdeps = Task_Dependency.objects.filter(task=task_object).filter(depends_on__order__gt=0)
    tdeps = tdeps.exclude(depends_on__outcome=Task.OUTCOME_NA).select_related("depends_on")
    return [x.depends_on for x in tdeps]

def _find_task_revdep(task_object):
    tdeps = Task_Dependency.objects.filter(depends_on=task_object).filter(task__order__gt=0)
    tdeps = tdeps.exclude(task__outcome = Task.OUTCOME_NA).select_related("task", "task__recipe", "task__build")

    # exclude self-dependencies to prevent infinite dependency loop
    # in generateCoveredList2()
    tdeps = tdeps.exclude(task=task_object)

    return [tdep.task for tdep in tdeps]

def _find_task_revdep_list(tasklist):
    tdeps = Task_Dependency.objects.filter(depends_on__in=tasklist).filter(task__order__gt=0)
    tdeps = tdeps.exclude(task__outcome=Task.OUTCOME_NA).select_related("task", "task__recipe", "task__build")

    # exclude self-dependencies to prevent infinite dependency loop
    # in generateCoveredList2()
    tdeps = tdeps.exclude(task=F('depends_on'))

    return [tdep.task for tdep in tdeps]

def _find_task_provider(task_object):
    task_revdeps = _find_task_revdep(task_object)
    for tr in task_revdeps:
        if tr.outcome != Task.OUTCOME_COVERED:
            return tr
    for tr in task_revdeps:
        trc = _find_task_provider(tr)
        if trc is not None:
            return trc
    return None

def configuration(request, build_id):
    template = 'configuration.html'

    var_names = ('BB_VERSION', 'BUILD_SYS', 'NATIVELSBSTRING', 'TARGET_SYS',
                 'MACHINE', 'DISTRO', 'DISTRO_VERSION', 'TUNE_FEATURES', 'TARGET_FPU')
    context = dict(Variable.objects.filter(build=build_id, variable_name__in=var_names)\
                                           .values_list('variable_name', 'variable_value'))
    build = Build.objects.get(pk=build_id)
    context.update({'objectname': 'configuration',
                    'object_search_display':'variables',
                    'filter_search_display':'variables',
                    'build': build,
                    'project': build.project,
                    'targets': Target.objects.filter(build=build_id)})
    return render(request, template, context)


def configvars(request, build_id):
    template = 'configvars.html'
    (pagesize, orderby) = _get_parameters_values(request, 100, 'variable_name:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby' : orderby, 'filter' : 'description__regex:.+' }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    (filter_string, search_term, ordering_string) = _search_tuple(request, Variable)
    if retval:
        # if new search, clear the default filter
        if search_term and len(search_term):
            mandatory_parameters['filter']=''
        return _redirect_parameters( 'configvars', request.GET, mandatory_parameters, build_id = build_id)

    queryset = Variable.objects.filter(build=build_id).exclude(variable_name__istartswith='B_').exclude(variable_name__istartswith='do_')
    queryset_with_search =  _get_queryset(Variable, queryset, None, search_term, ordering_string, 'variable_name').exclude(variable_value='',vhistory__file_name__isnull=True)
    queryset = _get_queryset(Variable, queryset, filter_string, search_term, ordering_string, 'variable_name')
    # remove records where the value is empty AND there are no history files
    queryset = queryset.exclude(variable_value='',vhistory__file_name__isnull=True)

    variables = _build_page_range(Paginator(queryset, pagesize), request.GET.get('page', 1))

    # show all matching files (not just the last one)
    file_filter= search_term + ":"
    if filter_string.find('/conf/') > 0:
        file_filter += 'conf/(local|bblayers).conf'
    if filter_string.find('conf/machine/') > 0:
        file_filter += 'conf/machine/'
    if filter_string.find('conf/distro/') > 0:
        file_filter += 'conf/distro/'
    if filter_string.find('/bitbake.conf') > 0:
        file_filter += '/bitbake.conf'
    build_dir=re.sub("/tmp/log/.*","",Build.objects.get(pk=build_id).cooker_log_path)

    build = Build.objects.get(pk=build_id)

    context = {
                'objectname': 'configvars',
                'object_search_display':'BitBake variables',
                'filter_search_display':'variables',
                'file_filter': file_filter,
                'build': build,
                'project': build.project,
                'objects' : variables,
                'total_count':queryset_with_search.count(),
                'default_orderby' : 'variable_name:+',
                'search_term':search_term,
            # Specifies the display of columns for the table, appearance in "Edit columns" box, toggling default show/hide, and specifying filters for columns
                'tablecols' : [
                {'name': 'Variable',
                 'qhelp': "BitBake is a generic task executor that considers a list of tasks with dependencies and handles metadata that consists of variables in a certain format that get passed to the tasks",
                 'orderfield': _get_toggle_order(request, "variable_name"),
                 'ordericon':_get_toggle_order_icon(request, "variable_name"),
                },
                {'name': 'Value',
                 'qhelp': "The value assigned to the variable",
                },
                {'name': 'Set in file',
                 'qhelp': "The last configuration file that touched the variable value",
                 'clclass': 'file', 'hidden' : 0,
                 'orderkey' : 'vhistory__file_name',
                 'filter' : {
                    'class' : 'vhistory__file_name',
                    'label': 'Show:',
                    'options' : [
                               ('Local configuration variables', 'vhistory__file_name__contains:'+build_dir+'/conf/',queryset_with_search.filter(vhistory__file_name__contains=build_dir+'/conf/').count(), 'Select this filter to see variables set by the <code>local.conf</code> and <code>bblayers.conf</code> configuration files inside the <code>/build/conf/</code> directory'),
                               ('Machine configuration variables', 'vhistory__file_name__contains:conf/machine/',queryset_with_search.filter(vhistory__file_name__contains='conf/machine').count(), 'Select this filter to see variables set by the configuration file(s) inside your layers <code>/conf/machine/</code> directory'),
                               ('Distro configuration variables', 'vhistory__file_name__contains:conf/distro/',queryset_with_search.filter(vhistory__file_name__contains='conf/distro').count(), 'Select this filter to see variables set by the configuration file(s) inside your layers <code>/conf/distro/</code> directory'),
                               ('Layer configuration variables', 'vhistory__file_name__contains:conf/layer.conf',queryset_with_search.filter(vhistory__file_name__contains='conf/layer.conf').count(), 'Select this filter to see variables set by the <code>layer.conf</code> configuration file inside your layers'),
                               ('bitbake.conf variables', 'vhistory__file_name__contains:/bitbake.conf',queryset_with_search.filter(vhistory__file_name__contains='/bitbake.conf').count(), 'Select this filter to see variables set by the <code>bitbake.conf</code> configuration file'),
                               ]
                             },
                },
                {'name': 'Description',
                 'qhelp': "A brief explanation of the variable",
                 'clclass': 'description', 'hidden' : 0,
                 'dclass': "span4",
                 'filter' : {
                    'class' : 'description',
                    'label': 'Show:',
                    'options' : [
                               ('Variables with description', 'description__regex:.+', queryset_with_search.filter(description__regex='.+').count(), 'We provide descriptions for the most common BitBake variables. The list of descriptions lives in <code>meta/conf/documentation.conf</code>'),
                               ]
                            },
                },
                ],
            }

    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def bfile(request, build_id, package_id):
    template = 'bfile.html'
    files = Package_File.objects.filter(package = package_id)
    build = Build.objects.get(pk=build_id)
    context = {
        'build': build,
        'project': build.project,
        'objects' : files
    }
    return render(request, template, context)


# A set of dependency types valid for both included and built package views
OTHER_DEPENDS_BASE = [
    Package_Dependency.TYPE_RSUGGESTS,
    Package_Dependency.TYPE_RPROVIDES,
    Package_Dependency.TYPE_RREPLACES,
    Package_Dependency.TYPE_RCONFLICTS,
    ]

# value for invalid row id
INVALID_KEY = -1

"""
Given a package id, target_id retrieves two sets of this image and package's
dependencies.  The return value is a dictionary consisting of two other
lists: a list of 'runtime' dependencies, that is, having RDEPENDS
values in source package's recipe, and a list of other dependencies, that is
the list of possible recipe variables as found in OTHER_DEPENDS_BASE plus
the RRECOMMENDS or TRECOMMENDS value.
The lists are built in the sort order specified for the package runtime
dependency views.
"""
def _get_package_dependencies(package_id, target_id = INVALID_KEY):
    runtime_deps = []
    other_deps = []
    other_depends_types = OTHER_DEPENDS_BASE

    if target_id != INVALID_KEY :
        rdepends_type = Package_Dependency.TYPE_TRDEPENDS
        other_depends_types +=  [Package_Dependency.TYPE_TRECOMMENDS]
    else :
        rdepends_type = Package_Dependency.TYPE_RDEPENDS
        other_depends_types += [Package_Dependency.TYPE_RRECOMMENDS]

    package = Package.objects.get(pk=package_id)
    if target_id != INVALID_KEY :
        alldeps = package.package_dependencies_source.filter(target_id__exact = target_id)
    else :
        alldeps = package.package_dependencies_source.all()
    for idep in alldeps:
        dep_package = Package.objects.get(pk=idep.depends_on_id)
        dep_entry = Package_Dependency.DEPENDS_DICT[idep.dep_type]
        if dep_package.version == '' :
            version = ''
        else :
            version = dep_package.version + "-" + dep_package.revision
        installed = False
        if target_id != INVALID_KEY :
            if Target_Installed_Package.objects.filter(target_id__exact = target_id, package_id__exact = dep_package.id).count() > 0:
                installed = True
        dep =   {
                'name' : dep_package.name,
                'version' : version,
                'size' : dep_package.size,
                'dep_type' : idep.dep_type,
                'dep_type_display' : dep_entry[0].capitalize(),
                'dep_type_help' : dep_entry[1] % (dep_package.name, package.name),
                'depends_on_id' : dep_package.id,
                'installed' : installed,
                }

        if target_id != INVALID_KEY:
                dep['alias'] = _get_package_alias(dep_package)

        if idep.dep_type == rdepends_type :
            runtime_deps.append(dep)
        elif idep.dep_type in other_depends_types :
            other_deps.append(dep)

    rdep_sorted = sorted(runtime_deps, key=lambda k: k['name'])
    odep_sorted = sorted(
            sorted(other_deps, key=lambda k: k['name']),
            key=lambda k: k['dep_type'])
    retvalues = {'runtime_deps' : rdep_sorted, 'other_deps' : odep_sorted}
    return retvalues

# Return the count of packages dependent on package for this target_id image
def _get_package_reverse_dep_count(package, target_id):
    return package.package_dependencies_target.filter(target_id__exact=target_id, dep_type__exact = Package_Dependency.TYPE_TRDEPENDS).count()

# Return the count of the packages that this package_id is dependent on.
# Use one of the two RDEPENDS types, either TRDEPENDS if the package was
# installed, or else RDEPENDS if only built.
def _get_package_dependency_count(package, target_id, is_installed):
    if is_installed :
        return package.package_dependencies_source.filter(target_id__exact = target_id,
            dep_type__exact = Package_Dependency.TYPE_TRDEPENDS).count()
    else :
        return package.package_dependencies_source.filter(dep_type__exact = Package_Dependency.TYPE_RDEPENDS).count()

def _get_package_alias(package):
    alias = package.installed_name
    if alias != None and alias != '' and alias != package.name:
        return alias
    else:
        return ''

def _get_fullpackagespec(package):
    r = package.name
    version_good = package.version != None and  package.version != ''
    revision_good = package.revision != None and package.revision != ''
    if version_good or revision_good:
        r += '_'
        if version_good:
            r += package.version
            if revision_good:
                r += '-'
        if revision_good:
            r += package.revision
    return r

def package_built_detail(request, build_id, package_id):
    template = "package_built_detail.html"
    if Build.objects.filter(pk=build_id).count() == 0 :
        return redirect(builds)

    # follow convention for pagination w/ search although not used for this view
    queryset = Package_File.objects.filter(package_id__exact=package_id)
    (pagesize, orderby) = _get_parameters_values(request, 25, 'path:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby' : orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'package_built_detail', request.GET, mandatory_parameters, build_id = build_id, package_id = package_id)

    (filter_string, search_term, ordering_string) = _search_tuple(request, Package_File)
    paths = _get_queryset(Package_File, queryset, filter_string, search_term, ordering_string, 'path')

    package = Package.objects.get(pk=package_id)
    package.fullpackagespec = _get_fullpackagespec(package)
    context = {
            'build' : Build.objects.get(pk=build_id),
            'package' : package,
            'dependency_count' : _get_package_dependency_count(package, -1, False),
            'objects' : paths,
            'tablecols':[
                {
                    'name':'File',
                    'orderfield': _get_toggle_order(request, "path"),
                    'ordericon':_get_toggle_order_icon(request, "path"),
                },
                {
                    'name':'Size',
                    'orderfield': _get_toggle_order(request, "size", True),
                    'ordericon':_get_toggle_order_icon(request, "size"),
                    'dclass': 'sizecol span2',
                },
            ]
    }
    if paths.all().count() < 2:
        context['disable_sort'] = True;

    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def package_built_dependencies(request, build_id, package_id):
    template = "package_built_dependencies.html"
    if Build.objects.filter(pk=build_id).count() == 0 :
         return redirect(builds)

    package = Package.objects.get(pk=package_id)
    package.fullpackagespec = _get_fullpackagespec(package)
    dependencies = _get_package_dependencies(package_id)
    context = {
            'build' : Build.objects.get(pk=build_id),
            'package' : package,
            'runtime_deps' : dependencies['runtime_deps'],
            'other_deps' :   dependencies['other_deps'],
            'dependency_count' : _get_package_dependency_count(package, -1,  False)
    }
    return render(request, template, context)


def package_included_detail(request, build_id, target_id, package_id):
    template = "package_included_detail.html"
    if Build.objects.filter(pk=build_id).count() == 0 :
        return redirect(builds)

    # follow convention for pagination w/ search although not used for this view
    (pagesize, orderby) = _get_parameters_values(request, 25, 'path:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby' : orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'package_included_detail', request.GET, mandatory_parameters, build_id = build_id, target_id = target_id, package_id = package_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Package_File)

    queryset = Package_File.objects.filter(package_id__exact=package_id)
    paths = _get_queryset(Package_File, queryset, filter_string, search_term, ordering_string, 'path')

    package = Package.objects.get(pk=package_id)
    package.fullpackagespec = _get_fullpackagespec(package)
    package.alias = _get_package_alias(package)
    target = Target.objects.get(pk=target_id)
    context = {
            'build' : Build.objects.get(pk=build_id),
            'target'  : target,
            'package' : package,
            'reverse_count' : _get_package_reverse_dep_count(package, target_id),
            'dependency_count' : _get_package_dependency_count(package, target_id, True),
            'objects': paths,
            'tablecols':[
                {
                    'name':'File',
                    'orderfield': _get_toggle_order(request, "path"),
                    'ordericon':_get_toggle_order_icon(request, "path"),
                },
                {
                    'name':'Size',
                    'orderfield': _get_toggle_order(request, "size", True),
                    'ordericon':_get_toggle_order_icon(request, "size"),
                    'dclass': 'sizecol span2',
                },
            ]
    }
    if paths.all().count() < 2:
        context['disable_sort'] = True
    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def package_included_dependencies(request, build_id, target_id, package_id):
    template = "package_included_dependencies.html"
    if Build.objects.filter(pk=build_id).count() == 0 :
        return redirect(builds)

    package = Package.objects.get(pk=package_id)
    package.fullpackagespec = _get_fullpackagespec(package)
    package.alias = _get_package_alias(package)
    target = Target.objects.get(pk=target_id)

    dependencies = _get_package_dependencies(package_id, target_id)
    context = {
            'build' : Build.objects.get(pk=build_id),
            'package' : package,
            'target' : target,
            'runtime_deps' : dependencies['runtime_deps'],
            'other_deps' :   dependencies['other_deps'],
            'reverse_count' : _get_package_reverse_dep_count(package, target_id),
            'dependency_count' : _get_package_dependency_count(package, target_id, True)
    }
    return render(request, template, context)

def package_included_reverse_dependencies(request, build_id, target_id, package_id):
    template = "package_included_reverse_dependencies.html"
    if Build.objects.filter(pk=build_id).count() == 0 :
        return redirect(builds)

    (pagesize, orderby) = _get_parameters_values(request, 25, 'package__name:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby': orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'package_included_reverse_dependencies', request.GET, mandatory_parameters, build_id = build_id, target_id = target_id, package_id = package_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Package_File)

    queryset = Package_Dependency.objects.select_related('depends_on__name', 'depends_on__size').filter(depends_on=package_id, target_id=target_id, dep_type=Package_Dependency.TYPE_TRDEPENDS)
    objects = _get_queryset(Package_Dependency, queryset, filter_string, search_term, ordering_string, 'package__name')

    package = Package.objects.get(pk=package_id)
    package.fullpackagespec = _get_fullpackagespec(package)
    package.alias = _get_package_alias(package)
    target = Target.objects.get(pk=target_id)
    for o in objects:
        if o.package.version != '':
            o.package.version += '-' + o.package.revision
        o.alias = _get_package_alias(o.package)
    context = {
            'build' : Build.objects.get(pk=build_id),
            'package' : package,
            'target' : target,
            'objects' : objects,
            'reverse_count' : _get_package_reverse_dep_count(package, target_id),
            'dependency_count' : _get_package_dependency_count(package, target_id, True),
            'tablecols':[
                {
                    'name':'Package',
                    'orderfield': _get_toggle_order(request, "package__name"),
                    'ordericon': _get_toggle_order_icon(request, "package__name"),
                },
                {
                    'name':'Version',
                },
                {
                    'name':'Size',
                    'orderfield': _get_toggle_order(request, "package__size", True),
                    'ordericon': _get_toggle_order_icon(request, "package__size"),
                    'dclass': 'sizecol span2',
                },
            ]
    }
    if objects.all().count() < 2:
        context['disable_sort'] = True
    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def image_information_dir(request, build_id, target_id, packagefile_id):
    # stubbed for now
    return redirect(builds)
    # the context processor that supplies data used across all the pages

# a context processor which runs on every request; this provides the
# projects and non_cli_projects (i.e. projects created by the user)
# variables referred to in templates, which used to determine the
# visibility of UI elements like the "New build" button
def managedcontextprocessor(request):
    projects = Project.objects.all()
    ret = {
        "projects": projects,
        "non_cli_projects": projects.exclude(is_default=True),
        "DEBUG" : toastermain.settings.DEBUG,
        "TOASTER_BRANCH": toastermain.settings.TOASTER_BRANCH,
        "TOASTER_REVISION" : toastermain.settings.TOASTER_REVISION,
    }
    return ret



import toastermain.settings

from orm.models import Project, ProjectLayer, ProjectTarget, ProjectVariable
from bldcontrol.models import  BuildEnvironment

# we have a set of functions if we're in managed mode, or
# a default "page not available" simple functions for interactive mode

if True:
    from django.contrib.auth.models import User
    from django.contrib.auth import authenticate, login
    from django.contrib.auth.decorators import login_required

    from orm.models import LayerSource, ToasterSetting, Release, Machine, LayerVersionDependency
    from bldcontrol.models import BuildRequest

    import traceback

    class BadParameterException(Exception):
        ''' The exception raised on invalid POST requests '''
        pass

    # new project
    def newproject(request):
        template = "newproject.html"
        context = {
            'email': request.user.email if request.user.is_authenticated() else '',
            'username': request.user.username if request.user.is_authenticated() else '',
            'releases': Release.objects.order_by("description"),
        }

        try:
            context['defaultbranch'] = ToasterSetting.objects.get(name = "DEFAULT_RELEASE").value
        except ToasterSetting.DoesNotExist:
            pass

        if request.method == "GET":
            # render new project page
            return render(request, template, context)
        elif request.method == "POST":
            mandatory_fields = ['projectname', 'ptype']
            try:
                ptype = request.POST.get('ptype')
                if ptype == "build":
                    mandatory_fields.append('projectversion')
                # make sure we have values for all mandatory_fields
                missing = [field for field in mandatory_fields if len(request.POST.get(field, '')) == 0]
                if missing:
                    # set alert for missing fields
                    raise BadParameterException("Fields missing: %s" % ", ".join(missing))

                if not request.user.is_authenticated():
                    user = authenticate(username = request.POST.get('username', '_anonuser'), password = 'nopass')
                    if user is None:
                        user = User.objects.create_user(username = request.POST.get('username', '_anonuser'), email = request.POST.get('email', ''), password = "nopass")

                        user = authenticate(username = user.username, password = 'nopass')
                    login(request, user)

                #  save the project
                if ptype == "analysis":
                    release = None
                else:
                    release = Release.objects.get(pk = request.POST.get('projectversion', None ))

                prj = Project.objects.create_project(name = request.POST['projectname'], release = release)
                prj.user_id = request.user.pk
                prj.save()
                return redirect(reverse(project, args=(prj.pk,)) + "?notify=new-project")

            except (IntegrityError, BadParameterException) as e:
                # fill in page with previously submitted values
                for field in mandatory_fields:
                    context.__setitem__(field, request.POST.get(field, "-- missing"))
                if isinstance(e, IntegrityError) and "username" in str(e):
                    context['alert'] = "Your chosen username is already used"
                else:
                    context['alert'] = str(e)
                return render(request, template, context)

        raise Exception("Invalid HTTP method for this page")

    # Shows the edit project page
    def project(request, pid):
        project = Project.objects.get(pk=pid)
        context = {"project": project}
        return render(request, "project.html", context)

    def jsunittests(request):
        """ Provides a page for the js unit tests """
        bbv = BitbakeVersion.objects.filter(branch="master").first()
        release = Release.objects.filter(bitbake_version=bbv).first()

        name = "_js_unit_test_prj_"

        # If there is an existing project by this name delete it.
        # We don't want Lots of duplicates cluttering up the projects.
        Project.objects.filter(name=name).delete()

        new_project = Project.objects.create_project(name=name,
                                                     release=release)
        # Add a layer
        layer = new_project.get_all_compatible_layer_versions().first()

        ProjectLayer.objects.get_or_create(layercommit=layer,
                                           project=new_project)

        # make sure we have a machine set for this project
        ProjectVariable.objects.get_or_create(project=new_project,
                                              name="MACHINE",
                                              value="qemux86")
        context = {'project': new_project}
        return render(request, "js-unit-tests.html", context)

    from django.views.decorators.csrf import csrf_exempt
    @csrf_exempt
    def xhr_testreleasechange(request, pid):
        def response(data):
            return HttpResponse(jsonfilter(data),
                                content_type="application/json")

        """ returns layer versions that would be deleted on the new
        release__pk """
        try:
            prj = Project.objects.get(pk = pid)
            new_release_id = request.GET['new_release_id']

            # If we're already on this project do nothing
            if prj.release.pk == int(new_release_id):
                return reponse({"error": "ok", "rows": []})

            retval = []

            for project in prj.projectlayer_set.all():
                release = Release.objects.get(pk = new_release_id)

                layer_versions = prj.get_all_compatible_layer_versions()
                layer_versions = layer_versions.filter(release = release)
                layer_versions = layer_versions.filter(layer__name = project.layercommit.layer.name)

                # there is no layer_version with the new release id,
                # and the same name
                if layer_versions.count() < 1:
                    retval.append(project)

            return response({"error":"ok",
                             "rows": [_lv_to_dict(prj) for y in [x.layercommit for x in retval]]
                            })

        except Exception as e:
            return response({"error": str(e) })

    def xhr_configvaredit(request, pid):
        try:
            prj = Project.objects.get(id = pid)
            # There are cases where user can add variables which hold values
            # like http://, file:/// etc. In such case a simple split(":")
            # would fail. One example is SSTATE_MIRRORS variable. So we use
            # max_split var to handle them.
            max_split = 1
            # add conf variables
            if 'configvarAdd' in request.POST:
                t=request.POST['configvarAdd'].strip()
                if ":" in t:
                    variable, value = t.split(":", max_split)
                else:
                    variable = t
                    value = ""

                pt, created = ProjectVariable.objects.get_or_create(project = prj, name = variable, value = value)
            # change conf variables
            if 'configvarChange' in request.POST:
                t=request.POST['configvarChange'].strip()
                if ":" in t:
                    variable, value = t.split(":", max_split)
                else:
                    variable = t
                    value = ""

                pt, created = ProjectVariable.objects.get_or_create(project = prj, name = variable)
                pt.value=value
                pt.save()
            # remove conf variables
            if 'configvarDel' in request.POST:
                t=request.POST['configvarDel'].strip()
                pt = ProjectVariable.objects.get(pk = int(t)).delete()

            # return all project settings, filter out blacklist and elsewhere-managed variables
            vars_managed,vars_fstypes,vars_blacklist = get_project_configvars_context()
            configvars_query = ProjectVariable.objects.filter(project_id = pid).all()
            for var in vars_managed:
                configvars_query = configvars_query.exclude(name = var)
            for var in vars_blacklist:
                configvars_query = configvars_query.exclude(name = var)

            return_data = {
                "error": "ok",
                'configvars': [(x.name, x.value, x.pk) for x in configvars_query]
               }
            try:
                return_data['distro'] = ProjectVariable.objects.get(project = prj, name = "DISTRO").value,
            except ProjectVariable.DoesNotExist:
                pass
            try:
                return_data['dl_dir'] = ProjectVariable.objects.get(project = prj, name = "DL_DIR").value,
            except ProjectVariable.DoesNotExist:
                pass
            try:
                return_data['fstypes'] = ProjectVariable.objects.get(project = prj, name = "IMAGE_FSTYPES").value,
            except ProjectVariable.DoesNotExist:
                pass
            try:
                return_data['image_install_append'] = ProjectVariable.objects.get(project = prj, name = "IMAGE_INSTALL_append").value,
            except ProjectVariable.DoesNotExist:
                pass
            try:
                return_data['package_classes'] = ProjectVariable.objects.get(project = prj, name = "PACKAGE_CLASSES").value,
            except ProjectVariable.DoesNotExist:
                pass
            try:
                return_data['sstate_dir'] = ProjectVariable.objects.get(project = prj, name = "SSTATE_DIR").value,
            except ProjectVariable.DoesNotExist:
                pass

            return HttpResponse(json.dumps( return_data ), content_type = "application/json")

        except Exception as e:
            return HttpResponse(json.dumps({"error":str(e) + "\n" + traceback.format_exc()}), content_type = "application/json")


    def xhr_importlayer(request):
        if ('vcs_url' not in request.POST or
            'name' not in request.POST or
            'git_ref' not in request.POST or
            'project_id' not in request.POST):
          return HttpResponse(jsonfilter({"error": "Missing parameters; requires vcs_url, name, git_ref and project_id"}), content_type = "application/json")

        layers_added = [];

        # Rudimentary check for any possible html tags
        for val in request.POST.values():
            if "<" in val:
                return HttpResponse(jsonfilter(
                    {"error": "Invalid character <"}),
                    content_type="application/json")

        prj = Project.objects.get(pk=request.POST['project_id'])

        # Strip trailing/leading whitespace from all values
        # put into a new dict because POST one is immutable.
        post_data = dict()
        for key,val in request.POST.items():
          post_data[key] = val.strip()


        try:
            layer, layer_created = Layer.objects.get_or_create(name=post_data['name'])
        except MultipleObjectsReturned:
            return HttpResponse(jsonfilter({"error": "hint-layer-exists"}), content_type = "application/json")

        if layer:
            if layer_created:
                layer.vcs_url = post_data.get('vcs_url')
                layer.local_source_dir = post_data.get('local_source_dir')
                layer.up_date = timezone.now()
                layer.save()
            else:
                # We have an existing layer by this name, let's see if the git
                # url is the same, if it is then we can just create a new layer
                # version for this layer. Otherwise we need to bail out.
                if layer.vcs_url != post_data['vcs_url']:
                    return HttpResponse(jsonfilter({"error": "hint-layer-exists-with-different-url" , "current_url" : layer.vcs_url, "current_id": layer.id }), content_type = "application/json")

            layer_version, version_created = \
                Layer_Version.objects.get_or_create(
                        layer_source=LayerSource.TYPE_IMPORTED,
                        layer=layer, project=prj,
                        release=prj.release,
                        branch=post_data['git_ref'],
                        commit=post_data['git_ref'],
                        dirpath=post_data['dir_path'])

            if layer_version:
                if not version_created:
                    return HttpResponse(jsonfilter({"error":
                                                    "hint-layer-version-exists",
                                                    "existing_layer_version":
                                                    layer_version.id }),
                                        content_type = "application/json")

                layer_version.layer_source = LayerSource.TYPE_IMPORTED

                layer_version.up_date = timezone.now()
                layer_version.save()

                # Add the dependencies specified for this new layer
                if ('layer_deps' in post_data and
                    version_created and
                    len(post_data["layer_deps"]) > 0):
                    for layer_dep_id in post_data["layer_deps"].split(","):

                        layer_dep_obj = Layer_Version.objects.get(pk=layer_dep_id)
                        LayerVersionDependency.objects.get_or_create(layer_version=layer_version, depends_on=layer_dep_obj)
                        # Now add them to the project, we could get an execption
                        # if the project now contains the exact
                        # dependency already (like modified on another page)
                        try:
                            prj_layer, prj_layer_created = ProjectLayer.objects.get_or_create(layercommit=layer_dep_obj, project=prj)
                        except IntegrityError as e:
                            logger.warning("Integrity error while saving Project Layers: %s (original %s)" % (e, e.__cause__))
                            continue

                        if prj_layer_created:
                            layerdepdetailurl = reverse('layerdetails', args=(prj.id, layer_dep_obj.pk))
                            layers_added.append({'id': layer_dep_obj.id, 'name': Layer.objects.get(id=layer_dep_obj.layer_id).name, 'layerdetailurl': layerdepdetailurl })


                # If an old layer version exists in our project then remove it
                for prj_layers in ProjectLayer.objects.filter(project=prj):
                    dup_layer_v = Layer_Version.objects.filter(id=prj_layers.layercommit_id, layer_id=layer.id)
                    if len(dup_layer_v) >0 :
                        prj_layers.delete()

                # finally add the imported layer (version id) to the project
                ProjectLayer.objects.create(layercommit=layer_version, project=prj,optional=1)

            else:
                # We didn't create a layer version so back out now and clean up.
                if layer_created:
                    layer.delete()

                return HttpResponse(jsonfilter({"error": "Uncaught error: Could not create layer version"}), content_type = "application/json")

        layerdetailurl = reverse('layerdetails', args=(prj.id, layer_version.pk))

        json_response = {"error": "ok",
                         "imported_layer" : {
                           "name" : layer.name,
                           "id": layer_version.id,
                           "layerdetailurl": layerdetailurl,
                         },
                         "deps_added": layers_added }

        return HttpResponse(jsonfilter(json_response), content_type = "application/json")

    def customrecipe_download(request, pid, recipe_id):
        recipe = get_object_or_404(CustomImageRecipe, pk=recipe_id)

        file_data = recipe.generate_recipe_file_contents()

        response = HttpResponse(file_data, content_type='text/plain')
        response['Content-Disposition'] = \
                'attachment; filename="%s_%s.bb"' % (recipe.name,
                                                     recipe.version)

        return response

    def importlayer(request, pid):
        template = "importlayer.html"
        context = {
            'project': Project.objects.get(id=pid),
        }
        return render(request, template, context)

    # TODO merge with api pseudo api here is used for deps modal
    @_template_renderer('layerdetails.html')
    def layerdetails(request, pid, layerid):
        project = Project.objects.get(pk=pid)
        layer_version = Layer_Version.objects.get(pk=layerid)

        project_layers = ProjectLayer.objects.filter(
            project=project).values_list("layercommit_id",
                                         flat=True)

        context = {
            'project': project,
            'layer_source': LayerSource.types_dict(),
            'layerversion': layer_version,
            'layerdeps': {
                "list": [
                    {
                        "id": dep.id,
                        "name": dep.layer.name,
                        "layerdetailurl": reverse('layerdetails',
                                                  args=(pid, dep.pk)),
                        "vcs_url": dep.layer.vcs_url,
                        "vcs_reference": dep.get_vcs_reference()
                    }
                    for dep in layer_version.get_alldeps(project.id)]
            },
            'projectlayers': list(project_layers)
        }

        return context


    def get_project_configvars_context():
        # Vars managed outside of this view
        vars_managed = {
            'MACHINE', 'BBLAYERS'
        }

        vars_blacklist  = {
            'PARALLEL_MAKE','BB_NUMBER_THREADS',
            'BB_DISKMON_DIRS','BB_NUMBER_THREADS','CVS_PROXY_HOST','CVS_PROXY_PORT',
            'PARALLEL_MAKE','TMPDIR',
            'all_proxy','ftp_proxy','http_proxy ','https_proxy'
            }

        vars_fstypes = Target_Image_File.SUFFIXES

        return(vars_managed,sorted(vars_fstypes),vars_blacklist)

    @_template_renderer("projectconf.html")
    def projectconf(request, pid):

        try:
            prj = Project.objects.get(id = pid)
        except Project.DoesNotExist:
            return HttpResponseNotFound("<h1>Project id " + pid + " is unavailable</h1>")

        # remove blacklist and externally managed varaibles from this list
        vars_managed,vars_fstypes,vars_blacklist = get_project_configvars_context()
        configvars = ProjectVariable.objects.filter(project_id = pid).all()
        for var in vars_managed:
            configvars = configvars.exclude(name = var)
        for var in vars_blacklist:
            configvars = configvars.exclude(name = var)

        context = {
            'project':          prj,
            'configvars':       configvars,
            'vars_managed':     vars_managed,
            'vars_fstypes':     vars_fstypes,
            'vars_blacklist':   vars_blacklist,
        }

        try:
            context['distro'] =  ProjectVariable.objects.get(project = prj, name = "DISTRO").value
            context['distro_defined'] = "1"
        except ProjectVariable.DoesNotExist:
            pass
        try:
            if ProjectVariable.objects.get(project = prj, name = "DL_DIR").value == "${TOPDIR}/../downloads":
                be = BuildEnvironment.objects.get(pk = str(1))
                dl_dir = os.path.join(dirname(be.builddir), "downloads")
                context['dl_dir'] =  dl_dir
                pv, created = ProjectVariable.objects.get_or_create(project = prj, name = "DL_DIR")
                pv.value = dl_dir
                pv.save()
            else:
                context['dl_dir'] = ProjectVariable.objects.get(project = prj, name = "DL_DIR").value
            context['dl_dir_defined'] = "1"
        except (ProjectVariable.DoesNotExist, BuildEnvironment.DoesNotExist):
            pass
        try:
            context['fstypes'] =  ProjectVariable.objects.get(project = prj, name = "IMAGE_FSTYPES").value
            context['fstypes_defined'] = "1"
        except ProjectVariable.DoesNotExist:
            pass
        try:
            context['image_install_append'] =  ProjectVariable.objects.get(project = prj, name = "IMAGE_INSTALL_append").value
            context['image_install_append_defined'] = "1"
        except ProjectVariable.DoesNotExist:
            pass
        try:
            context['package_classes'] =  ProjectVariable.objects.get(project = prj, name = "PACKAGE_CLASSES").value
            context['package_classes_defined'] = "1"
        except ProjectVariable.DoesNotExist:
            pass
        try:
            if ProjectVariable.objects.get(project = prj, name = "SSTATE_DIR").value == "${TOPDIR}/../sstate-cache":
                be = BuildEnvironment.objects.get(pk = str(1))
                sstate_dir = os.path.join(dirname(be.builddir), "sstate-cache")
                context['sstate_dir'] = sstate_dir
                pv, created = ProjectVariable.objects.get_or_create(project = prj, name = "SSTATE_DIR")
                pv.value = sstate_dir
                pv.save()
            else:
                context['sstate_dir'] = ProjectVariable.objects.get(project = prj, name = "SSTATE_DIR").value
            context['sstate_dir_defined'] = "1"
        except (ProjectVariable.DoesNotExist, BuildEnvironment.DoesNotExist):
            pass

        return context

    def _file_names_for_artifact(build, artifact_type, artifact_id):
        """
        Return a tuple (file path, file name for the download response) for an
        artifact of type artifact_type with ID artifact_id for build; if
        artifact type is not supported, returns (None, None)
        """
        file_name = None
        response_file_name = None

        if artifact_type == "cookerlog":
            file_name = build.cooker_log_path
            response_file_name = "cooker.log"

        elif artifact_type == "imagefile":
            file_name = Target_Image_File.objects.get(target__build = build, pk = artifact_id).file_name

        elif artifact_type == "targetkernelartifact":
            target = TargetKernelFile.objects.get(pk=artifact_id)
            file_name = target.file_name

        elif artifact_type == "targetsdkartifact":
            target = TargetSDKFile.objects.get(pk=artifact_id)
            file_name = target.file_name

        elif artifact_type == "licensemanifest":
            file_name = Target.objects.get(build = build, pk = artifact_id).license_manifest_path

        elif artifact_type == "packagemanifest":
            file_name = Target.objects.get(build = build, pk = artifact_id).package_manifest_path

        elif artifact_type == "tasklogfile":
            file_name = Task.objects.get(build = build, pk = artifact_id).logfile

        elif artifact_type == "logmessagefile":
            file_name = LogMessage.objects.get(build = build, pk = artifact_id).pathname

        if file_name and not response_file_name:
            response_file_name = os.path.basename(file_name)

        return (file_name, response_file_name)

    def build_artifact(request, build_id, artifact_type, artifact_id):
        """
        View which returns a build artifact file as a response
        """
        file_name = None
        response_file_name = None

        try:
            build = Build.objects.get(pk = build_id)
            file_name, response_file_name = _file_names_for_artifact(
                build, artifact_type, artifact_id
            )

            if file_name and response_file_name:
                fsock = open(file_name, "rb")
                content_type = MimeTypeFinder.get_mimetype(file_name)

                response = HttpResponse(fsock, content_type = content_type)

                disposition = "attachment; filename=" + response_file_name
                response["Content-Disposition"] = disposition

                return response
            else:
                return render(request, "unavailable_artifact.html")
        except (ObjectDoesNotExist, IOError):
            return render(request, "unavailable_artifact.html")
