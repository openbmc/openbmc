# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (c) 2013, Intel Corporation.
# All rights reserved.
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
#
# DESCRIPTION
# This module implements some basic help invocation functions along
# with the bulk of the help topic text for the OE Core Image Tools.
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#

import subprocess
import logging

from wic.plugin import pluginmgr, PLUGIN_TYPES

def subcommand_error(args):
    logging.info("invalid subcommand %s" % args[0])


def display_help(subcommand, subcommands):
    """
    Display help for subcommand.
    """
    if subcommand not in subcommands:
        return False

    hlp = subcommands.get(subcommand, subcommand_error)[2]
    if callable(hlp):
        hlp = hlp()
    pager = subprocess.Popen('less', stdin=subprocess.PIPE)
    pager.communicate(hlp)

    return True


def wic_help(args, usage_str, subcommands):
    """
    Subcommand help dispatcher.
    """
    if len(args) == 1 or not display_help(args[1], subcommands):
        print usage_str


def get_wic_plugins_help():
    """
    Combine wic_plugins_help with the help for every known
    source plugin.
    """
    result = wic_plugins_help
    for plugin_type in PLUGIN_TYPES:
        result += '\n\n%s PLUGINS\n\n' % plugin_type.upper()
        for name, plugin in pluginmgr.get_plugins(plugin_type).iteritems():
            result += "\n %s plugin:\n" % name
            if plugin.__doc__:
                result += plugin.__doc__
            else:
                result += "\n    %s is missing docstring\n" % plugin
    return result


def invoke_subcommand(args, parser, main_command_usage, subcommands):
    """
    Dispatch to subcommand handler borrowed from combo-layer.
    Should use argparse, but has to work in 2.6.
    """
    if not args:
        logging.error("No subcommand specified, exiting")
        parser.print_help()
        return 1
    elif args[0] == "help":
        wic_help(args, main_command_usage, subcommands)
    elif args[0] not in subcommands:
        logging.error("Unsupported subcommand %s, exiting\n" % (args[0]))
        parser.print_help()
        return 1
    else:
        usage = subcommands.get(args[0], subcommand_error)[1]
        subcommands.get(args[0], subcommand_error)[0](args[1:], usage)


##
# wic help and usage strings
##

wic_usage = """

 Create a customized OpenEmbedded image

 usage: wic [--version] | [--help] | [COMMAND [ARGS]]

 Current 'wic' commands are:
    help              Show help for command or one of the topics (see below)
    create            Create a new OpenEmbedded image
    list              List available canned images and source plugins

 Help topics:
    overview          wic overview - General overview of wic
    plugins           wic plugins - Overview and API
    kickstart         wic kickstart - wic kickstart reference
"""

wic_help_usage = """

 usage: wic help <subcommand>

 This command displays detailed help for the specified subcommand.
"""

wic_create_usage = """

 Create a new OpenEmbedded image

 usage: wic create <wks file or image name> [-o <DIRNAME> | --outdir <DIRNAME>]
            [-i <JSON PROPERTY FILE> | --infile <JSON PROPERTY_FILE>]
            [-e | --image-name] [-s, --skip-build-check] [-D, --debug]
            [-r, --rootfs-dir] [-b, --bootimg-dir]
            [-k, --kernel-dir] [-n, --native-sysroot] [-f, --build-rootfs]

 This command creates an OpenEmbedded image based on the 'OE kickstart
 commands' found in the <wks file>.

 The -o option can be used to place the image in a directory with a
 different name and location.

 See 'wic help create' for more detailed instructions.
"""

wic_create_help = """

NAME
    wic create - Create a new OpenEmbedded image

SYNOPSIS
    wic create <wks file or image name> [-o <DIRNAME> | --outdir <DIRNAME>]
        [-e | --image-name] [-s, --skip-build-check] [-D, --debug]
        [-r, --rootfs-dir] [-b, --bootimg-dir]
        [-k, --kernel-dir] [-n, --native-sysroot] [-f, --build-rootfs]
        [-c, --compress-with]

DESCRIPTION
    This command creates an OpenEmbedded image based on the 'OE
    kickstart commands' found in the <wks file>.

    In order to do this, wic needs to know the locations of the
    various build artifacts required to build the image.

    Users can explicitly specify the build artifact locations using
    the -r, -b, -k, and -n options.  See below for details on where
    the corresponding artifacts are typically found in a normal
    OpenEmbedded build.

    Alternatively, users can use the -e option to have 'wic' determine
    those locations for a given image.  If the -e option is used, the
    user needs to have set the appropriate MACHINE variable in
    local.conf, and have sourced the build environment.

    The -e option is used to specify the name of the image to use the
    artifacts from e.g. core-image-sato.

    The -r option is used to specify the path to the /rootfs dir to
    use as the .wks rootfs source.

    The -b option is used to specify the path to the dir containing
    the boot artifacts (e.g. /EFI or /syslinux dirs) to use as the
    .wks bootimg source.

    The -k option is used to specify the path to the dir containing
    the kernel to use in the .wks bootimg.

    The -n option is used to specify the path to the native sysroot
    containing the tools to use to build the image.

    The -f option is used to build rootfs by running "bitbake <image>"

    The -s option is used to skip the build check.  The build check is
    a simple sanity check used to determine whether the user has
    sourced the build environment so that the -e option can operate
    correctly.  If the user has specified the build artifact locations
    explicitly, 'wic' assumes the user knows what he or she is doing
    and skips the build check.

    The -D option is used to display debug information detailing
    exactly what happens behind the scenes when a create request is
    fulfilled (or not, as the case may be).  It enumerates and
    displays the command sequence used, and should be included in any
    bug report describing unexpected results.

    When 'wic -e' is used, the locations for the build artifacts
    values are determined by 'wic -e' from the output of the 'bitbake
    -e' command given an image name e.g. 'core-image-minimal' and a
    given machine set in local.conf.  In that case, the image is
    created as if the following 'bitbake -e' variables were used:

    -r:        IMAGE_ROOTFS
    -k:        STAGING_KERNEL_DIR
    -n:        STAGING_DIR_NATIVE
    -b:        empty (plugin-specific handlers must determine this)

    If 'wic -e' is not used, the user needs to select the appropriate
    value for -b (as well as -r, -k, and -n).

    The -o option can be used to place the image in a directory with a
    different name and location.

    The -c option is used to specify compressor utility to compress
    an image. gzip, bzip2 and xz compressors are supported.
"""

wic_list_usage = """

 List available OpenEmbedded images and source plugins

 usage: wic list images
        wic list <image> help
        wic list source-plugins

 This command enumerates the set of available canned images as well as
 help for those images.  It also can be used to list of available source
 plugins.

 The first form enumerates all the available 'canned' images.

 The second form lists the detailed help information for a specific
 'canned' image.

 The third form enumerates all the available --sources (source
 plugins).

 See 'wic help list' for more details.
"""

wic_list_help = """

NAME
    wic list - List available OpenEmbedded images and source plugins

SYNOPSIS
    wic list images
    wic list <image> help
    wic list source-plugins

DESCRIPTION
    This command enumerates the set of available canned images as well
    as help for those images.  It also can be used to list available
    source plugins.

    The first form enumerates all the available 'canned' images.
    These are actually just the set of .wks files that have been moved
    into the /scripts/lib/wic/canned-wks directory).

    The second form lists the detailed help information for a specific
    'canned' image.

    The third form enumerates all the available --sources (source
    plugins).  The contents of a given partition are driven by code
    defined in 'source plugins'.  Users specify a specific plugin via
    the --source parameter of the partition .wks command.  Normally
    this is the 'rootfs' plugin but can be any of the more specialized
    sources listed by the 'list source-plugins' command.  Users can
    also add their own source plugins - see 'wic help plugins' for
    details.
"""

wic_plugins_help = """

NAME
    wic plugins - Overview and API

DESCRIPTION
    plugins allow wic functionality to be extended and specialized by
    users.  This section documents the plugin interface, which is
    currently restricted to 'source' plugins.

    'Source' plugins provide a mechanism to customize various aspects
    of the image generation process in wic, mainly the contents of
    partitions.

    Source plugins provide a mechanism for mapping values specified in
    .wks files using the --source keyword to a particular plugin
    implementation that populates a corresponding partition.

    A source plugin is created as a subclass of SourcePlugin (see
    scripts/lib/wic/pluginbase.py) and the plugin file containing it
    is added to scripts/lib/wic/plugins/source/ to make the plugin
    implementation available to the wic implementation.

    Source plugins can also be implemented and added by external
    layers - any plugins found in a scripts/lib/wic/plugins/source/
    directory in an external layer will also be made available.

    When the wic implementation needs to invoke a partition-specific
    implementation, it looks for the plugin that has the same name as
    the --source param given to that partition.  For example, if the
    partition is set up like this:

      part /boot --source bootimg-pcbios   ...

    then the methods defined as class members of the plugin having the
    matching bootimg-pcbios .name class member would be used.

    To be more concrete, here's the plugin definition that would match
    a '--source bootimg-pcbios' usage, along with an example method
    that would be called by the wic implementation when it needed to
    invoke an implementation-specific partition-preparation function:

    class BootimgPcbiosPlugin(SourcePlugin):
        name = 'bootimg-pcbios'

    @classmethod
        def do_prepare_partition(self, part, ...)

    If the subclass itself doesn't implement a function, a 'default'
    version in a superclass will be located and used, which is why all
    plugins must be derived from SourcePlugin.

    The SourcePlugin class defines the following methods, which is the
    current set of methods that can be implemented/overridden by
    --source plugins.  Any methods not implemented by a SourcePlugin
    subclass inherit the implementations present in the SourcePlugin
    class (see the SourcePlugin source for details):

      do_prepare_partition()
          Called to do the actual content population for a
          partition. In other words, it 'prepares' the final partition
          image which will be incorporated into the disk image.

      do_configure_partition()
          Called before do_prepare_partition(), typically used to
          create custom configuration files for a partition, for
          example syslinux or grub config files.

      do_install_disk()
          Called after all partitions have been prepared and assembled
          into a disk image.  This provides a hook to allow
          finalization of a disk image, for example to write an MBR to
          it.

      do_stage_partition()
          Special content-staging hook called before
          do_prepare_partition(), normally empty.

          Typically, a partition will just use the passed-in
          parameters, for example the unmodified value of bootimg_dir.
          In some cases however, things may need to be more tailored.
          As an example, certain files may additionally need to be
          take from bootimg_dir + /boot.  This hook allows those files
          to be staged in a customized fashion.  Note that
          get_bitbake_var() allows you to access non-standard
          variables that you might want to use for these types of
          situations.

    This scheme is extensible - adding more hooks is a simple matter
    of adding more plugin methods to SourcePlugin and derived classes.
    The code that then needs to call the plugin methods uses
    plugin.get_source_plugin_methods() to find the method(s) needed by
    the call; this is done by filling up a dict with keys containing
    the method names of interest - on success, these will be filled in
    with the actual methods. Please see the implementation for
    examples and details.
"""

wic_overview_help = """

NAME
    wic overview - General overview of wic

DESCRIPTION
    The 'wic' command generates partitioned images from existing
    OpenEmbedded build artifacts.  Image generation is driven by
    partitioning commands contained in an 'Openembedded kickstart'
    (.wks) file (see 'wic help kickstart') specified either directly
    on the command-line or as one of a selection of canned .wks files
    (see 'wic list images').  When applied to a given set of build
    artifacts, the result is an image or set of images that can be
    directly written onto media and used on a particular system.

    The 'wic' command and the infrastructure it's based on is by
    definition incomplete - its purpose is to allow the generation of
    customized images, and as such was designed to be completely
    extensible via a plugin interface (see 'wic help plugins').

  Background and Motivation

    wic is meant to be a completely independent standalone utility
    that initially provides easier-to-use and more flexible
    replacements for a couple bits of existing functionality in
    oe-core: directdisk.bbclass and mkefidisk.sh.  The difference
    between wic and those examples is that with wic the functionality
    of those scripts is implemented by a general-purpose partitioning
    'language' based on Redhat kickstart syntax).

    The initial motivation and design considerations that lead to the
    current tool are described exhaustively in Yocto Bug #3847
    (https://bugzilla.yoctoproject.org/show_bug.cgi?id=3847).

  Implementation and Examples

    wic can be used in two different modes, depending on how much
    control the user needs in specifying the Openembedded build
    artifacts that will be used in creating the image: 'raw' and
    'cooked'.

    If used in 'raw' mode, artifacts are explicitly specified via
    command-line arguments (see example below).

    The more easily usable 'cooked' mode uses the current MACHINE
    setting and a specified image name to automatically locate the
    artifacts used to create the image.

    OE kickstart files (.wks) can of course be specified directly on
    the command-line, but the user can also choose from a set of
    'canned' .wks files available via the 'wic list images' command
    (example below).

    In any case, the prerequisite for generating any image is to have
    the build artifacts already available.  The below examples assume
    the user has already build a 'core-image-minimal' for a specific
    machine (future versions won't require this redundant step, but
    for now that's typically how build artifacts get generated).

    The other prerequisite is to source the build environment:

      $ source oe-init-build-env

    To start out with, we'll generate an image from one of the canned
    .wks files.  The following generates a list of availailable
    images:

      $ wic list images
        mkefidisk             Create an EFI disk image
        directdisk            Create a 'pcbios' direct disk image

    You can get more information about any of the available images by
    typing 'wic list xxx help', where 'xxx' is one of the image names:

      $ wic list mkefidisk help

    Creates a partitioned EFI disk image that the user can directly dd
    to boot media.

    At any time, you can get help on the 'wic' command or any
    subcommand (currently 'list' and 'create').  For instance, to get
    the description of 'wic create' command and its parameters:

      $ wic create

       Usage:

       Create a new OpenEmbedded image

       usage: wic create <wks file or image name> [-o <DIRNAME> | ...]
            [-i <JSON PROPERTY FILE> | --infile <JSON PROPERTY_FILE>]
            [-e | --image-name] [-s, --skip-build-check] [-D, --debug]
            [-r, --rootfs-dir] [-b, --bootimg-dir] [-k, --kernel-dir]
            [-n, --native-sysroot] [-f, --build-rootfs]

       This command creates an OpenEmbedded image based on the 'OE
       kickstart commands' found in the <wks file>.

       The -o option can be used to place the image in a directory
       with a different name and location.

       See 'wic help create' for more detailed instructions.
       ...

    As mentioned in the command, you can get even more detailed
    information by adding 'help' to the above:

      $ wic help create

    So, the easiest way to create an image is to use the -e option
    with a canned .wks file.  To use the -e option, you need to
    specify the image used to generate the artifacts and you actually
    need to have the MACHINE used to build them specified in your
    local.conf (these requirements aren't necessary if you aren't
    using the -e options.)  Below, we generate a directdisk image,
    pointing the process at the core-image-minimal artifacts for the
    current MACHINE:

      $ wic create directdisk -e core-image-minimal

      Checking basic build environment...
      Done.

      Creating image(s)...

      Info: The new image(s) can be found here:
      /var/tmp/wic/build/directdisk-201309252350-sda.direct

      The following build artifacts were used to create the image(s):

      ROOTFS_DIR:      ...
      BOOTIMG_DIR:     ...
      KERNEL_DIR:      ...
      NATIVE_SYSROOT:  ...

      The image(s) were created using OE kickstart file:
        .../scripts/lib/wic/canned-wks/directdisk.wks

    The output shows the name and location of the image created, and
    so that you know exactly what was used to generate the image, each
    of the artifacts and the kickstart file used.

    Similarly, you can create a 'mkefidisk' image in the same way
    (notice that this example uses a different machine - because it's
    using the -e option, you need to change the MACHINE in your
    local.conf):

      $ wic create mkefidisk -e core-image-minimal
      Checking basic build environment...
      Done.

      Creating image(s)...

      Info: The new image(s) can be found here:
         /var/tmp/wic/build/mkefidisk-201309260027-sda.direct

      ...

    Here's an example that doesn't take the easy way out and manually
    specifies each build artifact, along with a non-canned .wks file,
    and also uses the -o option to have wic create the output
    somewhere other than the default /var/tmp/wic:

      $ wic create ./test.wks -o ./out --rootfs-dir
      tmp/work/qemux86_64-poky-linux/core-image-minimal/1.0-r0/rootfs
      --bootimg-dir tmp/sysroots/qemux86-64/usr/share
      --kernel-dir tmp/deploy/images/qemux86-64
      --native-sysroot tmp/sysroots/x86_64-linux

     Creating image(s)...

     Info: The new image(s) can be found here:
           out/build/test-201507211313-sda.direct

     The following build artifacts were used to create the image(s):
       ROOTFS_DIR:                   tmp/work/qemux86_64-poky-linux/core-image-minimal/1.0-r0/rootfs
       BOOTIMG_DIR:                  tmp/sysroots/qemux86-64/usr/share
       KERNEL_DIR:                   tmp/deploy/images/qemux86-64
       NATIVE_SYSROOT:               tmp/sysroots/x86_64-linux

     The image(s) were created using OE kickstart file:
     ./test.wks

     Here is a content of test.wks:

     part /boot --source bootimg-pcbios --ondisk sda --label boot --active --align 1024
     part / --source rootfs --ondisk sda --fstype=ext3 --label platform --align 1024

     bootloader  --timeout=0  --append="rootwait rootfstype=ext3 video=vesafb vga=0x318 console=tty0"


    Finally, here's an example of the actual partition language
    commands used to generate the mkefidisk image i.e. these are the
    contents of the mkefidisk.wks OE kickstart file:

      # short-description: Create an EFI disk image
      # long-description: Creates a partitioned EFI disk image that the user
      # can directly dd to boot media.

      part /boot --source bootimg-efi --ondisk sda --fstype=efi --active

      part / --source rootfs --ondisk sda --fstype=ext3 --label platform

      part swap --ondisk sda --size 44 --label swap1 --fstype=swap

      bootloader  --timeout=10  --append="rootwait console=ttyPCH0,115200"

    You can get a complete listing and description of all the
    kickstart commands available for use in .wks files from 'wic help
    kickstart'.
"""

wic_kickstart_help = """

NAME
    wic kickstart - wic kickstart reference

DESCRIPTION
    This section provides the definitive reference to the wic
    kickstart language.  It also provides documentation on the list of
    --source plugins available for use from the 'part' command (see
    the 'Platform-specific Plugins' section below).

    The current wic implementation supports only the basic kickstart
    partitioning commands: partition (or part for short) and
    bootloader.

    The following is a listing of the commands, their syntax, and
    meanings. The commands are based on the Fedora kickstart
    documentation but with modifications to reflect wic capabilities.

      http://fedoraproject.org/wiki/Anaconda/Kickstart#part_or_partition
      http://fedoraproject.org/wiki/Anaconda/Kickstart#bootloader

  Commands

    * 'part' or 'partition'

       This command creates a partition on the system and uses the
       following syntax:

         part [<mountpoint>]

       The <mountpoint> is where the partition will be mounted and
       must take of one of the following forms:

         /<path>: For example: /, /usr, or /home

         swap: The partition will be used as swap space.

       If a <mountpoint> is not specified the partition will be created
       but will not be mounted.

       Partitions with a <mountpoint> specified will be automatically mounted.
       This is achieved by wic adding entries to the fstab during image
       generation. In order for a valid fstab to be generated one of the
       --ondrive, --ondisk or --use-uuid partition options must be used for
       each partition that specifies a mountpoint.


       The following are supported 'part' options:

         --size: The minimum partition size. Specify an integer value
                 such as 500. Multipliers k, M ang G can be used. If
                 not specified, the size is in MB.
                 You do not need this option if you use --source.

         --source: This option is a wic-specific option that names the
                   source of the data that will populate the
                   partition.  The most common value for this option
                   is 'rootfs', but can be any value which maps to a
                   valid 'source plugin' (see 'wic help plugins').

                   If '--source rootfs' is used, it tells the wic
                   command to create a partition as large as needed
                   and to fill it with the contents of the root
                   filesystem pointed to by the '-r' wic command-line
                   option (or the equivalent rootfs derived from the
                   '-e' command-line option).  The filesystem type
                   that will be used to create the partition is driven
                   by the value of the --fstype option specified for
                   the partition (see --fstype below).

                   If --source <plugin-name>' is used, it tells the
                   wic command to create a partition as large as
                   needed and to fill with the contents of the
                   partition that will be generated by the specified
                   plugin name using the data pointed to by the '-r'
                   wic command-line option (or the equivalent rootfs
                   derived from the '-e' command-line option).
                   Exactly what those contents and filesystem type end
                   up being are dependent on the given plugin
                   implementation.

                   If --source option is not used, the wic command
                   will create empty partition. --size parameter has
                   to be used to specify size of empty partition.

         --ondisk or --ondrive: Forces the partition to be created on
                                a particular disk.

         --fstype: Sets the file system type for the partition.  These
           apply to partitions created using '--source rootfs' (see
           --source above).  Valid values are:

             ext2
             ext3
             ext4
             btrfs
             squashfs
             swap

         --fsoptions: Specifies a free-form string of options to be
                      used when mounting the filesystem. This string
                      will be copied into the /etc/fstab file of the
                      installed system and should be enclosed in
                      quotes.  If not specified, the default string is
                      "defaults".

         --label label: Specifies the label to give to the filesystem
                        to be made on the partition. If the given
                        label is already in use by another filesystem,
                        a new label is created for the partition.

         --active: Marks the partition as active.

         --align (in KBytes): This option is specific to wic and says
                              to start a partition on an x KBytes
                              boundary.

         --no-table: This option is specific to wic. Space will be
                     reserved for the partition and it will be
                     populated but it will not be added to the
                     partition table. It may be useful for
                     bootloaders.

         --extra-space: This option is specific to wic. It adds extra
                        space after the space filled by the content
                        of the partition. The final size can go
                        beyond the size specified by --size.
                        By default, 10MB.

         --overhead-factor: This option is specific to wic. The
                            size of the partition is multiplied by
                            this factor. It has to be greater than or
                            equal to 1.
                            The default value is 1.3.

         --part-type: This option is specific to wic. It specifies partition
                      type GUID for GPT partitions.
                      List of partition type GUIDS can be found here:
                      http://en.wikipedia.org/wiki/GUID_Partition_Table#Partition_type_GUIDs

         --use-uuid: This option is specific to wic. It makes wic to generate
                     random globally unique identifier (GUID) for the partition
                     and use it in bootloader configuration to specify root partition.

         --uuid: This option is specific to wic. It specifies partition UUID.
                 It's useful if preconfigured partition UUID is added to kernel command line
                 in bootloader configuration before running wic. In this case .wks file can
                 be generated or modified to set preconfigured parition UUID using this option.

    * bootloader

      This command allows the user to specify various bootloader
      options.  The following are supported 'bootloader' options:

        --timeout: Specifies the number of seconds before the
                   bootloader times out and boots the default option.

        --append: Specifies kernel parameters. These will be added to
                  bootloader command-line - for example, the syslinux
                  APPEND or grub kernel command line.

        --configfile: Specifies a user defined configuration file for
                      the bootloader. This file must be located in the
                      canned-wks folder or could be the full path to the
                      file. Using this option will override any other
                      bootloader option.

      Note that bootloader functionality and boot partitions are
      implemented by the various --source plugins that implement
      bootloader functionality; the bootloader command essentially
      provides a means of modifying bootloader configuration.

    * include

      This command allows the user to include the content of .wks file
      into original .wks file.

      Command uses the following syntax:

         include <file>

      The <file> is either path to the file or its name. If name is
      specified wic will try to find file in the directories with canned
      .wks files.

"""
