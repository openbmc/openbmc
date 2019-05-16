#
# BitBake Toaster Implementation
#
# Copyright (C) 2015        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import subprocess

from toastergui.widgets import ToasterTypeAhead
from orm.models import Project
from django.core.urlresolvers import reverse
from django.core.cache import cache


class LayersTypeAhead(ToasterTypeAhead):
    """ Typeahead for layers available and not added in the current project's
    configuration """

    def apply_search(self, search_term, prj, request):
        layers = prj.get_all_compatible_layer_versions()
        layers = layers.order_by('layer__name')

        # Unlike the other typeaheads we also don't want to show suggestions
        # for layers already in the project unless required such as when adding
        # layerdeps to a new layer.
        if "include_added" in request.GET and \
           request.GET['include_added'] != "true":
            layers = layers.exclude(
                pk__in=prj.get_project_layer_versions(pk=True))

        primary_results = layers.filter(layer__name__istartswith=search_term)
        secondary_results = layers.filter(
            layer__name__icontains=search_term).exclude(
                pk__in=primary_results)

        results = []

        for layer_version in list(primary_results) + list(secondary_results):
            vcs_reference = layer_version.get_vcs_reference()

            detail = "[ %s | %s ]" % (layer_version.layer.vcs_url,
                                      vcs_reference)
            needed_fields = {
                'id': layer_version.pk,
                'name': layer_version.layer.name,
                'layerdetailurl': layer_version.get_detailspage_url(prj.pk),
                'xhrLayerUrl': reverse('xhr_layer',
                                       args=(prj.pk, layer_version.pk)),
                'vcs_url': layer_version.layer.vcs_url,
                'vcs_reference': vcs_reference,
                'detail': detail,
                'local_source_dir': layer_version.layer.local_source_dir,
            }

            results.append(needed_fields)

        return results


class MachinesTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the machines available in the current project's
    configuration """

    def apply_search(self, search_term, prj, request):
        machines = prj.get_available_machines()
        machines = machines.order_by("name")

        primary_results = machines.filter(name__istartswith=search_term)
        secondary_results = machines.filter(
            name__icontains=search_term).exclude(pk__in=primary_results)
        tertiary_results = machines.filter(
            layer_version__layer__name__icontains=search_term).exclude(
                pk__in=primary_results).exclude(pk__in=secondary_results)

        results = []

        for machine in list(primary_results) + list(secondary_results) + \
                list(tertiary_results):

            detail = "[ %s ]" % (machine.layer_version.layer.name)
            needed_fields = {
                'id': machine.pk,
                'name': machine.name,
                'detail': detail,
            }

            results.append(needed_fields)
        return results


class DistrosTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the distros available in the current project's
    configuration """
    def __init__(self):
        super(DistrosTypeAhead, self).__init__()

    def apply_search(self, search_term, prj, request):
        distros = prj.get_available_distros()
        distros = distros.order_by("name")

        primary_results = distros.filter(name__istartswith=search_term)
        secondary_results = distros.filter(name__icontains=search_term).exclude(pk__in=primary_results)
        tertiary_results = distros.filter(layer_version__layer__name__icontains=search_term).exclude(pk__in=primary_results).exclude(pk__in=secondary_results)

        results = []

        for distro in list(primary_results) + list(secondary_results) + list(tertiary_results):

            detail = "[ %s ]" % (distro.layer_version.layer.name)
            needed_fields = {
                'id' : distro.pk,
                'name' : distro.name,
                'detail' : detail,
            }

            results.append(needed_fields)

        return results


class RecipesTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the recipes available in the current project's
    configuration """
    def apply_search(self, search_term, prj, request):
        recipes = prj.get_available_recipes()
        recipes = recipes.order_by("name")

        primary_results = recipes.filter(name__istartswith=search_term)
        secondary_results = recipes.filter(
            name__icontains=search_term).exclude(pk__in=primary_results)
        tertiary_results = recipes.filter(
            layer_version__layer__name__icontains=search_term).exclude(
                pk__in=primary_results).exclude(pk__in=secondary_results)

        results = []

        for recipe in list(primary_results) + list(secondary_results) + \
                list(tertiary_results):

            detail = "[ %s ]" % (recipe.layer_version.layer.name)
            needed_fields = {
                'id': recipe.pk,
                'name': recipe.name,
                'detail': detail,
            }

            results.append(needed_fields)

        return results


class ProjectsTypeAhead(ToasterTypeAhead):
    """ Typeahead for all the projects, except for command line builds """
    def apply_search(self, search_term, prj, request):
        projects = Project.objects.exclude(is_default=True).order_by("name")

        primary_results = projects.filter(name__istartswith=search_term)
        secondary_results = projects.filter(
            name__icontains=search_term).exclude(pk__in=primary_results)

        results = []

        for project in list(primary_results) + list(secondary_results):
            needed_fields = {
                'id': project.pk,
                'name': project.name,
                'detail': "",
                'projectPageUrl': reverse('project', args=(project.pk,))
            }

            results.append(needed_fields)

        return results


class GitRevisionTypeAhead(ToasterTypeAhead):
    def apply_search(self, search_term, prj, request):
        results = []
        git_url = request.GET.get('git_url')
        ls_remote = cache.get(git_url)

        if ls_remote is None:
            ls_remote = subprocess.check_output(['git', 'ls-remote', git_url],
                                                universal_newlines=True)
            ls_remote = ls_remote.splitlines()
            # Avoid fetching the list of git refs on each new input
            cache.set(git_url, ls_remote, 120)

        for rev in ls_remote:
            git_rev = str(rev).split("/")[-1:][0]
            # "HEAD" has a special meaning in Toaster...  YOCTO #9924
            if "HEAD" in git_rev:
                continue

            if git_rev.startswith(search_term):
                results.append({'name': git_rev,
                                'detail': '[ %s ]' % str(rev)})

        return results
