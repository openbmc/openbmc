#
# BitBake Toaster Implementation
#
# Copyright (C) 2016        Intel Corporation
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

# Please run flake8 on this file before sending patches

import os
import re
import logging
import json
from collections import Counter

from orm.models import Project, ProjectTarget, Build, Layer_Version
from orm.models import LayerVersionDependency, LayerSource, ProjectLayer
from orm.models import Recipe, CustomImageRecipe, CustomImagePackage
from orm.models import Layer, Target, Package, Package_Dependency
from orm.models import ProjectVariable
from bldcontrol.models import BuildRequest, BuildEnvironment
from bldcontrol import bbcontroller

from django.http import HttpResponse, JsonResponse
from django.views.generic import View
from django.core.urlresolvers import reverse
from django.db.models import Q, F
from django.db import Error
from toastergui.templatetags.projecttags import filtered_filesizeformat

logger = logging.getLogger("toaster")


def error_response(error):
    return JsonResponse({"error": error})


class XhrBuildRequest(View):

    def get(self, request, *args, **kwargs):
        return HttpResponse()

    @staticmethod
    def cancel_build(br):
        """Cancel a build request"""
        try:
            bbctrl = bbcontroller.BitbakeController(br.environment)
            bbctrl.forceShutDown()
        except:
            # We catch a bunch of exceptions here because
            # this is where the server has not had time to start up
            # and the build request or build is in transit between
            # processes.
            # We can safely just set the build as cancelled
            # already as it never got started
            build = br.build
            build.outcome = Build.CANCELLED
            build.save()

        # We now hand over to the buildinfohelper to update the
        # build state once we've finished cancelling
        br.state = BuildRequest.REQ_CANCELLING
        br.save()

    def post(self, request, *args, **kwargs):
        """
          Build control

          Entry point: /xhr_buildrequest/<project_id>
          Method: POST

          Args:
              id: id of build to change
              buildCancel = build_request_id ...
              buildDelete = id ...
              targets = recipe_name ...

          Returns:
              {"error": "ok"}
            or
              {"error": <error message>}
        """

        project = Project.objects.get(pk=kwargs['pid'])

        if 'buildCancel' in request.POST:
            for i in request.POST['buildCancel'].strip().split(" "):
                try:
                    br = BuildRequest.objects.get(project=project, pk=i)
                    self.cancel_build(br)
                except BuildRequest.DoesNotExist:
                    return error_response('No such build request id %s' % i)

            return error_response('ok')

        if 'buildDelete' in request.POST:
            for i in request.POST['buildDelete'].strip().split(" "):
                try:
                    BuildRequest.objects.select_for_update().get(
                        project=project,
                        pk=i,
                        state__lte=BuildRequest.REQ_DELETED).delete()

                except BuildRequest.DoesNotExist:
                    pass
            return error_response("ok")

        if 'targets' in request.POST:
            ProjectTarget.objects.filter(project=project).delete()
            s = str(request.POST['targets'])
            for t in re.sub(r'[;%|"]', '', s).split(" "):
                if ":" in t:
                    target, task = t.split(":")
                else:
                    target = t
                    task = ""
                ProjectTarget.objects.create(project=project,
                                             target=target,
                                             task=task)
            project.schedule_build()

            return error_response('ok')

        response = HttpResponse()
        response.status_code = 500
        return response


class XhrLayer(View):
    """ Delete, Get, Add and Update Layer information

        Methods: GET POST DELETE PUT
    """

    def get(self, request, *args, **kwargs):
        """
        Get layer information

        Method: GET
        Entry point: /xhr_layer/<project id>/<layerversion_id>
        """

        try:
            layer_version = Layer_Version.objects.get(
                pk=kwargs['layerversion_id'])

            project = Project.objects.get(pk=kwargs['pid'])

            project_layers = ProjectLayer.objects.filter(
                project=project).values_list("layercommit_id",
                                             flat=True)

            ret = {
                'error': 'ok',
                'id': layer_version.pk,
                'name': layer_version.layer.name,
                'layerdetailurl':
                layer_version.get_detailspage_url(project.pk),
                'vcs_ref': layer_version.get_vcs_reference(),
                'vcs_url': layer_version.layer.vcs_url,
                'local_source_dir': layer_version.layer.local_source_dir,
                'layerdeps': {
                    "list": [
                        {
                            "id": dep.id,
                            "name": dep.layer.name,
                            "layerdetailurl":
                            dep.get_detailspage_url(project.pk),
                            "vcs_url": dep.layer.vcs_url,
                            "vcs_reference": dep.get_vcs_reference()
                        }
                        for dep in layer_version.get_alldeps(project.id)]
                },
                'projectlayers': list(project_layers)
            }

            return JsonResponse(ret)
        except Layer_Version.DoesNotExist:
            error_response("No such layer")

    def post(self, request, *args, **kwargs):
        """
          Update a layer

          Method: POST
          Entry point: /xhr_layer/<layerversion_id>

          Args:
              vcs_url, dirpath, commit, up_branch, summary, description,
              local_source_dir

              add_dep = append a layerversion_id as a dependency
              rm_dep = remove a layerversion_id as a depedency
          Returns:
              {"error": "ok"}
            or
              {"error": <error message>}
        """

        try:
            # We currently only allow Imported layers to be edited
            layer_version = Layer_Version.objects.get(
                id=kwargs['layerversion_id'],
                project=kwargs['pid'],
                layer_source=LayerSource.TYPE_IMPORTED)

        except Layer_Version.DoesNotExist:
            return error_response("Cannot find imported layer to update")

        if "vcs_url" in request.POST:
            layer_version.layer.vcs_url = request.POST["vcs_url"]
        if "dirpath" in request.POST:
            layer_version.dirpath = request.POST["dirpath"]
        if "commit" in request.POST:
            layer_version.commit = request.POST["commit"]
            layer_version.branch = request.POST["commit"]
        if "summary" in request.POST:
            layer_version.layer.summary = request.POST["summary"]
        if "description" in request.POST:
            layer_version.layer.description = request.POST["description"]
        if "local_source_dir" in request.POST:
            layer_version.layer.local_source_dir = \
                request.POST["local_source_dir"]

        if "add_dep" in request.POST:
            lvd = LayerVersionDependency(
                layer_version=layer_version,
                depends_on_id=request.POST["add_dep"])
            lvd.save()

        if "rm_dep" in request.POST:
            rm_dep = LayerVersionDependency.objects.get(
                layer_version=layer_version,
                depends_on_id=request.POST["rm_dep"])
            rm_dep.delete()

        try:
            layer_version.layer.save()
            layer_version.save()
        except Exception as e:
            return error_response("Could not update layer version entry: %s"
                                  % e)

        return error_response("ok")

    def put(self, request, *args, **kwargs):
        """ Add a new layer

        Method: PUT
        Entry point: /xhr_layer/<project id>/
        Args:
            project_id, name,
            [vcs_url, dir_path, git_ref], [local_source_dir], [layer_deps
            (csv)]

        """
        try:
            project = Project.objects.get(pk=kwargs['pid'])

            layer_data = json.loads(request.body.decode('utf-8'))

            # We require a unique layer name as otherwise the lists of layers
            # becomes very confusing
            existing_layers = \
                project.get_all_compatible_layer_versions().values_list(
                    "layer__name",
                    flat=True)

            add_to_project = False
            layer_deps_added = []
            if 'add_to_project' in layer_data:
                add_to_project = True

            if layer_data['name'] in existing_layers:
                return JsonResponse({"error": "layer-name-exists"})

            layer = Layer.objects.create(name=layer_data['name'])

            layer_version = Layer_Version.objects.create(
                layer=layer,
                project=project,
                layer_source=LayerSource.TYPE_IMPORTED)

            # Local layer
            if ('local_source_dir' in layer_data) and layer.local_source_dir:
                layer.local_source_dir = layer_data['local_source_dir']
            # git layer
            elif 'vcs_url' in layer_data:
                layer.vcs_url = layer_data['vcs_url']
                layer_version.dirpath = layer_data['dir_path']
                layer_version.commit = layer_data['git_ref']
                layer_version.branch = layer_data['git_ref']

            layer.save()
            layer_version.save()

            if add_to_project:
                ProjectLayer.objects.get_or_create(
                    layercommit=layer_version, project=project)

            # Add the layer dependencies
            if 'layer_deps' in layer_data:
                for layer_dep_id in layer_data['layer_deps'].split(","):
                    layer_dep = Layer_Version.objects.get(pk=layer_dep_id)
                    LayerVersionDependency.objects.get_or_create(
                        layer_version=layer_version, depends_on=layer_dep)

                    # Add layer deps to the project if specified
                    if add_to_project:
                        created, pl = ProjectLayer.objects.get_or_create(
                            layercommit=layer_dep, project=project)
                        layer_deps_added.append(
                            {'name': layer_dep.layer.name,
                             'layerdetailurl':
                             layer_dep.get_detailspage_url(project.pk)})

        except Layer_Version.DoesNotExist:
            return error_response("layer-dep-not-found")
        except Project.DoesNotExist:
            return error_response("project-not-found")
        except KeyError:
            return error_response("incorrect-parameters")

        return JsonResponse({'error': "ok",
                             'imported_layer': {
                                 'name': layer.name,
                                 'layerdetailurl':
                                 layer_version.get_detailspage_url()},
                             'deps_added': layer_deps_added})

    def delete(self, request, *args, **kwargs):
        """ Delete an imported layer

        Method: DELETE
        Entry point: /xhr_layer/<projed id>/<layerversion_id>

        """
        try:
            # We currently only allow Imported layers to be deleted
            layer_version = Layer_Version.objects.get(
                id=kwargs['layerversion_id'],
                project=kwargs['pid'],
                layer_source=LayerSource.TYPE_IMPORTED)
        except Layer_Version.DoesNotExist:
            return error_response("Cannot find imported layer to delete")

        try:
            ProjectLayer.objects.get(project=kwargs['pid'],
                                     layercommit=layer_version).delete()
        except ProjectLayer.DoesNotExist:
            pass

        layer_version.layer.delete()
        layer_version.delete()

        return JsonResponse({
            "error": "ok",
            "gotoUrl": reverse('projectlayers', args=(kwargs['pid'],))
        })


class XhrCustomRecipe(View):
    """ Create a custom image recipe """

    def post(self, request, *args, **kwargs):
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
                return error_response("Missing parameter '%s'" % param)

        # get project and baserecipe objects
        params = {}
        for name, model in [("project", Project),
                            ("base", Recipe)]:
            value = request.POST[name]
            try:
                params[name] = model.objects.get(id=value)
            except model.DoesNotExist:
                return error_response("Invalid %s id %s" % (name, value))

        # create custom recipe
        try:

            # Only allowed chars in name are a-z, 0-9 and -
            if re.search(r'[^a-z|0-9|-]', request.POST["name"]):
                return error_response("invalid-name")

            custom_images = CustomImageRecipe.objects.all()

            # Are there any recipes with this name already in our project?
            existing_image_recipes_in_project = custom_images.filter(
                name=request.POST["name"], project=params["project"])

            if existing_image_recipes_in_project.count() > 0:
                return error_response("image-already-exists")

            # Are there any recipes with this name which aren't custom
            # image recipes?
            custom_image_ids = custom_images.values_list('id', flat=True)
            existing_non_image_recipes = Recipe.objects.filter(
                Q(name=request.POST["name"]) & ~Q(pk__in=custom_image_ids)
            )

            if existing_non_image_recipes.count() > 0:
                return error_response("recipe-already-exists")

            # create layer 'Custom layer' and verion if needed
            layer, l_created = Layer.objects.get_or_create(
                name=CustomImageRecipe.LAYER_NAME,
                summary="Layer for custom recipes")

            if l_created:
                layer.local_source_dir = "toaster_created_layer"
                layer.save()

            # Check if we have a layer version already
            # We don't use get_or_create here because the dirpath will change
            # and is a required field
            lver = Layer_Version.objects.filter(Q(project=params['project']) &
                                                Q(layer=layer) &
                                                Q(build=None)).last()
            if lver is None:
                lver, lv_created = Layer_Version.objects.get_or_create(
                    project=params['project'],
                    layer=layer,
                    layer_source=LayerSource.TYPE_LOCAL,
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
            recipe, r_created = CustomImageRecipe.objects.get_or_create(
                name=request.POST["name"],
                base_recipe=params["base"],
                project=params["project"],
                layer_version=lver,
                is_image=True)

            # If we created the object then setup these fields. They may get
            # overwritten later on and cause the get_or_create to create a
            # duplicate if they've changed.
            if r_created:
                recipe.file_path = request.POST["name"]
                recipe.license = "MIT"
                recipe.version = "0.1"
                recipe.save()

        except Error as err:
            return error_response("Can't create custom recipe: %s" % err)

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
                    if built_package.recipe is None:
                        continue

                    config_package = CustomImagePackage.objects.get(
                        name=built_package.name)

                    recipe.includes_set.add(config_package)
                except Exception as e:
                    logger.warning("Error adding package %s %s" %
                                   (tpackage.package.name, e))
                    pass

        # pre-create layer directory structure, so that other builds
        # are not blocked by this new recipe dependecy
        # NOTE: this is parallel code to 'localhostbecontroller.py'
        be = BuildEnvironment.objects.all()[0]
        layerpath = os.path.join(be.builddir,
                                 CustomImageRecipe.LAYER_NAME)
        for name in ("conf", "recipes"):
            path = os.path.join(layerpath, name)
            if not os.path.isdir(path):
                os.makedirs(path)
        # pre-create layer.conf
        config = os.path.join(layerpath, "conf", "layer.conf")
        if not os.path.isfile(config):
            with open(config, "w") as conf:
                conf.write('BBPATH .= ":${LAYERDIR}"\nBBFILES += "${LAYERDIR}/recipes/*.bb"\n')
        # pre-create new image's recipe file
        recipe_path = os.path.join(layerpath, "recipes", "%s.bb" %
                                   recipe.name)
        with open(recipe_path, "w") as recipef:
            recipef.write(recipe.generate_recipe_file_contents())

        return JsonResponse(
            {"error": "ok",
             "packages": recipe.get_all_packages().count(),
             "url": reverse('customrecipe', args=(params['project'].pk,
                                                  recipe.id))})


class XhrCustomRecipeId(View):
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
    @staticmethod
    def _get_ci_recipe(recipe_id):
        """ Get Custom Image recipe or return an error response"""
        try:
            custom_recipe = \
                CustomImageRecipe.objects.get(pk=recipe_id)
            return custom_recipe, None

        except CustomImageRecipe.DoesNotExist:
            return None, error_response("Custom recipe with id=%s "
                                        "not found" % recipe_id)

    def get(self, request, *args, **kwargs):
        custom_recipe, error = self._get_ci_recipe(kwargs['recipe_id'])
        if error:
            return error

        if request.method == 'GET':
            info = {"id": custom_recipe.id,
                    "name": custom_recipe.name,
                    "base_recipe_id": custom_recipe.base_recipe.id,
                    "project_id": custom_recipe.project.id}

            return JsonResponse({"error": "ok", "info": info})

    def delete(self, request, *args, **kwargs):
        custom_recipe, error = self._get_ci_recipe(kwargs['recipe_id'])
        if error:
            return error

        project = custom_recipe.project

        custom_recipe.delete()
        return JsonResponse({"error": "ok",
                             "gotoUrl": reverse("projectcustomimages",
                                                args=(project.pk,))})


class XhrCustomRecipePackages(View):
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
    @staticmethod
    def _get_package(package_id):
        try:
            package = CustomImagePackage.objects.get(pk=package_id)
            return package, None
        except Package.DoesNotExist:
            return None, error_response("Package with id=%s "
                                        "not found" % package_id)

    def _traverse_dependents(self, next_package_id,
                             rev_deps, all_current_packages, tree_level=0):
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
            if (self._traverse_dependents(pkg["pk"], rev_deps,
                                          all_current_packages,
                                          tree_level+1)):
                return True

        return False

    def _get_all_dependents(self, package_id, all_current_packages):
        """
        Returns sorted list of recursive reverse dependencies for package_id,
        as a list of dictionary items, by recursing through dependency
        relationships.
        """
        rev_deps = []
        self._traverse_dependents(package_id, rev_deps, all_current_packages)
        rev_deps = sorted(rev_deps, key=lambda x: x["name"])
        return rev_deps

    def get(self, request, *args, **kwargs):
        recipe, error = XhrCustomRecipeId._get_ci_recipe(
            kwargs['recipe_id'])
        if error:
            return error

        # If no package_id then list all the current packages
        if not kwargs['package_id']:
            total_size = 0
            packages = recipe.get_all_packages().values("id",
                                                        "name",
                                                        "version",
                                                        "size")
            for package in packages:
                package['size_formatted'] = \
                    filtered_filesizeformat(package['size'])
                total_size += package['size']

            return JsonResponse({"error": "ok",
                                 "packages": list(packages),
                                 "total": len(packages),
                                 "total_size": total_size,
                                 "total_size_formatted":
                                 filtered_filesizeformat(total_size)})
        else:
            package, error = XhrCustomRecipePackages._get_package(
                kwargs['package_id'])
            if error:
                return error

            all_current_packages = recipe.get_all_packages()

            # Dependencies for package which aren't satisfied by the
            # current packages in the custom image recipe
            deps = package.package_dependencies_source.for_target_or_none(
                recipe.name)['packages'].annotate(
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
            reverse_deps = self._get_all_dependents(kwargs['package_id'],
                                                    all_current_packages)
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

            return JsonResponse(
                {"error": "ok",
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
                 filtered_filesizeformat(total_size_reverse_deps)})

    def put(self, request, *args, **kwargs):
        recipe, error = XhrCustomRecipeId._get_ci_recipe(kwargs['recipe_id'])
        package, error = self._get_package(kwargs['package_id'])
        if error:
            return error

        included_packages = recipe.includes_set.values_list('pk',
                                                            flat=True)

        # If we're adding back a package which used to be included in this
        # image all we need to do is remove it from the excludes
        if package.pk in included_packages:
            try:
                recipe.excludes_set.remove(package)
                return {"error": "ok"}
            except Package.DoesNotExist:
                return error_response("Package %s not found in excludes"
                                      " but was in included list" %
                                      package.name)
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
                except package.DoesNotExist:
                    # Don't care if the package had never been excluded
                    pass
            except:
                logger.warning("Could not add package's suggested"
                               "dependencies to the list")
        return JsonResponse({"error": "ok"})

    def delete(self, request, *args, **kwargs):
        recipe, error = XhrCustomRecipeId._get_ci_recipe(kwargs['recipe_id'])
        package, error = self._get_package(kwargs['package_id'])
        if error:
            return error

        try:
            included_packages = recipe.includes_set.values_list('pk',
                                                                flat=True)
            # If we're deleting a package which is included we need to
            # Add it to the excludes list.
            if package.pk in included_packages:
                recipe.excludes_set.add(package)
            else:
                recipe.appends_set.remove(package)

            # remove dependencies as well
            all_current_packages = recipe.get_all_packages()

            reverse_deps_dictlist = self._get_all_dependents(
                package.pk,
                all_current_packages)

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

            return JsonResponse({"error": "ok"})
        except CustomImageRecipe.DoesNotExist:
            return error_response("Tried to remove package that wasn't"
                                  " present")


class XhrProject(View):
    """ Create, delete or edit a project

    Entry point: /xhr_project/<project_id>
    """
    def post(self, request, *args, **kwargs):
        """
          Edit project control

          Args:
              layerAdd = layer_version_id layer_version_id ...
              layerDel = layer_version_id layer_version_id ...
              projectName = new_project_name
              machineName = new_machine_name

          Returns:
              {"error": "ok"}
            or
              {"error": <error message>}
        """
        try:
            prj = Project.objects.get(pk=kwargs['project_id'])
        except Project.DoesNotExist:
            return error_response("No such project")

        # Add layers
        if 'layerAdd' in request.POST and len(request.POST['layerAdd']) > 0:
            for layer_version_id in request.POST['layerAdd'].split(','):
                try:
                    lv = Layer_Version.objects.get(pk=int(layer_version_id))
                    ProjectLayer.objects.get_or_create(project=prj,
                                                       layercommit=lv)
                except Layer_Version.DoesNotExist:
                    return error_response("Layer version %s asked to add "
                                          "doesn't exist" % layer_version_id)

        # Remove layers
        if 'layerDel' in request.POST and len(request.POST['layerDel']) > 0:
            layer_version_ids = request.POST['layerDel'].split(',')
            ProjectLayer.objects.filter(
                project=prj,
                layercommit_id__in=layer_version_ids).delete()

        # Project name change
        if 'projectName' in request.POST:
            prj.name = request.POST['projectName']
            prj.save()

        # Machine name change
        if 'machineName' in request.POST:
            machinevar = prj.projectvariable_set.get(name="MACHINE")
            machinevar.value = request.POST['machineName']
            machinevar.save()

        # Distro name change
        if 'distroName' in request.POST:
            distrovar = prj.projectvariable_set.get(name="DISTRO")
            distrovar.value = request.POST['distroName']
            distrovar.save()

        return JsonResponse({"error": "ok"})

    def get(self, request, *args, **kwargs):
        """
        Returns:
            json object representing the current project
        or:
            {"error": <error message>}
        """

        try:
            project = Project.objects.get(pk=kwargs['project_id'])
        except Project.DoesNotExist:
            return error_response("Project %s does not exist" %
                                  kwargs['project_id'])

        # Create the frequently built targets list

        freqtargets = Counter(Target.objects.filter(
            Q(build__project=project),
            ~Q(build__outcome=Build.IN_PROGRESS)
        ).order_by("target").values_list("target", flat=True))

        freqtargets = freqtargets.most_common(5)

        # We now have the targets in order of frequency but if there are two
        # with the same frequency then we need to make sure those are in
        # alphabetical order without losing the frequency ordering

        tmp = []
        switch = None
        for i, freqtartget in enumerate(freqtargets):
            target, count = freqtartget
            try:
                target_next, count_next = freqtargets[i+1]
                if count == count_next and target > target_next:
                    switch = target
                    continue
            except IndexError:
                pass

            tmp.append(target)

            if switch:
                tmp.append(switch)
                switch = None

        freqtargets = tmp

        layers = []
        for layer in project.projectlayer_set.all():
            layers.append({
                "id": layer.layercommit.pk,
                "name": layer.layercommit.layer.name,
                "vcs_url": layer.layercommit.layer.vcs_url,
                "local_source_dir": layer.layercommit.layer.local_source_dir,
                "vcs_reference": layer.layercommit.get_vcs_reference(),
                "url": layer.layercommit.layer.layer_index_url,
                "layerdetailurl": layer.layercommit.get_detailspage_url(
                    project.pk),
                "xhrLayerUrl": reverse("xhr_layer",
                                       args=(project.pk,
                                             layer.layercommit.pk)),
                "layersource": layer.layercommit.layer_source
            })

        data = {
            "name": project.name,
            "layers": layers,
            "freqtargets": freqtargets,
        }

        if project.release is not None:
            data['release'] = {
                "id": project.release.pk,
                "name": project.release.name,
                "description": project.release.description
            }

        try:
            data["machine"] = {"name":
                               project.projectvariable_set.get(
                                   name="MACHINE").value}
        except ProjectVariable.DoesNotExist:
            data["machine"] = None
        try:
            data["distro"] = {"name":
                               project.projectvariable_set.get(
                                   name="DISTRO").value}
        except ProjectVariable.DoesNotExist:
            data["distro"] = None

        data['error'] = "ok"

        return JsonResponse(data)

    def put(self, request, *args, **kwargs):
        # TODO create new project api
        return HttpResponse()

    def delete(self, request, *args, **kwargs):
        """Delete a project. Cancels any builds in progress"""
        try:
            project = Project.objects.get(pk=kwargs['project_id'])
            # Cancel any builds in progress
            for br in BuildRequest.objects.filter(
                    project=project,
                    state=BuildRequest.REQ_INPROGRESS):
                XhrBuildRequest.cancel_build(br)

            project.delete()

        except Project.DoesNotExist:
            return error_response("Project %s does not exist" %
                                  kwargs['project_id'])

        return JsonResponse({
            "error": "ok",
            "gotoUrl": reverse("all-projects", args=[])
        })


class XhrBuild(View):
    """ Delete a build object

    Entry point: /xhr_build/<build_id>
    """
    def delete(self, request, *args, **kwargs):
        """
          Delete build data

          Args:
              build_id = build_id

          Returns:
              {"error": "ok"}
            or
              {"error": <error message>}
        """
        try:
            build = Build.objects.get(pk=kwargs['build_id'])
            project = build.project
            build.delete()
        except Build.DoesNotExist:
            return error_response("Build %s does not exist" %
                                  kwargs['build_id'])
        return JsonResponse({
            "error": "ok",
            "gotoUrl": reverse("projectbuilds", args=(project.pk,))
        })
