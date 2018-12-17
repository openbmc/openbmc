#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2018        Wind River Systems
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

# buildimport: import a project for project specific configuration
#
# Usage:
#  (a) Set up Toaster environent
#
#  (b) Call buildimport
#      $ /path/to/bitbake/lib/toaster/manage.py buildimport \
#        --name=$PROJECTNAME \
#        --path=$BUILD_DIRECTORY \
#        --callback="$CALLBACK_SCRIPT" \
#        --command="configure|reconfigure|import"
#
#  (c) Return is "|Default_image=%s|Project_id=%d"
#
#  (d) Open Toaster to this project using for example:
#      $ xdg-open http://localhost:$toaster_port/toastergui/project_specific/$project_id
#
#  (e) To delete a project:
#      $ /path/to/bitbake/lib/toaster/manage.py buildimport \
#        --name=$PROJECTNAME --delete-project
#


# ../bitbake/lib/toaster/manage.py buildimport --name=test --path=`pwd` --callback="" --command=import

from django.core.management.base import BaseCommand, CommandError
from django.core.exceptions import ObjectDoesNotExist
from orm.models import ProjectManager, Project, Release, ProjectVariable
from orm.models import Layer, Layer_Version, LayerSource, ProjectLayer
from toastergui.api import scan_layer_content
from django.db import OperationalError

import os
import re
import os.path
import subprocess
import shutil

# Toaster variable section delimiters
TOASTER_PROLOG = '#=== TOASTER_CONFIG_PROLOG ==='
TOASTER_EPILOG = '#=== TOASTER_CONFIG_EPILOG ==='

# quick development/debugging support
verbose = 2
def _log(msg):
    if 1 == verbose:
        print(msg)
    elif 2 == verbose:
        f1=open('/tmp/toaster.log', 'a')
        f1.write("|" + msg + "|\n" )
        f1.close()


__config_regexp__  = re.compile( r"""
    ^
    (?P<exp>export\s+)?
    (?P<var>[a-zA-Z0-9\-_+.${}/~]+?)
    (\[(?P<flag>[a-zA-Z0-9\-_+.]+)\])?

    \s* (
        (?P<colon>:=) |
        (?P<lazyques>\?\?=) |
        (?P<ques>\?=) |
        (?P<append>\+=) |
        (?P<prepend>=\+) |
        (?P<predot>=\.) |
        (?P<postdot>\.=) |
        =
    ) \s*

    (?!'[^']*'[^']*'$)
    (?!\"[^\"]*\"[^\"]*\"$)
    (?P<apo>['\"])
    (?P<value>.*)
    (?P=apo)
    $
    """, re.X)

class Command(BaseCommand):
    args    = "<name> <path> <release>"
    help    = "Import a command line build directory"
    vars    = {}
    toaster_vars = {}

    def add_arguments(self, parser):
        parser.add_argument(
            '--name', dest='name', required=True,
            help='name of the project',
            )
        parser.add_argument(
            '--path', dest='path', required=True,
            help='path to the project',
            )
        parser.add_argument(
            '--release', dest='release', required=False,
            help='release for the project',
            )
        parser.add_argument(
            '--callback', dest='callback', required=False,
            help='callback for project config update',
            )
        parser.add_argument(
            '--delete-project', dest='delete_project', required=False,
            help='delete this project from the database',
            )
        parser.add_argument(
            '--command', dest='command', required=False,
            help='command (configure,reconfigure,import)',
            )

    # Extract the bb variables from a conf file
    def scan_conf(self,fn):
        vars = self.vars
        toaster_vars = self.toaster_vars

        #_log("scan_conf:%s" % fn)
        if not os.path.isfile(fn):
            return
        f = open(fn, 'r')

        #statements = ast.StatementGroup()
        lineno = 0
        is_toaster_section = False
        while True:
            lineno = lineno + 1
            s = f.readline()
            if not s:
                break
            w = s.strip()
            # skip empty lines
            if not w:
                continue
            # evaluate Toaster sections
            if w.startswith(TOASTER_PROLOG):
                is_toaster_section = True
                continue
            if w.startswith(TOASTER_EPILOG):
                is_toaster_section = False
                continue
            s = s.rstrip()
            while s[-1] == '\\':
                s2 = f.readline().strip()
                lineno = lineno + 1
                if (not s2 or s2 and s2[0] != "#") and s[0] == "#" :
                    echo("There is a confusing multiline, partially commented expression on line %s of file %s (%s).\nPlease clarify whether this is all a comment or should be parsed." % (lineno, fn, s))
                s = s[:-1] + s2
            # skip comments
            if s[0] == '#':
                continue
            # process the line for just assignments
            m = __config_regexp__.match(s)
            if m:
                groupd = m.groupdict()
                var = groupd['var']
                value = groupd['value']

                if groupd['lazyques']:
                    if not var in vars:
                        vars[var] = value
                    continue
                if groupd['ques']:
                    if not var in vars:
                        vars[var] = value
                    continue
                # preset empty blank for remaining operators
                if not var in vars:
                    vars[var] = ''
                if groupd['append']:
                    vars[var] += value
                elif groupd['prepend']:
                    vars[var] = "%s%s" % (value,vars[var])
                elif groupd['predot']:
                    vars[var] = "%s %s" % (value,vars[var])
                elif groupd['postdot']:
                    vars[var] = "%s %s" % (vars[var],value)
                else:
                    vars[var] = "%s" % (value)
                # capture vars in a Toaster section
                if is_toaster_section:
                    toaster_vars[var] = vars[var]

        # DONE WITH PARSING
        f.close()
        self.vars = vars
        self.toaster_vars = toaster_vars

    # Update the scanned project variables
    def update_project_vars(self,project,name):
        pv, create = ProjectVariable.objects.get_or_create(project = project, name = name)
        if (not name in self.vars.keys()) or (not self.vars[name]):
            self.vars[name] = pv.value
        else:
            if pv.value != self.vars[name]:
                pv.value = self.vars[name]
        pv.save()

    # Find the git version of the installation
    def find_layer_dir_version(self,path):
        #  * rocko               ...

        install_version = ''
        cwd = os.getcwd()
        os.chdir(path)
        p = subprocess.Popen(['git', 'branch', '-av'], stdout=subprocess.PIPE,
                                                stderr=subprocess.PIPE)
        out, err = p.communicate()
        out = out.decode("utf-8")
        for branch in out.split('\n'):
            if ('*' == branch[0:1]) and ('no branch' not in branch):
                install_version = re.sub(' .*','',branch[2:])
                break
            if 'remotes/m/master' in branch:
                install_version = re.sub('.*base/','',branch)
                break
        os.chdir(cwd)
        return install_version

    # Compute table of the installation's registered layer versions (branch or commit)
    def find_layer_dir_versions(self,INSTALL_URL_PREFIX):
        lv_dict = {}
        layer_versions = Layer_Version.objects.all()
        for lv in layer_versions:
            layer = Layer.objects.filter(pk=lv.layer.pk)[0]
            if layer.vcs_url:
                url_short = layer.vcs_url.replace(INSTALL_URL_PREFIX,'')
            else:
                url_short = ''
            # register the core, branch, and the version variations
            lv_dict["%s,%s,%s" % (url_short,lv.dirpath,'')] = (lv.id,layer.name)
            lv_dict["%s,%s,%s" % (url_short,lv.dirpath,lv.branch)] = (lv.id,layer.name)
            lv_dict["%s,%s,%s" % (url_short,lv.dirpath,lv.commit)] = (lv.id,layer.name)
            #_log("  (%s,%s,%s|%s) = (%s,%s)" % (url_short,lv.dirpath,lv.branch,lv.commit,lv.id,layer.name))
        return lv_dict

    # Apply table of all layer versions
    def extract_bblayers(self):
        # set up the constants
        bblayer_str = self.vars['BBLAYERS']
        TOASTER_DIR = os.environ.get('TOASTER_DIR')
        INSTALL_CLONE_PREFIX = os.path.dirname(TOASTER_DIR) + "/"
        TOASTER_CLONE_PREFIX = TOASTER_DIR + "/_toaster_clones/"
        INSTALL_URL_PREFIX = ''
        layers = Layer.objects.filter(name='openembedded-core')
        for layer in layers:
            if layer.vcs_url:
                INSTALL_URL_PREFIX = layer.vcs_url
                break
        INSTALL_URL_PREFIX = INSTALL_URL_PREFIX.replace("/poky","/")
        INSTALL_VERSION_DIR = TOASTER_DIR
        INSTALL_URL_POSTFIX = INSTALL_URL_PREFIX.replace(':','_')
        INSTALL_URL_POSTFIX = INSTALL_URL_POSTFIX.replace('/','_')
        INSTALL_URL_POSTFIX = "%s_%s" % (TOASTER_CLONE_PREFIX,INSTALL_URL_POSTFIX)

        # get the set of available layer:layer_versions
        lv_dict = self.find_layer_dir_versions(INSTALL_URL_PREFIX)

        # compute the layer matches
        layers_list = []
        for line in bblayer_str.split(' '):
            if not line:
                continue
            if line.endswith('/local'):
                continue

            # isolate the repo
            layer_path = line
            line = line.replace(INSTALL_URL_POSTFIX,'').replace(INSTALL_CLONE_PREFIX,'').replace('/layers/','/').replace('/poky/','/')

            # isolate the sub-path
            path_index = line.rfind('/')
            if path_index > 0:
                sub_path = line[path_index+1:]
                line = line[0:path_index]
            else:
                sub_path = ''

            # isolate the version
            if TOASTER_CLONE_PREFIX in layer_path:
                is_toaster_clone = True
                # extract version from name syntax
                version_index = line.find('_')
                if version_index > 0:
                    version = line[version_index+1:]
                    line = line[0:version_index]
                else:
                    version = ''
                _log("TOASTER_CLONE(%s/%s), version=%s" % (line,sub_path,version))
            else:
                is_toaster_clone = False
                # version is from the installation
                version = self.find_layer_dir_version(layer_path)
                _log("LOCAL_CLONE(%s/%s), version=%s" % (line,sub_path,version))

            # capture the layer information into layers_list
            layers_list.append( (line,sub_path,version,layer_path,is_toaster_clone) )
        return layers_list,lv_dict

    #
    def find_import_release(self,layers_list,lv_dict,default_release):
        #   poky,meta,rocko => 4;openembedded-core
        release = default_release
        for line,path,version,layer_path,is_toaster_clone in layers_list:
            key = "%s,%s,%s" % (line,path,version)
            if key in lv_dict:
                lv_id = lv_dict[key]
                if 'openembedded-core' == lv_id[1]:
                    _log("Find_import_release(%s):version=%s,Toaster=%s" % (lv_id[1],version,is_toaster_clone))
                    # only versions in Toaster managed layers are accepted
                    if not is_toaster_clone:
                        break
                    try:
                        release = Release.objects.get(name=version)
                    except:
                        pass
                    break
        _log("Find_import_release:RELEASE=%s" % release.name)
        return release

    # Apply the found conf layers
    def apply_conf_bblayers(self,layers_list,lv_dict,project,release=None):
        for line,path,version,layer_path,is_toaster_clone in layers_list:
            # Assert release promote if present
            if release:
                version = release
            # try to match the key to a layer_version
            key = "%s,%s,%s" % (line,path,version)
            key_short = "%s,%s,%s" % (line,path,'')
            lv_id = ''
            if key in lv_dict:
                lv_id = lv_dict[key]
                lv = Layer_Version.objects.get(pk=int(lv_id[0]))
                pl,created = ProjectLayer.objects.get_or_create(project=project,
                                                   layercommit=lv)
                pl.optional=False
                pl.save()
                _log("  %s => %s;%s" % (key,lv_id[0],lv_id[1]))
            elif key_short in lv_dict:
                lv_id = lv_dict[key_short]
                lv = Layer_Version.objects.get(pk=int(lv_id[0]))
                pl,created = ProjectLayer.objects.get_or_create(project=project,
                                                   layercommit=lv)
                pl.optional=False
                pl.save()
                _log("  %s ?> %s" % (key,lv_dict[key_short]))
            else:
                _log("%s <= %s" % (key,layer_path))
                found = False
                # does local layer already exist in this project?
                try:
                    for pl in ProjectLayer.objects.filter(project=project):
                        if pl.layercommit.layer.local_source_dir == layer_path:
                            found = True
                            _log("  Project Local Layer found!")
                except Exception as e:
                    _log("ERROR: Local Layer '%s'" % e)
                    pass

                if not found:
                    # Does Layer name+path already exist?
                    try:
                        layer_name_base = os.path.basename(layer_path)
                        _log("Layer_lookup: try '%s','%s'" % (layer_name_base,layer_path))
                        layer = Layer.objects.get(name=layer_name_base,local_source_dir = layer_path)
                        # Found! Attach layer_version and ProjectLayer
                        layer_version = Layer_Version.objects.create(
                            layer=layer,
                            project=project,
                            layer_source=LayerSource.TYPE_IMPORTED)
                        layer_version.save()
                        pl,created = ProjectLayer.objects.get_or_create(project=project,
                                                           layercommit=layer_version)
                        pl.optional=False
                        pl.save()
                        found = True
                        # add layer contents to this layer version
                        scan_layer_content(layer,layer_version)
                        _log("  Parent Local Layer found in db!")
                    except Exception as e:
                        _log("Layer_exists_test_failed: Local Layer '%s'" % e)
                        pass

                if not found:
                    # Insure that layer path exists, in case of user typo
                    if not os.path.isdir(layer_path):
                        _log("ERROR:Layer path '%s' not found" % layer_path)
                        continue
                    # Add layer to db and attach project to it
                    layer_name_base = os.path.basename(layer_path)
                    # generate a unique layer name
                    layer_name_matches = {}
                    for layer in Layer.objects.filter(name__contains=layer_name_base):
                        layer_name_matches[layer.name] = '1'
                    layer_name_idx = 0
                    layer_name_test = layer_name_base
                    while layer_name_test in layer_name_matches.keys():
                        layer_name_idx += 1
                        layer_name_test = "%s_%d" % (layer_name_base,layer_name_idx)
                    # create the layer and layer_verion objects
                    layer = Layer.objects.create(name=layer_name_test)
                    layer.local_source_dir = layer_path
                    layer_version = Layer_Version.objects.create(
                        layer=layer,
                        project=project,
                        layer_source=LayerSource.TYPE_IMPORTED)
                    layer.save()
                    layer_version.save()
                    pl,created = ProjectLayer.objects.get_or_create(project=project,
                                                       layercommit=layer_version)
                    pl.optional=False
                    pl.save()
                    # register the layer's content
                    _log("  Local Layer Add content")
                    scan_layer_content(layer,layer_version)
                    _log("  Local Layer Added '%s'!" % layer_name_test)

    # Scan the project's conf files (if any)
    def scan_conf_variables(self,project_path):
        # scan the project's settings, add any new layers or variables
        if os.path.isfile("%s/conf/local.conf" % project_path):
            self.scan_conf("%s/conf/local.conf" % project_path)
            self.scan_conf("%s/conf/bblayers.conf" % project_path)
            # Import then disable old style Toaster conf files (before 'merged_attr')
            old_toaster_local = "%s/conf/toaster.conf" % project_path
            if os.path.isfile(old_toaster_local):
                self.scan_conf(old_toaster_local)
                shutil.move(old_toaster_local, old_toaster_local+"_old")
            old_toaster_layer = "%s/conf/toaster-bblayers.conf" % project_path
            if os.path.isfile(old_toaster_layer):
                self.scan_conf(old_toaster_layer)
                shutil.move(old_toaster_layer, old_toaster_layer+"_old")

    # Scan the found conf variables (if any)
    def apply_conf_variables(self,project,layers_list,lv_dict,release=None):
        if self.vars:
            # Catch vars relevant to Toaster (in case no Toaster section)
            self.update_project_vars(project,'DISTRO')
            self.update_project_vars(project,'MACHINE')
            self.update_project_vars(project,'IMAGE_INSTALL_append')
            self.update_project_vars(project,'IMAGE_FSTYPES')
            self.update_project_vars(project,'PACKAGE_CLASSES')
            # These vars are typically only assigned by Toaster
            #self.update_project_vars(project,'DL_DIR')
            #self.update_project_vars(project,'SSTATE_DIR')

            # Assert found Toaster vars
            for var in self.toaster_vars.keys():
                pv, create = ProjectVariable.objects.get_or_create(project = project, name = var)
                pv.value = self.toaster_vars[var]
                _log("* Add/update Toaster var '%s' = '%s'" % (pv.name,pv.value))
                pv.save()

            # Assert found BBLAYERS
            if 0 < verbose:
                for pl in ProjectLayer.objects.filter(project=project):
                    release_name = 'None' if not pl.layercommit.release else pl.layercommit.release.name
                    print(" BEFORE:ProjectLayer=%s,%s,%s,%s" % (pl.layercommit.layer.name,release_name,pl.layercommit.branch,pl.layercommit.commit))
            self.apply_conf_bblayers(layers_list,lv_dict,project,release)
            if 0 < verbose:
                for pl in ProjectLayer.objects.filter(project=project):
                    release_name = 'None' if not pl.layercommit.release else pl.layercommit.release.name
                    print(" AFTER :ProjectLayer=%s,%s,%s,%s" % (pl.layercommit.layer.name,release_name,pl.layercommit.branch,pl.layercommit.commit))


    def handle(self, *args, **options):
        project_name = options['name']
        project_path = options['path']
        project_callback = options['callback'] if options['callback'] else ''
        release_name = options['release'] if options['release'] else ''

        #
        # Delete project
        #

        if options['delete_project']:
            try:
                print("Project '%s' delete from Toaster database" % (project_name))
                project = Project.objects.get(name=project_name)
                # TODO: deep project delete
                project.delete()
                print("Project '%s' Deleted" % (project_name))
                return
            except Exception as e:
                print("Project '%s' not found, not deleted (%s)" % (project_name,e))
                return

        #
        # Create/Update/Import project
        #

        # See if project (by name) exists
        project = None
        try:
            # Project already exists
            project = Project.objects.get(name=project_name)
        except Exception as e:
            pass

        # Find the installation's default release
        default_release = Release.objects.get(id=1)

        # SANITY: if 'reconfig' but project does not exist (deleted externally), switch to 'import'
        if ("reconfigure" == options['command']) and (None == project):
            options['command'] = 'import'

        # 'Configure':
        if "configure" == options['command']:
            # Note: ignore any existing conf files
            # create project, SANITY: reuse any project of same name
            project = Project.objects.create_project(project_name,default_release,project)

        # 'Re-configure':
        if "reconfigure" == options['command']:
            # Scan the directory's conf files
            self.scan_conf_variables(project_path)
            # Scan the layer list
            layers_list,lv_dict = self.extract_bblayers()
            # Apply any new layers or variables
            self.apply_conf_variables(project,layers_list,lv_dict)

        # 'Import':
        if "import" == options['command']:
            # Scan the directory's conf files
            self.scan_conf_variables(project_path)
            # Remove these Toaster controlled variables
            for var in ('DL_DIR','SSTATE_DIR'):
                self.vars.pop(var, None)
                self.toaster_vars.pop(var, None)
            # Scan the layer list
            layers_list,lv_dict = self.extract_bblayers()
            # Find the directory's release, and promote to default_release if local paths
            release = self.find_import_release(layers_list,lv_dict,default_release)
            # create project, SANITY: reuse any project of same name
            project = Project.objects.create_project(project_name,release,project)
            # Apply any new layers or variables
            self.apply_conf_variables(project,layers_list,lv_dict,release)
            # WORKAROUND: since we now derive the release, redirect 'newproject_specific' to 'project_specific'
            project.set_variable('INTERNAL_PROJECT_SPECIFIC_SKIPRELEASE','1')

        # Set up the project's meta data
        project.builddir = project_path
        project.merged_attr = True
        project.set_variable(Project.PROJECT_SPECIFIC_CALLBACK,project_callback)
        project.set_variable(Project.PROJECT_SPECIFIC_STATUS,Project.PROJECT_SPECIFIC_EDIT)
        if ("configure" == options['command']) or ("import" == options['command']):
            # preset the mode and default image recipe
            project.set_variable(Project.PROJECT_SPECIFIC_ISNEW,Project.PROJECT_SPECIFIC_NEW)
            project.set_variable(Project.PROJECT_SPECIFIC_DEFAULTIMAGE,"core-image-minimal")
            # Assert any extended/custom actions or variables for new non-Toaster projects
            if not len(self.toaster_vars):
                pass
        else:
            project.set_variable(Project.PROJECT_SPECIFIC_ISNEW,Project.PROJECT_SPECIFIC_NONE)

        # Save the updated Project
        project.save()

        _log("Buildimport:project='%s' at '%d'" % (project_name,project.id))

        if ('DEFAULT_IMAGE' in self.vars) and (self.vars['DEFAULT_IMAGE']):
            print("|Default_image=%s|Project_id=%d" % (self.vars['DEFAULT_IMAGE'],project.id))
        else:
            print("|Project_id=%d" % (project.id))

