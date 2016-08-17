from django.core.management.base import BaseCommand, CommandError
from orm.models import LayerSource, ToasterSetting, Branch, Layer, Layer_Version
from orm.models import BitbakeVersion, Release, ReleaseDefaultLayer, ReleaseLayerSourcePriority
from django.db import IntegrityError
import os

from checksettings import DN

import logging
logger = logging.getLogger("toaster")

def _reduce_canon_path(path):
    components = []
    for c in path.split("/"):
        if c == "..":
            del components[-1]
        elif c == ".":
            pass
        else:
            components.append(c)
    if len(components) < 2:
        components.append('')
    return "/".join(components)

def _get_id_for_sourcetype(s):
    for i in LayerSource.SOURCE_TYPE:
        if s == i[1]:
            return i[0]
    raise Exception("Could not find definition for sourcetype '%s'. Valid source types are %s" % (str(s), ', '.join(map(lambda x: "'%s'" % x[1], LayerSource.SOURCE_TYPE ))))

class Command(BaseCommand):
    help = "Loads a toasterconf.json file in the database"
    args = "filepath"



    def _import_layer_config(self, filepath):
        if not os.path.exists(filepath) or not os.path.isfile(filepath):
            raise Exception("Failed to find toaster config file %s ." % filepath)

        import json
        data = json.loads(open(filepath, "r").read())

        # verify config file validity before updating settings
        for i in ['bitbake', 'releases', 'defaultrelease', 'config', 'layersources']:
            assert i in data

        def _read_git_url_from_local_repository(address):
            url = None
            # we detect the remote name at runtime
            import subprocess
            (remote, remote_name) = address.split(":", 1)
            cmd = subprocess.Popen("git remote -v", shell=True, cwd = os.path.dirname(filepath), stdout=subprocess.PIPE, stderr = subprocess.PIPE)
            (out,err) = cmd.communicate()
            if cmd.returncode != 0:
                logging.warning("Error while importing layer vcs_url: git error: %s" % err)
            for line in out.split("\n"):
                try:
                    (name, path) = line.split("\t", 1)
                    if name == remote_name:
                        url = path.split(" ")[0]
                        break
                except ValueError:
                    pass
            if url == None:
                logging.warning("Error while looking for remote \"%s\" in \"%s\"" % (remote_name, out))
            return url


        # import bitbake data
        for bvi in data['bitbake']:
            bvo, created = BitbakeVersion.objects.get_or_create(name=bvi['name'])
            if bvi['giturl'].startswith("remote:"):
                bvo.giturl = _read_git_url_from_local_repository(bvi['giturl'])
                if bvo.giturl is None:
                    logger.error("The toaster config file references the local git repo, but Toaster cannot detect it.\nYour local configuration for bitbake version %s is invalid. Make sure that the toasterconf.json file is correct." % bvi['name'])

            if bvo.giturl is None:
                bvo.giturl = bvi['giturl']
            bvo.branch = bvi['branch']
            bvo.dirpath = bvi['dirpath']
            bvo.save()

        # set the layer sources
        for lsi in data['layersources']:
            assert 'sourcetype' in lsi
            assert 'apiurl' in lsi
            assert 'name' in lsi
            assert 'branches' in lsi


            if _get_id_for_sourcetype(lsi['sourcetype']) == LayerSource.TYPE_LAYERINDEX or lsi['apiurl'].startswith("/"):
                apiurl = lsi['apiurl']
            else:
                apiurl = _reduce_canon_path(os.path.join(DN(os.path.abspath(filepath)), lsi['apiurl']))

            assert ((_get_id_for_sourcetype(lsi['sourcetype']) == LayerSource.TYPE_LAYERINDEX) or apiurl.startswith("/")), (lsi['sourcetype'],apiurl)

            try:
                ls, created = LayerSource.objects.get_or_create(sourcetype = _get_id_for_sourcetype(lsi['sourcetype']), apiurl = apiurl)
                ls.name = lsi['name']
                ls.save()
            except IntegrityError as e:
                logger.warning("IntegrityError %s \nWhile setting name %s for layer source %s " % (e, lsi['name'], ls))


            layerbranches = []
            for branchname in lsi['branches']:
                bo, created = Branch.objects.get_or_create(layer_source = ls, name = branchname)
                layerbranches.append(bo)

            if 'layers' in lsi:
                for layerinfo in lsi['layers']:
                    lo, created = Layer.objects.get_or_create(layer_source = ls, name = layerinfo['name'])
                    if layerinfo['local_path'].startswith("/"):
                        lo.local_path = layerinfo['local_path']
                    else:
                        lo.local_path = _reduce_canon_path(os.path.join(ls.apiurl, layerinfo['local_path']))

                    if not os.path.exists(lo.local_path):
                        logger.error("Local layer path %s must exists. Are you trying to import a layer that does not exist ? Check your local toasterconf.json" % lo.local_path)

                    if layerinfo['vcs_url'].startswith("remote:"):
                        lo.vcs_url = _read_git_url_from_local_repository(layerinfo['vcs_url'])
                        if lo.vcs_url is None:
                            logger.error("The toaster config file references the local git repo, but Toaster cannot detect it.\nYour local configuration for layer %s is invalid. Make sure that the toasterconf.json file is correct." % layerinfo['name'])

                    if lo.vcs_url is None:
                        lo.vcs_url = layerinfo['vcs_url']

                    if 'layer_index_url' in layerinfo:
                        lo.layer_index_url = layerinfo['layer_index_url']
                    lo.save()

                    for branch in layerbranches:
                        lvo, created = Layer_Version.objects.get_or_create(layer_source = ls,
                                up_branch = branch,
                                commit = branch.name,
                                layer = lo)
                        lvo.dirpath = layerinfo['dirpath']
                        lvo.save()
        # set releases
        for ri in data['releases']:
            bvo = BitbakeVersion.objects.get(name = ri['bitbake'])
            assert bvo is not None

            ro, created = Release.objects.get_or_create(name = ri['name'], bitbake_version = bvo, branch_name = ri['branch'])
            ro.description = ri['description']
            ro.helptext = ri['helptext']
            ro.save()

            # save layer source priority for release
            for ls_name in ri['layersourcepriority'].keys():
                rlspo, created = ReleaseLayerSourcePriority.objects.get_or_create(release = ro, layer_source = LayerSource.objects.get(name=ls_name))
                rlspo.priority = ri['layersourcepriority'][ls_name]
                rlspo.save()

            for dli in ri['defaultlayers']:
                # find layers with the same name
                ReleaseDefaultLayer.objects.get_or_create( release = ro, layer_name = dli)

        # set default release
        if ToasterSetting.objects.filter(name = "DEFAULT_RELEASE").count() > 0:
            ToasterSetting.objects.filter(name = "DEFAULT_RELEASE").update(value = data['defaultrelease'])
        else:
            ToasterSetting.objects.create(name = "DEFAULT_RELEASE", value = data['defaultrelease'])

        # set default config variables
        for configname in data['config']:
            if ToasterSetting.objects.filter(name = "DEFCONF_" + configname).count() > 0:
                ToasterSetting.objects.filter(name = "DEFCONF_" + configname).update(value = data['config'][configname])
            else:
                ToasterSetting.objects.create(name = "DEFCONF_" + configname, value = data['config'][configname])


    def handle(self, *args, **options):
        if len(args) == 0:
            raise CommandError("Need a path to the toasterconf.json file")
        filepath = args[0]
        self._import_layer_config(filepath)



