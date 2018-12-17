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

from toastergui.widgets import ToasterTable
from orm.models import Recipe, ProjectLayer, Layer_Version, Machine, Project
from orm.models import CustomImageRecipe, Package, Target, Build, LogMessage, Task
from orm.models import CustomImagePackage, Package_DependencyManager
from orm.models import Distro
from django.db.models import Q, Max, Sum, Count, When, Case, Value, IntegerField
from django.conf.urls import url
from django.core.urlresolvers import reverse, resolve
from django.http import HttpResponse
from django.views.generic import TemplateView

from toastergui.tablefilter import TableFilter
from toastergui.tablefilter import TableFilterActionToggle
from toastergui.tablefilter import TableFilterActionDateRange
from toastergui.tablefilter import TableFilterActionDay

import os

class ProjectFilters(object):
    @staticmethod
    def in_project(project_layers):
        return Q(layer_version__in=project_layers)

    @staticmethod
    def not_in_project(project_layers):
        return ~(ProjectFilters.in_project(project_layers))

class LayersTable(ToasterTable):
    """Table of layers in Toaster"""

    def __init__(self, *args, **kwargs):
        super(LayersTable, self).__init__(*args, **kwargs)
        self.default_orderby = "layer__name"
        self.title = "Compatible layers"

    def get_context_data(self, **kwargs):
        context = super(LayersTable, self).get_context_data(**kwargs)

        project = Project.objects.get(pk=kwargs['pid'])
        context['project'] = project

        return context

    def setup_filters(self, *args, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])
        self.project_layers = ProjectLayer.objects.filter(project=project)

        in_current_project_filter = TableFilter(
            "in_current_project",
            "Filter by project layers"
        )

        criteria = Q(projectlayer__in=self.project_layers)

        in_project_action = TableFilterActionToggle(
            "in_project",
            "Layers added to this project",
            criteria
        )

        not_in_project_action = TableFilterActionToggle(
            "not_in_project",
            "Layers not added to this project",
            ~criteria
        )

        in_current_project_filter.add_action(in_project_action)
        in_current_project_filter.add_action(not_in_project_action)
        self.add_filter(in_current_project_filter)

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        compatible_layers = prj.get_all_compatible_layer_versions()

        self.static_context_extra['current_layers'] = \
                prj.get_project_layer_versions(pk=True)

        self.queryset = compatible_layers.order_by(self.default_orderby)

    def setup_columns(self, *args, **kwargs):

        layer_link_template = '''
        <a href="{% url 'layerdetails' extra.pid data.id %}">
          {{data.layer.name}}
        </a>
        '''

        self.add_column(title="Layer",
                        hideable=False,
                        orderable=True,
                        static_data_name="layer__name",
                        static_data_template=layer_link_template)

        self.add_column(title="Summary",
                        field_name="layer__summary")

        git_url_template = '''
        <a href="{% url 'layerdetails' extra.pid data.id %}">
        {% if data.layer.local_source_dir %}
          <code>{{data.layer.local_source_dir}}</code>
        {% else %}
          <code>{{data.layer.vcs_url}}</code>
        </a>
        {% endif %}
        {% if data.get_vcs_link_url %}
        <a target="_blank" href="{{ data.get_vcs_link_url }}">
           <span class="glyphicon glyphicon-new-window"></span>
        </a>
        {% endif %}
        '''

        self.add_column(title="Layer source code location",
                        help_text="A Git repository or an absolute path to a directory",
                        hidden=True,
                        static_data_name="layer__vcs_url",
                        static_data_template=git_url_template)

        git_dir_template = '''
        {% if data.layer.local_source_dir %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.layer.name}} is not in a Git repository, so there is no subdirectory associated with it"> </span>
        {% else %}
        <a href="{% url 'layerdetails' extra.pid data.id %}">
         <code>{{data.dirpath}}</code>
        </a>
        {% endif %}
        {% if data.dirpath and data.get_vcs_dirpath_link_url %}
        <a target="_blank" href="{{ data.get_vcs_dirpath_link_url }}">
          <span class="glyphicon glyphicon-new-window"></span>
        </a>
        {% endif %}'''

        self.add_column(title="Subdirectory",
                        help_text="The layer directory within the Git repository",
                        hidden=True,
                        static_data_name="git_subdir",
                        static_data_template=git_dir_template)

        revision_template =  '''
        {% if data.layer.local_source_dir %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.layer.name}} is not in a Git repository, so there is no revision associated with it"> </span>
        {% else %}
        {% with vcs_ref=data.get_vcs_reference %}
        {% include 'snippets/gitrev_popover.html' %}
        {% endwith %}
        {% endif %}
        '''

        self.add_column(title="Git revision",
                        help_text="The Git branch, tag or commit. For the layers from the OpenEmbedded layer source, the revision is always the branch compatible with the Yocto Project version you selected for this project",
                        static_data_name="revision",
                        static_data_template=revision_template)

        deps_template = '''
        {% with ods=data.dependencies.all%}
        {% if ods.count %}
            <a class="btn btn-default" title="<a href='{% url "layerdetails" extra.pid data.id %}'>{{data.layer.name}}</a> dependencies"
        data-content="<ul class='list-unstyled'>
        {% for i in ods%}
        <li><a href='{% url "layerdetails" extra.pid i.depends_on.pk %}'>{{i.depends_on.layer.name}}</a></li>
        {% endfor %}
        </ul>">
        {{ods.count}}
        </a>
        {% endif %}
        {% endwith %}
        '''

        self.add_column(title="Dependencies",
                        help_text="Other layers a layer depends upon",
                        static_data_name="dependencies",
                        static_data_template=deps_template)

        self.add_column(title="Add | Remove",
                        help_text="Add or remove layers to / from your project",
                        hideable=False,
                        filter_name="in_current_project",
                        static_data_name="add-del-layers",
                        static_data_template='{% include "layer_btn.html" %}')


class MachinesTable(ToasterTable):
    """Table of Machines in Toaster"""

    def __init__(self, *args, **kwargs):
        super(MachinesTable, self).__init__(*args, **kwargs)
        self.empty_state = "Toaster has no machine information for this project. Sadly, 			   machine information cannot be obtained from builds, so this 				  page will remain empty."
        self.title = "Compatible machines"
        self.default_orderby = "name"

    def get_context_data(self, **kwargs):
        context = super(MachinesTable, self).get_context_data(**kwargs)
        context['project'] = Project.objects.get(pk=kwargs['pid'])
        return context

    def setup_filters(self, *args, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])

        in_current_project_filter = TableFilter(
            "in_current_project",
            "Filter by project machines"
        )

        in_project_action = TableFilterActionToggle(
            "in_project",
            "Machines provided by layers added to this project",
            ProjectFilters.in_project(self.project_layers)
        )

        not_in_project_action = TableFilterActionToggle(
            "not_in_project",
            "Machines provided by layers not added to this project",
            ProjectFilters.not_in_project(self.project_layers)
        )

        in_current_project_filter.add_action(in_project_action)
        in_current_project_filter.add_action(not_in_project_action)
        self.add_filter(in_current_project_filter)

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        self.queryset = prj.get_all_compatible_machines()
        self.queryset = self.queryset.order_by(self.default_orderby)

        self.static_context_extra['current_layers'] = \
                self.project_layers = \
                prj.get_project_layer_versions(pk=True)

    def setup_columns(self, *args, **kwargs):

        self.add_column(title="Machine",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Description",
                        field_name="description")

        layer_link_template = '''
        <a href="{% url 'layerdetails' extra.pid data.layer_version.id %}">
        {{data.layer_version.layer.name}}</a>
        '''

        self.add_column(title="Layer",
                        static_data_name="layer_version__layer__name",
                        static_data_template=layer_link_template,
                        orderable=True)

        self.add_column(title="Git revision",
                        help_text="The Git branch, tag or commit. For the layers from the OpenEmbedded layer source, the revision is always the branch compatible with the Yocto Project version you selected for this project",
                        hidden=True,
                        field_name="layer_version__get_vcs_reference")

        machine_file_template = '''<code>conf/machine/{{data.name}}.conf</code>
        <a href="{{data.get_vcs_machine_file_link_url}}" target="_blank"><span class="glyphicon glyphicon-new-window"></i></a>'''

        self.add_column(title="Machine file",
                        hidden=True,
                        static_data_name="machinefile",
                        static_data_template=machine_file_template)

        self.add_column(title="Select",
                        help_text="Sets the selected machine as the project machine. You can only have one machine per project",
                        hideable=False,
                        filter_name="in_current_project",
                        static_data_name="add-del-layers",
                        static_data_template='{% include "machine_btn.html" %}')


class LayerMachinesTable(MachinesTable):
    """ Smaller version of the Machines table for use in layer details """

    def __init__(self, *args, **kwargs):
        super(LayerMachinesTable, self).__init__(*args, **kwargs)

    def get_context_data(self, **kwargs):
        context = super(LayerMachinesTable, self).get_context_data(**kwargs)
        context['layerversion'] = Layer_Version.objects.get(pk=kwargs['layerid'])
        return context


    def setup_queryset(self, *args, **kwargs):
        MachinesTable.setup_queryset(self, *args, **kwargs)

        self.queryset = self.queryset.filter(layer_version__pk=int(kwargs['layerid']))
        self.queryset = self.queryset.order_by(self.default_orderby)
        self.static_context_extra['in_prj'] = ProjectLayer.objects.filter(Q(project=kwargs['pid']) & Q(layercommit=kwargs['layerid'])).count()

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Machine",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Description",
                        field_name="description")

        select_btn_template = '''
        <a href="{% url "project" extra.pid %}?setMachine={{data.name}}"
        class="btn btn-default btn-block select-machine-btn
        {% if extra.in_prj == 0%}disabled{%endif%}">Select machine</a>
        '''

        self.add_column(title="Select machine",
                        static_data_name="add-del-layers",
                        static_data_template=select_btn_template)


class RecipesTable(ToasterTable):
    """Table of All Recipes in Toaster"""

    def __init__(self, *args, **kwargs):
        super(RecipesTable, self).__init__(*args, **kwargs)
        self.empty_state = "Toaster has no recipe information. To generate recipe information you need to run a build."

    build_col = { 'title' : "Build",
            'help_text' : "Before building a recipe, you might need to add the corresponding layer to your project",
            'hideable' : False,
            'filter_name' : "in_current_project",
            'static_data_name' : "add-del-layers",
            'static_data_template' : '{% include "recipe_btn.html" %}'}
    if '1' == os.environ.get('TOASTER_PROJECTSPECIFIC'):
            build_col['static_data_template'] = '{% include "recipe_add_btn.html" %}'

    def get_context_data(self, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])
        context = super(RecipesTable, self).get_context_data(**kwargs)

        context['project'] = project
        context['projectlayers'] = [player.layercommit.id for player in ProjectLayer.objects.filter(project=context['project'])]

        return context

    def setup_filters(self, *args, **kwargs):
        table_filter = TableFilter(
            'in_current_project',
            'Filter by project recipes'
        )

        in_project_action = TableFilterActionToggle(
            'in_project',
            'Recipes provided by layers added to this project',
            ProjectFilters.in_project(self.project_layers)
        )

        not_in_project_action = TableFilterActionToggle(
            'not_in_project',
            'Recipes provided by layers not added to this project',
            ProjectFilters.not_in_project(self.project_layers)
        )

        table_filter.add_action(in_project_action)
        table_filter.add_action(not_in_project_action)
        self.add_filter(table_filter)

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])

        # Project layers used by the filters
        self.project_layers = prj.get_project_layer_versions(pk=True)

        # Project layers used to switch the button states
        self.static_context_extra['current_layers'] = self.project_layers

        self.queryset = prj.get_all_compatible_recipes()


    def setup_columns(self, *args, **kwargs):

        self.add_column(title="Version",
                        hidden=False,
                        field_name="version")

        self.add_column(title="Description",
                        field_name="get_description_or_summary")

        recipe_file_template = '''
        <code>{{data.file_path}}</code>
        <a href="{{data.get_vcs_recipe_file_link_url}}" target="_blank">
          <span class="glyphicon glyphicon-new-window"></i>
        </a>
         '''

        self.add_column(title="Recipe file",
                        help_text="Path to the recipe .bb file",
                        hidden=True,
                        static_data_name="recipe-file",
                        static_data_template=recipe_file_template)

        self.add_column(title="Section",
                        help_text="The section in which recipes should be categorized",
                        hidden=True,
                        orderable=True,
                        field_name="section")

        layer_link_template = '''
        <a href="{% url 'layerdetails' extra.pid data.layer_version.id %}">
        {{data.layer_version.layer.name}}</a>
        '''

        self.add_column(title="Layer",
                        help_text="The name of the layer providing the recipe",
                        orderable=True,
                        static_data_name="layer_version__layer__name",
                        static_data_template=layer_link_template)

        self.add_column(title="License",
                        help_text="The list of source licenses for the recipe. Multiple license names separated by the pipe character indicates a choice between licenses. Multiple license names separated by the ampersand character indicates multiple licenses exist that cover different parts of the source",
                        hidden=True,
                        orderable=True,
                        field_name="license")

        revision_link_template = '''
        {% if data.layer_version.layer.local_source_dir %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.layer_version.layer.name}} is not in a Git repository, so there is no revision associated with it"> </span>
        {% else %}
        {{data.layer_version.get_vcs_reference}}
        {% endif %}
        '''

        self.add_column(title="Git revision",
                        hidden=True,
                        static_data_name="layer_version__get_vcs_reference",
                        static_data_template=revision_link_template)


class LayerRecipesTable(RecipesTable):
    """ Smaller version of the Recipes table for use in layer details """

    def __init__(self, *args, **kwargs):
        super(LayerRecipesTable, self).__init__(*args, **kwargs)
        self.default_orderby = "name"

    def get_context_data(self, **kwargs):
        context = super(LayerRecipesTable, self).get_context_data(**kwargs)
        context['layerversion'] = Layer_Version.objects.get(pk=kwargs['layerid'])
        return context


    def setup_queryset(self, *args, **kwargs):
        self.queryset = \
                Recipe.objects.filter(layer_version__pk=int(kwargs['layerid']))

        self.queryset = self.queryset.order_by(self.default_orderby)
        self.static_context_extra['in_prj'] = ProjectLayer.objects.filter(Q(project=kwargs['pid']) & Q(layercommit=kwargs['layerid'])).count()

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Recipe",
                        help_text="Information about a single piece of software, including where to download the source, configuration options, how to compile the source files and how to package the compiled output",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Version",
                        field_name="version")

        self.add_column(title="Description",
                        field_name="get_description_or_summary")

        build_recipe_template = '''
        <a class="btn btn-default btn-block build-recipe-btn
        {% if extra.in_prj == 0 %}disabled{% endif %}"
        data-recipe-name="{{data.name}}">Build recipe</a>
        '''

        self.add_column(title="Build recipe",
                        static_data_name="add-del-layers",
                        static_data_template=build_recipe_template)

class CustomImagesTable(ToasterTable):
    """ Table to display your custom images """
    def __init__(self, *args, **kwargs):
        super(CustomImagesTable, self).__init__(*args, **kwargs)
        self.title = "Custom images"
        self.default_orderby = "name"

    def get_context_data(self, **kwargs):
        context = super(CustomImagesTable, self).get_context_data(**kwargs)

        empty_state_template = '''
        You have not created any custom images yet.
        <a href="{% url 'newcustomimage' data.pid %}">
        Create your first custom image</a>
        '''
        context['empty_state'] = self.render_static_data(empty_state_template,
                                                         kwargs)
        project = Project.objects.get(pk=kwargs['pid'])

        # TODO put project into the ToasterTable base class
        context['project'] = project
        return context

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        self.queryset = CustomImageRecipe.objects.filter(project=prj)
        self.queryset = self.queryset.order_by(self.default_orderby)

    def setup_columns(self, *args, **kwargs):

        name_link_template = '''
        <a href="{% url 'customrecipe' extra.pid data.id %}">
          {{data.name}}
        </a>
        '''

        self.add_column(title="Custom image",
                        hideable=False,
                        orderable=True,
                        field_name="name",
                        static_data_name="name",
                        static_data_template=name_link_template)

        recipe_file_template = '''
        {% if data.get_base_recipe_file %}
        <code>{{data.name}}_{{data.version}}.bb</code>
        <a href="{% url 'customrecipedownload' extra.pid data.pk %}"
        class="glyphicon glyphicon-download-alt get-help" title="Download recipe file"></a>
        {% endif %}'''

        self.add_column(title="Recipe file",
                        static_data_name='recipe_file_download',
                        static_data_template=recipe_file_template)

        approx_packages_template = '''
        {% if data.get_all_packages.count > 0 %}
        <a href="{% url 'customrecipe' extra.pid data.id %}">
          {{data.get_all_packages.count}}
        </a>
        {% endif %}'''

        self.add_column(title="Packages",
                        static_data_name='approx_packages',
                        static_data_template=approx_packages_template)


        build_btn_template = '''
        <button data-recipe-name="{{data.name}}"
        class="btn btn-default btn-block build-recipe-btn">
        Build
        </button>'''

        self.add_column(title="Build",
                        hideable=False,
                        static_data_name='build_custom_img',
                        static_data_template=build_btn_template)

class ImageRecipesTable(RecipesTable):
    """ A subset of the recipes table which displayed just image recipes """

    def __init__(self, *args, **kwargs):
        super(ImageRecipesTable, self).__init__(*args, **kwargs)
        self.title = "Compatible image recipes"
        self.default_orderby = "name"

    def setup_queryset(self, *args, **kwargs):
        super(ImageRecipesTable, self).setup_queryset(*args, **kwargs)

        custom_image_recipes = CustomImageRecipe.objects.filter(
                project=kwargs['pid'])
        self.queryset = self.queryset.filter(
                Q(is_image=True) & ~Q(pk__in=custom_image_recipes))
        self.queryset = self.queryset.order_by(self.default_orderby)


    def setup_columns(self, *args, **kwargs):

        name_link_template = '''
        <a href="{% url 'recipedetails' extra.pid data.pk %}">{{data.name}}</a>
        '''

        self.add_column(title="Image recipe",
                        help_text="When you build an image recipe, you get an "
                                  "image: a root file system you can"
                                  "deploy to a machine",
                        hideable=False,
                        orderable=True,
                        static_data_name="name",
                        static_data_template=name_link_template,
                        field_name="name")

        super(ImageRecipesTable, self).setup_columns(*args, **kwargs)

        self.add_column(**RecipesTable.build_col)


class NewCustomImagesTable(ImageRecipesTable):
    """ Table which displays Images recipes which can be customised """
    def __init__(self, *args, **kwargs):
        super(NewCustomImagesTable, self).__init__(*args, **kwargs)
        self.title = "Select the image recipe you want to customise"

    def setup_queryset(self, *args, **kwargs):
        super(ImageRecipesTable, self).setup_queryset(*args, **kwargs)
        prj = Project.objects.get(pk = kwargs['pid'])
        self.static_context_extra['current_layers'] = \
                prj.get_project_layer_versions(pk=True)

        self.queryset = self.queryset.filter(is_image=True)

    def setup_columns(self, *args, **kwargs):

        name_link_template = '''
        <a href="{% url 'recipedetails' extra.pid data.pk %}">{{data.name}}</a>
        '''

        self.add_column(title="Image recipe",
                        help_text="When you build an image recipe, you get an "
                                  "image: a root file system you can"
                                  "deploy to a machine",
                        hideable=False,
                        orderable=True,
                        static_data_name="name",
                        static_data_template=name_link_template,
                        field_name="name")

        super(ImageRecipesTable, self).setup_columns(*args, **kwargs)

        self.add_column(title="Customise",
                        hideable=False,
                        filter_name="in_current_project",
                        static_data_name="customise-or-add-recipe",
                        static_data_template='{% include "customise_btn.html" %}')


class SoftwareRecipesTable(RecipesTable):
    """ Displays just the software recipes """
    def __init__(self, *args, **kwargs):
        super(SoftwareRecipesTable, self).__init__(*args, **kwargs)
        self.title = "Compatible software recipes"
        self.default_orderby = "name"

    def setup_queryset(self, *args, **kwargs):
        super(SoftwareRecipesTable, self).setup_queryset(*args, **kwargs)

        self.queryset = self.queryset.filter(is_image=False)
        self.queryset = self.queryset.order_by(self.default_orderby)


    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Software recipe",
                        help_text="Information about a single piece of "
                        "software, including where to download the source, "
                        "configuration options, how to compile the source "
                        "files and how to package the compiled output",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        super(SoftwareRecipesTable, self).setup_columns(*args, **kwargs)

        self.add_column(**RecipesTable.build_col)

class PackagesTable(ToasterTable):
    """ Table to display the packages in a recipe from it's last successful
    build"""

    def __init__(self, *args, **kwargs):
        super(PackagesTable, self).__init__(*args, **kwargs)
        self.title = "Packages included"
        self.packages = None
        self.default_orderby = "name"

    def create_package_list(self, recipe, project_id):
        """Creates a list of packages for the specified recipe by looking for
        the last SUCCEEDED build of ther recipe"""

        target = Target.objects.filter(Q(target=recipe.name) &
                                       Q(build__project_id=project_id) &
                                       Q(build__outcome=Build.SUCCEEDED)
                                      ).last()

        if target:
            pkgs = target.target_installed_package_set.values_list('package',
                                                                   flat=True)
            return Package.objects.filter(pk__in=pkgs)

        # Target/recipe never successfully built so empty queryset
        return Package.objects.none()

    def get_context_data(self, **kwargs):
        """Context for rendering the sidebar and other items on the recipe
        details page """
        context = super(PackagesTable, self).get_context_data(**kwargs)

        recipe = Recipe.objects.get(pk=kwargs['recipe_id'])
        project = Project.objects.get(pk=kwargs['pid'])

        in_project = (recipe.layer_version.pk in
                      project.get_project_layer_versions(pk=True))

        packages = self.create_package_list(recipe, project.pk)

        context.update({'project': project,
                        'recipe' : recipe,
                        'packages': packages,
                        'approx_pkg_size' : packages.aggregate(Sum('size')),
                        'in_project' : in_project,
                       })

        return context

    def setup_queryset(self, *args, **kwargs):
        recipe = Recipe.objects.get(pk=kwargs['recipe_id'])
        self.static_context_extra['target_name'] = recipe.name

        self.queryset = self.create_package_list(recipe, kwargs['pid'])
        self.queryset = self.queryset.order_by('name')

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Package",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Package Version",
                        field_name="version",
                        hideable=False)

        self.add_column(title="Approx Size",
                        orderable=True,
                        field_name="size",
                        static_data_name="size",
                        static_data_template="{% load projecttags %} \
                        {{data.size|filtered_filesizeformat}}")

        self.add_column(title="License",
                        field_name="license",
                        orderable=True,
                        hidden=True)


        self.add_column(title="Dependencies",
                        static_data_name="dependencies",
                        static_data_template='\
                        {% include "snippets/pkg_dependencies_popover.html" %}')

        self.add_column(title="Reverse dependencies",
                        static_data_name="reverse_dependencies",
                        static_data_template='\
                        {% include "snippets/pkg_revdependencies_popover.html" %}',
                        hidden=True)

        self.add_column(title="Recipe",
                        field_name="recipe__name",
                        orderable=True,
                        hidden=True)

        self.add_column(title="Recipe version",
                        field_name="recipe__version",
                        hidden=True)


class SelectPackagesTable(PackagesTable):
    """ Table to display the packages to add and remove from an image """

    def __init__(self, *args, **kwargs):
        super(SelectPackagesTable, self).__init__(*args, **kwargs)
        self.title = "Add | Remove packages"

    def setup_queryset(self, *args, **kwargs):
        self.cust_recipe =\
            CustomImageRecipe.objects.get(pk=kwargs['custrecipeid'])
        prj = Project.objects.get(pk = kwargs['pid'])

        current_packages = self.cust_recipe.get_all_packages()

        current_recipes = prj.get_available_recipes()

        # only show packages where recipes->layers are in the project
        self.queryset = CustomImagePackage.objects.filter(
                ~Q(recipe=None) &
                Q(recipe__in=current_recipes))

        self.queryset = self.queryset.order_by('name')

        # This target is the target used to work out which group of dependences
        # to display, if we've built the custom image we use it otherwise we
        # can use the based recipe instead
        if prj.build_set.filter(target__target=self.cust_recipe.name).count()\
           > 0:
            self.static_context_extra['target_name'] = self.cust_recipe.name
        else:
            self.static_context_extra['target_name'] =\
                    Package_DependencyManager.TARGET_LATEST

        self.static_context_extra['recipe_id'] = kwargs['custrecipeid']


        self.static_context_extra['current_packages'] = \
                current_packages.values_list('pk', flat=True)

    def get_context_data(self, **kwargs):
        # to reuse the Super class map the custrecipeid to the recipe_id
        kwargs['recipe_id'] = kwargs['custrecipeid']
        context = super(SelectPackagesTable, self).get_context_data(**kwargs)
        custom_recipe = \
            CustomImageRecipe.objects.get(pk=kwargs['custrecipeid'])

        context['recipe'] = custom_recipe
        context['approx_pkg_size'] = \
                        custom_recipe.get_all_packages().aggregate(Sum('size'))
        return context


    def setup_columns(self, *args, **kwargs):
        super(SelectPackagesTable, self).setup_columns(*args, **kwargs)

        add_remove_template = '{% include "pkg_add_rm_btn.html" %}'

        self.add_column(title="Add | Remove",
                        hideable=False,
                        help_text="Use the add and remove buttons to modify "
                        "the package content of your custom image",
                        static_data_name="add_rm_pkg_btn",
                        static_data_template=add_remove_template,
                        filter_name='in_current_image_filter')

    def setup_filters(self, *args, **kwargs):
        in_current_image_filter = TableFilter(
            'in_current_image_filter',
            'Filter by added packages'
        )

        in_image_action = TableFilterActionToggle(
            'in_image',
            'Packages in %s' % self.cust_recipe.name,
            Q(pk__in=self.static_context_extra['current_packages'])
        )

        not_in_image_action = TableFilterActionToggle(
            'not_in_image',
            'Packages not added to %s' % self.cust_recipe.name,
            ~Q(pk__in=self.static_context_extra['current_packages'])
        )

        in_current_image_filter.add_action(in_image_action)
        in_current_image_filter.add_action(not_in_image_action)
        self.add_filter(in_current_image_filter)

class ProjectsTable(ToasterTable):
    """Table of projects in Toaster"""

    def __init__(self, *args, **kwargs):
        super(ProjectsTable, self).__init__(*args, **kwargs)
        self.default_orderby = '-updated'
        self.title = 'All projects'
        self.static_context_extra['Build'] = Build

    def get_context_data(self, **kwargs):
        return super(ProjectsTable, self).get_context_data(**kwargs)

    def setup_queryset(self, *args, **kwargs):
        queryset = Project.objects.all()

        # annotate each project with its number of builds
        queryset = queryset.annotate(num_builds=Count('build'))

        # exclude the command line builds project if it has no builds
        q_default_with_builds = Q(is_default=True) & Q(num_builds__gt=0)
        queryset = queryset.filter(Q(is_default=False) |
                                   q_default_with_builds)

        # order rows
        queryset = queryset.order_by(self.default_orderby)

        self.queryset = queryset

    # columns: last activity on (updated) - DEFAULT, project (name), release,
    # machine, number of builds, last build outcome, recipe (name),  errors,
    # warnings, image files
    def setup_columns(self, *args, **kwargs):
        name_template = '''
        {% load project_url_tag %}
        <span data-project-field="name">
          <a href="{% project_url data %}">
            {{data.name}}
          </a>
        </span>
        '''

        last_activity_on_template = '''
        {% load project_url_tag %}
        <span data-project-field="updated">
            {{data.updated | date:"d/m/y H:i"}}
        </span>
        '''

        release_template = '''
        <span data-project-field="release">
          {% if data.release %}
            {{data.release.name}}
          {% elif data.is_default %}
            <span class="text-muted">Not applicable</span>
            <span class="glyphicon glyphicon-question-sign get-help hover-help"
               title="This project does not have a release set.
               It simply collects information about the builds you start from
               the command line while Toaster is running"
               style="visibility: hidden;">
            </span>
          {% else %}
            No release available
          {% endif %}
        </span>
        '''

        machine_template = '''
        <span data-project-field="machine">
          {% if data.is_default %}
            <span class="text-muted">Not applicable</span>
            <span class="glyphicon glyphicon-question-sign get-help hover-help"
               title="This project does not have a machine
               set. It simply collects information about the builds you
               start from the command line while Toaster is running"
               style="visibility: hidden;"></span>
          {% else %}
            {{data.get_current_machine_name}}
          {% endif %}
        </span>
        '''

        number_of_builds_template = '''
        {% if data.get_number_of_builds > 0 %}
          <a href="{% url 'projectbuilds' data.id %}">
            {{data.get_number_of_builds}}
          </a>
        {% endif %}
        '''

        last_build_outcome_template = '''
        {% if data.get_number_of_builds > 0 %}
          {% if data.get_last_outcome == extra.Build.SUCCEEDED %}
            <span class="glyphicon glyphicon-ok-circle"></span>
          {% elif data.get_last_outcome == extra.Build.FAILED %}
            <span class="glyphicon glyphicon-minus-sign"></span>
          {% endif %}
        {% endif %}
        '''

        recipe_template = '''
        {% if data.get_number_of_builds > 0 %}
          <a href="{% url "builddashboard" data.get_last_build_id %}">
            {{data.get_last_target}}
          </a>
        {% endif %}
        '''

        errors_template = '''
        {% if data.get_number_of_builds > 0 and data.get_last_errors > 0 %}
          <a class="errors.count text-danger"
             href="{% url "builddashboard" data.get_last_build_id %}#errors">
            {{data.get_last_errors}} error{{data.get_last_errors | pluralize}}
          </a>
        {% endif %}
        '''

        warnings_template = '''
        {% if data.get_number_of_builds > 0 and data.get_last_warnings > 0 %}
          <a class="warnings.count text-warning"
             href="{% url "builddashboard" data.get_last_build_id %}#warnings">
            {{data.get_last_warnings}} warning{{data.get_last_warnings | pluralize}}
          </a>
        {% endif %}
        '''

        image_files_template = '''
        {% if data.get_number_of_builds > 0 and data.get_last_outcome == extra.Build.SUCCEEDED %}
          {{data.get_last_build_extensions}}
        {% endif %}
        '''

        self.add_column(title='Project',
                        hideable=False,
                        orderable=True,
                        static_data_name='name',
                        static_data_template=name_template)

        self.add_column(title='Last activity on',
                        help_text='Starting date and time of the \
                                   last project build. If the project has no \
                                   builds, this shows the date the project was \
                                   created.',
                        hideable=False,
                        orderable=True,
                        static_data_name='updated',
                        static_data_template=last_activity_on_template)

        self.add_column(title='Release',
                        help_text='The version of the build system used by \
                                   the project',
                        hideable=False,
                        orderable=True,
                        static_data_name='release',
                        static_data_template=release_template)

        self.add_column(title='Machine',
                        help_text='The hardware currently selected for the \
                                   project',
                        hideable=False,
                        orderable=False,
                        static_data_name='machine',
                        static_data_template=machine_template)

        self.add_column(title='Builds',
                        help_text='The number of builds which have been run \
                                   for the project',
                        hideable=False,
                        orderable=False,
                        static_data_name='number_of_builds',
                        static_data_template=number_of_builds_template)

        self.add_column(title='Last build outcome',
                        help_text='Indicates whether the last project build \
                                   completed successfully or failed',
                        hideable=True,
                        orderable=False,
                        static_data_name='last_build_outcome',
                        static_data_template=last_build_outcome_template)

        self.add_column(title='Recipe',
                        help_text='The last recipe which was built in this \
                                   project',
                        hideable=True,
                        orderable=False,
                        static_data_name='recipe_name',
                        static_data_template=recipe_template)

        self.add_column(title='Errors',
                        help_text='The number of errors encountered during \
                                   the last project build (if any)',
                        hideable=True,
                        orderable=False,
                        static_data_name='errors',
                        static_data_template=errors_template)

        self.add_column(title='Warnings',
                        help_text='The number of warnings encountered during \
                                   the last project build (if any)',
                        hideable=True,
                        hidden=True,
                        orderable=False,
                        static_data_name='warnings',
                        static_data_template=warnings_template)

        self.add_column(title='Image files',
                        help_text='The root file system types produced by \
                                   the last project build',
                        hideable=True,
                        hidden=True,
                        orderable=False,
                        static_data_name='image_files',
                        static_data_template=image_files_template)

class BuildsTable(ToasterTable):
    """Table of builds in Toaster"""

    def __init__(self, *args, **kwargs):
        super(BuildsTable, self).__init__(*args, **kwargs)
        self.default_orderby = '-completed_on'
        self.static_context_extra['Build'] = Build
        self.static_context_extra['Task'] = Task

        # attributes that are overridden in subclasses

        # title for the page
        self.title = ''

        # 'project' or 'all'; determines how the mrb (most recent builds)
        # section is displayed
        self.mrb_type = ''

    def get_builds(self):
        """
        overridden in ProjectBuildsTable to return builds for a
        single project
        """
        return Build.objects.all()

    def get_context_data(self, **kwargs):
        context = super(BuildsTable, self).get_context_data(**kwargs)

        # should be set in subclasses
        context['mru'] = []

        context['mrb_type'] = self.mrb_type

        return context

    def setup_queryset(self, *args, **kwargs):
        """
        The queryset is annotated so that it can be sorted by number of
        errors and number of warnings; but note that the criteria for
        finding the log messages to populate these fields should match those
        used in the Build model (orm/models.py) to populate the errors and
        warnings properties
        """
        queryset = self.get_builds()

        # Don't include in progress builds pr cancelled builds
        queryset = queryset.exclude(Q(outcome=Build.IN_PROGRESS) |
                                    Q(outcome=Build.CANCELLED))

        # sort
        queryset = queryset.order_by(self.default_orderby)

        # annotate with number of ERROR, EXCEPTION and CRITICAL log messages
        criteria = (Q(logmessage__level=LogMessage.ERROR) |
                    Q(logmessage__level=LogMessage.EXCEPTION) |
                    Q(logmessage__level=LogMessage.CRITICAL))

        queryset = queryset.annotate(
            errors_no=Count(
                Case(
                    When(criteria, then=Value(1)),
                    output_field=IntegerField()
                )
            )
        )

        # annotate with number of WARNING log messages
        queryset = queryset.annotate(
            warnings_no=Count(
                Case(
                    When(logmessage__level=LogMessage.WARNING, then=Value(1)),
                    output_field=IntegerField()
                )
            )
        )

        self.queryset = queryset

    def setup_columns(self, *args, **kwargs):
        outcome_template = '''
        {% if data.outcome == data.SUCCEEDED %}
            <span class="glyphicon glyphicon-ok-circle"></span>
        {% elif data.outcome == data.FAILED %}
            <span class="glyphicon glyphicon-minus-sign"></span>
        {% endif %}

        {% if data.cooker_log_path %}
            &nbsp;
            <a href="{% url "build_artifact" data.id "cookerlog" data.id %}">
               <span class="glyphicon glyphicon-download-alt get-help"
               data-original-title="Download build log"></span>
            </a>
        {% endif %}
        '''

        recipe_template = '''
        {% for target_label in data.target_labels %}
            <a href="{% url "builddashboard" data.id %}">
                {{target_label}}
            </a>
            <br />
        {% endfor %}
        '''

        machine_template = '''
        {{data.machine}}
        '''

        started_on_template = '''
        {{data.started_on | date:"d/m/y H:i"}}
        '''

        completed_on_template = '''
        {{data.completed_on | date:"d/m/y H:i"}}
        '''

        failed_tasks_template = '''
        {% if data.failed_tasks.count == 1 %}
            <a class="text-danger" href="{% url "task" data.id data.failed_tasks.0.id %}">
                <span>
                    {{data.failed_tasks.0.recipe.name}} {{data.failed_tasks.0.task_name}}
                </span>
            </a>
            <a href="{% url "build_artifact" data.id "tasklogfile" data.failed_tasks.0.id %}">
                <span class="glyphicon glyphicon-download-alt get-help"
                   title="Download task log file">
                </span>
            </a>
        {% elif data.failed_tasks.count > 1 %}
            <a href="{% url "tasks" data.id %}?filter=outcome%3A{{extra.Task.OUTCOME_FAILED}}">
                <span class="text-danger">{{data.failed_tasks.count}} tasks</span>
            </a>
        {% endif %}
        '''

        errors_template = '''
        {% if data.errors_no %}
            <a class="errors.count text-danger" href="{% url "builddashboard" data.id %}#errors">
                {{data.errors_no}} error{{data.errors_no|pluralize}}
            </a>
        {% endif %}
        '''

        warnings_template = '''
        {% if data.warnings_no %}
            <a class="warnings.count text-warning" href="{% url "builddashboard" data.id %}#warnings">
                {{data.warnings_no}} warning{{data.warnings_no|pluralize}}
            </a>
        {% endif %}
        '''

        time_template = '''
        {% load projecttags %}
        {% if data.outcome == extra.Build.SUCCEEDED %}
            <a href="{% url "buildtime" data.id %}">
                {{data.timespent_seconds | sectohms}}
            </a>
        {% else %}
            {{data.timespent_seconds | sectohms}}
        {% endif %}
        '''

        image_files_template = '''
        {% if data.outcome == extra.Build.SUCCEEDED %}
            {{data.get_image_file_extensions}}
        {% endif %}
        '''

        self.add_column(title='Outcome',
                        help_text='Final state of the build (successful \
                                   or failed)',
                        hideable=False,
                        orderable=True,
                        filter_name='outcome_filter',
                        static_data_name='outcome',
                        static_data_template=outcome_template)

        self.add_column(title='Recipe',
                        help_text='What was built (i.e. one or more recipes \
                                   or image recipes)',
                        hideable=False,
                        orderable=False,
                        static_data_name='target',
                        static_data_template=recipe_template)

        self.add_column(title='Machine',
                        help_text='Hardware for which you are building a \
                                   recipe or image recipe',
                        hideable=False,
                        orderable=True,
                        static_data_name='machine',
                        static_data_template=machine_template)

        self.add_column(title='Started on',
                        help_text='The date and time when the build started',
                        hideable=True,
                        hidden=True,
                        orderable=True,
                        filter_name='started_on_filter',
                        static_data_name='started_on',
                        static_data_template=started_on_template)

        self.add_column(title='Completed on',
                        help_text='The date and time when the build finished',
                        hideable=False,
                        orderable=True,
                        filter_name='completed_on_filter',
                        static_data_name='completed_on',
                        static_data_template=completed_on_template)

        self.add_column(title='Failed tasks',
                        help_text='The number of tasks which failed during \
                                   the build',
                        hideable=True,
                        orderable=False,
                        filter_name='failed_tasks_filter',
                        static_data_name='failed_tasks',
                        static_data_template=failed_tasks_template)

        self.add_column(title='Errors',
                        help_text='The number of errors encountered during \
                                   the build (if any)',
                        hideable=True,
                        orderable=True,
                        static_data_name='errors_no',
                        static_data_template=errors_template)

        self.add_column(title='Warnings',
                        help_text='The number of warnings encountered during \
                                   the build (if any)',
                        hideable=True,
                        orderable=True,
                        static_data_name='warnings_no',
                        static_data_template=warnings_template)

        self.add_column(title='Time',
                        help_text='How long the build took to finish',
                        hideable=True,
                        hidden=True,
                        orderable=False,
                        static_data_name='time',
                        static_data_template=time_template)

        self.add_column(title='Image files',
                        help_text='The root file system types produced by \
                                   the build',
                        hideable=True,
                        orderable=False,
                        static_data_name='image_files',
                        static_data_template=image_files_template)

    def setup_filters(self, *args, **kwargs):
        # outcomes
        outcome_filter = TableFilter(
            'outcome_filter',
            'Filter builds by outcome'
        )

        successful_builds_action = TableFilterActionToggle(
            'successful_builds',
            'Successful builds',
            Q(outcome=Build.SUCCEEDED)
        )

        failed_builds_action = TableFilterActionToggle(
            'failed_builds',
            'Failed builds',
            Q(outcome=Build.FAILED)
        )

        outcome_filter.add_action(successful_builds_action)
        outcome_filter.add_action(failed_builds_action)
        self.add_filter(outcome_filter)

        # started on
        started_on_filter = TableFilter(
            'started_on_filter',
            'Filter by date when build was started'
        )

        started_today_action = TableFilterActionDay(
            'today',
            'Today\'s builds',
            'started_on',
            'today'
        )

        started_yesterday_action = TableFilterActionDay(
            'yesterday',
            'Yesterday\'s builds',
            'started_on',
            'yesterday'
        )

        by_started_date_range_action = TableFilterActionDateRange(
            'date_range',
            'Build date range',
            'started_on'
        )

        started_on_filter.add_action(started_today_action)
        started_on_filter.add_action(started_yesterday_action)
        started_on_filter.add_action(by_started_date_range_action)
        self.add_filter(started_on_filter)

        # completed on
        completed_on_filter = TableFilter(
            'completed_on_filter',
            'Filter by date when build was completed'
        )

        completed_today_action = TableFilterActionDay(
            'today',
            'Today\'s builds',
            'completed_on',
            'today'
        )

        completed_yesterday_action = TableFilterActionDay(
            'yesterday',
            'Yesterday\'s builds',
            'completed_on',
            'yesterday'
        )

        by_completed_date_range_action = TableFilterActionDateRange(
            'date_range',
            'Build date range',
            'completed_on'
        )

        completed_on_filter.add_action(completed_today_action)
        completed_on_filter.add_action(completed_yesterday_action)
        completed_on_filter.add_action(by_completed_date_range_action)
        self.add_filter(completed_on_filter)

        # failed tasks
        failed_tasks_filter = TableFilter(
            'failed_tasks_filter',
            'Filter builds by failed tasks'
        )

        criteria = Q(task_build__outcome=Task.OUTCOME_FAILED)

        with_failed_tasks_action = TableFilterActionToggle(
            'with_failed_tasks',
            'Builds with failed tasks',
            criteria
        )

        without_failed_tasks_action = TableFilterActionToggle(
            'without_failed_tasks',
            'Builds without failed tasks',
            ~criteria
        )

        failed_tasks_filter.add_action(with_failed_tasks_action)
        failed_tasks_filter.add_action(without_failed_tasks_action)
        self.add_filter(failed_tasks_filter)


class AllBuildsTable(BuildsTable):
    """ Builds page for all builds """

    def __init__(self, *args, **kwargs):
        super(AllBuildsTable, self).__init__(*args, **kwargs)
        self.title = 'All builds'
        self.mrb_type = 'all'

    def setup_columns(self, *args, **kwargs):
        """
        All builds page shows a column for the project
        """

        super(AllBuildsTable, self).setup_columns(*args, **kwargs)

        project_template = '''
        {% load project_url_tag %}
        <a href="{% project_url data.project %}">
            {{data.project.name}}
        </a>
        {% if data.project.is_default %}
            <span class="glyphicon glyphicon-question-sign get-help hover-help" title=""
               data-original-title="This project shows information about
               the builds you start from the command line while Toaster is
               running" style="visibility: hidden;"></span>
        {% endif %}
        '''

        self.add_column(title='Project',
                        hideable=True,
                        orderable=True,
                        static_data_name='project',
                        static_data_template=project_template)

    def get_context_data(self, **kwargs):
        """ Get all builds for the recent builds area """
        context = super(AllBuildsTable, self).get_context_data(**kwargs)
        context['mru'] = Build.get_recent()
        return context

class ProjectBuildsTable(BuildsTable):
    """
    Builds page for a single project; a BuildsTable, with the queryset
    filtered by project
    """

    def __init__(self, *args, **kwargs):
        super(ProjectBuildsTable, self).__init__(*args, **kwargs)
        self.title = 'All project builds'
        self.mrb_type = 'project'

        # set from the querystring
        self.project_id = None

    def setup_columns(self, *args, **kwargs):
        """
        Project builds table doesn't show the machines column by default
        """

        super(ProjectBuildsTable, self).setup_columns(*args, **kwargs)

        # hide the machine column
        self.set_column_hidden('Machine', True)

        # allow the machine column to be hidden by the user
        self.set_column_hideable('Machine', True)

    def setup_queryset(self, *args, **kwargs):
        """
        NOTE: self.project_id must be set before calling super(),
        as it's used in setup_queryset()
        """
        self.project_id = kwargs['pid']
        super(ProjectBuildsTable, self).setup_queryset(*args, **kwargs)
        project = Project.objects.get(pk=self.project_id)
        self.queryset = self.queryset.filter(project=project)

    def get_context_data(self, **kwargs):
        """
        Get recent builds for this project, and the project itself

        NOTE: self.project_id must be set before calling super(),
        as it's used in get_context_data()
        """
        self.project_id = kwargs['pid']
        context = super(ProjectBuildsTable, self).get_context_data(**kwargs)

        empty_state_template = '''
        This project has no builds.
        <a href="{% url 'projectimagerecipes' data.pid %}">
        Choose a recipe to build</a>
        '''
        context['empty_state'] = self.render_static_data(empty_state_template,
                                                         kwargs)

        project = Project.objects.get(pk=self.project_id)
        context['mru'] = Build.get_recent(project)
        context['project'] = project

        self.setup_queryset(**kwargs)
        if self.queryset.count() == 0 and \
           project.build_set.filter(outcome=Build.IN_PROGRESS).count() > 0:
            context['build_in_progress_none_completed'] = True
        else:
            context['build_in_progress_none_completed'] = False

        return context


class DistrosTable(ToasterTable):
    """Table of Distros in Toaster"""

    def __init__(self, *args, **kwargs):
        super(DistrosTable, self).__init__(*args, **kwargs)
        self.empty_state = "Toaster has no distro information for this project. Sadly, 			   distro information cannot be obtained from builds, so this 				  page will remain empty."
        self.title = "Compatible Distros"
        self.default_orderby = "name"

    def get_context_data(self, **kwargs):
        context = super(DistrosTable, self).get_context_data(**kwargs)
        context['project'] = Project.objects.get(pk=kwargs['pid'])
        return context

    def setup_filters(self, *args, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])

        in_current_project_filter = TableFilter(
            "in_current_project",
            "Filter by project Distros"
        )

        in_project_action = TableFilterActionToggle(
            "in_project",
            "Distro provided by layers added to this project",
            ProjectFilters.in_project(self.project_layers)
        )

        not_in_project_action = TableFilterActionToggle(
            "not_in_project",
            "Distros provided by layers not added to this project",
            ProjectFilters.not_in_project(self.project_layers)
        )

        in_current_project_filter.add_action(in_project_action)
        in_current_project_filter.add_action(not_in_project_action)
        self.add_filter(in_current_project_filter)

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        self.queryset = prj.get_all_compatible_distros()
        self.queryset = self.queryset.order_by(self.default_orderby)

        self.static_context_extra['current_layers'] = \
                self.project_layers = \
                prj.get_project_layer_versions(pk=True)

    def setup_columns(self, *args, **kwargs):

        self.add_column(title="Distro",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Description",
                        field_name="description")

        layer_link_template = '''
        <a href="{% url 'layerdetails' extra.pid data.layer_version.id %}">
        {{data.layer_version.layer.name}}</a>
        '''

        self.add_column(title="Layer",
                        static_data_name="layer_version__layer__name",
                        static_data_template=layer_link_template,
                        orderable=True)

        self.add_column(title="Git revision",
                        help_text="The Git branch, tag or commit. For the layers from the OpenEmbedded layer source, the revision is always the branch compatible with the Yocto Project version you selected for this project",
                        hidden=True,
                        field_name="layer_version__get_vcs_reference")

        distro_file_template = '''<code>conf/distro/{{data.name}}.conf</code>
        {% if 'None' not in data.get_vcs_distro_file_link_url %}<a href="{{data.get_vcs_distro_file_link_url}}" target="_blank"><span class="glyphicon glyphicon-new-window"></i></a>{% endif %}'''
        self.add_column(title="Distro file",
                        hidden=True,
                        static_data_name="templatefile",
                        static_data_template=distro_file_template)

        self.add_column(title="Select",
                        help_text="Sets the selected distro to the project",
                        hideable=False,
                        filter_name="in_current_project",
                        static_data_name="add-del-layers",
                        static_data_template='{% include "distro_btn.html" %}')

