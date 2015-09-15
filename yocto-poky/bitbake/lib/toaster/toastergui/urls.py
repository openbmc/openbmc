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

from django.conf.urls import patterns, include, url
from django.views.generic import RedirectView, TemplateView

from django.http import HttpResponseBadRequest
from toastergui import tables
from toastergui import typeaheads

urlpatterns = patterns('toastergui.views',
        # landing page
        url(r'^landing/$', 'landing', name='landing'),

        url(r'^builds/$', 'builds', name='all-builds'),
        # build info navigation
        url(r'^build/(?P<build_id>\d+)$', 'builddashboard', name="builddashboard"),

        url(r'^build/(?P<build_id>\d+)/tasks/$', 'tasks', name='tasks'),
        url(r'^build/(?P<build_id>\d+)/tasks/(?P<task_id>\d+)/$', 'tasks_task', name='tasks_task'),
        url(r'^build/(?P<build_id>\d+)/task/(?P<task_id>\d+)$', 'task', name='task'),

        url(r'^build/(?P<build_id>\d+)/recipes/$', 'recipes', name='recipes'),
        url(r'^build/(?P<build_id>\d+)/recipe/(?P<recipe_id>\d+)/active_tab/(?P<active_tab>\d{1})$', 'recipe', name='recipe'),
        url(r'^build/(?P<build_id>\d+)/recipe/(?P<recipe_id>\d+)$', 'recipe', name='recipe'),
        url(r'^build/(?P<build_id>\d+)/recipe_packages/(?P<recipe_id>\d+)$', 'recipe_packages', name='recipe_packages'),

        url(r'^build/(?P<build_id>\d+)/packages/$', 'bpackage', name='packages'),
        url(r'^build/(?P<build_id>\d+)/package/(?P<package_id>\d+)$', 'package_built_detail',
                name='package_built_detail'),
        url(r'^build/(?P<build_id>\d+)/package_built_dependencies/(?P<package_id>\d+)$',
            'package_built_dependencies', name='package_built_dependencies'),
        url(r'^build/(?P<build_id>\d+)/package_included_detail/(?P<target_id>\d+)/(?P<package_id>\d+)$',
            'package_included_detail', name='package_included_detail'),
        url(r'^build/(?P<build_id>\d+)/package_included_dependencies/(?P<target_id>\d+)/(?P<package_id>\d+)$',
            'package_included_dependencies', name='package_included_dependencies'),
        url(r'^build/(?P<build_id>\d+)/package_included_reverse_dependencies/(?P<target_id>\d+)/(?P<package_id>\d+)$',
            'package_included_reverse_dependencies', name='package_included_reverse_dependencies'),

        # images are known as targets in the internal model
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)$', 'target', name='target'),
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)/targetpkg$', 'targetpkg', name='targetpkg'),
        url(r'^dentries/build/(?P<build_id>\d+)/target/(?P<target_id>\d+)$', 'xhr_dirinfo', name='dirinfo_ajax'),
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)/dirinfo$', 'dirinfo', name='dirinfo'),
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)/dirinfo_filepath/_(?P<file_path>(?:/[^/\n]+)*)$', 'dirinfo', name='dirinfo_filepath'),
        url(r'^build/(?P<build_id>\d+)/configuration$', 'configuration', name='configuration'),
        url(r'^build/(?P<build_id>\d+)/configvars$', 'configvars', name='configvars'),
        url(r'^build/(?P<build_id>\d+)/buildtime$', 'buildtime', name='buildtime'),
        url(r'^build/(?P<build_id>\d+)/cpuusage$', 'cpuusage', name='cpuusage'),
        url(r'^build/(?P<build_id>\d+)/diskio$', 'diskio', name='diskio'),

        # image information dir
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)/packagefile/(?P<packagefile_id>\d+)$',
             'image_information_dir', name='image_information_dir'),

        # build download artifact
        url(r'^build/(?P<build_id>\d+)/artifact/(?P<artifact_type>\w+)/id/(?P<artifact_id>\w+)', 'build_artifact', name="build_artifact"),

        # project URLs
        url(r'^newproject/$', 'newproject', name='newproject'),


        url(r'^projects/$', 'projects', name='all-projects'),

        url(r'^project/(?P<pid>\d+)/$', 'project', name='project'),
        url(r'^project/(?P<pid>\d+)/configuration$', 'projectconf', name='projectconf'),
        url(r'^project/(?P<pid>\d+)/builds/$', 'projectbuilds', name='projectbuilds'),

        # the import layer is a project-specific functionality;
        url(r'^project/(?P<pid>\d+)/importlayer$', 'importlayer', name='importlayer'),

        # the table pages that have been converted to ToasterTable widget
        url(r'^project/(?P<pid>\d+)/machines/$',
            tables.MachinesTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.MachinesTable.__name__.lower(),
              'title' : 'Compatible machines' },
            name="projectmachines"),

        url(r'^project/(?P<pid>\d+)/recipes/$',
            tables.RecipesTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.RecipesTable.__name__.lower(),
              'title' : 'Compatible recipes' },
            name="projecttargets"),

        url(r'^project/(?P<pid>\d+)/availablerecipes/$',
            tables.ProjectLayersRecipesTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.ProjectLayersRecipesTable.__name__.lower(),
              'title' : 'Recipes available for layers in the current project' },
            name="projectavailabletargets"),

        url(r'^project/(?P<pid>\d+)/layers/$',
            tables.LayersTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.LayersTable.__name__.lower(),
              'title' : 'Compatible layers' },
            name="projectlayers"),

        url(r'^project/(?P<pid>\d+)/layer/(?P<layerid>\d+)$',
            'layerdetails', name='layerdetails'),

        url(r'^project/(?P<pid>\d+)/layer/(?P<layerid>\d+)/recipes/$',
            tables.LayerRecipesTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.LayerRecipesTable.__name__.lower(),
              'title' : 'All recipes in layer' },
             name=tables.LayerRecipesTable.__name__.lower()),

        url(r'^project/(?P<pid>\d+)/layer/(?P<layerid>\d+)/machines/$',
            tables.LayerMachinesTable.as_view(template_name="generic-toastertable-page.html"),
            { 'table_name': tables.LayerMachinesTable.__name__.lower(),
              'title' : 'All machines in layer' },
            name=tables.LayerMachinesTable.__name__.lower()),


        # typeahead api end points
        url(r'^xhr_typeahead/(?P<pid>\d+)/layers$',
            typeaheads.LayersTypeAhead.as_view(), name='xhr_layerstypeahead'),
        url(r'^xhr_typeahead/(?P<pid>\d+)/machines$',
            typeaheads.MachinesTypeAhead.as_view(), name='xhr_machinestypeahead'),
        url(r'^xhr_typeahead/(?P<pid>\d+)/recipes$',
            typeaheads.RecipesTypeAhead.as_view(), name='xhr_recipestypeahead'),
        url(r'^xhr_typeahead/projects$',
            typeaheads.ProjectsTypeAhead.as_view(), name='xhr_projectstypeahead'),



        url(r'^xhr_testreleasechange/(?P<pid>\d+)$', 'xhr_testreleasechange',
            name='xhr_testreleasechange'),
        url(r'^xhr_configvaredit/(?P<pid>\d+)$', 'xhr_configvaredit',
            name='xhr_configvaredit'),

        url(r'^xhr_importlayer/$', 'xhr_importlayer', name='xhr_importlayer'),
        url(r'^xhr_updatelayer/$', 'xhr_updatelayer', name='xhr_updatelayer'),

        # JS Unit tests
        url(r'^js-unit-tests/$', 'jsunittests', name='js-unit-tests'),

        # default redirection
        url(r'^$', RedirectView.as_view( url= 'landing')),
)
