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

# pylint: disable=method-hidden
# Gives E:848, 4: An attribute defined in json.encoder line 162 hides this method (method-hidden)
# which is an invalid warning

import operator,re

from django.db.models import F, Q, Sum, Count, Max
from django.db import IntegrityError, Error
from django.shortcuts import render, redirect, get_object_or_404
from orm.models import Build, Target, Task, Layer, Layer_Version, Recipe, LogMessage, Variable
from orm.models import Task_Dependency, Recipe_Dependency, Package, Package_File, Package_Dependency
from orm.models import Target_Installed_Package, Target_File, Target_Image_File, BuildArtifact, CustomImagePackage
from orm.models import BitbakeVersion, CustomImageRecipe
from bldcontrol import bbcontroller
from django.views.decorators.cache import cache_control
from django.core.urlresolvers import reverse, resolve
from django.core.exceptions import MultipleObjectsReturned, ObjectDoesNotExist
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.http import HttpResponseBadRequest, HttpResponseNotFound
from django.utils import timezone
from django.utils.html import escape
from datetime import timedelta, datetime
from django.utils import formats
from toastergui.templatetags.projecttags import json as jsonfilter
from decimal import Decimal
import json
import os
from os.path import dirname
from functools import wraps
import itertools
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
            "detail": "(%s" % x.layer.vcs_url + (")" if x.up_branch == None else " | "+x.get_vcs_reference()+")"),
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
    import urllib
    url = reverse(view, kwargs=kwargs)
    params = {}
    for i in g:
        params[i] = g[i]
    for i in mandatory_parameters:
        if not i in params:
            params[i] = urllib.unquote(str(mandatory_parameters[i]))

    return redirect(url + "?%s" % urllib.urlencode(params), permanent = False, **kwargs)

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
    if "OR" in value:
        return reduce(operator.or_, map(lambda x: __get_q_for_val(name, x), [ x for x in value.split("OR") ]))
    if "AND" in value:
        return reduce(operator.and_, map(lambda x: __get_q_for_val(name, x), [ x for x in value.split("AND") ]))
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

    and_query = []
    for kv in zip(and_keys, and_values):
        or_keys = kv[0].split(OR_VALUE_SEPARATOR)
        or_values = kv[1].split(OR_VALUE_SEPARATOR)
        querydict = dict(zip(or_keys, or_values))
        and_query.append(reduce(operator.or_, map(lambda x: __get_q_for_val(x, querydict[x]), [k for k in querydict])))

    return reduce(operator.and_, [k for k in and_query])

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
            if not reduce(lambda x, y: x or y, [ field.startswith(x) for x in valid_fields ]):
                return None, (field, [ x for x in valid_fields ])

    return field_input, invalid

# uses search_allowed_fields in orm/models.py to create a search query
# for these fields with the supplied input text
def _get_search_results(search_term, queryset, model):
    search_objects = []
    for st in search_term.split(" "):
        q_map = map(lambda x: Q(**{x+'__icontains': st}),
                model.search_allowed_fields)

        search_objects.append(reduce(operator.or_, q_map))
    search_object = reduce(operator.and_, search_objects)
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

    ##
    # set up custom target list with computed package and image data
    #

    targets = [ ]
    ntargets = 0
    hasImages = False
    targetHasNoImages = False
    for t in tgts:
        elem = { }
        elem[ 'target' ] = t
        if t.is_image:
            hasImages = True
        npkg = 0
        pkgsz = 0
        package = None
        for package in Package.objects.filter(id__in = [x.package_id for x in t.target_installed_package_set.all()]):
            pkgsz = pkgsz + package.size
            if ( package.installed_name ):
                npkg = npkg + 1
        elem[ 'npkg' ] = npkg
        elem[ 'pkgsz' ] = pkgsz
        ti = Target_Image_File.objects.filter( target_id = t.id )
        imageFiles = [ ]
        for i in ti:
            ndx = i.file_name.rfind( '/' )
            if ( ndx < 0 ):
                ndx = 0;
            f = i.file_name[ ndx + 1: ]
            imageFiles.append({
                'id': i.id,
                'path': f,
                'size': i.file_size,
                'suffix': i.suffix
            })
        if t.is_image and (len(imageFiles) <= 0 or len(t.license_manifest_path) <= 0):
            targetHasNoImages = True
        elem[ 'imageFiles' ] = imageFiles
        elem[ 'targetHasNoImages' ] = targetHasNoImages
        targets.append( elem )

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
            'hasImages'       : hasImages,
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

def target_common( request, build_id, target_id, variant ):
    template = "target.html"
    default_orderby = 'name:+'

    (pagesize, orderby) = _get_parameters_values(request, 25, default_orderby)
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby': orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters(
                    variant, request.GET, mandatory_parameters,
                    build_id = build_id, target_id = target_id )
    ( filter_string, search_term, ordering_string ) = _search_tuple( request, Package )

    # FUTURE:  get rid of nested sub-queries replacing with ManyToMany field
    queryset = Package.objects.filter(
                    size__gte = 0,
                    id__in = Target_Installed_Package.objects.filter(
                        target_id=target_id ).values( 'package_id' ))
    packages_sum =  queryset.aggregate( Sum( 'installed_size' ))
    queryset = _get_queryset(
            Package, queryset, filter_string, search_term, ordering_string, 'name' )
    queryset = queryset.select_related("recipe", "recipe__layer_version", "recipe__layer_version__layer")
    packages = _build_page_range( Paginator(queryset, pagesize), request.GET.get( 'page', 1 ))

    build = Build.objects.get( pk = build_id )

    # bring in package dependencies
    for p in packages.object_list:
        p.runtime_dependencies = p.package_dependencies_source.filter(
            target_id = target_id, dep_type=Package_Dependency.TYPE_TRDEPENDS ).select_related("depends_on")
        p.reverse_runtime_dependencies = p.package_dependencies_target.filter(
            target_id = target_id, dep_type=Package_Dependency.TYPE_TRDEPENDS ).select_related("package")
    tc_package = {
        'name'       : 'Package',
        'qhelp'      : 'Packaged output resulting from building a recipe included in this image',
        'orderfield' : _get_toggle_order( request, "name" ),
        'ordericon'  : _get_toggle_order_icon( request, "name" ),
        }
    tc_packageVersion = {
        'name'       : 'Package version',
        'qhelp'      : 'The package version and revision',
        }
    tc_size = {
        'name'       : 'Size',
        'qhelp'      : 'The size of the package',
        'orderfield' : _get_toggle_order( request, "size", True ),
        'ordericon'  : _get_toggle_order_icon( request, "size" ),
        'orderkey'   : 'size',
        'clclass'    : 'size',
        'dclass'     : 'span2',
        }
    if ( variant == 'target' ):
        tc_size[ "hidden" ] = 0
    else:
        tc_size[ "hidden" ] = 1
    tc_sizePercentage = {
        'name'       : 'Size over total (%)',
        'qhelp'      : 'Proportion of the overall size represented by this package',
        'clclass'    : 'size_over_total',
        'hidden'     : 1,
        }
    tc_license = {
        'name'       : 'License',
        'qhelp'      : 'The license under which the package is distributed. Separate license names u\
sing | (pipe) means there is a choice between licenses. Separate license names using & (ampersand) m\
eans multiple licenses exist that cover different parts of the source',
        'orderfield' : _get_toggle_order( request, "license" ),
        'ordericon'  : _get_toggle_order_icon( request, "license" ),
        'orderkey'   : 'license',
        'clclass'    : 'license',
        }
    if ( variant == 'target' ):
        tc_license[ "hidden" ] = 1
    else:
        tc_license[ "hidden" ] = 0
    tc_dependencies = {
        'name'       : 'Dependencies',
        'qhelp'      : "Package runtime dependencies (other packages)",
        'clclass'    : 'depends',
        }
    if ( variant == 'target' ):
        tc_dependencies[ "hidden" ] = 0
    else:
        tc_dependencies[ "hidden" ] = 1
    tc_rdependencies = {
        'name'       : 'Reverse dependencies',
        'qhelp'      : 'Package run-time reverse dependencies (i.e. which other packages depend on this package',
        'clclass'    : 'brought_in_by',
        }
    if ( variant == 'target' ):
        tc_rdependencies[ "hidden" ] = 0
    else:
        tc_rdependencies[ "hidden" ] = 1
    tc_recipe = {
        'name'       : 'Recipe',
        'qhelp'      : 'The name of the recipe building the package',
        'orderfield' : _get_toggle_order( request, "recipe__name" ),
        'ordericon'  : _get_toggle_order_icon( request, "recipe__name" ),
        'orderkey'   : "recipe__name",
        'clclass'    : 'recipe_name',
        'hidden'     : 0,
        }
    tc_recipeVersion = {
        'name'       : 'Recipe version',
        'qhelp'      : 'Version and revision of the recipe building the package',
        'clclass'    : 'recipe_version',
        'hidden'     : 1,
        }
    tc_layer = {
        'name'       : 'Layer',
        'qhelp'      : 'The name of the layer providing the recipe that builds the package',
        'orderfield' : _get_toggle_order( request, "recipe__layer_version__layer__name" ),
        'ordericon'  : _get_toggle_order_icon( request, "recipe__layer_version__layer__name" ),
        'orderkey'   : "recipe__layer_version__layer__name",
        'clclass'    : 'layer_name',
        'hidden'     : 1,
        }
    tc_layerBranch = {
        'name'       : 'Layer branch',
        'qhelp'      : 'The Git branch of the layer providing the recipe that builds the package',
        'orderfield' : _get_toggle_order( request, "recipe__layer_version__branch" ),
        'ordericon'  : _get_toggle_order_icon( request, "recipe__layer_version__branch" ),
        'orderkey'   : "recipe__layer_version__branch",
        'clclass'    : 'layer_branch',
        'hidden'     : 1,
        }
    tc_layerCommit = {
        'name'       : 'Layer commit',
        'qhelp'      : 'The Git commit of the layer providing the recipe that builds the package',
        'clclass'    : 'layer_commit',
        'hidden'     : 1,
        }

    context = {
        'objectname': variant,
        'build'                : build,
        'project'              : build.project,
        'target'               : Target.objects.filter( pk = target_id )[ 0 ],
        'objects'              : packages,
        'packages_sum'         : packages_sum[ 'installed_size__sum' ],
        'object_search_display': "packages included",
        'default_orderby'      : default_orderby,
        'tablecols'            : [
                    tc_package,
                    tc_packageVersion,
                    tc_license,
                    tc_size,
                    tc_sizePercentage,
                    tc_dependencies,
                    tc_rdependencies,
                    tc_recipe,
                    tc_recipeVersion,
                    tc_layer,
                    tc_layerBranch,
                    tc_layerCommit,
                ]
        }


    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def target( request, build_id, target_id ):
    return( target_common( request, build_id, target_id, "target" ))

def targetpkg( request, build_id, target_id ):
    return( target_common( request, build_id, target_id, "targetpkg" ))

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
            print "Exception ", e
            traceback.print_exc(e)

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
    return map(lambda x: x.depends_on, Task_Dependency.objects.filter(task=task_object).filter(depends_on__order__gt = 0).exclude(depends_on__outcome = Task.OUTCOME_NA).select_related("depends_on"))


def _find_task_revdep(task_object):
    tp = []
    tp = map(lambda t: t.task, Task_Dependency.objects.filter(depends_on=task_object).filter(task__order__gt=0).exclude(task__outcome = Task.OUTCOME_NA).select_related("task", "task__recipe", "task__build"))
    return tp

def _find_task_revdep_list(tasklist):
    tp = []
    tp = map(lambda t: t.task, Task_Dependency.objects.filter(depends_on__in=tasklist).filter(task__order__gt=0).exclude(task__outcome = Task.OUTCOME_NA).select_related("task", "task__recipe", "task__build"))
    return tp

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

def tasks_common(request, build_id, variant, task_anchor):
# This class is shared between these pages
#
# Column    tasks  buildtime  diskio  cpuusage
# --------- ------ ---------- ------- ---------
# Cache      def
# CPU                                   min -
# Disk                         min -
# Executed   def     def       def      def
# Log
# Order      def +
# Outcome    def     def       def      def
# Recipe     min     min       min      min
# Version
# Task       min     min       min      min
# Time               min -
#
# 'min':on always, 'def':on by default, else hidden
# '+' default column sort up, '-' default column sort down

    anchor = request.GET.get('anchor', '')
    if not anchor:
        anchor=task_anchor

    # default ordering depends on variant
    default_orderby = None
    filter_search_display = 'tasks'

    if 'buildtime' == variant:
        default_orderby = 'elapsed_time:-'
        title_variant = 'Time'
        object_search_display = 'time data'
    elif 'diskio' == variant:
        default_orderby = 'disk_io:-'
        title_variant = 'Disk I/O'
        object_search_display = 'disk I/O data'
    elif 'cputime' == variant:
        default_orderby = 'cpu_time_system:-'
        title_variant='CPU time'
        object_search_display = 'CPU time data'
    else:
        default_orderby = 'order:+'
        title_variant = 'Tasks'
        object_search_display = 'tasks'

    (pagesize, orderby) = _get_parameters_values(request, 25, default_orderby)

    mandatory_parameters = {'count': pagesize, 'page' : 1, 'orderby': orderby}

    template = 'tasks.html'
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        if task_anchor:
            mandatory_parameters['anchor']=task_anchor
        return _redirect_parameters( variant, request.GET, mandatory_parameters, build_id = build_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Task)
    queryset_all = Task.objects.filter(build=build_id).exclude(order__isnull=True).exclude(outcome=Task.OUTCOME_NA)
    queryset_all = queryset_all.select_related("recipe", "build")

    queryset_with_search = _get_queryset(Task, queryset_all, None , search_term, ordering_string, 'order')

    if ordering_string.startswith('outcome'):
        queryset = _get_queryset(Task, queryset_all, filter_string, search_term, 'order:+', 'order')
        queryset = sorted(queryset, key=lambda ur: (ur.outcome_text), reverse=ordering_string.endswith('-'))
    elif ordering_string.startswith('sstate_result'):
        queryset = _get_queryset(Task, queryset_all, filter_string, search_term, 'order:+', 'order')
        queryset = sorted(queryset, key=lambda ur: (ur.sstate_text), reverse=ordering_string.endswith('-'))
    else:
        queryset = _get_queryset(Task, queryset_all, filter_string, search_term, ordering_string, 'order')


    # compute the anchor's page
    if anchor:
        request.GET = request.GET.copy()
        del request.GET['anchor']
        i=0
        a=int(anchor)
        count_per_page=int(pagesize)
        for task_object in queryset.iterator():
            if a == task_object.order:
                new_page= (i / count_per_page ) + 1
                request.GET.__setitem__('page', new_page)
                mandatory_parameters['page']=new_page
                return _redirect_parameters( variant, request.GET, mandatory_parameters, build_id = build_id)
            i += 1

    task_objects = _build_page_range(Paginator(queryset, pagesize),request.GET.get('page', 1))

    # define (and modify by variants) the 'tablecols' members
    tc_order={
        'name':'Order',
        'qhelp':'The running sequence of each task in the build',
        'clclass': 'order', 'hidden' : 1,
        'orderkey' : 'order',
        'orderfield':_get_toggle_order(request, "order"),
        'ordericon':_get_toggle_order_icon(request, "order")}
    if 'tasks' == variant:
        tc_order['hidden']='0'
        del tc_order['clclass']

    tc_recipe={
        'name':'Recipe',
        'qhelp':'The name of the recipe to which each task applies',
        'orderkey' : 'recipe__name',
        'orderfield': _get_toggle_order(request, "recipe__name"),
        'ordericon':_get_toggle_order_icon(request, "recipe__name"),
    }
    tc_recipe_version={
        'name':'Recipe version',
        'qhelp':'The version of the recipe to which each task applies',
        'clclass': 'recipe_version', 'hidden' : 1,
    }
    tc_task={
        'name':'Task',
        'qhelp':'The name of the task',
        'orderfield': _get_toggle_order(request, "task_name"),
        'ordericon':_get_toggle_order_icon(request, "task_name"),
        'orderkey' : 'task_name',
    }
    tc_executed={
        'name':'Executed',
        'qhelp':"This value tells you if a task had to run (executed) in order to generate the task output, or if the output was provided by another task and therefore the task didn't need to run (not executed)",
        'clclass': 'executed', 'hidden' : 0,
        'orderfield': _get_toggle_order(request, "task_executed"),
        'ordericon':_get_toggle_order_icon(request, "task_executed"),
        'orderkey' : 'task_executed',
        'filter' : {
                   'class' : 'executed',
                   'label': 'Show:',
                   'options' : [
                               ('Executed Tasks', 'task_executed:1', queryset_with_search.filter(task_executed=1).count()),
                               ('Not Executed Tasks', 'task_executed:0', queryset_with_search.filter(task_executed=0).count()),
                               ]
                   }

    }
    tc_outcome={
        'name':'Outcome',
        'qhelp':"This column tells you if 'executed' tasks succeeded or failed. The column also tells you why 'not executed' tasks did not need to run",
        'clclass': 'outcome', 'hidden' : 0,
        'orderfield': _get_toggle_order(request, "outcome"),
        'ordericon':_get_toggle_order_icon(request, "outcome"),
        'orderkey' : 'outcome',
        'filter' : {
                   'class' : 'outcome',
                   'label': 'Show:',
                   'options' : [
                               ('Succeeded Tasks', 'outcome:%d'%Task.OUTCOME_SUCCESS, queryset_with_search.filter(outcome=Task.OUTCOME_SUCCESS).count(), "'Succeeded' tasks are those that ran and completed during the build" ),
                               ('Failed Tasks', 'outcome:%d'%Task.OUTCOME_FAILED, queryset_with_search.filter(outcome=Task.OUTCOME_FAILED).count(), "'Failed' tasks are those that ran but did not complete during the build"),
                               ('Cached Tasks', 'outcome:%d'%Task.OUTCOME_CACHED, queryset_with_search.filter(outcome=Task.OUTCOME_CACHED).count(), 'Cached tasks restore output from the <code>sstate-cache</code> directory or mirrors'),
                               ('Prebuilt Tasks', 'outcome:%d'%Task.OUTCOME_PREBUILT, queryset_with_search.filter(outcome=Task.OUTCOME_PREBUILT).count(),'Prebuilt tasks didn\'t need to run because their output was reused from a previous build'),
                               ('Covered Tasks', 'outcome:%d'%Task.OUTCOME_COVERED, queryset_with_search.filter(outcome=Task.OUTCOME_COVERED).count(), 'Covered tasks didn\'t need to run because their output is provided by another task in this build'),
                               ('Empty Tasks', 'outcome:%d'%Task.OUTCOME_EMPTY, queryset_with_search.filter(outcome=Task.OUTCOME_EMPTY).count(), 'Empty tasks have no executable content'),
                               ]
                   }

    }

    tc_cache={
        'name':'Cache attempt',
        'qhelp':'This column tells you if a task tried to restore output from the <code>sstate-cache</code> directory or mirrors, and reports the result: Succeeded, Failed or File not in cache',
        'clclass': 'cache_attempt', 'hidden' : 0,
        'orderfield': _get_toggle_order(request, "sstate_result"),
        'ordericon':_get_toggle_order_icon(request, "sstate_result"),
        'orderkey' : 'sstate_result',
        'filter' : {
                   'class' : 'cache_attempt',
                   'label': 'Show:',
                   'options' : [
                               ('Tasks with cache attempts', 'sstate_result__gt:%d'%Task.SSTATE_NA, queryset_with_search.filter(sstate_result__gt=Task.SSTATE_NA).count(), 'Show all tasks that tried to restore ouput from the <code>sstate-cache</code> directory or mirrors'),
                               ("Tasks with 'File not in cache' attempts", 'sstate_result:%d'%Task.SSTATE_MISS,  queryset_with_search.filter(sstate_result=Task.SSTATE_MISS).count(), 'Show tasks that tried to restore output, but did not find it in the <code>sstate-cache</code> directory or mirrors'),
                               ("Tasks with 'Failed' cache attempts", 'sstate_result:%d'%Task.SSTATE_FAILED,  queryset_with_search.filter(sstate_result=Task.SSTATE_FAILED).count(), 'Show tasks that found the required output in the <code>sstate-cache</code> directory or mirrors, but could not restore it'),
                               ("Tasks with 'Succeeded' cache attempts", 'sstate_result:%d'%Task.SSTATE_RESTORED,  queryset_with_search.filter(sstate_result=Task.SSTATE_RESTORED).count(), 'Show tasks that successfully restored the required output from the <code>sstate-cache</code> directory or mirrors'),
                               ]
                   }

    }
    #if   'tasks' == variant: tc_cache['hidden']='0';
    tc_time={
        'name':'Time (secs)',
        'qhelp':'How long it took the task to finish in seconds',
        'orderfield': _get_toggle_order(request, "elapsed_time", True),
        'ordericon':_get_toggle_order_icon(request, "elapsed_time"),
        'orderkey' : 'elapsed_time',
        'clclass': 'time_taken', 'hidden' : 1,
    }
    if 'buildtime' == variant:
        tc_time['hidden']='0'
        del tc_time['clclass']
        tc_cache['hidden']='1'

    tc_cpu_time_system={
        'name':'System CPU time (secs)',
        'qhelp':'Total amount of time spent executing in kernel mode, in ' +
                'seconds. Note that this time can be greater than the task ' +
                'time due to parallel execution.',
        'orderfield': _get_toggle_order(request, "cpu_time_system", True),
        'ordericon':_get_toggle_order_icon(request, "cpu_time_system"),
        'orderkey' : 'cpu_time_system',
        'clclass': 'cpu_time_system', 'hidden' : 1,
    }

    tc_cpu_time_user={
        'name':'User CPU time (secs)',
        'qhelp':'Total amount of time spent executing in user mode, in seconds. ' +
                'Note that this time can be greater than the task time due to ' +
                'parallel execution.',
        'orderfield': _get_toggle_order(request, "cpu_time_user", True),
        'ordericon':_get_toggle_order_icon(request, "cpu_time_user"),
        'orderkey' : 'cpu_time_user',
        'clclass': 'cpu_time_user', 'hidden' : 1,
    }

    if 'cputime' == variant:
        tc_cpu_time_system['hidden']='0'
        tc_cpu_time_user['hidden']='0'
        del tc_cpu_time_system['clclass']
        del tc_cpu_time_user['clclass']
        tc_cache['hidden']='1'

    tc_diskio={
        'name':'Disk I/O (bytes)',
        'qhelp':'Number of bytes written to and read from the disk during the task',
        'orderfield': _get_toggle_order(request, "disk_io", True),
        'ordericon':_get_toggle_order_icon(request, "disk_io"),
        'orderkey' : 'disk_io',
        'clclass': 'disk_io', 'hidden' : 1,
    }
    if 'diskio' == variant:
        tc_diskio['hidden']='0'
        del tc_diskio['clclass']
        tc_cache['hidden']='1'

    build = Build.objects.get(pk=build_id)

    context = { 'objectname': variant,
                'object_search_display': object_search_display,
                'filter_search_display': filter_search_display,
                'mainheading': title_variant,
                'build': build,
                'project': build.project,
                'objects': task_objects,
                'default_orderby' : default_orderby,
                'search_term': search_term,
                'total_count': queryset_with_search.count(),
                'tablecols':[
                    tc_order,
                    tc_recipe,
                    tc_recipe_version,
                    tc_task,
                    tc_executed,
                    tc_outcome,
                    tc_cache,
                    tc_time,
                    tc_cpu_time_system,
                    tc_cpu_time_user,
                    tc_diskio,
                ]}


    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

def tasks(request, build_id):
    return tasks_common(request, build_id, 'tasks', '')

def tasks_task(request, build_id, task_id):
    return tasks_common(request, build_id, 'tasks', task_id)

def buildtime(request, build_id):
    return tasks_common(request, build_id, 'buildtime', '')

def diskio(request, build_id):
    return tasks_common(request, build_id, 'diskio', '')

def cputime(request, build_id):
    return tasks_common(request, build_id, 'cputime', '')

def recipes(request, build_id):
    template = 'recipes.html'
    (pagesize, orderby) = _get_parameters_values(request, 100, 'name:+')
    mandatory_parameters = { 'count': pagesize,  'page' : 1, 'orderby' : orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'recipes', request.GET, mandatory_parameters, build_id = build_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Recipe)

    build = Build.objects.get(pk=build_id)

    queryset = build.get_recipes()
    queryset = _get_queryset(Recipe, queryset, filter_string, search_term, ordering_string, 'name')

    recipes = _build_page_range(Paginator(queryset, pagesize),request.GET.get('page', 1))

    # prefetch the forward and reverse recipe dependencies
    deps = { }
    revs = { }
    queryset_dependency=Recipe_Dependency.objects.filter(recipe__layer_version__build_id = build_id).select_related("depends_on", "recipe")
    for recipe in recipes:
        deplist = [ ]
        for recipe_dep in [x for x in queryset_dependency if x.recipe_id == recipe.id]:
            deplist.append(recipe_dep)
        deps[recipe.id] = deplist
        revlist = [ ]
        for recipe_dep in [x for x in queryset_dependency if x.depends_on_id == recipe.id]:
            revlist.append(recipe_dep)
        revs[recipe.id] = revlist

    context = {
        'objectname': 'recipes',
        'build': build,
        'project': build.project,
        'objects': recipes,
        'default_orderby' : 'name:+',
        'recipe_deps' : deps,
        'recipe_revs' : revs,
        'tablecols':[
            {
                'name':'Recipe',
                'qhelp':'Information about a single piece of software, including where to download the source, configuration options, how to compile the source files and how to package the compiled output',
                'orderfield': _get_toggle_order(request, "name"),
                'ordericon':_get_toggle_order_icon(request, "name"),
            },
            {
                'name':'Recipe version',
                'qhelp':'The recipe version and revision',
            },
            {
                'name':'Dependencies',
                'qhelp':'Recipe build-time dependencies (i.e. other recipes)',
                'clclass': 'depends_on', 'hidden': 1,
            },
            {
                'name':'Reverse dependencies',
                'qhelp':'Recipe build-time reverse dependencies (i.e. the recipes that depend on this recipe)',
                'clclass': 'depends_by', 'hidden': 1,
            },
            {
                'name':'Recipe file',
                'qhelp':'Path to the recipe .bb file',
                'orderfield': _get_toggle_order(request, "file_path"),
                'ordericon':_get_toggle_order_icon(request, "file_path"),
                'orderkey' : 'file_path',
                'clclass': 'recipe_file', 'hidden': 0,
            },
            {
                'name':'Section',
                'qhelp':'The section in which recipes should be categorized',
                'orderfield': _get_toggle_order(request, "section"),
                'ordericon':_get_toggle_order_icon(request, "section"),
                'orderkey' : 'section',
                'clclass': 'recipe_section', 'hidden': 0,
            },
            {
                'name':'License',
                'qhelp':'The list of source licenses for the recipe. Multiple license names separated by the pipe character indicates a choice between licenses. Multiple license names separated by the ampersand character indicates multiple licenses exist that cover different parts of the source',
                'orderfield': _get_toggle_order(request, "license"),
                'ordericon':_get_toggle_order_icon(request, "license"),
                'orderkey' : 'license',
                'clclass': 'recipe_license', 'hidden': 0,
            },
            {
                'name':'Layer',
                'qhelp':'The name of the layer providing the recipe',
                'orderfield': _get_toggle_order(request, "layer_version__layer__name"),
                'ordericon':_get_toggle_order_icon(request, "layer_version__layer__name"),
                'orderkey' : 'layer_version__layer__name',
                'clclass': 'layer_version__layer__name', 'hidden': 0,
            },
            {
                'name':'Layer branch',
                'qhelp':'The Git branch of the layer providing the recipe',
                'orderfield': _get_toggle_order(request, "layer_version__branch"),
                'ordericon':_get_toggle_order_icon(request, "layer_version__branch"),
                'orderkey' : 'layer_version__branch',
                'clclass': 'layer_version__branch', 'hidden': 1,
            },
            {
                'name':'Layer commit',
                'qhelp':'The Git commit of the layer providing the recipe',
                'clclass': 'layer_version__layer__commit', 'hidden': 1,
            },
            ]
        }

    response = render(request, template, context)
    _set_parameters_values(pagesize, orderby, request)
    return response

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
                 'dclass': "span4",
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

def bpackage(request, build_id):
    template = 'bpackage.html'
    (pagesize, orderby) = _get_parameters_values(request, 100, 'name:+')
    mandatory_parameters = { 'count' : pagesize,  'page' : 1, 'orderby' : orderby }
    retval = _verify_parameters( request.GET, mandatory_parameters )
    if retval:
        return _redirect_parameters( 'packages', request.GET, mandatory_parameters, build_id = build_id)
    (filter_string, search_term, ordering_string) = _search_tuple(request, Package)
    queryset = Package.objects.filter(build = build_id).filter(size__gte=0)
    queryset = _get_queryset(Package, queryset, filter_string, search_term, ordering_string, 'name')

    packages = _build_page_range(Paginator(queryset, pagesize),request.GET.get('page', 1))

    build = Build.objects.get( pk = build_id )

    context = {
        'objectname': 'packages built',
        'build': build,
        'project': build.project,
        'objects' : packages,
        'default_orderby' : 'name:+',
        'tablecols':[
            {
                'name':'Package',
                'qhelp':'Packaged output resulting from building a recipe',
                'orderfield': _get_toggle_order(request, "name"),
                'ordericon':_get_toggle_order_icon(request, "name"),
            },
            {
                'name':'Package version',
                'qhelp':'The package version and revision',
            },
            {
                'name':'Size',
                'qhelp':'The size of the package',
                'orderfield': _get_toggle_order(request, "size", True),
                'ordericon':_get_toggle_order_icon(request, "size"),
                'orderkey' : 'size',
                'clclass': 'size', 'hidden': 0,
                'dclass' : 'span2',
            },
            {
                'name':'License',
                'qhelp':'The license under which the package is distributed. Multiple license names separated by the pipe character indicates a choice between licenses. Multiple license names separated by the ampersand character indicates multiple licenses exist that cover different parts of the source',
                'orderfield': _get_toggle_order(request, "license"),
                'ordericon':_get_toggle_order_icon(request, "license"),
                'orderkey' : 'license',
                'clclass': 'license', 'hidden': 1,
            },
            {
                'name':'Recipe',
                'qhelp':'The name of the recipe building the package',
                'orderfield': _get_toggle_order(request, "recipe__name"),
                'ordericon':_get_toggle_order_icon(request, "recipe__name"),
                'orderkey' : 'recipe__name',
                'clclass': 'recipe__name', 'hidden': 0,
            },
            {
                'name':'Recipe version',
                'qhelp':'Version and revision of the recipe building the package',
                'clclass': 'recipe__version', 'hidden': 1,
            },
            {
                'name':'Layer',
                'qhelp':'The name of the layer providing the recipe that builds the package',
                'orderfield': _get_toggle_order(request, "recipe__layer_version__layer__name"),
                'ordericon':_get_toggle_order_icon(request, "recipe__layer_version__layer__name"),
                'orderkey' : 'recipe__layer_version__layer__name',
                'clclass': 'recipe__layer_version__layer__name', 'hidden': 1,
            },
            {
                'name':'Layer branch',
                'qhelp':'The Git branch of the layer providing the recipe that builds the package',
                'orderfield': _get_toggle_order(request, "recipe__layer_version__branch"),
                'ordericon':_get_toggle_order_icon(request, "recipe__layer_version__branch"),
                'orderkey' : 'recipe__layer_version__branch',
                'clclass': 'recipe__layer_version__branch', 'hidden': 1,
            },
            {
                'name':'Layer commit',
                'qhelp':'The Git commit of the layer providing the recipe that builds the package',
                'clclass': 'recipe__layer_version__layer__commit', 'hidden': 1,
            },
            ]
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

    from orm.models import Branch, LayerSource, ToasterSetting, Release, Machine, LayerVersionDependency
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
                if reduce( lambda x, y: x or y, map(lambda x: len(request.POST.get(x, '')) == 0, mandatory_fields)):
                # set alert for missing fields
                    raise BadParameterException("Fields missing: " +
            ", ".join([x for x in mandatory_fields if len(request.POST.get(x, '')) == 0 ]))

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
                map(lambda x: context.__setitem__(x, request.POST.get(x, "-- missing")), mandatory_fields)
                if isinstance(e, IntegrityError) and "username" in str(e):
                    context['alert'] = "Your chosen username is already used"
                else:
                    context['alert'] = str(e)
                return render(request, template, context)

        raise Exception("Invalid HTTP method for this page")



    # Shows the edit project page
    @_template_renderer('project.html')
    def project(request, pid):
        prj = Project.objects.get(id = pid)

        try:
            puser = User.objects.get(id = prj.user_id)
        except User.DoesNotExist:
            puser = None

        # execute POST requests
        if request.method == "POST":
            # add layers
            if 'layerAdd' in request.POST and len(request.POST['layerAdd']) > 0:
                for lc in Layer_Version.objects.filter(pk__in=[i for i in request.POST['layerAdd'].split(",") if len(i) > 0]):
                    ProjectLayer.objects.get_or_create(project = prj, layercommit = lc)

            # remove layers
            if 'layerDel' in request.POST and len(request.POST['layerDel']) > 0:
                for t in request.POST['layerDel'].strip().split(" "):
                    pt = ProjectLayer.objects.filter(project = prj, layercommit_id = int(t)).delete()

            if 'projectName' in request.POST:
                prj.name = request.POST['projectName']
                prj.save();

            if 'projectVersion' in request.POST:
                # If the release is the current project then return now
                if prj.release.pk == int(request.POST.get('projectVersion',-1)):
                    return {}

                prj.release = Release.objects.get(pk = request.POST['projectVersion'])
                # we need to change the bitbake version
                prj.bitbake_version = prj.release.bitbake_version
                prj.save()
                # we need to change the layers
                for project in prj.projectlayer_set.all():
                    # find and add a similarly-named layer on the new branch
                    try:
                        layer_versions = prj.get_all_compatible_layer_versions()
                        layer_versions = layer_versions.filter(layer__name = project.layercommit.layer.name)
                        ProjectLayer.objects.get_or_create(project = prj, layercommit = layer_versions.first())
                    except IndexError:
                        pass
                    finally:
                        # get rid of the old entry
                        project.delete()

            if 'machineName' in request.POST:
                machinevar = prj.projectvariable_set.get(name="MACHINE")
                machinevar.value=request.POST['machineName']
                machinevar.save()


        # we use implicit knowledge of the current user's project to filter layer information, e.g.
        pid = prj.id

        from collections import Counter
        freqtargets = []
        try:
            freqtargets += map(lambda x: x.target, reduce(lambda x, y: x + y,   map(lambda x: list(x.target_set.all()), Build.objects.filter(project = prj, outcome__lt = Build.IN_PROGRESS))))
            freqtargets += map(lambda x: x.target, reduce(lambda x, y: x + y,   map(lambda x: list(x.brtarget_set.all()), BuildRequest.objects.filter(project = prj, state = BuildRequest.REQ_FAILED))))
        except TypeError:
            pass
        freqtargets = Counter(freqtargets)
        freqtargets = sorted(freqtargets, key = lambda x: freqtargets[x], reverse=True)

        context = {
            "project" : prj,
            "lvs_nos" : Layer_Version.objects.all().count(),
            "completedbuilds": Build.objects.exclude(outcome = Build.IN_PROGRESS).filter(project_id = pid),
            "prj" : {"name": prj.name, },
            "buildrequests" : prj.build_set.filter(outcome=Build.IN_PROGRESS),
            "builds" : Build.get_recent(prj),
            "layers" :  map(lambda x: {
                        "id": x.layercommit.pk,
                        "orderid": x.pk,
                        "name" : x.layercommit.layer.name,
                        "vcs_url": x.layercommit.layer.vcs_url,
                        "vcs_reference" : x.layercommit.get_vcs_reference(),
                        "url": x.layercommit.layer.layer_index_url,
                        "layerdetailurl": x.layercommit.get_detailspage_url(prj.pk),
                # This branch name is actually the release
                        "branch" : { "name" : x.layercommit.get_vcs_reference(), "layersource" : x.layercommit.up_branch.layer_source.name if x.layercommit.up_branch != None else None}},
                    prj.projectlayer_set.all().order_by("id")),
            "targets" : map(lambda x: {"target" : x.target, "task" : x.task, "pk": x.pk}, prj.projecttarget_set.all()),
            "variables": map(lambda x: (x.name, x.value), prj.projectvariable_set.all()),
            "freqtargets": freqtargets[:5],
            "releases": map(lambda x: {"id": x.pk, "name": x.name, "description":x.description}, Release.objects.all()),
            "project_html": 1,
            "recipesTypeAheadUrl": reverse('xhr_recipestypeahead', args=(prj.pk,)),
            "projectBuildsUrl": reverse('projectbuilds', args=(prj.pk,)),
        }

        if prj.release is not None:
            context['release'] = { "id": prj.release.pk, "name": prj.release.name, "description": prj.release.description}


        try:
            context["machine"] = {"name": prj.projectvariable_set.get(name="MACHINE").value}
        except ProjectVariable.DoesNotExist:
            context["machine"] = None
        try:
            context["distro"] = prj.projectvariable_set.get(name="DISTRO").value
        except ProjectVariable.DoesNotExist:
            context["distro"] = "-- not set yet"

        return context

    def xhr_response(fun):
        """
        Decorator for REST methods.
        calls jsonfilter on the returned dictionary and returns result
        as HttpResponse object of content_type application/json
        """
        @wraps(fun)
        def wrapper(*args, **kwds):
            return HttpResponse(jsonfilter(fun(*args, **kwds)),
                                content_type="application/json")
        return wrapper

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
                             "rows" : map( _lv_to_dict(prj),
                                          map(lambda x: x.layercommit, retval ))
                            })

        except Exception as e:
            return response({"error": str(e) })

    def xhr_configvaredit(request, pid):
        try:
            prj = Project.objects.get(id = pid)
            # add conf variables
            if 'configvarAdd' in request.POST:
                t=request.POST['configvarAdd'].strip()
                if ":" in t:
                    variable, value = t.split(":")
                else:
                    variable = t
                    value = ""

                pt, created = ProjectVariable.objects.get_or_create(project = prj, name = variable, value = value)
            # change conf variables
            if 'configvarChange' in request.POST:
                t=request.POST['configvarChange'].strip()
                if ":" in t:
                    variable, value = t.split(":")
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
                'configvars'   : map(lambda x: (x.name, x.value, x.pk), configvars_query),
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
        if (not request.POST.has_key('vcs_url') or
            not request.POST.has_key('name') or
            not request.POST.has_key('git_ref') or
            not request.POST.has_key('project_id')):
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
        # put into a new dict because POST one is immutable
        post_data = dict()
        for key,val in request.POST.iteritems():
          post_data[key] = val.strip()


        # We need to know what release the current project is so that we
        # can set the imported layer's up_branch_id
        prj_branch_name = Release.objects.get(pk=prj.release_id).branch_name
        up_branch, branch_created = Branch.objects.get_or_create(name=prj_branch_name, layer_source_id=LayerSource.TYPE_IMPORTED)

        layer_source = LayerSource.objects.get(sourcetype=LayerSource.TYPE_IMPORTED)
        try:
            layer, layer_created = Layer.objects.get_or_create(name=post_data['name'])
        except MultipleObjectsReturned:
            return HttpResponse(jsonfilter({"error": "hint-layer-exists"}), content_type = "application/json")

        if layer:
            if layer_created:
                layer.layer_source = layer_source
                layer.vcs_url = post_data['vcs_url']
                layer.up_date = timezone.now()
                layer.save()
            else:
                # We have an existing layer by this name, let's see if the git
                # url is the same, if it is then we can just create a new layer
                # version for this layer. Otherwise we need to bail out.
                if layer.vcs_url != post_data['vcs_url']:
                    return HttpResponse(jsonfilter({"error": "hint-layer-exists-with-different-url" , "current_url" : layer.vcs_url, "current_id": layer.id }), content_type = "application/json")


            layer_version, version_created = Layer_Version.objects.get_or_create(layer_source=layer_source, layer=layer, project=prj, up_branch_id=up_branch.id,branch=post_data['git_ref'],  commit=post_data['git_ref'], dirpath=post_data['dir_path'])

            if layer_version:
                if not version_created:
                    return HttpResponse(jsonfilter({"error": "hint-layer-version-exists", "existing_layer_version": layer_version.id }), content_type = "application/json")

                layer_version.up_date = timezone.now()
                layer_version.save()

                # Add the dependencies specified for this new layer
                if (post_data.has_key("layer_deps") and
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

    def xhr_updatelayer(request):

        def error_response(error):
            return HttpResponse(jsonfilter({"error": error}), content_type = "application/json")

        if not request.POST.has_key("layer_version_id"):
            return error_response("Please specify a layer version id")
        try:
            layer_version_id = request.POST["layer_version_id"]
            layer_version = Layer_Version.objects.get(id=layer_version_id)
        except Layer_Version.DoesNotExist:
            return error_response("Cannot find layer to update")


        if request.POST.has_key("vcs_url"):
            layer_version.layer.vcs_url = request.POST["vcs_url"]
        if request.POST.has_key("dirpath"):
            layer_version.dirpath = request.POST["dirpath"]
        if request.POST.has_key("commit"):
            layer_version.commit = request.POST["commit"]
        if request.POST.has_key("up_branch"):
            layer_version.up_branch_id = int(request.POST["up_branch"])

        if request.POST.has_key("add_dep"):
            lvd = LayerVersionDependency(layer_version=layer_version, depends_on_id=request.POST["add_dep"])
            lvd.save()

        if request.POST.has_key("rm_dep"):
            rm_dep = LayerVersionDependency.objects.get(layer_version=layer_version, depends_on_id=request.POST["rm_dep"])
            rm_dep.delete()

        if request.POST.has_key("summary"):
            layer_version.layer.summary = request.POST["summary"]
        if request.POST.has_key("description"):
            layer_version.layer.description = request.POST["description"]

        try:
            layer_version.layer.save()
            layer_version.save()
        except Exception as e:
            return error_response("Could not update layer version entry: %s" % e)

        return HttpResponse(jsonfilter({"error": "ok",}), content_type = "application/json")

    @xhr_response
    def xhr_customrecipe(request):
        """
        Custom image recipe REST API

        Entry point: /xhr_customrecipe/
        Method: POST

        Args:
            name: name of custom recipe to create
            project: target project id of orm.models.Project
            base: base recipe id of orm.models.Recipe

        Returns:
            {"error": "ok",
             "url": <url of the created recipe>}
            or
            {"error": <error message>}
        """
        # check if request has all required parameters
        for param in ('name', 'project', 'base'):
            if param not in request.POST:
                return {"error": "Missing parameter '%s'" % param}

        # get project and baserecipe objects
        params = {}
        for name, model in [("project", Project),
                            ("base", Recipe)]:
            value = request.POST[name]
            try:
                params[name] = model.objects.get(id=value)
            except model.DoesNotExist:
                return {"error": "Invalid %s id %s" % (name, value)}

        # create custom recipe
        try:

            # Only allowed chars in name are a-z, 0-9 and -
            if re.search(r'[^a-z|0-9|-]', request.POST["name"]):
                return {"error": "invalid-name"}

            custom_images = CustomImageRecipe.objects.all()

            # Are there any recipes with this name already in our project?
            existing_image_recipes_in_project = custom_images.filter(
                name=request.POST["name"], project=params["project"])

            if existing_image_recipes_in_project.count() > 0:
                return {"error": "image-already-exists"}

            # Are there any recipes with this name which aren't custom
            # image recipes?
            custom_image_ids = custom_images.values_list('id', flat=True)
            existing_non_image_recipes = Recipe.objects.filter(
                Q(name=request.POST["name"]) & ~Q(pk__in=custom_image_ids)
            )

            if existing_non_image_recipes.count() > 0:
                return {"error": "recipe-already-exists"}

            # create layer 'Custom layer' and verion if needed
            layer = Layer.objects.get_or_create(
                name=CustomImageRecipe.LAYER_NAME,
                summary="Layer for custom recipes",
                vcs_url="file:///toaster_created_layer")[0]

            # Check if we have a layer version already
            # We don't use get_or_create here because the dirpath will change
            # and is a required field
            lver = Layer_Version.objects.filter(Q(project=params['project']) &
                                                Q(layer=layer) &
                                                Q(build=None)).last()
            if lver == None:
                lver, created = Layer_Version.objects.get_or_create(
                    project=params['project'],
                    layer=layer,
                    dirpath="toaster_created_layer")

            # Add a dependency on our layer to the base recipe's layer
            LayerVersionDependency.objects.get_or_create(
                layer_version=lver,
                depends_on=params["base"].layer_version)

            # Add it to our current project if needed
            ProjectLayer.objects.get_or_create(project=params['project'],
                                               layercommit=lver,
                                               optional=False)

            # Create the actual recipe
            recipe, created = CustomImageRecipe.objects.get_or_create(
                name=request.POST["name"],
                base_recipe=params["base"],
                project=params["project"],
                layer_version=lver,
                is_image=True)

            # If we created the object then setup these fields. They may get
            # overwritten later on and cause the get_or_create to create a
            # duplicate if they've changed.
            if created:
                recipe.file_path = request.POST["name"]
                recipe.license = "MIT"
                recipe.version = "0.1"
                recipe.save()

        except Error as err:
            return {"error": "Can't create custom recipe: %s" % err}

        # Find the package list from the last build of this recipe/target
        target = Target.objects.filter(Q(build__outcome=Build.SUCCEEDED) &
                                       Q(build__project=params['project']) &
                                       (Q(target=params['base'].name) |
                                        Q(target=recipe.name))).last()
        if target:
            # Copy in every package
            # We don't want these packages to be linked to anything because
            # that underlying data may change e.g. delete a build
            for tpackage in target.target_installed_package_set.all():
                try:
                    built_package = tpackage.package
                    # The package had no recipe information so is a ghost
                    # package skip it
                    if built_package.recipe == None:
                        continue;

                    config_package = CustomImagePackage.objects.get(
                        name=built_package.name)

                    recipe.includes_set.add(config_package)
                except Exception as e:
                    logger.warning("Error adding package %s %s" %
                                   (tpackage.package.name, e))
                    pass

        return {"error": "ok",
                "packages" : recipe.get_all_packages().count(),
                "url": reverse('customrecipe', args=(params['project'].pk,
                                                     recipe.id))}

    @xhr_response
    def xhr_customrecipe_id(request, recipe_id):
        """
        Set of ReST API processors working with recipe id.

        Entry point: /xhr_customrecipe/<recipe_id>

        Methods:
            GET - Get details of custom image recipe
            DELETE - Delete custom image recipe

        Returns:
            GET:
            {"error": "ok",
             "info": dictionary of field name -> value pairs
                     of the CustomImageRecipe model}
            DELETE:
            {"error": "ok"}
            or
            {"error": <error message>}
        """
        try:
            custom_recipe = CustomImageRecipe.objects.get(id=recipe_id)
        except CustomImageRecipe.DoesNotExist:
            return {"error": "Custom recipe with id=%s "
                             "not found" % recipe_id}

        if request.method == 'GET':
            info = {"id" : custom_recipe.id,
                    "name" : custom_recipe.name,
                    "base_recipe_id": custom_recipe.base_recipe.id,
                    "project_id": custom_recipe.project.id,
                   }

            return {"error": "ok", "info": info}

        elif request.method == 'DELETE':
            custom_recipe.delete()
            return {"error": "ok"}
        else:
            return {"error": "Method %s is not supported" % request.method}

    def customrecipe_download(request, pid, recipe_id):
        recipe = get_object_or_404(CustomImageRecipe, pk=recipe_id)

        file_data = recipe.generate_recipe_file_contents()

        response = HttpResponse(file_data, content_type='text/plain')
        response['Content-Disposition'] = \
                'attachment; filename="%s_%s.bb"' % (recipe.name,
                                                     recipe.version)

        return response

    def _traverse_dependents(next_package_id, rev_deps, all_current_packages, tree_level=0):
        """
        Recurse through reverse dependency tree for next_package_id.
        Limit the reverse dependency search to packages not already scanned,
        that is, not already in rev_deps.
        Limit the scan to a depth (tree_level) not exceeding the count of
        all packages in the custom image, and if that depth is exceeded
        return False, pop out of the recursion, and write a warning
        to the log, but this is unlikely, suggesting a dependency loop
        not caught by bitbake.
        On return, the input/output arg rev_deps is appended with queryset
        dictionary elements, annotated for use in the customimage template.
        The list has unsorted, but unique elements.
        """
        max_dependency_tree_depth = all_current_packages.count()
        if tree_level >= max_dependency_tree_depth:
            logger.warning(
                "The number of reverse dependencies "
                "for this package exceeds " + max_dependency_tree_depth +
                " and the remaining reverse dependencies will not be removed")
            return True

        package = CustomImagePackage.objects.get(id=next_package_id)
        dependents = \
            package.package_dependencies_target.annotate(
                name=F('package__name'),
                pk=F('package__pk'),
                size=F('package__size'),
            ).values("name", "pk", "size").exclude(
                ~Q(pk__in=all_current_packages)
            )

        for pkg in dependents:
            if pkg in rev_deps:
                # already seen, skip dependent search
                continue

            rev_deps.append(pkg)
            if (_traverse_dependents(
                pkg["pk"], rev_deps, all_current_packages, tree_level+1)):
                return True

        return False

    def _get_all_dependents(package_id, all_current_packages):
        """
        Returns sorted list of recursive reverse dependencies for package_id,
        as a list of dictionary items, by recursing through dependency
        relationships.
        """
        rev_deps = []
        _traverse_dependents(package_id, rev_deps, all_current_packages)
        rev_deps = sorted(rev_deps, key=lambda x: x["name"])
        return rev_deps

    @xhr_response
    def xhr_customrecipe_packages(request, recipe_id, package_id):
        """
        ReST API to add/remove packages to/from custom recipe.

        Entry point: /xhr_customrecipe/<recipe_id>/packages/<package_id>

        Methods:
            PUT - Add package to the recipe
            DELETE - Delete package from the recipe
            GET - Get package information

        Returns:
            {"error": "ok"}
            or
            {"error": <error message>}
        """
        try:
            recipe = CustomImageRecipe.objects.get(id=recipe_id)
        except CustomImageRecipe.DoesNotExist:
            return {"error": "Custom recipe with id=%s "
                             "not found" % recipe_id}

        if package_id:
            try:
                package = CustomImagePackage.objects.get(id=package_id)
            except Package.DoesNotExist:
                return {"error": "Package with id=%s "
                        "not found" % package_id}

        if request.method == 'GET':
            # If no package_id then list the current packages
            if not package_id:
                total_size = 0
                packages = recipe.get_all_packages().values("id",
                                                            "name",
                                                            "version",
                                                            "size")
                for package in packages:
                    package['size_formatted'] = \
                            filtered_filesizeformat(package['size'])
                    total_size += package['size']

                return {"error": "ok",
                        "packages" : list(packages),
                        "total" : len(packages),
                        "total_size" : total_size,
                        "total_size_formatted" :
                        filtered_filesizeformat(total_size)
                       }
            else:
                all_current_packages = recipe.get_all_packages()

                # Dependencies for package which aren't satisfied by the
                # current packages in the custom image recipe
                deps = package.package_dependencies_source.annotate(
                    name=F('depends_on__name'),
                    pk=F('depends_on__pk'),
                    size=F('depends_on__size'),
                ).values("name", "pk", "size").filter(
                    # There are two depends types we don't know why
                    (Q(dep_type=Package_Dependency.TYPE_TRDEPENDS) |
                    Q(dep_type=Package_Dependency.TYPE_RDEPENDS)) &
                    ~Q(pk__in=all_current_packages)
                )

                # Reverse dependencies which are needed by packages that are
                # in the image. Recursive search providing all dependents,
                # not just immediate dependents.
                reverse_deps = _get_all_dependents(package_id, all_current_packages)
                total_size_deps = 0
                total_size_reverse_deps = 0

                for dep in deps:
                    dep['size_formatted'] = \
                            filtered_filesizeformat(dep['size'])
                    total_size_deps += dep['size']

                for dep in reverse_deps:
                    dep['size_formatted'] = \
                            filtered_filesizeformat(dep['size'])
                    total_size_reverse_deps += dep['size']


                return {"error": "ok",
                        "id": package.pk,
                        "name": package.name,
                        "version": package.version,
                        "unsatisfied_dependencies": list(deps),
                        "unsatisfied_dependencies_size": total_size_deps,
                        "unsatisfied_dependencies_size_formatted":
                        filtered_filesizeformat(total_size_deps),
                        "reverse_dependencies": list(reverse_deps),
                        "reverse_dependencies_size": total_size_reverse_deps,
                        "reverse_dependencies_size_formatted":
                        filtered_filesizeformat(total_size_reverse_deps)}

        included_packages = recipe.includes_set.values_list('pk', flat=True)

        if request.method == 'PUT':
            # If we're adding back a package which used to be included in this
            # image all we need to do is remove it from the excludes
            if package.pk in included_packages:
                try:
                   recipe.excludes_set.remove(package)
                   return {"error": "ok"}
                except Package.DoesNotExist:
                   return {"error":
                           "Package %s not found in excludes but was in "
                           "included list" % package.name}

            else:
                recipe.appends_set.add(package)
                # Make sure that package is not in the excludes set
                try:
                    recipe.excludes_set.remove(package)
                except:
                    pass
                # Add the dependencies we think will be added to the recipe
                # as a result of appending this package.
                # TODO this should recurse down the entire deps tree
                for dep in package.package_dependencies_source.all_depends():
                    try:
                        cust_package = CustomImagePackage.objects.get(
                                           name=dep.depends_on.name)

                        recipe.includes_set.add(cust_package)
                        try:
                            # When adding the pre-requisite package, make
                            # sure it's not in the excluded list from a
                            # prior removal.
                            recipe.excludes_set.remove(cust_package)
                        except Package.DoesNotExist:
                            # Don't care if the package had never been excluded
                            pass
                    except:
                        logger.warning("Could not add package's suggested"
                                       "dependencies to the list")

            return {"error": "ok"}

        elif request.method == 'DELETE':
            try:
                # If we're deleting a package which is included we need to
                # Add it to the excludes list.
                if package.pk in included_packages:
                    recipe.excludes_set.add(package)
                else:
                    recipe.appends_set.remove(package)
                all_current_packages = recipe.get_all_packages()
                reverse_deps_dictlist = _get_all_dependents(package.pk, all_current_packages)
                ids = [entry['pk'] for entry in reverse_deps_dictlist]
                reverse_deps = CustomImagePackage.objects.filter(id__in=ids)
                for r in reverse_deps:
                    try:
                        if r.id in included_packages:
                            recipe.excludes_set.add(r)
                        else:
                            recipe.appends_set.remove(r)
                    except:
                        pass

                return {"error": "ok"}
            except CustomImageRecipe.DoesNotExist:
                return {"error": "Tried to remove package that wasn't present"}

        else:
            return {"error": "Method %s is not supported" % request.method}

    def importlayer(request, pid):
        template = "importlayer.html"
        context = {
            'project': Project.objects.get(id=pid),
        }
        return render(request, template, context)

    @_template_renderer('layerdetails.html')
    def layerdetails(request, pid, layerid):
        project = Project.objects.get(pk=pid)
        layer_version = Layer_Version.objects.get(pk=layerid)

        context = {'project' : project,
            'layerversion' : layer_version,
            'layerdeps' : {"list": [{"id": dep.id,
                "name": dep.layer.name,
                "layerdetailurl": reverse('layerdetails', args=(pid, dep.pk)),
                "vcs_url": dep.layer.vcs_url,
                "vcs_reference": dep.get_vcs_reference()} \
                for dep in layer_version.get_alldeps(project.id)]},
            'projectlayers': map(lambda prjlayer: prjlayer.layercommit.id, ProjectLayer.objects.filter(project=project))
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
            'PARALLEL_MAKE','SSTATE_MIRRORS','TMPDIR',
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
        except ProjectVariable.DoesNotExist,BuildEnvironment.DoesNotExist:
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
        except ProjectVariable.DoesNotExist, BuildEnvironment.DoesNotExist:
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

        elif artifact_type == "buildartifact":
            file_name = BuildArtifact.objects.get(build = build, pk = artifact_id).file_name

        elif artifact_type == "licensemanifest":
            file_name = Target.objects.get(build = build, pk = artifact_id).license_manifest_path

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
                fsock = open(file_name, "r")
                content_type = MimeTypeFinder.get_mimetype(file_name)

                response = HttpResponse(fsock, content_type = content_type)

                disposition = "attachment; filename=" + response_file_name
                response["Content-Disposition"] = disposition

                return response
            else:
                return render(request, "unavailable_artifact.html")
        except ObjectDoesNotExist, IOError:
            return render(request, "unavailable_artifact.html")
