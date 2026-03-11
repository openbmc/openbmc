#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import ast
import re
import subprocess
import sys

import bb.cooker
from bb.ui import toasterui
from bb.ui import eventreplay

from django.db.models import F, Q, Sum
from django.db import IntegrityError
from django.shortcuts import render, redirect, get_object_or_404, HttpResponseRedirect
from django.utils.http import urlencode
from orm.models import Build, Target, Task, Layer, Layer_Version, Recipe
from orm.models import LogMessage, Variable, Package_Dependency, Package
from orm.models import Task_Dependency, Package_File
from orm.models import Target_Installed_Package, Target_File
from orm.models import TargetKernelFile, TargetSDKFile, Target_Image_File
from orm.models import BitbakeVersion, CustomImageRecipe, EventLogsImports

from django.urls import reverse, resolve
from django.contrib import messages

from django.core.exceptions import ObjectDoesNotExist
from django.core.files.storage import FileSystemStorage
from django.core.files.uploadedfile import InMemoryUploadedFile, TemporaryUploadedFile
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.http import HttpResponseNotFound, JsonResponse
from django.utils import timezone
from django.views.generic import TemplateView
from datetime import timedelta, datetime
from toastergui.templatetags.projecttags import json as jsonfilter
from decimal import Decimal
import json
import os
from os.path import dirname
import mimetypes

from toastergui.forms import LoadFileForm

from collections import namedtuple

import logging

from toastermain.logs import log_view_mixin

logger = logging.getLogger("toaster")

# Project creation and managed build enable
project_enable = ('1' == os.environ.get('TOASTER_BUILDSERVER'))
is_project_specific = ('1' == os.environ.get('TOASTER_PROJECTSPECIFIC'))
import_page = False

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
        if guessed_type is None:
            guessed_type = 'application/octet-stream'
        return guessed_type

# single point to add global values into the context before rendering
@log_view_mixin
def toaster_render(request, page, context):
    context['project_enable'] = project_enable
    context['project_specific'] = is_project_specific
    return render(request, page, context)


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

    return toaster_render(request, 'landing.html', context)

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


def _lv_to_dict(prj, x = None):
    if x is None:
        def wrapper(x):
            return _lv_to_dict(prj, x)
        return wrapper

    return {"id": x.pk,
            "name": x.layer.name,
            "tooltip": "%s | %s" % (x.layer.vcs_url,x.get_vcs_reference()),
            "detail": "(%s" % x.layer.vcs_url + (")" if x.release is None else " | "+x.get_vcs_reference()+")"),
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
        valid_fields = [f.name for f in model._meta.get_fields()]
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
        # Chunk the query to avoid "too many SQL variables" error
        package_set = t.target_installed_package_set.all()
        package_set_len = len(package_set)
        for ps_start in range(0,package_set_len,500):
            ps_stop = min(ps_start+500,package_set_len)
            for package in Package.objects.filter(id__in = [x.package_id for x in package_set[ps_start:ps_stop]]):
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
    return toaster_render( request, template, context )



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

    return toaster_render( request, template, context )

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
    return toaster_render(request, template, context)

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
    response = toaster_render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

from django.http import HttpResponse
@log_view_mixin
def xhr_dirinfo(request, build_id, target_id):
    top = request.GET.get('start', '/')
    return HttpResponse(_get_dir_entries(build_id, target_id, top), content_type = "application/json")

from django.utils.functional import Promise
from django.utils.encoding import force_str
class LazyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, Promise):
            return force_str(obj)
        return super(LazyEncoder, self).default(obj)

from toastergui.templatetags.projecttags import filtered_filesizeformat
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
                    while resolved_id != "" and resolved_id is not None:
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
                if o.sym_target_id != "" and o.sym_target_id is not None:
                    entry['link_to'] = Target_File.objects.get(pk=o.sym_target_id).path
            entry['size'] = filtered_filesizeformat(o.size)
            if entry['link_to'] is not None:
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
    if file_path is not None:
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
    return toaster_render(request, template, context)

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
    return toaster_render(request, template, context)


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

    response = toaster_render(request, template, context)
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
    return toaster_render(request, template, context)


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
    if alias is not None and alias != '' and alias != package.name:
        return alias
    else:
        return ''

def _get_fullpackagespec(package):
    r = package.name
    version_good = package.version is not None and  package.version != ''
    revision_good = package.revision is not None and package.revision != ''
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

    response = toaster_render(request, template, context)
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
    return toaster_render(request, template, context)


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
    response = toaster_render(request, template, context)
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
    return toaster_render(request, template, context)

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

    queryset = Package_Dependency.objects.select_related('depends_on').filter(depends_on=package_id, target_id=target_id, dep_type=Package_Dependency.TYPE_TRDEPENDS)
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
    response = toaster_render(request, template, context)
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

# REST-based API calls to return build/building status to external Toaster
# managers and aggregators via JSON

def _json_build_status(build_id,extend):
    build_stat = None
    try:
        build = Build.objects.get( pk = build_id )
        build_stat = {}
        build_stat['id'] = build.id
        build_stat['name'] = build.build_name
        build_stat['machine'] = build.machine
        build_stat['distro'] = build.distro
        build_stat['start'] = build.started_on
        # look up target name
        target= Target.objects.get( build = build )
        if target:
            if target.task:
                build_stat['target'] = '%s:%s' % (target.target,target.task)
            else:
                build_stat['target'] = '%s' % (target.target)
        else:
            build_stat['target'] = ''
        # look up project name
        project = Project.objects.get( build = build )
        if project:
            build_stat['project'] = project.name
        else:
            build_stat['project'] = ''
        if Build.IN_PROGRESS == build.outcome:
            now = timezone.now()
            timediff = now - build.started_on
            build_stat['seconds']='%.3f' % timediff.total_seconds()
            build_stat['clone']='%d:%d' % (build.repos_cloned,build.repos_to_clone)
            build_stat['parse']='%d:%d' % (build.recipes_parsed,build.recipes_to_parse)
            tf = Task.objects.filter(build = build)
            tfc = tf.count()
            if tfc > 0:
                tfd = tf.exclude(order__isnull=True).count()
            else:
                tfd = 0
            build_stat['task']='%d:%d' % (tfd,tfc)
        else:
            build_stat['outcome'] = build.get_outcome_text()
            timediff = build.completed_on - build.started_on
            build_stat['seconds']='%.3f' % timediff.total_seconds()
            build_stat['stop'] = build.completed_on
            messages = LogMessage.objects.all().filter(build = build)
            errors = len(messages.filter(level=LogMessage.ERROR) |
                 messages.filter(level=LogMessage.EXCEPTION) |
                 messages.filter(level=LogMessage.CRITICAL))
            build_stat['errors'] = errors
            warnings = len(messages.filter(level=LogMessage.WARNING))
            build_stat['warnings'] = warnings
        if extend:
            build_stat['cooker_log'] = build.cooker_log_path
    except Exception as e:
        build_state = str(e)
    return build_stat

def json_builds(request):
    build_table = []
    builds = []
    try:
        builds = Build.objects.exclude(outcome=Build.IN_PROGRESS).order_by("-started_on")
        for build in builds:
            build_table.append(_json_build_status(build.id,False))
    except Exception as e:
        build_table = str(e)
    return JsonResponse({'builds' : build_table, 'count' : len(builds)})

def json_building(request):
    build_table = []
    builds = []
    try:
        builds = Build.objects.filter(outcome=Build.IN_PROGRESS).order_by("-started_on")
        for build in builds:
            build_table.append(_json_build_status(build.id,False))
    except Exception as e:
        build_table = str(e)
    return JsonResponse({'building' : build_table, 'count' : len(builds)})

def json_build(request,build_id):
    return JsonResponse({'build' : _json_build_status(build_id,True)})


import toastermain.settings

from orm.models import Project, ProjectLayer, ProjectVariable
from bldcontrol.models import  BuildEnvironment

# we have a set of functions if we're in managed mode, or
# a default "page not available" simple functions for interactive mode

if True:
    from django.contrib.auth.models import User
    from django.contrib.auth import authenticate, login

    from orm.models import LayerSource, ToasterSetting, Release

    import traceback

    class BadParameterException(Exception):
        ''' The exception raised on invalid POST requests '''
        pass

    # new project
    def newproject(request):
        if not project_enable:
            return redirect( landing )

        template = "newproject.html"
        context = {
            'email': request.user.email if request.user.is_authenticated else '',
            'username': request.user.username if request.user.is_authenticated else '',
            'releases': Release.objects.order_by("description"),
        }

        try:
            context['defaultbranch'] = ToasterSetting.objects.get(name = "DEFAULT_RELEASE").value
        except ToasterSetting.DoesNotExist:
            pass

        if request.method == "GET":
            # render new project page
            return toaster_render(request, template, context)
        elif request.method == "POST":
            mandatory_fields = ['projectname', 'ptype']
            try:
                ptype = request.POST.get('ptype')
                if ptype == "import":
                    mandatory_fields.append('importdir')
                else:
                    mandatory_fields.append('projectversion')
                # make sure we have values for all mandatory_fields
                missing = [field for field in mandatory_fields if len(request.POST.get(field, '')) == 0]
                if missing:
                    # set alert for missing fields
                    raise BadParameterException("Fields missing: %s" % ", ".join(missing))

                if not request.user.is_authenticated:
                    user = authenticate(username = request.POST.get('username', '_anonuser'), password = 'nopass')
                    if user is None:
                        user = User.objects.create_user(username = request.POST.get('username', '_anonuser'), email = request.POST.get('email', ''), password = "nopass")

                        user = authenticate(username = user.username, password = 'nopass')
                    login(request, user)

                #  save the project
                if ptype == "import":
                    if not os.path.isdir('%s/conf' % request.POST['importdir']):
                        raise BadParameterException("Bad path or missing 'conf' directory (%s)" % request.POST['importdir'])
                    from django.core import management
                    management.call_command('buildimport', '--command=import', '--name=%s' % request.POST['projectname'], '--path=%s' % request.POST['importdir'])
                    prj = Project.objects.get(name = request.POST['projectname'])
                    prj.merged_attr = True
                    prj.save()
                else:
                    release = Release.objects.get(pk = request.POST.get('projectversion', None ))
                    prj = Project.objects.create_project(name = request.POST['projectname'], release = release)
                    prj.user_id = request.user.pk
                    if 'mergeattr' == request.POST.get('mergeattr', ''):
                        prj.merged_attr = True
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
                return toaster_render(request, template, context)

        raise Exception("Invalid HTTP method for this page")

    # new project
    def newproject_specific(request, pid):
        if not project_enable:
            return redirect( landing )

        project = Project.objects.get(pk=pid)
        template = "newproject_specific.html"
        context = {
            'email': request.user.email if request.user.is_authenticated else '',
            'username': request.user.username if request.user.is_authenticated else '',
            'releases': Release.objects.order_by("description"),
            'projectname': project.name,
            'project_pk': project.pk,
        }

        # WORKAROUND: if we already know release, redirect 'newproject_specific' to 'project_specific'
        if '1' == project.get_variable('INTERNAL_PROJECT_SPECIFIC_SKIPRELEASE'):
            return redirect(reverse(project_specific, args=(project.pk,)))

        try:
            context['defaultbranch'] = ToasterSetting.objects.get(name = "DEFAULT_RELEASE").value
        except ToasterSetting.DoesNotExist:
            pass

        if request.method == "GET":
            # render new project page
            return toaster_render(request, template, context)
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

                if not request.user.is_authenticated:
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

                prj = Project.objects.create_project(name = request.POST['projectname'], release = release, existing_project = project)
                prj.user_id = request.user.pk
                prj.save()
                return redirect(reverse(project_specific, args=(prj.pk,)) + "?notify=new-project")

            except (IntegrityError, BadParameterException) as e:
                # fill in page with previously submitted values
                for field in mandatory_fields:
                    context.__setitem__(field, request.POST.get(field, "-- missing"))
                if isinstance(e, IntegrityError) and "username" in str(e):
                    context['alert'] = "Your chosen username is already used"
                else:
                    context['alert'] = str(e)
                return toaster_render(request, template, context)

        raise Exception("Invalid HTTP method for this page")

    # Shows the edit project page
    def project(request, pid):
        project = Project.objects.get(pk=pid)

        if '1' == os.environ.get('TOASTER_PROJECTSPECIFIC'):
            if request.GET:
                #Example:request.GET=<QueryDict: {'setMachine': ['qemuarm']}>
                params = urlencode(request.GET).replace('%5B%27','').replace('%27%5D','')
                return redirect("%s?%s" % (reverse(project_specific, args=(project.pk,)),params))
            else:
                return redirect(reverse(project_specific, args=(project.pk,)))
        context = {"project": project}
        return toaster_render(request, "project.html", context)

    # Shows the edit project-specific page
    def project_specific(request, pid):
        project = Project.objects.get(pk=pid)

        # Are we refreshing from a successful project specific update clone?
        if Project.PROJECT_SPECIFIC_CLONING_SUCCESS == project.get_variable(Project.PROJECT_SPECIFIC_STATUS):
            return redirect(reverse(landing_specific,args=(project.pk,)))

        context = {
            "project": project,
            "is_new" : project.get_variable(Project.PROJECT_SPECIFIC_ISNEW),
            "default_image_recipe" : project.get_variable(Project.PROJECT_SPECIFIC_DEFAULTIMAGE),
            "mru" : Build.objects.all().filter(project=project,outcome=Build.IN_PROGRESS),
            }
        if project.build_set.filter(outcome=Build.IN_PROGRESS).count() > 0:
            context['build_in_progress_none_completed'] = True
        else:
            context['build_in_progress_none_completed'] = False
        return toaster_render(request, "project.html", context)

    # perform the final actions for the project specific page
    def project_specific_finalize(cmnd, pid):
        project = Project.objects.get(pk=pid)
        callback = project.get_variable(Project.PROJECT_SPECIFIC_CALLBACK)
        if "update" == cmnd:
            # Delete all '_PROJECT_PREPARE_' builds
            for b in Build.objects.all().filter(project=project):
                delete_build = False
                for t in b.target_set.all():
                    if '_PROJECT_PREPARE_' == t.target:
                        delete_build = True
                if delete_build:
                    from django.core import management
                    management.call_command('builddelete', str(b.id), interactive=False)
            # perform callback at this last moment if defined, in case Toaster gets shutdown next
            default_target = project.get_variable(Project.PROJECT_SPECIFIC_DEFAULTIMAGE)
            if callback:
                callback = callback.replace("<IMAGE>",default_target)
        if "cancel" == cmnd:
            if callback:
                callback = callback.replace("<IMAGE>","none")
                callback = callback.replace("--update","--cancel")
        # perform callback at this last moment if defined, in case this Toaster gets shutdown next
        ret = ''
        if callback:
            ret = os.system('bash -c "%s"' % callback)
            project.set_variable(Project.PROJECT_SPECIFIC_CALLBACK,'')
        # Delete the temp project specific variables
        project.set_variable(Project.PROJECT_SPECIFIC_ISNEW,'')
        project.set_variable(Project.PROJECT_SPECIFIC_STATUS,Project.PROJECT_SPECIFIC_NONE)
        # WORKAROUND: Release this workaround flag
        project.set_variable('INTERNAL_PROJECT_SPECIFIC_SKIPRELEASE','')

    # Shows the final landing page for project specific update
    def landing_specific(request, pid):
        project_specific_finalize("update", pid)
        context = {
            "install_dir": os.environ['TOASTER_DIR'],
        }
        return toaster_render(request, "landing_specific.html", context)

    # Shows the related landing-specific page
    def landing_specific_cancel(request, pid):
        project_specific_finalize("cancel", pid)
        context = {
            "install_dir": os.environ['TOASTER_DIR'],
            "status": "cancel",
        }
        return toaster_render(request, "landing_specific.html", context)

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
                                              value="qemux86-64")
        context = {'project': new_project}
        return toaster_render(request, "js-unit-tests.html", context)

    from django.views.decorators.csrf import csrf_exempt
    @csrf_exempt
    @log_view_mixin
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

    @log_view_mixin
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

            # return all project settings, filter out disallowed and elsewhere-managed variables
            vars_managed,vars_fstypes,vars_disallowed = get_project_configvars_context()
            configvars_query = ProjectVariable.objects.filter(project_id = pid).all()
            for var in vars_managed:
                configvars_query = configvars_query.exclude(name = var)
            for var in vars_disallowed:
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
                return_data['image_install:append'] = ProjectVariable.objects.get(project = prj, name = "IMAGE_INSTALL:append").value,
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


    @log_view_mixin
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
        return toaster_render(request, template, context)

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

        return toaster_render(request, 'layerdetails.html', context)


    def get_project_configvars_context():
        # Vars managed outside of this view
        vars_managed = {
            'MACHINE', 'BBLAYERS'
        }

        vars_disallowed  = {
            'PARALLEL_MAKE','BB_NUMBER_THREADS',
            'BB_DISKMON_DIRS','BB_NUMBER_THREADS','CVS_PROXY_HOST','CVS_PROXY_PORT',
            'PARALLEL_MAKE','TMPDIR',
            'all_proxy','ftp_proxy','http_proxy ','https_proxy'
            }

        vars_fstypes = Target_Image_File.SUFFIXES

        return(vars_managed,sorted(vars_fstypes),vars_disallowed)

    def projectconf(request, pid):

        try:
            prj = Project.objects.get(id = pid)
        except Project.DoesNotExist:
            return HttpResponseNotFound("<h1>Project id " + pid + " is unavailable</h1>")

        # remove disallowed and externally managed varaibles from this list
        vars_managed,vars_fstypes,vars_disallowed = get_project_configvars_context()
        configvars = ProjectVariable.objects.filter(project_id = pid).all()
        for var in vars_managed:
            configvars = configvars.exclude(name = var)
        for var in vars_disallowed:
            configvars = configvars.exclude(name = var)

        context = {
            'project':          prj,
            'configvars':       configvars,
            'vars_managed':     vars_managed,
            'vars_fstypes':     vars_fstypes,
            'vars_disallowed':  vars_disallowed,
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
            context['image_install:append'] =  ProjectVariable.objects.get(project = prj, name = "IMAGE_INSTALL:append").value
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

        return toaster_render(request, "projectconf.html", context)

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
                return toaster_render(request, "unavailable_artifact.html")
        except (ObjectDoesNotExist, IOError):
            return toaster_render(request, "unavailable_artifact.html")


class CommandLineBuilds(TemplateView):
    model = EventLogsImports
    template_name = 'command_line_builds.html'

    def get_context_data(self, **kwargs):
        context = super(CommandLineBuilds, self).get_context_data(**kwargs)
        #get value from BB_DEFAULT_EVENTLOG defined in bitbake.conf
        eventlog = subprocess.check_output(['bitbake-getvar', 'BB_DEFAULT_EVENTLOG', '--value'])
        if eventlog:
            logs_dir = os.path.dirname(eventlog.decode().strip('\n'))
            files = os.listdir(logs_dir)
            imported_files = EventLogsImports.objects.all()
            files_list = []

            # Filter files that end with ".json"
            event_files = []
            for file in files:
                if file.endswith(".json"):
                    # because BB_DEFAULT_EVENTLOG is a directory, we need to check if the file is a valid eventlog
                    with open("{}/{}".format(logs_dir, file)) as efile:
                        content = efile.read()
                        if 'allvariables' in content:
                            event_files.append(file)

            #build dict for template using db data
            for event_file in event_files:
                if imported_files.filter(name=event_file):
                    files_list.append({
                        'name': event_file,
                        'imported': True,
                        'build_id': imported_files.filter(name=event_file)[0].build_id,
                        'size': os.path.getsize("{}/{}".format(logs_dir, event_file))
                    })
                else:
                    files_list.append({
                        'name': event_file,
                        'imported': False,
                        'build_id': None,
                        'size': os.path.getsize("{}/{}".format(logs_dir, event_file))
                    })
                    context['import_all'] = True

            context['files'] = files_list
            context['dir'] = logs_dir
        else:
            context['files'] = []
            context['dir'] = ''

        # enable session variable
        if not self.request.session.get('file'):
            self.request.session['file'] = ""

        context['form'] = LoadFileForm()
        context['project_enable'] = project_enable
        return context

    def post(self, request, **kwargs):
        logs_dir = request.POST.get('dir')
        all_files =  request.POST.get('all')

        # check if a build is already in progress
        if Build.objects.filter(outcome=Build.IN_PROGRESS):
            messages.add_message(
                self.request,
                messages.ERROR,
                "A build is already in progress. Please wait for it to complete before starting a new build."
            )
            return JsonResponse({'response': 'building'})
        imported_files = EventLogsImports.objects.all()
        try:
            if all_files == 'true':
                # use of session variable to deactivate icon for builds in progress
                request.session['all_builds'] = True
                request.session.modified = True
                request.session.save()

                files = ast.literal_eval(request.POST.get('file'))
                for file in files:
                    if imported_files.filter(name=file.get('name')).exists():
                        imported_files.filter(name=file.get('name'))[0].imported = True
                    else:
                        with open("{}/{}".format(logs_dir, file.get('name'))) as eventfile:
                            # load variables from the first line
                            variables = None
                            while line := eventfile.readline().strip():
                                try:
                                    variables = json.loads(line)['allvariables']
                                    break
                                except (KeyError, json.JSONDecodeError):
                                    continue
                            if not variables:
                                raise Exception("File content missing  build variables")
                            eventfile.seek(0)
                            params = namedtuple('ConfigParams', ['observe_only'])(True)
                            player = eventreplay.EventPlayer(eventfile, variables)

                            toasterui.main(player, player, params)
                        event_log_import = EventLogsImports.objects.create(name=file.get('name'), imported=True)
                        event_log_import.build_id = Build.objects.last().id
                        event_log_import.save()
            else:
                if self.request.FILES.get('eventlog_file'):
                    file = self.request.FILES['eventlog_file']
                else:
                    file = request.POST.get('file')
                    # use of session variable to deactivate icon for build in progress
                    request.session['file'] = file
                    request.session['all_builds'] = False
                    request.session.modified = True
                    request.session.save()

                if imported_files.filter(name=file).exists():
                    imported_files.filter(name=file)[0].imported = True
                else:
                    if isinstance(file, InMemoryUploadedFile) or isinstance(file, TemporaryUploadedFile):
                        variables = None
                        while line := file.readline().strip():
                            try:
                                variables = json.loads(line)['allvariables']
                                break
                            except (KeyError, json.JSONDecodeError):
                                continue
                        if not variables:
                            raise Exception("File content missing  build variables")
                        file.seek(0)
                        params = namedtuple('ConfigParams', ['observe_only'])(True)
                        player = eventreplay.EventPlayer(file, variables)
                        if not os.path.exists('{}/{}'.format(logs_dir, file.name)):
                            fs = FileSystemStorage(location=logs_dir)
                            fs.save(file.name, file)
                        toasterui.main(player, player, params)
                    else:
                        with open("{}/{}".format(logs_dir, file)) as eventfile:
                            # load variables from the first line
                            variables = None
                            while line := eventfile.readline().strip():
                                try:
                                    variables = json.loads(line)['allvariables']
                                    break
                                except (KeyError, json.JSONDecodeError):
                                    continue
                            if not variables:
                                raise Exception("File content missing  build variables")
                            eventfile.seek(0)
                            params = namedtuple('ConfigParams', ['observe_only'])(True)
                            player = eventreplay.EventPlayer(eventfile, variables)
                            toasterui.main(player, player, params)
                    event_log_import = EventLogsImports.objects.create(name=file, imported=True)
                    event_log_import.build_id = Build.objects.last().id
                    event_log_import.save()
                    request.session['file'] = ""
        except Exception:
            messages.add_message(
                self.request,
                messages.ERROR,
                "The file content is not in the correct format. Update file content or upload a different file."
            )
            return HttpResponseRedirect("/toastergui/cmdline/")
        return HttpResponseRedirect('/toastergui/builds/')
