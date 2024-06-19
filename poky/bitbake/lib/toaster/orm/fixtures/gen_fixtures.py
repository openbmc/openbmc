#!/usr/bin/env python3
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Generate Toaster Fixtures for 'poky.xml' and 'oe-core.xml'
#
# Copyright (C) 2022      Wind River Systems
# SPDX-License-Identifier: GPL-2.0-only
#
# Edit the 'current_releases' table for each new release cycle
#
# Usage: ./get_fixtures all
#

import os
import sys
import argparse

verbose = False

####################################
# Releases
#
# https://wiki.yoctoproject.org/wiki/Releases
#
# NOTE: for the current releases table, it helps to keep continuing releases
# in the same table positions since this minimizes the patch diff for review.
# The order of the table does not matter since Toaster presents them sorted.
#
# Traditionally, the two most current releases are included in addition to the
# 'master' branch and the local installation's 'HEAD'.
# It is also policy to include all active LTS releases.
#

# [Codename, Yocto Project Version, Release Date, Current Version, Support Level, Poky Version, BitBake branch]
current_releases = [
    # Release slot #1
	['Scarthgap','5.0','April 2024','5.0.0 (April 2024)','Long Term Support (until April 2028)','','2.8'],
    # Release slot #2 'local'
    ['HEAD','HEAD','','Local Yocto Project','HEAD','','HEAD'],
    # Release slot #3 'master'
    ['Master','master','','Yocto Project master','master','','master'],
    # Release slot #4
#   ['Nanbield','4.3','November 2023','4.3.0 (November 2023)','Support for 7 months (until May 2024)','','2.6'],
#   ['Mickledore','4.2','April 2023','4.2.0 (April 2023)','Support for 7 months (until October 2023)','','2.4'],
#   ['Langdale','4.1','October 2022','4.1.2 (January 2023)','Support for 7 months (until May 2023)','','2.2'],
    ['Kirkstone','4.0','April 2022','4.0.8 (March 2023)','Stable - Long Term Support (until Apr. 2024)','','2.0'],
#   ['Honister','3.4','October 2021','3.4.2 (February 2022)','Support for 7 months (until May 2022)','26.0','1.52'],
#   ['Hardknott','3.3','April 2021','3.3.5 (March 2022)','Stable - Support for 13 months (until Apr. 2022)','25.0','1.50'],
#   ['Gatesgarth','3.2','Oct 2020','3.2.4 (May 2021)','EOL','24.0','1.48'],
    # Optional Release slot #5
    #['Dunfell','3.1','April 2020','3.1.23 (February 2023)','Stable - Long Term Support (until Apr. 2024)','23.0','1.46'],
]

default_poky_layers = [
    'openembedded-core',
    'meta-poky',
    'meta-yocto-bsp',
]

default_oe_core_layers = [
    'openembedded-core',
]

####################################
# Templates

prolog_template = '''\
<?xml version="1.0" encoding="utf-8"?>
<django-objects version="1.0">
  <!-- Set the project default value for DISTRO -->
  <object model="orm.toastersetting" pk="1">
    <field type="CharField" name="name">DEFCONF_DISTRO</field>
    <field type="CharField" name="value">{{distro}}</field>
  </object>
'''

#<!-- Bitbake versions which correspond to the metadata release -->')
bitbakeversion_poky_template = '''\
  <object model="orm.bitbakeversion" pk="{{bitbake_id}}">
    <field type="CharField" name="name">{{name}}</field>
    <field type="CharField" name="giturl">git://git.yoctoproject.org/poky</field>
    <field type="CharField" name="branch">{{branch}}</field>
    <field type="CharField" name="dirpath">bitbake</field>
  </object>
'''
bitbakeversion_oecore_template = '''\
  <object model="orm.bitbakeversion" pk="{{bitbake_id}}">
    <field type="CharField" name="name">{{name}}</field>
    <field type="CharField" name="giturl">git://git.openembedded.org/bitbake</field>
    <field type="CharField" name="branch">{{bitbakeversion}}</field>
  </object>
'''

# <!-- Releases available -->
releases_available_template = '''\
  <object model="orm.release" pk="{{ra_count}}">
    <field type="CharField" name="name">{{name}}</field>
    <field type="CharField" name="description">{{description}}</field>
    <field rel="ManyToOneRel" to="orm.bitbakeversion" name="bitbake_version">{{ra_count}}</field>
    <field type="CharField" name="branch_name">{{release}}</field>
    <field type="TextField" name="helptext">Toaster will run your builds {{help_source}}.</field>
  </object>
'''

# <!-- Default project layers for each release -->
default_layers_template = '''\
  <object model="orm.releasedefaultlayer" pk="{{rdl_count}}">
    <field rel="ManyToOneRel" to="orm.release" name="release">{{release_id}}</field>
    <field type="CharField" name="layer_name">{{layer}}</field>
  </object>
'''

default_layers_preface = '''\
  <!-- Default layers provided by poky
       openembedded-core
       meta-poky
       meta-yocto-bsp
  -->
'''

layer_poky_template = '''\
  <object model="orm.layer" pk="{{layer_id}}">
    <field type="CharField" name="name">{{layer}}</field>
    <field type="CharField" name="layer_index_url"></field>
    <field type="CharField" name="vcs_url">{{vcs_url}}</field>
    <field type="CharField" name="vcs_web_url">{{vcs_web_url}}</field>
    <field type="CharField" name="vcs_web_tree_base_url">{{vcs_web_tree_base_url}}</field>
    <field type="CharField" name="vcs_web_file_base_url">{{vcs_web_file_base_url}}</field>
  </object>
'''

layer_oe_core_template = '''\
  <object model="orm.layer" pk="{{layer_id}}">
    <field type="CharField" name="name">{{layer}}</field>
    <field type="CharField" name="vcs_url">{{vcs_url}}</field>
    <field type="CharField" name="vcs_web_url">{{vcs_web_url}}</field>
    <field type="CharField" name="vcs_web_tree_base_url">{{vcs_web_tree_base_url}}</field>
    <field type="CharField" name="vcs_web_file_base_url">{{vcs_web_file_base_url}}</field>
  </object>
'''

layer_version_template = '''\
  <object model="orm.layer_version" pk="{{lv_count}}">
    <field rel="ManyToOneRel" to="orm.layer" name="layer">{{layer_id}}</field>
    <field type="IntegerField" name="layer_source">0</field>
    <field rel="ManyToOneRel" to="orm.release" name="release">{{release_id}}</field>
    <field type="CharField" name="branch">{{branch}}</field>
    <field type="CharField" name="dirpath">{{dirpath}}</field>
  </object>
'''

layer_version_HEAD_template = '''\
  <object model="orm.layer_version" pk="{{lv_count}}">
    <field rel="ManyToOneRel" to="orm.layer" name="layer">{{layer_id}}</field>
    <field type="IntegerField" name="layer_source">0</field>
    <field rel="ManyToOneRel" to="orm.release" name="release">{{release_id}}</field>
    <field type="CharField" name="branch">{{branch}}</field>
    <field type="CharField" name="commit">{{commit}}</field>
    <field type="CharField" name="dirpath">{{dirpath}}</field>
  </object>
'''

layer_version_oe_core_template = '''\
  <object model="orm.layer_version" pk="1">
    <field rel="ManyToOneRel" to="orm.layer" name="layer">1</field>
    <field rel="ManyToOneRel" to="orm.release" name="release">2</field>
    <field type="CharField" name="local_path">OE-CORE-LAYER-DIR</field>
    <field type="CharField" name="branch">HEAD</field>
    <field type="CharField" name="dirpath">meta</field>
    <field type="IntegerField" name="layer_source">0</field>
  </object>
'''

epilog_template = '''\
</django-objects>
'''

#################################
# Helper Routines
#

def print_str(str,fd):
    # Avoid extra newline at end
    if str and (str[-1] == '\n'):
        str = str[0:-1]
    print(str,file=fd)

def print_template(template,params,fd):
    for line in template.split('\n'):
        p = line.find('{{')
        while p > 0:
            q = line.find('}}')
            key = line[p+2:q]
            if key in params:
                line = line[0:p] + params[key] + line[q+2:]
            else:
                line = line[0:p] + '?' + key + '?' + line[q+2:]
            p = line.find('{{')
        if line:
            print(line,file=fd)

#################################
# Generate poky.xml
#

def generate_poky():
    fd = open('poky.xml','w')

    params = {}
    params['distro'] = 'poky'
    print_template(prolog_template,params,fd)
    print_str('',fd)

    print_str('  <!-- Bitbake versions which correspond to the metadata release -->',fd)
    for i,release in enumerate(current_releases):
        params = {}
        params['release'] = release[0]
        params['Release'] = release[0]
        params['release_version'] = release[1]
        if not (params['release'] in ('HEAD')):    # 'master',
            params['release'] = params['release'][0].lower() + params['release'][1:]
        params['name'] = params['release']
        params['bitbake_id'] = str(i+1)
        params['branch'] = params['release']
        print_template(bitbakeversion_poky_template,params,fd)
    print_str('',fd)

    print_str('',fd)
    print_str('  <!-- Releases available -->',fd)
    for i,release in enumerate(current_releases):
        params = {}
        params['release'] = release[0]
        params['Release'] = release[0]
        params['release_version'] = release[1]
        if not (params['release'] in ('HEAD')): #'master',
            params['release'] = params['release'][0].lower() + params['release'][1:]
        params['h_release'] = '?h={{release}}'
        params['name'] = params['release']
        params['ra_count'] = str(i+1)
        params['branch'] = params['release']

        if 'HEAD' == params['release']:
            params['help_source'] = 'with the version of the Yocto Project you have cloned or downloaded to your computer'
            params['description'] = 'Local Yocto Project'
            params['name'] = 'local'
        else:
            params['help_source'] = 'using the tip of the &lt;a href="https://git.yoctoproject.org/cgit/cgit.cgi/poky/log/{{h_release}}"&gt;Yocto Project {{Release}} branch&lt;/a&gt;'
            params['description'] = 'Yocto Project {{release_version}} "{{Release}}"'
        if 'master' == params['release']:
            params['h_release'] = ''
            params['description'] = 'Yocto Project master'

        print_template(releases_available_template,params,fd)
    print_str('',fd)

    print_str('  <!-- Default project layers for each release -->',fd)
    rdl_count = 1
    for i,release in enumerate(current_releases):
        for j,layer in enumerate(default_poky_layers):
            params = {}
            params['layer'] = layer
            params['release'] = release[0]
            params['Release'] = release[0]
            params['release_version'] = release[1]
            if not (params['release'] in ('master','HEAD')):
                params['release'] = params['release'][0].lower() + params['release'][1:]
            params['release_id'] = str(i+1)
            params['rdl_count'] = str(rdl_count)
            params['branch'] = params['release']
            print_template(default_layers_template,params,fd)
            rdl_count += 1
    print_str('',fd)

    print_str(default_layers_preface,fd)
    lv_count = 1
    for i,layer in enumerate(default_poky_layers):
        params = {}
        params['layer'] = layer
        params['layer_id'] = str(i+1)
        params['vcs_url'] = 'git://git.yoctoproject.org/poky'
        params['vcs_web_url'] = 'https://git.yoctoproject.org/cgit/cgit.cgi/poky'
        params['vcs_web_tree_base_url'] = 'https://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/%path%?h=%branch%'
        params['vcs_web_file_base_url'] = 'https://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/%path%?h=%branch%'

        if i:
            print_str('',fd)
        print_template(layer_poky_template,params,fd)
        for j,release in enumerate(current_releases):
            params['release'] = release[0]
            params['Release'] = release[0]
            params['release_version'] = release[1]
            if not (params['release'] in ('master','HEAD')):
                params['release'] = params['release'][0].lower() + params['release'][1:]
            params['release_id'] = str(j+1)
            params['lv_count'] = str(lv_count)
            params['branch'] = params['release']
            params['commit'] = params['release']

            params['dirpath'] = params['layer']
            if params['layer'] in ('openembedded-core'):   #'openembedded-core',
                params['dirpath'] = 'meta'

            if 'HEAD' == params['release']:
                print_template(layer_version_HEAD_template,params,fd)
            else:
                print_template(layer_version_template,params,fd)
            lv_count += 1

    print_str(epilog_template,fd)
    fd.close()

#################################
# Generate oe-core.xml
#

def generate_oe_core():
    fd = open('oe-core.xml','w')

    params = {}
    params['distro'] = 'nodistro'
    print_template(prolog_template,params,fd)
    print_str('',fd)

    print_str('  <!-- Bitbake versions which correspond to the metadata release -->',fd)
    for i,release in enumerate(current_releases):
        params = {}
        params['release'] = release[0]
        params['Release'] = release[0]
        params['bitbakeversion'] = release[6]
        params['release_version'] = release[1]
        if not (params['release'] in ('HEAD')):    # 'master',
            params['release'] = params['release'][0].lower() + params['release'][1:]
        params['name'] = params['release']
        params['bitbake_id'] = str(i+1)
        params['branch'] = params['release']
        print_template(bitbakeversion_oecore_template,params,fd)
    print_str('',fd)

    print_str('  <!-- Releases available -->',fd)
    for i,release in enumerate(current_releases):
        params = {}
        params['release'] = release[0]
        params['Release'] = release[0]
        params['release_version'] = release[1]
        if not (params['release'] in ('HEAD')): #'master',
            params['release'] = params['release'][0].lower() + params['release'][1:]
        params['h_release'] = '?h={{release}}'
        params['name'] = params['release']
        params['ra_count'] = str(i+1)
        params['branch'] = params['release']

        if 'HEAD' == params['release']:
            params['help_source'] = 'with the version of OpenEmbedded that you have cloned or downloaded to your computer'
            params['description'] = 'Local Openembedded'
            params['name'] = 'local'
        else:
            params['help_source'] = 'using the tip of the &lt;a href=\\"https://cgit.openembedded.org/openembedded-core/log/{{h_release}}\\"&gt;OpenEmbedded {{Release}}&lt;/a&gt; branch'
            params['description'] = 'Openembedded {{Release}}'
        if 'master' == params['release']:
            params['h_release'] = ''
            params['description'] = 'OpenEmbedded core master'
            params['Release'] = params['release']

        print_template(releases_available_template,params,fd)
    print_str('',fd)

    print_str('  <!-- Default layers for each release -->',fd)
    rdl_count = 1
    for i,release in enumerate(current_releases):
        for j,layer in enumerate(default_oe_core_layers):
            params = {}
            params['layer'] = layer
            params['release'] = release[0]
            params['Release'] = release[0]
            params['release_version'] = release[1]
            if not (params['release'] in ('master','HEAD')):
                params['release'] = params['release'][0].lower() + params['release'][1:]
            params['release_id'] = str(i+1)
            params['rdl_count'] = str(rdl_count)
            params['branch'] = params['release']
            print_template(default_layers_template,params,fd)
            rdl_count += 1
    print_str('',fd)

    print_str('',fd)
    print_str('  <!-- Layer for the Local release -->',fd)
    lv_count = 1
    for i,layer in enumerate(default_oe_core_layers):
        params = {}
        params['layer'] = layer
        params['layer_id'] = str(i+1)
        params['vcs_url'] = 'git://git.openembedded.org/openembedded-core'
        params['vcs_web_url'] = 'https://cgit.openembedded.org/openembedded-core'
        params['vcs_web_tree_base_url'] = 'https://cgit.openembedded.org/openembedded-core/tree/%path%?h=%branch%'
        params['vcs_web_file_base_url'] = 'https://cgit.openembedded.org/openembedded-core/tree/%path%?h=%branch%'
        if i:
            print_str('',fd)
        print_template(layer_oe_core_template,params,fd)

        print_template(layer_version_oe_core_template,params,fd)
    print_str('',fd)

    print_str(epilog_template,fd)
    fd.close()

#################################
# Help
#

def list_releases():
    print("Release    ReleaseVer  BitbakeVer Support Level")
    print("========== =========== ========== ==============================================")
    for release in current_releases:
        print("%10s %10s %11s %s" % (release[0],release[1],release[6],release[4]))

#################################
# main
#

def main(argv):
    global verbose

    parser = argparse.ArgumentParser(description='gen_fixtures.py: table generate the fixture files')
    parser.add_argument('--poky', '-p', action='store_const', const='poky', dest='command', help='Generate the poky.xml file')
    parser.add_argument('--oe-core', '-o', action='store_const', const='oe_core', dest='command', help='Generate the oe-core.xml file')
    parser.add_argument('--all', '-a', action='store_const', const='all', dest='command', help='Generate all fixture files')
    parser.add_argument('--list', '-l', action='store_const', const='list', dest='command', help='List the release table')
    parser.add_argument('--verbose', '-v', action='store_true', dest='verbose', help='Enable verbose debugging output')
    args = parser.parse_args()

    verbose = args.verbose
    if 'poky' == args.command:
        generate_poky()
    elif 'oe_core' == args.command:
        generate_oe_core()
    elif 'all' == args.command:
        generate_poky()
        generate_oe_core()
    elif 'all' == args.command:
        list_releases()
    elif 'list' == args.command:
        list_releases()

    else:
        print("No command for 'gen_fixtures.py' selected")

if __name__ == '__main__':
    main(sys.argv[1:])
