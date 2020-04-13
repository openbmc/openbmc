#
# BitBake Toaster Implementation
#
# Copyright (C) 2016-2017   Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.core.management.base import BaseCommand

from orm.models import Layer, Release, Layer_Version
from orm.models import LayerVersionDependency, Machine, Recipe
from orm.models import Distro
from orm.models import ToasterSetting

import os
import sys

import logging
import threading
import time
logger = logging.getLogger("toaster")

DEFAULT_LAYERINDEX_SERVER = "http://layers.openembedded.org/layerindex/api/"

# Add path to bitbake modules for layerindexlib
# lib/toaster/orm/management/commands/lsupdates.py (abspath)
# lib/toaster/orm/management/commands (dirname)
# lib/toaster/orm/management (dirname)
# lib/toaster/orm (dirname)
# lib/toaster/ (dirname)
# lib/ (dirname)
path = os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))))
sys.path.insert(0, path)

import layerindexlib


class Spinner(threading.Thread):
    """ A simple progress spinner to indicate download/parsing is happening"""
    def __init__(self, *args, **kwargs):
        super(Spinner, self).__init__(*args, **kwargs)
        self.setDaemon(True)
        self.signal = True

    def run(self):
        os.system('setterm -cursor off')
        while self.signal:
            for char in ["/", "-", "\\", "|"]:
                sys.stdout.write("\r" + char)
                sys.stdout.flush()
                time.sleep(0.25)
        os.system('setterm -cursor on')

    def stop(self):
        self.signal = False


class Command(BaseCommand):
    args = ""
    help = "Updates locally cached information from a layerindex server"

    def mini_progress(self, what, i, total):
        i = i + 1
        pec = (float(i)/float(total))*100

        sys.stdout.write("\rUpdating %s %d%%" %
                         (what,
                          pec))
        sys.stdout.flush()
        if int(pec) is 100:
            sys.stdout.write("\n")
            sys.stdout.flush()

    def update(self):
        """
            Fetches layer, recipe and machine information from a layerindex
            server
        """
        os.system('setterm -cursor off')

        self.apiurl = DEFAULT_LAYERINDEX_SERVER
        if ToasterSetting.objects.filter(name='CUSTOM_LAYERINDEX_SERVER').count() == 1:
            self.apiurl = ToasterSetting.objects.get(name = 'CUSTOM_LAYERINDEX_SERVER').value

        assert self.apiurl is not None

        # update branches; only those that we already have names listed in the
        # Releases table
        whitelist_branch_names = [rel.branch_name
                                  for rel in Release.objects.all()]
        if len(whitelist_branch_names) == 0:
            raise Exception("Failed to make list of branches to fetch")

        logger.info("Fetching metadata for %s",
                    " ".join(whitelist_branch_names))

        # We require a non-empty bb.data, but we can fake it with a dictionary
        layerindex = layerindexlib.LayerIndex({"DUMMY" : "VALUE"})

        http_progress = Spinner()
        http_progress.start()

        if whitelist_branch_names:
            url_branches = ";branch=%s" % ','.join(whitelist_branch_names)
        else:
            url_branches = ""
        layerindex.load_layerindex("%s%s" % (self.apiurl, url_branches))

        http_progress.stop()

        # We know we're only processing one entry, so we reference it here
        # (this is cheating...)
        index = layerindex.indexes[0]

        # Map the layer index branches to toaster releases
        li_branch_id_to_toaster_release = {}

        logger.info("Processing releases")

        total = len(index.branches)
        for i, id in enumerate(index.branches):
            li_branch_id_to_toaster_release[id] = \
                    Release.objects.get(name=index.branches[id].name)
            self.mini_progress("Releases", i, total)

        # keep a track of the layerindex (li) id mappings so that
        # layer_versions can be created for these layers later on
        li_layer_id_to_toaster_layer_id = {}

        logger.info("Processing layers")

        total = len(index.layerItems)
        for i, id in enumerate(index.layerItems):
            try:
                l, created = Layer.objects.get_or_create(name=index.layerItems[id].name)
                l.up_date = index.layerItems[id].updated
                l.summary = index.layerItems[id].summary
                l.description = index.layerItems[id].description

                if created:
                    # predefined layers in the fixtures (for example poky.xml)
                    # always preempt the Layer Index for these values
                    l.vcs_url = index.layerItems[id].vcs_url
                    l.vcs_web_url = index.layerItems[id].vcs_web_url
                    l.vcs_web_tree_base_url = index.layerItems[id].vcs_web_tree_base_url
                    l.vcs_web_file_base_url = index.layerItems[id].vcs_web_file_base_url
                l.save()
            except Layer.MultipleObjectsReturned:
                logger.info("Skipped %s as we found multiple layers and "
                            "don't know which to update" %
                            index.layerItems[id].name)

            li_layer_id_to_toaster_layer_id[id] = l.pk

            self.mini_progress("layers", i, total)

        # update layer_versions
        logger.info("Processing layer versions")

        # Map Layer index layer_branch object id to
        # layer_version toaster object id
        li_layer_branch_id_to_toaster_lv_id = {}

        total = len(index.layerBranches)
        for i, id in enumerate(index.layerBranches):
            # release as defined by toaster map to layerindex branch
            release = li_branch_id_to_toaster_release[index.layerBranches[id].branch_id]

            try:
                lv, created = Layer_Version.objects.get_or_create(
                    layer=Layer.objects.get(
                        pk=li_layer_id_to_toaster_layer_id[index.layerBranches[id].layer_id]),
                    release=release
                )
            except KeyError:
                logger.warning(
                    "No such layerindex layer referenced by layerbranch %d" %
                    index.layerBranches[id].layer_id)
                continue

            if created:
                lv.release = li_branch_id_to_toaster_release[index.layerBranches[id].branch_id]
                lv.up_date = index.layerBranches[id].updated
                lv.commit = index.layerBranches[id].actual_branch
                lv.dirpath = index.layerBranches[id].vcs_subdir
                lv.save()

            li_layer_branch_id_to_toaster_lv_id[index.layerBranches[id].id] =\
                lv.pk
            self.mini_progress("layer versions", i, total)

        logger.info("Processing layer version dependencies")

        dependlist = {}
        for id in index.layerDependencies:
            try:
                lv = Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[index.layerDependencies[id].layerbranch_id])
            except Layer_Version.DoesNotExist as e:
                continue

            if lv not in dependlist:
                dependlist[lv] = []
            try:
                layer_id = li_layer_id_to_toaster_layer_id[index.layerDependencies[id].dependency_id]

                dependlist[lv].append(
                    Layer_Version.objects.get(layer__pk=layer_id,
                                              release=lv.release))

            except Layer_Version.DoesNotExist:
                logger.warning("Cannot find layer version (ls:%s),"
                               "up_id:%s lv:%s" %
                               (self, index.layerDependencies[id].dependency_id, lv))

        total = len(dependlist)
        for i, lv in enumerate(dependlist):
            LayerVersionDependency.objects.filter(layer_version=lv).delete()
            for lvd in dependlist[lv]:
                LayerVersionDependency.objects.get_or_create(layer_version=lv,
                                                             depends_on=lvd)
            self.mini_progress("Layer version dependencies", i, total)

        # update Distros
        logger.info("Processing distro information")

        total = len(index.distros)
        for i, id in enumerate(index.distros):
            distro, created = Distro.objects.get_or_create(
                name=index.distros[id].name,
                layer_version=Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[index.distros[id].layerbranch_id]))
            distro.up_date = index.distros[id].updated
            distro.name = index.distros[id].name
            distro.description = index.distros[id].description
            distro.save()
            self.mini_progress("distros", i, total)

        # update machines
        logger.info("Processing machine information")

        total = len(index.machines)
        for i, id in enumerate(index.machines):
            mo, created = Machine.objects.get_or_create(
                name=index.machines[id].name,
                layer_version=Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[index.machines[id].layerbranch_id]))
            mo.up_date = index.machines[id].updated
            mo.name = index.machines[id].name
            mo.description = index.machines[id].description
            mo.save()
            self.mini_progress("machines", i, total)

        # update recipes; paginate by layer version / layer branch
        logger.info("Processing recipe information")

        total = len(index.recipes)
        for i, id in enumerate(index.recipes):
            try:
                lv_id = li_layer_branch_id_to_toaster_lv_id[index.recipes[id].layerbranch_id]
                lv = Layer_Version.objects.get(pk=lv_id)

                ro, created = Recipe.objects.get_or_create(
                    layer_version=lv,
                    name=index.recipes[id].pn
                )

                ro.layer_version = lv
                ro.up_date = index.recipes[id].updated
                ro.name = index.recipes[id].pn
                ro.version = index.recipes[id].pv
                ro.summary = index.recipes[id].summary
                ro.description = index.recipes[id].description
                ro.section = index.recipes[id].section
                ro.license = index.recipes[id].license
                ro.homepage = index.recipes[id].homepage
                ro.bugtracker = index.recipes[id].bugtracker
                ro.file_path = index.recipes[id].fullpath
                ro.is_image = 'image' in index.recipes[id].inherits.split()
                ro.save()
            except Exception as e:
                logger.warning("Failed saving recipe %s", e)

            self.mini_progress("recipes", i, total)

        os.system('setterm -cursor on')

    def handle(self, **options):
        self.update()
