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
from orm.models import CustomImageRecipe, Package
from django.db.models import Q, Max
from django.conf.urls import url
from django.core.urlresolvers import reverse
from django.views.generic import TemplateView


class ProjectFiltersMixin(object):
    """Common mixin for recipe, machine in project filters"""

    def filter_in_project(self, count_only=False):
        query = self.queryset.filter(layer_version__in=self.project_layers)
        if count_only:
            return query.count()

        self.queryset = query

    def filter_not_in_project(self, count_only=False):
        query = self.queryset.exclude(layer_version__in=self.project_layers)
        if count_only:
            return query.count()

        self.queryset = query

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


        self.add_filter(title="Filter by project layers",
                        name="in_current_project",
                        filter_actions=[
                            self.make_filter_action("in_project", "Layers added to this project", self.filter_in_project),
                            self.make_filter_action("not_in_project", "Layers not added to this project", self.filter_not_in_project)
                        ])

    def filter_in_project(self, count_only=False):
        query = self.queryset.filter(projectlayer__in=self.project_layers)
        if count_only:
            return query.count()

        self.queryset = query

    def filter_not_in_project(self, count_only=False):
        query = self.queryset.exclude(projectlayer__in=self.project_layers)
        if count_only:
            return query.count()

        self.queryset = query


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
          <code>{{data.layer.vcs_url}}</code>
        </a>
        {% if data.get_vcs_link_url %}
        <a target="_blank" href="{{ data.get_vcs_link_url }}">
           <i class="icon-share get-info"></i>
        </a>
        {% endif %}
        '''

        self.add_column(title="Git repository URL",
                        help_text="The Git repository for the layer source code",
                        hidden=True,
                        static_data_name="layer__vcs_url",
                        static_data_template=git_url_template)

        git_dir_template = '''
        <a href="{% url 'layerdetails' extra.pid data.id %}">
         <code>{{data.dirpath}}</code>
        </a>
        {% if data.dirpath and data.get_vcs_dirpath_link_url %}
        <a target="_blank" href="{{ data.get_vcs_dirpath_link_url }}">
          <i class="icon-share get-info"></i>
        </a>
        {% endif %}'''

        self.add_column(title="Subdirectory",
                        help_text="The layer directory within the Git repository",
                        hidden=True,
                        static_data_name="git_subdir",
                        static_data_template=git_dir_template)

        revision_template =  '''
        {% load projecttags  %}
        {% with vcs_ref=data.get_vcs_reference %}
        {% if vcs_ref|is_shaid %}
        <a class="btn" data-content="<ul class='unstyled'> <li>{{vcs_ref}}</li> </ul>">
        {{vcs_ref|truncatechars:10}}
        </a>
        {% else %}
        {{vcs_ref}}
        {% endif %}
        {% endwith %}
        '''

        self.add_column(title="Revision",
                        help_text="The Git branch, tag or commit. For the layers from the OpenEmbedded layer source, the revision is always the branch compatible with the Yocto Project version you selected for this project",
                        static_data_name="revision",
                        static_data_template=revision_template)

        deps_template = '''
        {% with ods=data.dependencies.all%}
        {% if ods.count %}
            <a class="btn" title="<a href='{% url "layerdetails" extra.pid data.id %}'>{{data.layer.name}}</a> dependencies"
        data-content="<ul class='unstyled'>
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

        self.add_column(title="Add | Delete",
                        help_text="Add or delete layers to / from your project",
                        hideable=False,
                        filter_name="in_current_project",
                        static_data_name="add-del-layers",
                        static_data_template='{% include "layer_btn.html" %}')

        project = Project.objects.get(pk=kwargs['pid'])
        self.add_column(title="LayerDetailsUrl",
                        displayable = False,
                        field_name="layerdetailurl",
                        computation = lambda x: reverse('layerdetails', args=(project.id, x.id)))

        self.add_column(title="name",
                        displayable = False,
                        field_name="name",
                        computation = lambda x: x.layer.name)


class MachinesTable(ToasterTable, ProjectFiltersMixin):
    """Table of Machines in Toaster"""

    def __init__(self, *args, **kwargs):
        super(MachinesTable, self).__init__(*args, **kwargs)
        self.empty_state = "No machines maybe you need to do a build?"
        self.title = "Compatible machines"
        self.default_orderby = "name"

    def get_context_data(self, **kwargs):
        context = super(MachinesTable, self).get_context_data(**kwargs)
        context['project'] = Project.objects.get(pk=kwargs['pid'])
        context['projectlayers'] = map(lambda prjlayer: prjlayer.layercommit.id, ProjectLayer.objects.filter(project=context['project']))
        return context

    def setup_filters(self, *args, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])
        self.project_layers = project.get_project_layer_versions()

        self.add_filter(title="Filter by project machines",
                        name="in_current_project",
                        filter_actions=[
                            self.make_filter_action("in_project", "Machines provided by layers added to this project", self.filter_in_project),
                            self.make_filter_action("not_in_project", "Machines provided by layers not added to this project", self.filter_not_in_project)
                        ])

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        self.queryset = prj.get_all_compatible_machines()
        self.queryset = self.queryset.order_by(self.default_orderby)

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

        self.add_column(title="Revision",
                        help_text="The Git branch, tag or commit. For the layers from the OpenEmbedded layer source, the revision is always the branch compatible with the Yocto Project version you selected for this project",
                        hidden=True,
                        field_name="layer_version__get_vcs_reference")

        machine_file_template = '''<code>conf/machine/{{data.name}}.conf</code>
        <a href="{{data.get_vcs_machine_file_link_url}}" target="_blank"><i class="icon-share get-info"></i></a>'''

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
        self.static_context_extra['in_prj'] = ProjectLayer.objects.filter(Q(project=kwargs['pid']) & Q(layercommit=kwargs['layerid'])).count()

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Machine",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Description",
                        field_name="description")

        select_btn_template = '<a href="{% url "project" extra.pid %}?setMachine={{data.name}}" class="btn btn-block select-machine-btn" {% if extra.in_prj == 0%}disabled="disabled"{%endif%}>Select machine</a>'

        self.add_column(title="Select machine",
                        static_data_name="add-del-layers",
                        static_data_template=select_btn_template)


class RecipesTable(ToasterTable, ProjectFiltersMixin):
    """Table of All Recipes in Toaster"""

    def __init__(self, *args, **kwargs):
        super(RecipesTable, self).__init__(*args, **kwargs)
        self.empty_state = "Toaster has no recipe information. To generate recipe information you can configure a layer source then run a build."
        self.default_orderby = "name"

    build_col = { 'title' : "Build",
            'help_text' : "Add or delete recipes to and from your project",
            'hideable' : False,
            'filter_name' : "in_current_project",
            'static_data_name' : "add-del-layers",
            'static_data_template' : '{% include "recipe_btn.html" %}'}

    def get_context_data(self, **kwargs):
        project = Project.objects.get(pk=kwargs['pid'])
        context = super(RecipesTable, self).get_context_data(**kwargs)

        context['project'] = project

        context['projectlayers'] = map(lambda prjlayer: prjlayer.layercommit.id, ProjectLayer.objects.filter(project=context['project']))

        return context

    def setup_filters(self, *args, **kwargs):
        self.add_filter(title="Filter by project recipes",
                        name="in_current_project",
                        filter_actions=[
                            self.make_filter_action("in_project", "Recipes provided by layers added to this project", self.filter_in_project),
                            self.make_filter_action("not_in_project", "Recipes provided by layers not added to this project", self.filter_not_in_project)
                        ])

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])

        # Project layers used by the filters
        self.project_layers = prj.get_project_layer_versions(pk=True)

        # Project layers used to switch the button states
        self.static_context_extra['current_layers'] = self.project_layers

        self.queryset = prj.get_all_compatible_recipes()
        self.queryset = self.queryset.order_by(self.default_orderby)


    def setup_columns(self, *args, **kwargs):

        self.add_column(title="Version",
                        hidden=False,
                        field_name="version")

        self.add_column(title="Description",
                        field_name="get_description_or_summary")

        recipe_file_template = '''
        <code>{{data.file_path}}</code>
        <a href="{{data.get_vcs_recipe_file_link_url}}" target="_blank">
          <i class="icon-share get-info"></i>
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

        self.add_column(title="Revision",
                        hidden=True,
                        field_name="layer_version__get_vcs_reference")


class LayerRecipesTable(RecipesTable):
    """ Smaller version of the Recipes table for use in layer details """

    def __init__(self, *args, **kwargs):
        super(LayerRecipesTable, self).__init__(*args, **kwargs)

    def get_context_data(self, **kwargs):
        context = super(LayerRecipesTable, self).get_context_data(**kwargs)
        context['layerversion'] = Layer_Version.objects.get(pk=kwargs['layerid'])
        return context


    def setup_queryset(self, *args, **kwargs):
        self.queryset = \
                Recipe.objects.filter(layer_version__pk=int(kwargs['layerid']))

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

        build_recipe_template ='<button class="btn btn-block build-recipe-btn" data-recipe-name="{{data.name}}" {%if extra.in_prj == 0 %}disabled="disabled"{%endif%}>Build recipe</button>'

        self.add_column(title="Build recipe",
                        static_data_name="add-del-layers",
                        static_data_template=build_recipe_template)

class CustomImagesTable(ToasterTable):
    """ Table to display your custom images """
    def __init__(self, *args, **kwargs):
        super(CustomImagesTable, self).__init__(*args, **kwargs)
        self.title = "Custom images"

    def get_context_data(self, **kwargs):
        context = super(CustomImagesTable, self).get_context_data(**kwargs)
        project = Project.objects.get(pk=kwargs['pid'])
        context['project'] = project
        context['projectlayers'] = map(lambda prjlayer: prjlayer.layercommit.id, ProjectLayer.objects.filter(project=context['project']))
        return context

    def setup_queryset(self, *args, **kwargs):
        prj = Project.objects.get(pk = kwargs['pid'])
        self.queryset = CustomImageRecipe.objects.filter(project=prj)
        self.queryset = self.queryset.order_by('name')

    def setup_columns(self, *args, **kwargs):

        name_link_template = '''
        <a href="{% url 'customrecipe' extra.pid data.id %}">
          {{data.name}}
        </a>
        '''

        self.add_column(title="Custom image",
                        hideable=False,
                        static_data_name="name",
                        static_data_template=name_link_template)

        self.add_column(title="Recipe file",
                        static_data_name='recipe_file',
                        static_data_template='')

        approx_packages_template = '<a href="#imagedetails">{{data.packages.all|length}}</a>'
        self.add_column(title="Approx packages",
                        static_data_name='approx_packages',
                        static_data_template=approx_packages_template)


        build_btn_template = '''<button data-recipe-name="{{data.name}}"
        class="btn btn-block build-recipe-btn" style="margin-top: 5px;" >
        Build</button>'''

        self.add_column(title="Build",
                        hideable=False,
                        static_data_name='build_custom_img',
                        static_data_template=build_btn_template)

class ImageRecipesTable(RecipesTable):
    """ A subset of the recipes table which displayed just image recipes """

    def __init__(self, *args, **kwargs):
        super(ImageRecipesTable, self).__init__(*args, **kwargs)
        self.title = "Compatible image recipes"

    def setup_queryset(self, *args, **kwargs):
        super(ImageRecipesTable, self).setup_queryset(*args, **kwargs)

        self.queryset = self.queryset.filter(is_image=True)


    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Image recipe",
                        help_text="When you build an image recipe, you get an "
                                  "image: a root file system you can"
                                  "deploy to a machine",
                        hideable=False,
                        orderable=True,
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

        self.queryset = self.queryset.filter(is_image=True)

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Image recipe",
                        help_text="When you build an image recipe, you get an "
                                  "image: a root file system you can"
                                  "deploy to a machine",
                        hideable=False,
                        orderable=True,
                        field_name="recipe__name")

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

    def setup_queryset(self, *args, **kwargs):
        super(SoftwareRecipesTable, self).setup_queryset(*args, **kwargs)

        self.queryset = self.queryset.filter(is_image=False)


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


class SelectPackagesTable(ToasterTable):
    """ Table to display the packages to add and remove from an image """

    def __init__(self, *args, **kwargs):
        super(SelectPackagesTable, self).__init__(*args, **kwargs)
        self.title = "Add | Remove packages"

    def setup_queryset(self, *args, **kwargs):
        cust_recipe = CustomImageRecipe.objects.get(pk=kwargs['recipeid'])
        prj = Project.objects.get(pk = kwargs['pid'])

        current_packages = cust_recipe.packages.all()

        # Get all the packages that are in the custom image
        # Get all the packages built by builds in the current project
        # but not those ones that are already in the custom image
        self.queryset = Package.objects.filter(
                            Q(pk__in=current_packages) |
                            (Q(build__project=prj) &
                            ~Q(name__in=current_packages.values_list('name'))))

        self.queryset = self.queryset.order_by('name')

        self.static_context_extra['recipe_id'] = kwargs['recipeid']
        self.static_context_extra['current_packages'] = \
                cust_recipe.packages.values_list('pk', flat=True)

    def setup_columns(self, *args, **kwargs):
        self.add_column(title="Package",
                        hideable=False,
                        orderable=True,
                        field_name="name")

        self.add_column(title="Package Version",
                        field_name="version")

        self.add_column(title="Approx Size",
                        orderable=True,
                        static_data_name="size",
                        static_data_template="{% load projecttags %} \
                        {{data.size|filtered_filesizeformat}}")
        self.add_column(title="summary",
                        field_name="summary")

        self.add_column(title="Add | Remove",
                        help_text="Use the add and remove buttons to modify "
                        "the package content of you custom image",
                        static_data_name="add_rm_pkg_btn",
                        static_data_template='{% include "pkg_add_rm_btn.html" %}')
