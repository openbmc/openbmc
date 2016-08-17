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
from toastergui import api

urlpatterns = patterns('toastergui.views',
        # landing page
        url(r'^landing/$', 'landing', name='landing'),

        url(r'^builds/$',
            tables.AllBuildsTable.as_view(template_name="builds-toastertable.html"),
            name='all-builds'),

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
        url(r'^build/(?P<build_id>\d+)/cputime$', 'cputime', name='cputime'),
        url(r'^build/(?P<build_id>\d+)/diskio$', 'diskio', name='diskio'),

        # image information dir
        url(r'^build/(?P<build_id>\d+)/target/(?P<target_id>\d+)/packagefile/(?P<packagefile_id>\d+)$',
             'image_information_dir', name='image_information_dir'),

        # build download artifact
        url(r'^build/(?P<build_id>\d+)/artifact/(?P<artifact_type>\w+)/id/(?P<artifact_id>\w+)', 'build_artifact', name="build_artifact"),

        # project URLs
        url(r'^newproject/$', 'newproject', name='newproject'),

        url(r'^projects/$',
            tables.ProjectsTable.as_view(template_name="projects-toastertable.html"),
            name='all-projects'),

        url(r'^project/(?P<pid>\d+)/$', 'project', name='project'),
        url(r'^project/(?P<pid>\d+)/configuration$', 'projectconf', name='projectconf'),
        url(r'^project/(?P<pid>\d+)/builds/$',
            tables.ProjectBuildsTable.as_view(template_name="projectbuilds-toastertable.html"),
            name='projectbuilds'),

        # the import layer is a project-specific functionality;
        url(r'^project/(?P<pid>\d+)/importlayer$', 'importlayer', name='importlayer'),

        # the table pages that have been converted to ToasterTable widget
        url(r'^project/(?P<pid>\d+)/machines/$',
            tables.MachinesTable.as_view(template_name="generic-toastertable-page.html"),
            name="projectmachines"),

        url(r'^project/(?P<pid>\d+)/softwarerecipes/$',
            tables.SoftwareRecipesTable.as_view(template_name="generic-toastertable-page.html"),
            name="projectsoftwarerecipes"),

        url(r'^project/(?P<pid>\d+)/images/$',
            tables.ImageRecipesTable.as_view(template_name="generic-toastertable-page.html"), name="projectimagerecipes"),

        url(r'^project/(?P<pid>\d+)/customimages/$',
            tables.CustomImagesTable.as_view(template_name="generic-toastertable-page.html"), name="projectcustomimages"),

        url(r'^project/(?P<pid>\d+)/newcustomimage/$',
            tables.NewCustomImagesTable.as_view(template_name="newcustomimage.html"),
            name="newcustomimage"),

        url(r'^project/(?P<pid>\d+)/layers/$',
            tables.LayersTable.as_view(template_name="generic-toastertable-page.html"),
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


        url(r'^project/(?P<pid>\d+)/customrecipe/(?P<custrecipeid>\d+)/selectpackages/$',
            tables.SelectPackagesTable.as_view(), name="recipeselectpackages"),


        url(r'^project/(?P<pid>\d+)/customrecipe/(?P<custrecipeid>\d+)$',
            tables.SelectPackagesTable.as_view(template_name="customrecipe.html"),
            name="customrecipe"),

        url(r'^project/(?P<pid>\d+)/customrecipe/(?P<recipe_id>\d+)/download$',
            'customrecipe_download',
            name="customrecipedownload"),

        url(r'^project/(?P<pid>\d+)/recipe/(?P<recipe_id>\d+)$',
            tables.PackagesTable.as_view(template_name="recipedetails.html"),
            name="recipedetails"),

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

        # image customisation functionality
        url(r'^xhr_customrecipe/(?P<recipe_id>\d+)/packages/(?P<package_id>\d+|)$',
            'xhr_customrecipe_packages', name='xhr_customrecipe_packages'),

        url(r'^xhr_customrecipe/(?P<recipe_id>\d+)/packages/$',
            'xhr_customrecipe_packages', name='xhr_customrecipe_packages'),

        url(r'^xhr_customrecipe/(?P<recipe_id>\d+)$', 'xhr_customrecipe_id',
            name='xhr_customrecipe_id'),
        url(r'^xhr_customrecipe/', 'xhr_customrecipe',
            name='xhr_customrecipe'),

        url(r'^xhr_buildrequest/project/(?P<pid>\d+)$',
           api.XhrBuildRequest.as_view(),
            name='xhr_buildrequest'),

          # default redirection
        url(r'^$', RedirectView.as_view(url='landing', permanent=True)),
)
