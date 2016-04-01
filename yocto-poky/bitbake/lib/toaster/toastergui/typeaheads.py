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

from toastergui.widgets import ToasterTypeAhead
from orm.models import Project
from django.core.urlresolvers import reverse

class LayersTypeAhead(ToasterTypeAhead):
    """ Typeahead for layers available and not added in the current project's
    configuration """
    def __init__(self):
      super(LayersTypeAhead, self).__init__()

    def apply_search(self, search_term, prj, request):
        layers = prj.get_all_compatible_layer_versions()
        layers = layers.order_by('layer__name')

        # Unlike the other typeaheads we also don't want to show suggestions
        # for layers already in the project unless required such as when adding
        # layerdeps to a new layer.
        if ("include_added" in request.GET and
                request.GET['include_added'] != "true"):
            layers = layers.exclude(
                pk__in=prj.get_project_layer_versions(pk=True))

        primary_results = layers.filter(layer__name__istartswith=search_term)
        secondary_results = layers.filter(layer__name__icontains=search_term).exclude(pk__in=primary_results)

        results = []

        for layer_version in list(primary_results) + list(secondary_results):
            vcs_reference = layer_version.get_vcs_reference()

            detail = "[ %s | %s ]" % (layer_version.layer.vcs_url,
                                      vcs_reference)
            needed_fields = {
                'id' : layer_version.pk,
                'name' : layer_version.layer.name,
                'layerdetailurl' : layer_version.get_detailspage_url(prj.pk),
                'vcs_url' : layer_version.layer.vcs_url,
                'vcs_reference' : vcs_reference,
                'detail' : detail,
            }

            results.append(needed_fields)

        return results

class MachinesTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the machines available in the current project's
    configuration """
    def __init__(self):
        super(MachinesTypeAhead, self).__init__()

    def apply_search(self, search_term, prj, request):
        machines = prj.get_available_machines()
        machines = machines.order_by("name")

        primary_results = machines.filter(name__istartswith=search_term)
        secondary_results = machines.filter(name__icontains=search_term).exclude(pk__in=primary_results)
        tertiary_results = machines.filter(layer_version__layer__name__icontains=search_term).exclude(pk__in=primary_results).exclude(pk__in=secondary_results)

        results = []

        for machine in list(primary_results) + list(secondary_results) + list(tertiary_results):

            detail = "[ %s ]" % (machine.layer_version.layer.name)
            needed_fields = {
                'id' : machine.pk,
                'name' : machine.name,
                'detail' : detail,
            }

            results.append(needed_fields)

        return results

class RecipesTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the recipes available in the current project's
    configuration """
    def __init__(self):
        super(RecipesTypeAhead, self).__init__()

    def apply_search(self, search_term, prj, request):
        recipes = prj.get_available_recipes()
        recipes = recipes.order_by("name")


        primary_results = recipes.filter(name__istartswith=search_term)
        secondary_results = recipes.filter(name__icontains=search_term).exclude(pk__in=primary_results)
        tertiary_results = recipes.filter(layer_version__layer__name__icontains=search_term).exclude(pk__in=primary_results).exclude(pk__in=secondary_results)

        results = []

        for recipe in list(primary_results) + list(secondary_results) + list(tertiary_results):

            detail = "[ %s ]" % (recipe.layer_version.layer.name)
            needed_fields = {
                'id' : recipe.pk,
                'name' : recipe.name,
                'detail' : detail,
            }

            results.append(needed_fields)

        return results

class ProjectsTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the projects, except for command line builds """
    def __init__(self):
        super(ProjectsTypeAhead, self).__init__()

    def apply_search(self, search_term, prj, request):
        projects = Project.objects.exclude(is_default=True).order_by("name")

        primary_results = projects.filter(name__istartswith=search_term)
        secondary_results = projects.filter(name__icontains=search_term).exclude(pk__in=primary_results)

        results = []

        for project in list(primary_results) + list(secondary_results):
            needed_fields = {
                'id' : project.pk,
                'name' : project.name,
                'detail' : "",
                'projectPageUrl' : reverse('project', args=(project.pk,))
            }

            results.append(needed_fields)

        return results
