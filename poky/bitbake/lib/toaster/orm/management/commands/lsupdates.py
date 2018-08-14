#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016-2017   Intel Corporation
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

from django.core.management.base import BaseCommand

from orm.models import LayerSource, Layer, Release, Layer_Version
from orm.models import LayerVersionDependency, Machine, Recipe
from orm.models import Distro
from orm.models import ToasterSetting

import os
import sys

import json
import logging
import threading
import time
logger = logging.getLogger("toaster")

DEFAULT_LAYERINDEX_SERVER = "http://layers.openembedded.org/layerindex/api/"


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
        try:
            from urllib.request import urlopen, URLError
            from urllib.parse import urlparse
        except ImportError:
            from urllib2 import urlopen, URLError
            from urlparse import urlparse

        proxy_settings = os.environ.get("http_proxy", None)

        def _get_json_response(apiurl=None):
            if None == apiurl:
                apiurl=self.apiurl
            http_progress = Spinner()
            http_progress.start()

            _parsedurl = urlparse(apiurl)
            path = _parsedurl.path

            # logger.debug("Fetching %s", apiurl)
            try:
                res = urlopen(apiurl)
            except URLError as e:
                raise Exception("Failed to read %s: %s" % (path, e.reason))

            parsed = json.loads(res.read().decode('utf-8'))

            http_progress.stop()
            return parsed

        # verify we can get the basic api
        try:
            apilinks = _get_json_response()
        except Exception as e:
            import traceback
            if proxy_settings is not None:
                logger.info("EE: Using proxy %s" % proxy_settings)
            logger.warning("EE: could not connect to %s, skipping update:"
                           "%s\n%s" % (self.apiurl, e, traceback.format_exc()))
            return

        # update branches; only those that we already have names listed in the
        # Releases table
        whitelist_branch_names = [rel.branch_name
                                  for rel in Release.objects.all()]
        if len(whitelist_branch_names) == 0:
            raise Exception("Failed to make list of branches to fetch")

        logger.info("Fetching metadata releases for %s",
                    " ".join(whitelist_branch_names))

        branches_info = _get_json_response(apilinks['branches'] +
                                           "?filter=name:%s"
                                           % "OR".join(whitelist_branch_names))

        # Map the layer index branches to toaster releases
        li_branch_id_to_toaster_release = {}

        total = len(branches_info)
        for i, branch in enumerate(branches_info):
            li_branch_id_to_toaster_release[branch['id']] = \
                    Release.objects.get(name=branch['name'])
            self.mini_progress("Releases", i, total)

        # keep a track of the layerindex (li) id mappings so that
        # layer_versions can be created for these layers later on
        li_layer_id_to_toaster_layer_id = {}

        logger.info("Fetching layers")

        layers_info = _get_json_response(apilinks['layerItems'])

        total = len(layers_info)
        for i, li in enumerate(layers_info):
            try:
                l, created = Layer.objects.get_or_create(name=li['name'])
                l.up_date = li['updated']
                l.summary = li['summary']
                l.description = li['description']

                if created:
                    # predefined layers in the fixtures (for example poky.xml)
                    # always preempt the Layer Index for these values
                    l.vcs_url = li['vcs_url']
                    l.vcs_web_url = li['vcs_web_url']
                    l.vcs_web_tree_base_url = li['vcs_web_tree_base_url']
                    l.vcs_web_file_base_url = li['vcs_web_file_base_url']
                l.save()
            except Layer.MultipleObjectsReturned:
                logger.info("Skipped %s as we found multiple layers and "
                            "don't know which to update" %
                            li['name'])

            li_layer_id_to_toaster_layer_id[li['id']] = l.pk

            self.mini_progress("layers", i, total)

        # update layer_versions
        logger.info("Fetching layer versions")
        layerbranches_info = _get_json_response(
            apilinks['layerBranches'] + "?filter=branch__name:%s" %
            "OR".join(whitelist_branch_names))

        # Map Layer index layer_branch object id to
        # layer_version toaster object id
        li_layer_branch_id_to_toaster_lv_id = {}

        total = len(layerbranches_info)
        for i, lbi in enumerate(layerbranches_info):
            # release as defined by toaster map to layerindex branch
            release = li_branch_id_to_toaster_release[lbi['branch']]

            try:
                lv, created = Layer_Version.objects.get_or_create(
                    layer=Layer.objects.get(
                        pk=li_layer_id_to_toaster_layer_id[lbi['layer']]),
                    release=release
                )
            except KeyError:
                logger.warning(
                    "No such layerindex layer referenced by layerbranch %d" %
                    lbi['layer'])
                continue

            if created:
                lv.release = li_branch_id_to_toaster_release[lbi['branch']]
                lv.up_date = lbi['updated']
                lv.commit = lbi['actual_branch']
                lv.dirpath = lbi['vcs_subdir']
                lv.save()

            li_layer_branch_id_to_toaster_lv_id[lbi['id']] =\
                lv.pk
            self.mini_progress("layer versions", i, total)

        logger.info("Fetching layer version dependencies")
        # update layer dependencies
        layerdependencies_info = _get_json_response(
            apilinks['layerDependencies'] +
            "?filter=layerbranch__branch__name:%s" %
            "OR".join(whitelist_branch_names))

        dependlist = {}
        for ldi in layerdependencies_info:
            try:
                lv = Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[ldi['layerbranch']])
            except Layer_Version.DoesNotExist as e:
                continue

            if lv not in dependlist:
                dependlist[lv] = []
            try:
                layer_id = li_layer_id_to_toaster_layer_id[ldi['dependency']]

                dependlist[lv].append(
                    Layer_Version.objects.get(layer__pk=layer_id,
                                              release=lv.release))

            except Layer_Version.DoesNotExist:
                logger.warning("Cannot find layer version (ls:%s),"
                               "up_id:%s lv:%s" %
                               (self, ldi['dependency'], lv))

        total = len(dependlist)
        for i, lv in enumerate(dependlist):
            LayerVersionDependency.objects.filter(layer_version=lv).delete()
            for lvd in dependlist[lv]:
                LayerVersionDependency.objects.get_or_create(layer_version=lv,
                                                             depends_on=lvd)
            self.mini_progress("Layer version dependencies", i, total)

        # update Distros
        logger.info("Fetching distro information")
        distros_info = _get_json_response(
            apilinks['distros'] + "?filter=layerbranch__branch__name:%s" %
            "OR".join(whitelist_branch_names))

        total = len(distros_info)
        for i, di in enumerate(distros_info):
            distro, created = Distro.objects.get_or_create(
                name=di['name'],
                layer_version=Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[di['layerbranch']]))
            distro.up_date = di['updated']
            distro.name = di['name']
            distro.description = di['description']
            distro.save()
            self.mini_progress("distros", i, total)

        # update machines
        logger.info("Fetching machine information")
        machines_info = _get_json_response(
            apilinks['machines'] + "?filter=layerbranch__branch__name:%s" %
            "OR".join(whitelist_branch_names))

        total = len(machines_info)
        for i, mi in enumerate(machines_info):
            mo, created = Machine.objects.get_or_create(
                name=mi['name'],
                layer_version=Layer_Version.objects.get(
                    pk=li_layer_branch_id_to_toaster_lv_id[mi['layerbranch']]))
            mo.up_date = mi['updated']
            mo.name = mi['name']
            mo.description = mi['description']
            mo.save()
            self.mini_progress("machines", i, total)

        # update recipes; paginate by layer version / layer branch
        logger.info("Fetching recipe information")
        recipes_info = _get_json_response(
            apilinks['recipes'] + "?filter=layerbranch__branch__name:%s" %
            "OR".join(whitelist_branch_names))

        total = len(recipes_info)
        for i, ri in enumerate(recipes_info):
            try:
                lv_id = li_layer_branch_id_to_toaster_lv_id[ri['layerbranch']]
                lv = Layer_Version.objects.get(pk=lv_id)

                ro, created = Recipe.objects.get_or_create(
                    layer_version=lv,
                    name=ri['pn']
                )

                ro.layer_version = lv
                ro.up_date = ri['updated']
                ro.name = ri['pn']
                ro.version = ri['pv']
                ro.summary = ri['summary']
                ro.description = ri['description']
                ro.section = ri['section']
                ro.license = ri['license']
                ro.homepage = ri['homepage']
                ro.bugtracker = ri['bugtracker']
                ro.file_path = ri['filepath'] + "/" + ri['filename']
                if 'inherits' in ri:
                    ro.is_image = 'image' in ri['inherits'].split()
                else:  # workaround for old style layer index
                    ro.is_image = "-image-" in ri['pn']
                ro.save()
            except Exception as e:
                logger.warning("Failed saving recipe %s", e)

            self.mini_progress("recipes", i, total)

        os.system('setterm -cursor on')

    def handle(self, **options):
        self.update()
